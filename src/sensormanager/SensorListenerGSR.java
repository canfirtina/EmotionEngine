package sensormanager;

import gnu.io.*;
import shared.TimestampedRawData;


import java.util.List;
import java.io.IOException;
import java.io.InputStream;
/**
 * Connects to a GSR sensor on a specific port, 
 * gets raw GSR data from the sensor and notifies its observers periodically
 *
 */
public class SensorListenerGSR extends SensorListener {

	private static final int COMM_PORT_TIMEOUT = 2000;
	private static final int COMM_PORT_BAUD_RATE = 9600;
	private static final int COMM_PORT_DATABITS = SerialPort.DATABITS_8;
	private static final int COMM_PORT_STOPBITS = SerialPort.STOPBITS_1;
	private static final int COMM_PORT_PARITY = SerialPort.PARITY_NONE;
	private static final int MESSAGE_LENGTH = 2;
	private static final int BUFFER_LENGTH = 10010;
	private static final int RECEIVE_TIMEOUT = 5000;

	private CommPortIdentifier commPortIdentifier;
	private CommPort commPort;
	private SerialPort serialPort;
	private InputStream inputStream;
	private String comPortString;

	private boolean threadsActive;
	private boolean connectionEstablished;
	private boolean streamingOn;
	private List<TimestampedRawData> lastEpoch;



	public SensorListenerGSR(String comPort) {

		this.comPortString = comPort;
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
			threadsActive = true;
			inputStream = serialPort.getInputStream();
			serialPort.enableReceiveTimeout(RECEIVE_TIMEOUT);
			new Thread(new SerialReader(inputStream)).start();
		} catch (Exception e) {
			notifyObserversConnectionFail();
			return false;
		}


		return true;
	}



	@Override
	public boolean disconnect() {
		threadsActive = false;
		serialPort.close();
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
	public void startStreaming() {
		streamingOn = true;
	}

	@Override
	public void stopStreaming() {
		streamingOn = false;

	}

	@Override
	protected void notifyObservers() {
		threadsActive = false;
		for(SensorObserver observer : observerCollection)
			observer.dataArrived(this);
		
	}

	private void notifyObserversConnectionFail() {
		threadsActive = false;
		System.out.println("connection fail");
		for(SensorObserver observer : observerCollection)
			observer.dataArrived(this);
	}

	@Override
	public boolean registerObserver(SensorObserver observer) {
		return observerCollection.add(observer);
	}

	@Override
	public boolean removeObserver(SensorObserver observer) {
		return observerCollection.remove(observer);
	}

	private void notifyObserversConnectionEstablished() {
		for (SensorObserver observer : observerCollection)
			observer.connectionEstablished(this);
	}

	private void notifyObserversConnectionError() {
		System.out.println("connectionerror");
		for (SensorObserver observer : observerCollection)
			observer.connectionError(this);
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
			try {
				while( (len = inputStream.read(buffer)) > -1 ) {

					if(len == 0) {
						throw new IOException();
					}

					//disconnection statement
					if (!threadsActive)
						return;

					if (connectionEstablished == false) {
						connectionEstablished = true;
						notifyObserversConnectionEstablished();
					}

					if (streamingOn) {
						for (int i = 0; i < len; i++) {
							if(buffer[i] == 13) {
								continue;
							}
							if(buffer[i] == 10) {
								double rd[] = new double[1];
								rd[0] = (double) val;
								System.out.println(val);
								TimestampedRawData rawData = new TimestampedRawData(rd);
								if (dataEpocher.addData(rawData) == false) {
									lastEpoch = dataEpocher.getEpoch();
									dataEpocher.reset();
									dataEpocher.addData(rawData);
									notifyObservers();

								}
								val=0;
							} else {
								val = val * 10 + (buffer[i]) - '0';
							}



						}
					}
				}
			} catch (IOException e) {
				notifyObserversConnectionError();
			}
		}

	}


}
