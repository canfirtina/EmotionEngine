package user.manager;

import java.util.ArrayList;
import javafx.util.Pair;

import persistentdatamanagement.DataManager;
import emotionlearner.engine.Tutorial;

/**
 * Manages users and user related like logging in, creating or changing users,
 * keeping track of which sensors are used by which users.
 */
public class UserManager {

    private static UserManager instance = null;
    private ArrayList<User> users;
    private String currentUser;

    /**
     * Public constructor method.
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }

        return instance;
    }

    /**
     * Private constructor.
     */
    protected UserManager() {
        users = DataManager.getInstance().getAllUsers();
        currentUser = DataManager.getInstance().getCurrentUser().getName();
    }
    
    /**
     * Allows user to rate a tutorial
     * @param tutorial
     */
    public boolean rate(String tutorial, int rating){        
        return DataManager.getInstance().rateTutorial(currentUser, tutorial, rating);
    }
    
    /**
     * Allows user to update his/her tutorial rating
     * @param tutorial
     */
    public boolean updateRating(String tutorial, int rating){        
        return DataManager.getInstance().updateRating(currentUser, tutorial, rating);
    }
    
    public double getAverageRating(String tutorial){
        return DataManager.getInstance().getAverageRating(tutorial);
    }
    
    public double getUserRating(String user, String tutorial){
        return DataManager.getInstance().getUserRating(user, tutorial);
    }
    
    

    /**
     * Checks the validity of the given credentials and logs the user in
     * (changes the current user).
     *
     * @param userName
     * @param password
     * @return
     */
    public boolean login(String userName, String password) {
        User user = DataManager.getInstance().getUser(userName);

        boolean res = (user != null) && user.checkPassword(password) && DataManager.getInstance().setCurrentUser(user.getName());

        if (res) {
            currentUser = user.getName();
        }

        return res;
    }

    /**
     * Logs the user out.
     */
    public boolean logout() {
        if (DataManager.getInstance().setCurrentUser("default")) {
            currentUser = "default";
            return true;
        }
        return false;
    }

    /**
     * Returns a list of users.
     *
     * @return
     */
    public ArrayList<User> getAllUsers() {
        return users;
    }

    /**
     * Returns the user object with the given name.
     *
     * @param userName
     * @return
     */
    public User getUser(String userName) {
        for (User usr : users) {
            if (usr.getName().equals(userName)) {
                return usr;
            }
        }

        return null;
    }

    /**
     * Returns the current user.
     *
     * @return
     */
    public User getCurrentUser() {
        return getUser(currentUser);
    }

    /**
     * Creates a new user
     *
     * @param userName
     * @param password
     * @return
     */
    public boolean newUser(String userName, String password) {
        // if user already exists
        if (getUser(userName) != null) {
            return false;
        }

        User user = new User(userName, password);
        if (DataManager.getInstance().saveUser(user)) {
            users.add(user);
            return true;
        }

        return false;
    }

    /**
     * Checks if the given sensor exists in the current user.
     *
     * @param sensorID
     * @return if sensor is in use
     */
    public boolean checkSensor(String sensorID) {
        return getCurrentUser().checkSensor(sensorID);
    }

    /**
     * Enables the given sensor for the current user. In the case that sensor
     * does not exist, adds it to enabled sensors.
     *
     * @param sensorID
     */
    /*public void enableSensor(String sensorID) {
        getCurrentUser().enableSensor(sensorID);
        DataManager.getInstance().saveUser(getCurrentUser());
    }*/

    /**
     * Disables the given sensor for the current user.
     *
     * @param sensorID
     */
    /*public void disableSensor(String sensorID) {
        getCurrentUser().disableSensor(sensorID);
        DataManager.getInstance().saveUser(getCurrentUser());
    }*/

    /**
     * Updates which tutorials the user has played
     *
     * @param tutPair Sensor-Tutorial pair
     */
    public void playedTutorial(Pair<String, String> tutPair) {
        getCurrentUser().playedTutorial(tutPair);
        DataManager.getInstance().saveUser(getCurrentUser());
    }
    
    public boolean saveUser( User user){
        
        return DataManager.getInstance().saveUser(user);
    }

    /**
     * Gets how many times the user played the given tutorial for the given
     * sensor
     *
     * @param tutPair
     * @return
     */
    public int getTutorialPlayCount(Pair<String, String> tutPair) {
        return getCurrentUser().getTutorialPlayCount(tutPair);
    }
}
