package sensormanager.listener;

import sensormanager.data.DataEpocher;
import sensormanager.data.TimestampedRawData;

import java.util.ArrayList;
import java.util.List;

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
	protected int freq;
        
	protected String serialPortString;
        
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
	public abstract List<TimestampedRawData> getSensorData();

	
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
        
	public String getSerialPort(){
            
		return serialPortString;
	}

	public DataEpocher getDataEpocher() {
		return this.dataEpocher;
	}
	
	/**
	 * returns weight of a sensor
	 * @return 
	 */
	public abstract double weight();


	/**
	 * Frequnct of the sensor
	 * @return
	 */
	public abstract int getFrequency();

        
}
