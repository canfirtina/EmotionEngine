package usermanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class UserManager {
	private static UserManager instance = null;

	private ArrayList<User> users;
	private User currentUser;

	protected UserManager() {
		users = new ArrayList<User>();
	}

	public static UserManager getInstance() {
		if (instance == null) {
			throw new RuntimeException("Get byte [] of the userManager and put it in instance");
		}
		return instance;
	}

	/**
	 * Returns a list of users
	 * 
	 * @return
	 */
	public ArrayList<User> getUsers() {
		return users;
	}

	/**
	 * Called after a change is made to any user. Sends user
	 */
	public void saveChanges() {

	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	public static Object deserialize(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
