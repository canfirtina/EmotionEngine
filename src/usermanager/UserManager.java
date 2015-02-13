package userManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import PersistentDataManagement.DataManager;

public class UserManager implements Serializable {
	private static UserManager instance = null;

	private ArrayList<User> users;
	private String currentUser;

	protected UserManager() {
		users = new ArrayList<User>(Arrays.asList((User[]) DataManager.getInstance().loadUsersData()));
	}

	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		
		return instance;
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
}
