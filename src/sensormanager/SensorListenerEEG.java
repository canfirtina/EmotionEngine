package sensormanager;

import gnu.io.*;
import shared.TimestampedRawData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
    private static final int BUFFER_SIZE = 1024;
    private static final int CHANNEL_LENGTH = 8; //8 or 16
    private static final int MESSAGE_LENGTH = 33;
    private static final long CONTINUOUS_COMMAND_DELAY = 500;
    private static final byte DATA_HEADER = (byte) 0xA0;
    private static final byte DATA_FOOT = (byte) 0xC0;
    private static final int NOT_AVAILABLE_LIMIT = 100;
    private static final int CONNECTION_RETRY_INTERVAL = 50;
    private static final int RECEIVE_TIMEOUT = 5000;

    private DataInterpreter dataInterpreter;
    private SerialReader serialReader;
    private SerialWriter serialWriter;
    private CommPortIdentifier commPortIdentifier;
    private CommPort commPort;
    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Thread readerThread;
    private Thread interpreterThread;
    private boolean threadsActive;

    private BlockingQueue<Byte> readQueue;
    private String identificationString;
    private List<TimestampedRawData> lastEpoch;
    private String comPortString;

    private boolean connectionEstablished;
    private boolean streamingOn;
    private boolean deviceReady;

    public SensorListenerEEG(String comPort) {
        this.comPortString = comPort;
        this.readQueue = new ArrayBlockingQueue<Byte>(BUFFER_SIZE);
    }

    @Override
    public boolean connect() {
        try {
            this.commPortIdentifier = CommPortIdentifier.getPortIdentifier(comPortString);
            if (commPortIdentifier.isCurrentlyOwned()) {
                throw new Exception("EEG port is currently owned");
            }

            commPort = commPortIdentifier.open(this.getClass().getName(), COMM_PORT_TIMEOUT);

            if (commPort instanceof SerialPort == false) {
                throw new Exception("EEG port is not a serial port");
            }

            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(COMM_PORT_BAUD_RATE, COMM_PORT_DATABITS, COMM_PORT_STOPBITS, COMM_PORT_PARITY);

            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            serialPort.enableReceiveTimeout(RECEIVE_TIMEOUT);
        } catch (PortInUseException | UnsupportedCommOperationException e) {
            notifyObserversConnectionFailed();
            return false;
        } catch (IOException e) {
            notifyObserversConnectionFailed();
            return false;
        } catch (Exception e) {
            notifyObserversConnectionFailed();
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                threadsActive = true;

                serialWriter = new SerialWriter(outputStream);

                serialReader = new SerialReader(inputStream);

                readerThread = new Thread(serialReader);
                System.out.println("thread baslangic");
                readerThread.start();

                dataInterpreter = new DataInterpreter();
                System.out.println("interpreter started");
                interpreterThread = new Thread(dataInterpreter);
                interpreterThread.start();


            }
        }).start();

        return true;
    }

    public void startStreaming() {
        streamingOn = true;
        serialWriter.writeByte(CODE_START_STREAMING);
    }

    public void stopStreaming() {
        streamingOn = false;
        serialWriter.writeByte(CODE_STOP_STREAMING);
    }

    public void setChannelState(int channel, boolean state) {
        if (state) {
            serialWriter.writeByte(CODE_CHANNEL_ON[channel]);
        } else {
            serialWriter.writeByte(CODE_CHANNEL_OFF[channel]);
        }
    }

    public void sendReset() {
        serialWriter.writeByte(CODE_RESET);
    }

    @Override
    public boolean disconnect() {
        serialPort.close();
        threadsActive = false;
        return true;
    }

    @Override
    public List<TimestampedRawData> getSensorData() {
        return lastEpoch;
    }

    @Override
    public int getFrequency() {
        // TODO Auto-generated method stub
        return 0;
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
        for (SensorObserver observer : observerCollection) {
            observer.connectionEstablished(this);
        }
    }

    protected void notifyObserversConnectionFailed() {
        threadsActive = false;
        System.out.println("connection fail");
        for (SensorObserver observer : observerCollection) {
            observer.connectionFailed(this);
        }
    }

    protected void notifyObserversConnectionError() {
        threadsActive = false;
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
    private class DataInterpreter implements Runnable {

        @Override
        public void run() {
            try {

                byte[] data = new byte[MESSAGE_LENGTH - 2];
                Byte b;
                String answer = "";
                Thread.sleep(2000);
                sendReset();
                while (true) {
                    //this thread terminates itself if requested by the object
                    if (!threadsActive) {
                        return;
                    }

                    //Read raw packet header from thread safe queue
                    b = readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS);


                    if(b==null)
                        continue;
                    if(connectionEstablished) {
                        if (streamingOn) {
                            //make sure packet is not broken
                            if (b != null && b.compareTo(DATA_HEADER) == 0) {

                                //header is valid, so read the rest of the packet
                                for (int i = 0; i < MESSAGE_LENGTH - 2; i++) {
                                    data[i] = readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS);
                                }
                                //make sure footer is valid
                                if (readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS).compareTo(DATA_FOOT) == 0) {
                                    System.out.println(Arrays.toString(convertData(data,MESSAGE_LENGTH-2)));
                                    //we are sure that packet is not damaged.
                                    //interpret it and put it into epoch
                                    TimestampedRawData tsrd = new TimestampedRawData(convertData(data, MESSAGE_LENGTH - 2));

                                    if (dataEpocher.addData(tsrd) == false) {
                                        lastEpoch = dataEpocher.getEpoch();
                                        dataEpocher.reset();
                                        dataEpocher.addData(tsrd);
                                        notifyObservers();

                                    }

                                }
                            }
                        }
                    } else {
                        System.out.println(answer);
                        answer += String.valueOf((char)b.byteValue());
                        if(answer.endsWith("$$$")) {
                            answer = "";
                            setAndNotifyConnectionEstablished();
                        }
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private class SerialReader implements Runnable {

        private InputStream inputStream;

        /**
         * Constructor listens for identification string.
         * @param inputStream Input stream where sensor puts data onto
         * @throws IOException If identification string sent by sensor does not match OpenBCI identification string
         * this method throws.
         */
        public SerialReader(InputStream inputStream) {

            this.inputStream = inputStream;

        }

        @Override
        public void run() {
            byte[] buffer = new byte[MESSAGE_LENGTH];
            int len = -1;
            System.out.println("runnnnn");
            try {
                while ( (len = inputStream.read(buffer)) > -1 ) {

                    //this thread destroys itself if requested by object
                    if (!threadsActive) {
                        return;
                    }

                    //streaming is on and there is no data. There is some connection error
                    if(streamingOn && len == 0) {
                        throw new IOException();
                    }
                    if(len == 0)
                        continue;

                    for (int i = 0; i < len; i++) {
                        //System.out.println(buffer[i]);
                        readQueue.put(buffer[i]);
                    }
                }
            } catch (IOException e) {
                notifyObserversConnectionError();
            } catch (InterruptedException e) {
                e.printStackTrace();
                notifyObserversConnectionError();
            }
        }
    }

    /**
     * Inner class that provides write method to sensor
     */
    private class SerialWriter {

        /**
         * Output stream object that is connected to the sensor
         */
        private OutputStream outputStream;

        /**
         * Last time a command was sent to the sensor. There needs to be some delay between subsequent writes to sensor
         */
        private long lastSentTime;

        public SerialWriter(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void writeByte(byte bytes) {
            try {
                //Make sure enough time has passed since last write operation
                if (System.currentTimeMillis() - lastSentTime - CONTINUOUS_COMMAND_DELAY < 0) {
                    Thread.sleep(Math.max(CONTINUOUS_COMMAND_DELAY - System.currentTimeMillis() + lastSentTime, 0));
                }
                lastSentTime = System.currentTimeMillis();
                System.out.println("written byte: " + bytes);
                outputStream.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String toString(){
        
        return "OpenBCI EEG";
    }
}
