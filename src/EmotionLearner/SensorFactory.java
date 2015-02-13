package EmotionLearner;

import SensorManager.SensorListener;

public interface SensorFactory {
	public void createSensorListener(int portNumber, Class<SensorListener> sensorType);
}
