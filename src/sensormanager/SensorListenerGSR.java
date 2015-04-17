package sensormanager;

import shared.TimestampedRawData;

import java.util.ArrayList;

/**
 * Connects to a GSR sensor on a specific port, 
 * gets raw GSR data from the sensor and notifies its observers periodically
 *
 */
public class SensorListenerGSR extends SensorListener{

	public SensorListenerGSR(int portNumber) {
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<TimestampedRawData> getSensorData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startStreaming() {

	}

	@Override
	public void stopStreaming() {

	}

	@Override
	protected void notifyObservers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean registerObserver(SensorObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeObserver(SensorObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

}
