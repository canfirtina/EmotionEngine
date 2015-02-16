package sensormanager;

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
	public byte[] getSensorData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPortNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
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
