package sensormanager;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import shared.TimestampedRawData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Connects to a EEG sensor on a specific port, 
 * gets raw EEG data from the sensor and notifies its observers periodically
 *
 */
public class SensorListenerEEG extends SensorListener{

	private static final int COMM_PORT_TIMEOUT = 2000;
	private static final int COMM_PORT_BAUD_RATE = 115200;
	private static final int COMM_PORT_DATABITS = SerialPort.DATABITS_8;
	private static final int COMM_PORT_STOPBITS = SerialPort.STOPBITS_1;
	private static final int COMM_PORT_PARITY = SerialPort.PARITY_NONE;
	private static final int EEG_GAIN = 24;
	private static final byte CODE_RESET = 'v';
	private static final byte CODE_START_STREAMING = 'b';
	private static final byte CODE_CHANNEL_ON[] = {'!', '@', '#', '$', '%', '^', '&', '*','Q','W','E','R','T','Y','U','I'};
	private static final byte CODE_CHANNEL_OFF[] = {'1','2','3','4','5','6','7','8','q','w','e','r','t','y','u','i'};
	private static final byte CODE_STOP_STREAMING = 's';
	private static final int QUEUE_CAPACITY = 1024;
	private static final int CHANNEL_LENGTH = 8; //8 or 16
	private static final int MESSAGE_LENGTH = 33;
	private static final long CONTINUOUS_COMMAND_DELAY = 500;
	private static final byte DATA_HEADER = (byte) 0xA0;
	private static final byte DATA_FOOT = (byte) 0xC0;
	private static int windowSize = 50;

	private DataInterpreter dataInterpreter;
	private SerialReader serialReader;
	private SerialWriter serialWriter;
	private CommPortIdentifier commPortIdentifier;
	private CommPort commPort;
	private int frequency;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Thread readerThread;
	private Thread interpreterThread;
	private boolean streaming;

	private BlockingQueue<Byte> readQueue;
	private String identificationString;
	private ArrayList<SensorObserver> observers = new ArrayList<>(1);
	private ArrayBlockingQueue<TimestampedRawData[]> dataStacked = new ArrayBlockingQueue<>(500);


	public SensorListenerEEG(int portNumber) {
		
		// TODO Auto-generated constructor stub
	}

