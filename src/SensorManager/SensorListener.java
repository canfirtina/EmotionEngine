package SensorManager;

import java.util.ArrayList;

public abstract class SensorListener {
	
	private int portNumber;
	private Class<SensorListener> sensorType;
	private boolean connectionStatus;
	private ArrayList<SensorObserver> observerCollection;
	
	public SensorListener( int portNumber, Class<SensorListener> sensorType){
		
		this.portNumber = portNumber;
		this.sensorType = sensorType;
	}
}
