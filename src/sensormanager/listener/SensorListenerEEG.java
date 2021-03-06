package sensormanager.listener;

import gnu.io.*;
import sensormanager.data.TimestampedRawData;
import sensormanager.util.Filter;

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
    private static final int BUFFER_SIZE = 825;
    private static final int CHANNEL_LENGTH = 8; //8 or 16
    private static final int MESSAGE_LENGTH = 33;
    private static final long CONSECUTIVE_COMMAND_DELAY = 100;
    private static final byte DATA_HEADER = (byte) 0xA0;
    private static final byte DATA_FOOT = (byte) 0xC0;
    private static final int RECEIVE_TIMEOUT = 2000;

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;

    private ExecutorService executorWriter = Executors.newSingleThreadExecutor();
    private ExecutorService executorReader = Executors.newSingleThreadExecutor();

    private List<TimestampedRawData> lastEpoch;

    private boolean connectionEstablished;

    /**
     * There should be some time between consecutive write operations.
     * Each write request is processed in a different Callable submitted to the same SingleThreadExecutor.
     * To instantl reflect changes made by different threads this should strictly be volatile
     */
    private volatile long lastCommandSendTime;

    private Object lockStreaming = new Object();
    private volatile boolean streamingOn;

    public SensorListenerEEG(String comPort) {
        this.serialPortString = comPort;
    }

    /**
     * connects to sensor and initiates a handshake with sensor
     * @return true if handshake is initiated. false if port is busy or handshake could not be initiated
     */
    @Override
    public boolean connect() {
        try {
            CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(serialPortString);
            
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

            sendReset();

        } catch (Exception e) {
            e.printStackTrace();
            notifyObserversConnectionFailed();
            return false;
        }

        return true;
    }

    /**
     * Tells OpenBCI to start streaming and starts a thread to listen to OpenBCI. As new data arrives dataArrived method
     * of registered Observers is called
     */
    public void startStreaming() {
        synchronized (lockStreaming) {
            dataEpocher.reset();
            streamingOn = true;
            executorReader.submit(new DataInterpreter());
            writeBytes(CODE_START_STREAMING);
        }
    }

    /**
     * Tells OpenBCI to stop streaming
     */
    public void stopStreaming() {
        synchronized (lockStreaming) {
            streamingOn = false;
            writeBytes(CODE_STOP_STREAMING);
        }
    }

    /**
     * Open/close selected channel
     * @param channel Channel number of desired channel. Valid channels are 0-15
     * @param state true for opening and false for closing
     */
    public void setChannelState(int channel, boolean state) {
        if (state) {
            writeBytes(CODE_CHANNEL_ON[channel]);
        } else {
            writeBytes(CODE_CHANNEL_OFF[channel]);
        }
    }

    /**
     * Resets OpenBCI board to default state
     */
    public void sendReset() {
        executorReader.submit(new ConnectionInitiator());
    }
	
	/**
	 * TODO: delete this method
	 */
	/*
	public void setSensorData(List<TimestampedRawData> epoch){
		lastEpoch = epoch;
	}*/
	
    /**
     * Gets last recorded epoch
     * @return A list of raw sensor data ordered by time of arrival
     */
    @Override
    public List<TimestampedRawData> getSensorData() {
        return lastEpoch;
    }

    @Override
    public boolean isConnected() {
        return connectionEstablished;
    }

    @Override
    public boolean disconnect() {
        System.out.println("disconnnneeeeeeeeeeeect");
        synchronized (lockStreaming) {
            streamingOn = false;
        }
        executorReader.shutdownNow();
        writeBytes(CODE_STOP_STREAMING);
        executorWriter.shutdown();

        serialPort.close();
        connectionEstablished = false;
        return true;
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
        for (SensorObserver observer : observerCollection) {
            observer.connectionFailed(this);
        }
    }

    protected void notifyObserversConnectionError() {
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

    private synchronized void setAndNotifyConnectionEstablished() {
        if(!connectionEstablished) {
            connectionEstablished = true;
            notifyObserversConnectionEstablished();
        }
    }

    private synchronized void setAndNotifyConnectionError() {
        if(connectionEstablished) {
            connectionEstablished = false;
            notifyObserversConnectionError();
        }
    }

    public void sendQuerySettings() {
        writeBytes(CODE_QUERY);
        executorReader.submit(new ResponseReader());
    }


    private void writeBytes(final byte bytes) {
        byte arr[] = {bytes};
        writeBytes(arr);
    }

    private void writeBytes(final byte bytes[]) {
        try {
            executorWriter.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    //Make sure enough time has passed since last write operation
                    if (System.currentTimeMillis() - lastCommandSendTime - CONSECUTIVE_COMMAND_DELAY < 0) {
                        Thread.sleep(Math.max(CONSECUTIVE_COMMAND_DELAY - System.currentTimeMillis() + lastCommandSendTime, 0));
                    }
                    lastCommandSendTime = System.currentTimeMillis();
                    outputStream.write(bytes);
                    return null;
                }
            });
        } catch(RejectedExecutionException e) {
            notifyObserversConnectionError();
        }
    }

    @Override
    public int getFrequency() {
        return 250;
    }

    @Override
    public String toString(){
        return "OpenBCI EEG";
    }

	@Override
	public double weight() {
		return 1.0;
	}

    /**
     * DataInterpreter interprets raw OpenBCI data packets.
     */
    private class DataInterpreter implements Callable<Void> {

        @Override
        public Void call() {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                byte[] packet = new byte[MESSAGE_LENGTH];
                byte[] rawdata = new byte[MESSAGE_LENGTH-2];
                int index = 0;
                int len;
/*                long[] tss = new long[825];
                int c = 0;*/
                while ((len = inputStream.read(buffer)) > -1) {

                    if(len == 0)
                        throw new IOException();

                    //Read raw packet header from thread safe queue
                    for (int i = 0; i < len; i++) {
                        packet[index++] = buffer[i];
                        if(streamingOn == false)
                            break;

                        if(index==MESSAGE_LENGTH) {

                            if (isValid(packet)) {
                                for (int j = 0; j < rawdata.length; j++) {
                                    rawdata[j] = packet[j+1];
                                }

                                double dat[] = convertData(rawdata, MESSAGE_LENGTH - 2);
                                /*
                                Filter.filterNotch50(dat);
                                Filter.bandpass1_50(dat);
*/
                                TimestampedRawData tsrd = new TimestampedRawData(dat);
/*                                tss[((int) c)] = tsrd.getTime();
                                c = (c+1)%25;
                                if(c==0){
                                    for(int x=0;x<10;x++) {
                                        System.out.print(tss[x] + " ");
                                    }
                                    System.out.println();
                                }*/
                                if(dataEpocher.readyForEpoch()) {
                                    lastEpoch = dataEpocher.getEpoch();
                                    System.out.println(lastEpoch.size());
                                    dataEpocher.reset();
                                    notifyObservers();
                                }

                                dataEpocher.addData(tsrd);


                            }
                            index = 0;
                        }
                    }

                }
            } catch (IOException e) {
                if(streamingOn)
                    notifyObserversConnectionError();
            }
            synchronized (lockStreaming) {
                streamingOn = false;
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
                doubleData[i] = (int)((double) integerData[i] * (4.5 / EEG_GAIN / (2 << 23 - 1)) * 1000000);
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


    private class ConnectionInitiator implements Callable<Void> {
        @Override
        public Void call() {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;

            int waitingCount;
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
                outputStream.write(CODE_STOP_STREAMING);
                Thread.sleep(100);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notifyObserversConnectionFailed();
            return null;
        }

    }

    private class ResponseReader implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            int len;
            String response = "";
            byte[] buffer = new byte[BUFFER_SIZE];
            while( (len=inputStream.read(buffer)) > -1 ) {
                for (int i = 0; i < len; i++) {
                    response += (char)buffer[i];
                    if(response.endsWith("$$$")) {
                        return null;
                    }
                }
            }
            setAndNotifyConnectionError();
            return null;
        }
    }




}
