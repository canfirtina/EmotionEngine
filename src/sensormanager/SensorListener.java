package sensormanager;

import emotionlearner.DataEpocher;
import shared.TimestampedRawData;

import java.util.ArrayList;

/**
 * Blue print of listeners of the sensors. Connects to a sensor on a specific port, 
 * gets raw data from the sensor and notifies its observers periodically
 *
 */
public abstract class SensorListener {

	/**
	 * Meant to be passed by observer
	 */
	protected DataEpocher dataEpocher;

	/**
	 * Port number of the sensor
	 */
	protected int portNumber;
	
	/**
	 * Tells if the connection is established.
	 * True: Connection established
	 * False: Connection is not established
	 */
	private boolean connectionStatus;
	
	/**
	 * Stores all the observers that registered to
	 * observe the changes of the status of this sensor
	 */
	protected ArrayList<SensorObserver> observerCollection = new ArrayList<SensorObserver>();
	
	/**
	 * Raw data of the sensor. As this data changes, observers
	 * are notified.
	 */
	private byte[] sensorData;
	
	/**
	 * Frequency of the sensor (Hz)
	 */
	private int freq;
	
	/**
	 * Tries to connect with the sensor
	 * @return true if the connection is established
	 */
	public abstract boolean connect();
	
	/**
	 * Disconnects the sensor from Emotion Engine
	 * @return true if the disconnection is successful
	 */
	public abstract boolean disconnect();
	
	/**
	 * Gives the current sensor data as bytes
	 * @return the raw data of the sensor
	 */
	public abstract ArrayList<TimestampedRawData> getSensorData();
	
	/**
	 * Frequency of the sensors in Hz.
	 * @return the numerical value of the frequency in Hz.
	 */
	public abstract int getFrequency();
	
	/**
	 * Port number of the sensor
	 * @return the numerical value of the port number of the sensor
	 */
	public abstract int getPortNumber();
	
	/**
	 * States if Emotion Engine is currently connected to a sensor
	 * @return
	 */
	public abstract boolean isConnected();
	
	/**
	 * When a data arrives, a connection is established, or the sensor
	 * is disconnected, observers are notified.
	 */

	/**
	 * starts data stream and packaging
	 */
	public abstract void startStreaming();

	/**
	 * stops data stream
	 */
	public abstract void stopStreaming();

	protected abstract void notifyObservers();
	
	/**
	 * Registers the observer to observe the changes of this sensor
	 * @param observer is registered to be notified
	 * @return
	 */
	public abstract boolean registerObserver( SensorObserver observer);
	
	/**
	 * Removes the observer so that it won't be notified for any change of this sensor
	 * @param observer is removed from observers list of this sensor.
	 * @return
	 */
	public abstract boolean removeObserver( SensorObserver observer);

	public void setDataEpocher(DataEpocher dataEpocher) {
		this.dataEpocher = dataEpocher;
	}
}
