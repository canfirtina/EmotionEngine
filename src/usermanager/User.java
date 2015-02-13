package usermanager;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
	private String userName;
	private String password;
	private HashMap<String, Boolean> enabledSensors;

	/**
	 * Constructor for User class that takes userName and password
	 * 
	 * @param userName 
	 * @param password
	 */
	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;
		enabledSensors = new HashMap<String, Boolean>();
	}

	/**
	 * Checks if the given sensor exists
	 * 
	 * @param sensorID
	 * @return
	 */
	public boolean checkSensor(String sensorID) {
		if (!enabledSensors.containsKey(sensorID))
			return false;
		return enabledSensors.get(sensorID);
	}
	
	public String getName() {
		return this.userName;
	}

	/**
	 * Enables the given sensor. In the case that sensor does not exist, adds it
	 * to enabled sensors
	 * 
	 * @param sensorID
	 */
	public void enableSensor(String sensorID) {
		enabledSensors.put(sensorID, true);
	}

	/**
	 * Disables the given sensor.
	 * 
	 * @param sensorID
	 */
	public void disableSensor(String sensorID) {
		if (!enabledSensors.containsKey(sensorID))
			return;
		enabledSensors.put(sensorID, false);
	}

	/**
	 * Checks if the given password is correct
	 * 
	 * @param password
	 */
	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}
	
	/**
	 * Changes the password
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(String oldPassword, String newPassword){
		if(checkPassword(oldPassword)){
			this.password = newPassword;
			return true;
		}
		
		return false;
	}
}
