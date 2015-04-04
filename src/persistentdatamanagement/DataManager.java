package persistentdatamanagement;

import shared.Sample;
import usermanager.User;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Responsible for providing other packages with file input output operations.
 */
public class DataManager {
    private static final String GAME_RECORDS_DIRECTORY = "User Data";
    private static final String USER_OBJECT_NAME = "user_object.obj";
    private static final String CURRENT_USER = "curr_user_object.obj";

    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    /**
     * Keeps the locations of directories of user-games records.
     */
    private String userFilesDirectory;
    private String currentUser;
    private String currentGame;

    private DataManager() {
        File dir = new File(GAME_RECORDS_DIRECTORY);
        // If data directory doesn't exist, create it
        if (!dir.exists()) {
            dir.mkdir();

            // if directory is newly created, create a default user
            saveUser(new User("default", "default"));
        }

        currentUser = getCurrentUser().getName();
    }

    /**
     * Gets the location of records for given user.
     *
     * @param username
     * @return
     */
    public String getUserDirectory(String username) {
        return GAME_RECORDS_DIRECTORY + "/" + username;
    }

    /**
     * Creates an directory for user records.
     *
     * @param username
     */
    public void addUserDirectory(String username) {
        File dir = new File(GAME_RECORDS_DIRECTORY + "/" + username);
        // If user directory doesn't exist, create it
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Saves given sample to current game.
     * @param sample
     */
    public void saveSample( Sample sample) {

    }

    /**
     * Gets all samples for current user and game.
     * @return Sample array
     */
    public Sample[] getGameData() {

        return new Sample[0];
    }

    /**
     * Updates user information.
     * @param userData serializable user object
     */
    public boolean saveUser(User userData) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(getUserDirectory(userData.getName()) + "/" + USER_OBJECT_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(userData);
            oos.flush();
            oos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets data of all users.
     * @return array of objects
     */
    public ArrayList<User> getAllUsers() {
        File file = new File(GAME_RECORDS_DIRECTORY);
        String[] names = file.list();
        ArrayList<User> users = new ArrayList<User>();

        for(String name : names)
        {
            if (new File(GAME_RECORDS_DIRECTORY + "/" + name).isDirectory())
            {
                users.add(getUser(name));
            }
        }

        return users;
    }

    /**
     * Checks if the user exists
     * @param userName
     * @return
     */
    public boolean checkUserExist(String userName){
        return !(getUser(userName) == null);
    }

    /**
     * Gets data of a user.
     * @return User object
     */
    public User getUser(String userName) {
        User currUser = null;

        // read current user
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(GAME_RECORDS_DIRECTORY + "/" + userName + "/" + USER_OBJECT_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start getting the objects out in the order in which they were written
        try {
            currUser = (User) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return currUser;
    }

    /**
     * Changes active user to userName.
     * @param userName
     */
    public boolean setCurrentUser(String userName){
        User user = getUser(userName);
        if (user == null)
            return false;

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(GAME_RECORDS_DIRECTORY + "/" + CURRENT_USER);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(user);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Gets active user.
     * @return user
     */
    public User getCurrentUser(){
        User currUser = null;

        // read current user
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(GAME_RECORDS_DIRECTORY + "/" + CURRENT_USER));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start getting the objects out in the order in which they were written
        try {
            currUser = (User) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return currUser;
    }

    /**
     * Changes active game.
     * @param gameId unique identifier of game
     */
    public void setCurrentGame(String gameId){

    }

    /**
     * Gets active game.
     * @return unique identifier of game
     */
    public String getCurrentGame(){
        throw new RuntimeException("get current game from file and return");
        //return "asd";
    }

    /**
     * Registers a new game to the system.
     * @param gameId unique identifier of game
     */
    public void addGame(String gameId) {

    }

}