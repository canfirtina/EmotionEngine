package emotionlearner;

import sensormanager.SensorListener;

/**
 * Factory class for creating sensor listeners
 *
 */
public interface SensorFactory {
	/**
	 * Creates a sensor listener of a type which uses a certain port number
	 * @param portNumber
	 * @param sensorType
	 */
	public void createSensorListener(int portNumber, Class<SensorListener> sensorType);
}
