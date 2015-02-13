package SensorManager;

import java.util.ArrayList;

public abstract class SensorListener {
	
	private int portNumber;
	private boolean connectionStatus;
	private ArrayList<SensorObserver> observerCollection;
	private byte[] sensorData;
	private int freq;
	
	public abstract boolean connect();
	public abstract boolean disconnect();
	public abstract byte[] getSensorData();
	public abstract int getFrequency();
	public abstract int getPortNumber();
	public abstract boolean isConnected();
	protected abstract void notifyObservers();
	public abstract boolean registerObserver( SensorObserver observer);
	public abstract boolean removeObserver( SensorObserver observer);
}
