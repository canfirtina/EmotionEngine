package sensormanager;

import gnu.io.*;
import shared.TimestampedRawData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                try {
                    threadsActive = true;
                    try {
                        serialReader = new SerialReader(inputStream);
                    } catch (IOException e) {
                        threadsActive = false;
                        notifyObserversConnectionFailed();
                        return;
                    }

                    readerThread = new Thread(serialReader);
                    System.out.println("thread baslangic");
                    readerThread.start();

                    dataInterpreter = new DataInterpreter();
                    System.out.println("interpreter started");
                    interpreterThread = new Thread(dataInterpreter);
                    interpreterThread.start();

                    serialWriter = new SerialWriter(outputStream);
                    serialWriter.writeByte(CODE_RESET);

                    connectionEstablished = true;

                    notifyObserversConnectionEstablished();

                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Override
    public boolean disconnect() {
        serialPort.close();
        threadsActive = false;
        return true;
    }

	public void setSensorData(List<TimestampedRawData> data){
		lastEpoch = data;
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

    private class DataInterpreter implements Runnable {

        @Override
        public void run() {
            try {

                byte[] data = new byte[MESSAGE_LENGTH - 2];
                Byte b;

                while (true) {
                    b = readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS);
                    //disconnection routine
                    if (!threadsActive) {
                        return;
                    }

                    if (b != null && b.compareTo(DATA_HEADER) == 0) {

                        for (int i = 0; i < MESSAGE_LENGTH - 2; i++) {
                            data[i] = readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS);
                        }
                        if (readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS).compareTo(DATA_FOOT) == 0) {

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

    private class SerialReader implements Runnable {

        private InputStream inputStream;

        public SerialReader(InputStream inputStream) throws IOException {

            this.inputStream = inputStream;

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            identificationString = "";
            int bytesread = 0;

            while (identificationString.endsWith("$$$") == false) {


                len = inputStream.read(buffer);

                if (len == 0 || bytesread > 500) {
                    throw new IOException();
                }
                bytesread += len;
                for (int i = 0; i < len; i++) {
                    identificationString += String.valueOf((char) buffer[i]);
                }
            }


            if (identificationString.endsWith("$$$")) {
                System.out.println(identificationString);
                //send reset signal
            } else {
                throw new IOException("Identification does not match with expected ->" + identificationString);
            }

        }

        @Override
        public void run() {
            byte[] buffer = new byte[MESSAGE_LENGTH];
            int len = -1;
            System.out.println("runnnnn");
            try {
                while ( (len = inputStream.read(buffer)) > -1 ) {

                    if(streamingOn && len == 0) {
                        throw new IOException();
                    }

                    //disconnection routine
                    if (!threadsActive) {
                        return;
                    }

                    for (int i = 0; i < len; i++) {
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

    private class SerialWriter {

        private OutputStream outputStream;
        private long lastSentTime;

        public SerialWriter(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void writeByte(byte bytes) {
            if (!connectionEstablished) {
                return;
            }
            try {
                if (System.currentTimeMillis() - lastSentTime - CONTINUOUS_COMMAND_DELAY < 0) {
                    Thread.sleep(Math.max(CONTINUOUS_COMMAND_DELAY - System.currentTimeMillis() + lastSentTime, 0));
                }
                lastSentTime = System.currentTimeMillis();
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
