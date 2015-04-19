package sensormanager;

/**
 * Sensor Observer interface provides the communication between sensor listener and its observers 
 *
 */
public interface SensorObserver {
	
	/**
	 * Called when new data is ready to use of the sensor
	 * who calls notifies its observers by this function
	 * @param sensor is the sensor that has new data
	 */
	public void dataArrived( SensorListener sensor);
	
	/**
	 * Called when a connection error of a sensor occurs
	 * @param sensor is the sensor which has the connection error
	 */
	public void connectionError( SensorListener sensor);
	
	/**
	 * Called when the connection of a sensor is established
	 * @param sensor is the sensor whose connection is established
	 */
	public void connectionEstablished( SensorListener sensor);
	
	/**
	 * Called when the pending connection is failed
	 * @param sensor is the sensor whose connection is failed
	 */
	public void connectionFailed(SensorListener sensor);
}
