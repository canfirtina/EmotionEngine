package usermanager;

import java.io.Serializable;
import java.util.HashMap;
import javafx.util.Pair;
import shared.Sensor;
import shared.Tutorial;

/**
 * Class that is used for storing User related data. Implements "Serializable"
 * in order to be able to save the object to a file.
 */
public class User implements Serializable {

    private String userName;
    private String password;
    private HashMap<String, Boolean> enabledSensors;
    private HashMap<Pair<Sensor, Tutorial>, Integer> playCount;
    private HashMap<String, Integer> gamesPlayed;

    /**
     * Constructor for User class that takes userName and password.
     *
     * @param userName
     * @param password
     */
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        enabledSensors = new HashMap<>();
        playCount = new HashMap<Pair<Sensor, Tutorial>, Integer>();
        gamesPlayed = new HashMap<>();
    }

    /**
     * Checks if the given sensor exists.
     *
     * @param sensorID
     * @return
     */
    public boolean checkSensor(String sensorID) {
        if (!enabledSensors.containsKey(sensorID)) {
            return false;
        }
        return enabledSensors.get(sensorID);
    }

    /**
     * Returns the name of the user.
     *
     * @return
     */
    public String getName() {
        return this.userName;
    }

    /**
     * Enables the given sensor. In the case that sensor does not exist, adds it
     * to enabled sensors.
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
        if (!enabledSensors.containsKey(sensorID)) {
            return;
        }
        enabledSensors.put(sensorID, false);
    }

    /**
     * Checks if the given password is correct.
     *
     * @param password
     * @return 
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Changes the password.
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (checkPassword(oldPassword)) {
            this.password = newPassword;
            return true;
        }

        return false;
    }

    public void playedTutorial(Pair<Sensor, Tutorial> tutPair) {
        if(playCount.containsKey(tutPair))
            playCount.put(tutPair, playCount.replace(tutPair, playCount.get(tutPair)+1));
        else
            playCount.put(tutPair, 1);
    }
    
    public int getTutorialPlayCount(Pair<Sensor, Tutorial> tutPair){
        return playCount.get(tutPair);
    }
    
    
}
