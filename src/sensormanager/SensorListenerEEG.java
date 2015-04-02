package sensormanager;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ThreadFactory;

/**
 * Connects to a EEG sensor on a specific port, 
 * gets raw EEG data from the sensor and notifies its observers periodically
 *
 */
public class SensorListenerEEG extends SensorListener{

	private static final int BYTE_BUFFER_SIZE = 1024;
	private static final int COMM_PORT_TIMEOUT = 2000;
	private static final int COMM_PORT_BAUD_RATE = 115200;
	private static final int COMM_PORT_DATABITS = SerialPort.DATABITS_8;
	private static final int COMM_PORT_STOPBITS = SerialPort.STOPBITS_1;
	private static final int COMM_PORT_PARITY = SerialPort.PARITY_NONE;

	private SerialReader serialReader;
	private SerialWriter serialWriter;
	private CommPortIdentifier commPortIdentifier;
	private CommPort commPort;
	private int frequency;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Thread readerThread;

	public SensorListenerEEG(int portNumber) {
		
		// TODO Auto-generated constructor stub
	}

	public SensorListenerEEG(String comPort) {
		try {
			this.commPortIdentifier = CommPortIdentifier.getPortIdentifier(comPort);
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean connect() {
		try {

			if(commPortIdentifier.isCurrentlyOwned()) {
				throw new Exception("EEG port is currently owned");
			}

			commPort = commPortIdentifier.open(this.getClass().getName(), COMM_PORT_TIMEOUT);

			if(commPort instanceof SerialPort == false) {
				throw new Exception("EEG port is not a serial port");
			}
			
			serialPort = (SerialPort)commPort;
			serialPort.setSerialPortParams(COMM_PORT_BAUD_RATE, COMM_PORT_DATABITS, COMM_PORT_STOPBITS, COMM_PORT_PARITY);

			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();

			serialReader = new SerialReader(inputStream);

			readerThread = new Thread(serialReader);
			readerThread.start();


		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] getSensorData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPortNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
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
			byte[] buffer = new byte[BYTE_BUFFER_SIZE];
			int len = -1;

			try {
				while( (len = inputStream.read(buffer)) > -1) {
					//do something
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class SerialWriter  {
		private OutputStream outputStream;

		public SerialWriter(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		public void writeBytes(byte[] bytes) {
			try {
				outputStream.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
