package sensormanager;

import gnu.io.*;
import shared.TimestampedRawData;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import shared.Sensor;

/**
 * Connects to a GSR sensor on a specific port, gets raw GSR data from the
 * sensor and notifies its observers periodically
 *
 */
public class SensorListenerGSR extends SensorListener {

    private static final int COMM_PORT_TIMEOUT = 2000;
    private static final int COMM_PORT_BAUD_RATE = 9600;
    private static final int COMM_PORT_DATABITS = SerialPort.DATABITS_8;
    private static final int COMM_PORT_STOPBITS = SerialPort.STOPBITS_1;
    private static final int COMM_PORT_PARITY = SerialPort.PARITY_NONE;
    private static final int MESSAGE_LENGTH = 2;
    private static final int BUFFER_LENGTH = 10;

    private CommPortIdentifier commPortIdentifier;
    private CommPort commPort;
    private SerialPort serialPort;
    private InputStream inputStream;
    private Thread readerThread;

    private boolean threadsActive;

    public SensorListenerGSR(String comPort) {

        try {
            this.commPortIdentifier = CommPortIdentifier.getPortIdentifier(comPort);
            //this.readQueue = new ArrayBlockingQueue<Byte>(BUFFER_SIZE);
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        }
        sensorType = Sensor.GSR;
    }

    @Override
    public boolean connect() {
        try {

            if (commPortIdentifier.isCurrentlyOwned()) {
                throw new Exception("EEG port is currently owned");
            }

            commPort = commPortIdentifier.open(this.getClass().getName(), COMM_PORT_TIMEOUT);

            if (commPort instanceof SerialPort == false) {
                throw new Exception("EEG port is not a serial port");
            }

            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(COMM_PORT_BAUD_RATE, COMM_PORT_DATABITS, COMM_PORT_STOPBITS, COMM_PORT_PARITY);
            threadsActive = true;
            inputStream = serialPort.getInputStream();
            new Thread(new SerialReader(inputStream)).run();
        } catch (PortInUseException | UnsupportedCommOperationException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean disconnect() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<TimestampedRawData> getSensorData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFrequency() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void startStreaming() {

    }

    @Override
    public void stopStreaming() {

    }

    @Override
    protected void notifyObservers() {
		// TODO Auto-generated method stub

    }

    @Override
    public boolean registerObserver(SensorObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeObserver(SensorObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    private class SerialReader implements Runnable {

        private InputStream inputStream;

        public SerialReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[BUFFER_LENGTH];
            int len = -1;
            int val = 0;
            int d = 0;
            System.out.println("runnnnn");
            try {
                while ((len = inputStream.read(buffer)) > -1) {
                    //disconnection routine
                    if (!threadsActive) {
                        return;
                    }

                    for (int i = 0; i < len; i++) {
                        val = (val << 8) | buffer[i];
                        d++;
                        d = d % 2;
                        if (d == 0) {
                            System.out.println(val);
                            val = 0;
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String toString(){
        
        return "GSR";
    }
}
