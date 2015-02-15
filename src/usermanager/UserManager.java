package usermanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import PersistentDataManagement.DataManager;

public class UserManager {
	private static UserManager instance = null;

	/**
	 * Public constructor method.
	 */
	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		
		return instance;
	}
	
	private ArrayList<User> users;
	private String currentUser;

	/**
	 * Private constructor
	 */
	protected UserManager() {
		users = new ArrayList<User>(Arrays.asList((User[]) DataManager.getInstance().loadUsersData()));
		currentUser = DataManager.getInstance().getCurrentUser();
	}
	
	/**
	 * Checks the validity of the given credentials
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean login(String userName, String password){
		return true;
	}
	
	/**
	 * Logs the user out
	 */
	public void logout(){
		currentUser = null;
	}

	/**
	 * Returns a list of users
	 * 
	 * @return
	 */
	public ArrayList<User> getAllUsers() {
		return users;
	}
	
	/**
	 * Returns the user object with the given name
	 * @param userName
	 * @return
	 */
	public User getUser(String userName) {
		for (User usr : users)
			if (usr.getName().equals(userName))
				return usr;

		return null;
	}
	
	/**
	 * Returns the current user
	 * @return
	 */
	public User getCurrentUser(){
		return getUser(currentUser);
	}
	
	/**
	 * Creates a new user
	 * @param userName
	 * @param password
	 */
	public void newUser(String userName, String password){
		DataManager.getInstance().saveUser(userName, new User(userName,password));
	}

	/**
	 * Changes the current user
	 * 
	 * @param userName
	 * @param password
	 */
	public void changeUser(String userName, String password) {
		if (getUser(userName).checkPassword(password))
			currentUser = userName;
		
		DataManager.getInstance().setCurrentUser(userName);
	}
	
	/**
	 * Checks if the given sensor exists in the current user
	 * 
	 * @param sensorID
	 * @return if sensor is in use
	 */
	public boolean checkSensor(String sensorID) {
		return getCurrentUser().checkSensor(sensorID);
	}
	
	/**
	 * Enables the given sensor for the current user. In the case that sensor does not exist, adds it
	 * to enabled sensors
	 * 
	 * @param sensorID
	 */
	public void enableSensor(String sensorID) {
		getCurrentUser().enableSensor(sensorID);
	}
	
	/**
	 * Disables the given sensor for the current user.
	 * @param sensorID
	 */
	public void disableSensor(String sensorID) {
		getCurrentUser().disableSensor(sensorID);
	}
}
