package usermanager;

import java.io.Serializable;
import java.util.ArrayList;

public class UserManager implements Serializable{
	private static UserManager instance = null;

	private ArrayList<User> users;
	private User currentUser;

	protected UserManager() {
		users = new ArrayList<User>();
	}

	public static UserManager getInstance() {
		if (instance == null) {
			throw new RuntimeException("Persistent data manager'dan al objeyi. eger ordan gelmiyorsa yeni yarat");
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
	
	public User getUser(String userName){
		for(User usr : users){
			if(usr.getName().equals(userName))
				return usr;
		}
		
		return null;
	}

	/**
	 * Called after a change is made to any user. Saves the 
	 */
	public void saveChanges() {
		throw new RuntimeException("Persistent data manager'a söyle kaydetsin");
	}
	
	/**
	 * Changes the current user
	 * @param userName
	 * @param password
	 */
	public void changeUser(String userName, String password) {
		if(getUser(userName).checkPassword(password))
			currentUser=getUser(userName);
	}
}