	public SensorListenerEEG(String comPort) {
		try {
			this.commPortIdentifier = CommPortIdentifier.getPortIdentifier(comPort);
			this.readQueue = new ArrayBlockingQueue<Byte>(QUEUE_CAPACITY);
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean connect() {
		System.out.println(Integer.toBinaryString(DATA_HEADER));
		System.out.println(Integer.toBinaryString(DATA_FOOT));
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
			System.out.println("thread baslangic");
			readerThread.start();

			dataInterpreter = new DataInterpreter();
			System.out.println("interpreter started");
			interpreterThread = new Thread(dataInterpreter);
			interpreterThread.start();

			serialWriter = new SerialWriter(outputStream);
			serialWriter.writeByte(CODE_RESET);


		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public void startStreaming() {
		serialWriter.writeByte(CODE_START_STREAMING);
	}

	public void stopStreaming() {
		serialWriter.writeByte(CODE_STOP_STREAMING);
	}

	public void setChannelState(int channel, boolean state) {
		if(state) {
			serialWriter.writeByte(CODE_CHANNEL_ON[channel]);
		} else {
			serialWriter.writeByte(CODE_CHANNEL_OFF[channel]);
		}
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TimestampedRawData[] getSensorData() {
		return null;
	}

	public float[] getNextSample() {
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
		for(SensorObserver observer: observers)
			observer.dataArrived(this);
		
	}

	@Override
	public boolean registerObserver(SensorObserver observer) {

		observers.add(observer);
		return true; //TODO i do not know how this function can return false
	}

	@Override
	public boolean removeObserver(SensorObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	private class DataInterpreter implements Runnable {

		@Override
		public void run() {
			try {

				byte[] data = new byte[MESSAGE_LENGTH-2];
				Byte b;
				ArrayList<TimestampedRawData> datas = new ArrayList<>(windowSize);//TODO optimization

				while( (b = readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS)) != null ) {

					if (b.compareTo(DATA_HEADER) == 0 ) {

						for(int i = 0;i<MESSAGE_LENGTH-2;i++) {
							data[i] = readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS);
						}
						if(readQueue.poll(COMM_PORT_TIMEOUT, TimeUnit.MILLISECONDS).compareTo( DATA_FOOT) == 0) {


							datas.add(new TimestampedRawData(convertData(data, MESSAGE_LENGTH -2)));
							if (datas.size() == windowSize) {
								dataStacked.add((TimestampedRawData[]) datas.toArray());
								notifyObservers();
								datas = new ArrayList<>(windowSize);
							}


/*
							for (int i = 0; i < CHANNEL_LENGTH; i++) {
								System.out.print(retfloat[i] + " ");
							}
							System.out.println();
*/
						}
					}


                }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private double[] convertData(byte[] bytes,int len) {
			int sampleNumber = ((int)bytes[0]) & 0x00FF;

			int[] integerData = new int[CHANNEL_LENGTH];
			System.out.print(sampleNumber + "\t");
			for (int i=0;i<CHANNEL_LENGTH;i++) {
				byte triple[] = {bytes[1+i*3],bytes[1+i*3+1],bytes[1+i*3+2]};
				integerData[i] = interpret24bitAsInt32(triple);
			}
			double[] doubleData = new double[CHANNEL_LENGTH];
			for(int i=0;i<CHANNEL_LENGTH;i++) {
				doubleData[i] = ((double)integerData[i]*(4.5/EEG_GAIN/(2<<23-1)));
			}
			return doubleData;
		}

		private int interpret24bitAsInt32(byte[] byteArray) {
			int newInt = (
					((0xFF & byteArray[0]) << 16) |
							((0xFF & byteArray[1]) << 8) |
							(0xFF & byteArray[2])
			);
			if ((newInt & 0x00800000) > 0) {
				newInt |= 0xFF000000;
			} else {
				newInt &= 0x00FFFFFF;
			}
			return newInt;
		}

		int interpret16bitAsInt32(byte[] byteArray) {
			int newInt = (
					((0xFF & byteArray[0]) << 8) |
							(0xFF & byteArray[1])
			);
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

		public SerialReader(InputStream inputStream) throws Exception {

			this.inputStream = inputStream;

			byte[] buffer = new byte[QUEUE_CAPACITY];
			int len = 0;
			byte lastByte = (byte)'a';
			identificationString = "";
			while( identificationString.endsWith("$$$") == false ) {
				try {
					len = inputStream.read(buffer);
					for(int i=0;i<len;i++)
						identificationString += String.valueOf((char)buffer[i]);
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}

			if(identificationString.endsWith("$$$")) {
				System.out.println(identificationString);
				//send reset signal
			} else {
				throw new Exception("Identification does not match with expected ->" + identificationString);
			}

		}

		@Override
		public void run() {
			byte[] buffer = new byte[QUEUE_CAPACITY];
			int len = -1;
			System.out.println("runnnnn");
			try {
				while( ( len = inputStream.read(buffer) ) > -1) {
					for(int i=0;i<len;i++) {
						readQueue.put(buffer[i]);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class SerialWriter  {
		private OutputStream outputStream;
		private long lastSentTime;

		public SerialWriter(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		public void writeByte(byte bytes) {
			try {
				if(System.currentTimeMillis() - lastSentTime - CONTINUOUS_COMMAND_DELAY < 0)
					Thread.sleep(Math.max(CONTINUOUS_COMMAND_DELAY - System.currentTimeMillis() + lastSentTime,0));
				lastSentTime = System.currentTimeMillis();
				outputStream.write(bytes);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
