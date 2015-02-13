package SensorManager;

public interface SensorObserver {
	
	public void dataArrived( SensorListener sensor);
	public void connectionError( SensorListener sensor);
	public void connectionEstablished( SensorListener sensor);
}
