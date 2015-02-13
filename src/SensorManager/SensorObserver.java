package SensorManager;

public interface SensorObserver {
	
	public void dataArrived( Class<SensorListener> sensorType, byte[] data);
	public void connectionError( Class<SensorListener> sensorType);
	public void connectionEstablished( Class<SensorListener> sensorType);
}
