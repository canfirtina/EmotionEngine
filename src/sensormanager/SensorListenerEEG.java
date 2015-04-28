package sensormanager;

import gnu.io.*;
import shared.TimestampedRawData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Connects to a EEG sensor on a specific port, gets raw EEG data from the
 * sensor and notifies its observers periodically
 *
 */
public class SensorListenerEEG extends SensorListener {

    private static final int COMM_PORT_TIMEOUT = 2000;
    private static final int COMM_PORT_BAUD_RATE = 115200;
    private static final int COMM_PORT_DATABITS = SerialPort.DATABITS_8;
    private static final int COMM_PORT_STOPBITS = SerialPort.STOPBITS_1;
    private static final int COMM_PORT_PARITY = SerialPort.PARITY_NONE;
    private static final int EEG_GAIN = 24;
    private static final byte CODE_RESET = 'v';
    private static final byte CODE_START_STREAMING = 'b';
    private static final byte CODE_CHANNEL_ON[] = {'!', '@', '#', '$', '%', '^', '&', '*', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I'};
    private static final byte CODE_CHANNEL_OFF[] = {'1', '2', '3', '4', '5', '6', '7', '8', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i'};
    private static final byte CODE_STOP_STREAMING = 's';
    private static final byte CODE_QUERY = '?';
    private static final int BUFFER_SIZE = 1024;
    private static final int CHANNEL_LENGTH = 8; //8 or 16
    private static final int MESSAGE_LENGTH = 33;
    private static final long CONSECUTIVE_COMMAND_DELAY = 100;
    private static final byte DATA_HEADER = (byte) 0xA0;
    private static final byte DATA_FOOT = (byte) 0xC0;
    private static final int RECEIVE_TIMEOUT = 2000;

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String comPortString;

    private ExecutorService executorSender = Executors.newSingleThreadExecutor();
    private ExecutorService executorReceiver = Executors.newSingleThreadExecutor();
    private BlockingQueue<Byte> readQueue;
    private volatile boolean interpreterActive;
    private volatile boolean readerActive;

    private List<TimestampedRawData> lastEpoch;
    private String lastResponse = "";

    private boolean connectionEstablished;
    private volatile boolean streamingMode;

    /**
     * There should be some time between consecutive write operations.
     * Each write request is processed in a different Callable submitted to the same SingleThreadExecutor.
     * To instantl reflect changes made by different threads this should strictly be volatile
     */
    private volatile long lastCommandSendTime;

    public SensorListenerEEG(String comPort) {
        this.comPortString = comPort;
        this.readQueue = new ArrayBlockingQueue<>(BUFFER_SIZE);
    }

    @Override
    public boolean connect() {
        try {
            CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(comPortString);
            if (commPortIdentifier.isCurrentlyOwned()) {
                throw new Exception("EEG port is currently owned");
            }

            CommPort commPort = commPortIdentifier.open(this.getClass().getName(), COMM_PORT_TIMEOUT);

            if (commPort instanceof SerialPort == false) {
                throw new Exception("EEG port is not a serial port");
            }

            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(COMM_PORT_BAUD_RATE, COMM_PORT_DATABITS, COMM_PORT_STOPBITS, COMM_PORT_PARITY);

            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            serialPort.enableReceiveTimeout(RECEIVE_TIMEOUT);

            executorSender.submit(new ConnectionInitiator());

        } catch (Exception e) {
            notifyObserversConnectionFailed();
            return false;
        }


        return true;
    }

    public void startStreaming() {
        //TODO sendcommand
        executorReceiver.submit(new DataInterpreter());
    }

    public void stopStreaming() {
        writeBytes(CODE_STOP_STREAMING);
    }

    public void setChannelState(int channel, boolean state) {
        if (state) {
            writeBytes(CODE_CHANNEL_ON[channel]);
        } else {
            writeBytes(CODE_CHANNEL_OFF[channel]);
        }
    }

    public void sendReset() {
        writeBytes(CODE_RESET);
    }

    @Override
    public boolean disconnect() {
        interpreterActive = false;
        readerActive = false;
        executorSender.shutdownNow();
        serialPort.close();
        connectionEstablished = false;
        return true;
    }

    @Override
    public List<TimestampedRawData> getSensorData() {
        return lastEpoch;
    }

    @Override
    public boolean isConnected() {
        return connectionEstablished;
    }

    @Override
    protected void notifyObservers() {
        for (SensorObserver observer : observerCollection) {
            observer.dataArrived(this);
        }

    }

    protected void notifyObserversConnectionEstablished() {
        System.out.println("connection established");
        for (SensorObserver observer : observerCollection) {
            observer.connectionEstablished(this);
        }
    }

    protected void notifyObserversConnectionFailed() {
        System.out.println("connection fail");
        for (SensorObserver observer : observerCollection) {
            observer.connectionFailed(this);
        }
    }

    protected void notifyObserversConnectionError() {
        System.out.println("connection error");
        for (SensorObserver observer : observerCollection) {
            observer.connectionError(this);
        }

    }

    @Override
    public boolean registerObserver(SensorObserver observer) {

        return observerCollection.add(observer);
    }

    @Override
    public boolean removeObserver(SensorObserver observer) {
        return observerCollection.remove(observer);
    }

    /**
     * DataInterpreter interprets raw OpenBCI data packets.
     */
    private class DataInterpreter implements Callable<Void> {

        @Override
        public Void call() {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                byte[] data = new byte[MESSAGE_LENGTH];
                int index = 0;
                int len;

                outputStream.write(CODE_START_STREAMING);


                while ((len = inputStream.read(buffer)) > -1) { //TODO -1 / 0
                    //Read raw packet header from thread safe queue
                    for (int i = 0; i < len; i++) {
                        data[index++] = buffer[i];
                        if(index==MESSAGE_LENGTH) {
                            if (isValid(data)) {
                                System.out.println(Arrays.toString(convertData(data,MESSAGE_LENGTH-2)));
                                TimestampedRawData tsrd = new TimestampedRawData(convertData(buffer, MESSAGE_LENGTH - 2));

                                if (dataEpocher.addData(tsrd) == false) {
                                    lastEpoch = dataEpocher.getEpoch();
                                    dataEpocher.reset();
                                    dataEpocher.addData(tsrd);
                                    notifyObservers();

                                }
                            }
                            index = 0;
                        }
                    }
                    /*
                    if (len < 1) {
                        //TODO
                        System.out.println("message broken");
                        break;

                    }
                    for (int bufferIndex = 0; bufferIndex < len; bufferIndex++) {

                        //make sure packet is not broken
                        if (buffer[bufferIndex] == DATA_HEADER) {
                            data[index++] = buffer[bufferIndex];


                            //make sure footer is valid
                            if (buffer[MESSAGE_LENGTH - 1] == DATA_FOOT) {
                                //System.out.println(Arrays.toString(convertData(data,MESSAGE_LENGTH-2)));
                                //we are sure that packet is not damaged.
                                //interpret it and put it into epoch

                                //debug
                                System.out.println(Arrays.toString(convertData(buffer, MESSAGE_LENGTH - 2)));

                                TimestampedRawData tsrd = new TimestampedRawData(convertData(buffer, MESSAGE_LENGTH - 2));

                                if (dataEpocher.addData(tsrd) == false) {
                                    lastEpoch = dataEpocher.getEpoch();
                                    dataEpocher.reset();
                                    dataEpocher.addData(tsrd);
                                    notifyObservers();

                                }


                            }
                        }
                    }
                 */

                }
            } catch (Exception e) {
                System.out.println(readQueue);
                e.printStackTrace();
            }
            return null;
        }

        private boolean isValid(byte[] data) {
            if( data[0] == DATA_HEADER && data[data.length-1] == DATA_FOOT)
                return true;
            else
                return false;
        }


        /**
         * Gets volt values of all channels
         * @param bytes OpenBCI data packet
         * @param len Length of data packet in bytes
         * @return An array of volt values ordered by channel number
         */
        private double[] convertData(byte[] bytes, int len) {
            int sampleNumber = ((int) bytes[0]) & 0x00FF;

            int[] integerData = new int[CHANNEL_LENGTH];
            for (int i = 0; i < CHANNEL_LENGTH; i++) {
                byte triple[] = {bytes[1 + i * 3], bytes[1 + i * 3 + 1], bytes[1 + i * 3 + 2]};
                integerData[i] = interpret24bitAsInt32(triple);
            }
            double[] doubleData = new double[CHANNEL_LENGTH];
            for (int i = 0; i < CHANNEL_LENGTH; i++) {
                doubleData[i] = ((double) integerData[i] * (4.5 / EEG_GAIN / (2 << 23 - 1)));
            }
            return doubleData;
        }

        /**
         * Converts 24 bit integers to 32 bit integers.
         * @param byteArray bytes to be converted. First 3 bytes are used if there is more than 3 in the array.
         * @return 32 bit representation of the given integer
         */
        private int interpret24bitAsInt32(byte[] byteArray) {
            int newInt = (((0xFF & byteArray[0]) << 16)
                    | ((0xFF & byteArray[1]) << 8)
                    | (0xFF & byteArray[2]));
            if ((newInt & 0x00800000) > 0) {
                newInt |= 0xFF000000;
            } else {
                newInt &= 0x00FFFFFF;
            }
            return newInt;
        }

        /**
         * Converts 16 bit integers to 32 bit integers.
         * @param byteArray bytes to be converted. First 2 bytes are used if there is more than 3 in the array.
         * @return 32 bit representation of the given integer
         */
        int interpret16bitAsInt32(byte[] byteArray) {
            int newInt = (((0xFF & byteArray[0]) << 8)
                    | (0xFF & byteArray[1]));
            if ((newInt & 0x00008000) > 0) {
                newInt |= 0xFFFF0000;
            } else {
                newInt &= 0x0000FFFF;
            }
            return newInt;
        }
    }

    private synchronized void setAndNotifyConnectionEstablished() {
        if(!connectionEstablished) {
            connectionEstablished = true;
            notifyObserversConnectionEstablished();
        }
    }

    private class ConnectionInitiator implements Callable<Void> {
        @Override
        public Void call() {
            byte[] buffer = new byte[MESSAGE_LENGTH];
            boolean expectClearIndication = false;
            int len;

            int waitingCount;
            int notRespondingCount = 0;
            String responseString = "";

            waitingCount = 0;
            try {
                while ((len = inputStream.read(buffer)) > -1) {
                    if (len == 0) {
                        break;
                    } else {
                        waitingCount++;
                    }
                    if (waitingCount > 4) {
                        notifyObserversConnectionFailed();
                        return null;
                    }

                }

                outputStream.write(CODE_RESET);
                while ((len = inputStream.read(buffer)) > -1) {
                    if(len < 1)
                        break;
                    for (int i = 0; i < len; i++) {
                        responseString += (char)buffer[i];
                    }
                    if(responseString.endsWith("$$$")) {
                        setAndNotifyConnectionEstablished();
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            notifyObserversConnectionFailed();
            return null;
        }

    }

    public void sendQuerySettings() {
        writeBytes(CODE_QUERY);
    }

    public String getLastResponse() {
        return lastResponse;
    }

    private void writeBytes(final byte bytes) {
        System.out.println("write request made");
        byte arr[] = {bytes};
        writeBytes(arr);
    }

    private void writeBytes(final byte bytes[]) {
        try {
            executorSender.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    //Make sure enough time has passed since last write operation
                    if (System.currentTimeMillis() - lastCommandSendTime - CONSECUTIVE_COMMAND_DELAY < 0) {
                        Thread.sleep(Math.max(CONSECUTIVE_COMMAND_DELAY - System.currentTimeMillis() + lastCommandSendTime, 0));
                    }
                    lastCommandSendTime = System.currentTimeMillis();
                    outputStream.write(bytes);
                    System.out.println("written bytes: " + Arrays.toString(bytes));
                    return null;
                }
            });
        } catch(RejectedExecutionException e) {
            notifyObserversConnectionError();
        }
    }

    @Override
    public String toString(){
        return "OpenBCI EEG";
    }
}
