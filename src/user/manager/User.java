package user.manager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javafx.util.Pair;

/**
 * Class that is used for storing User related data. Implements "Serializable"
 * in order to be able to save the object to a file.
 */
public class User implements Serializable {

    private String userName;
    private String password;
    private HashMap<String, Boolean> enabledSensors;
    private HashMap<Pair<String, String>, Integer> playCount;
    private HashMap<String, Integer> softwaresUsed;

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
        playCount = new HashMap<Pair<String, String>, Integer>();
        softwaresUsed = new HashMap<>();
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
    
    public String getPass(){
        return password;
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

    public void playedTutorial(Pair<String, String> sen_tut) {
        if (playCount.containsKey(sen_tut)) {
            playCount.replace(sen_tut, 1 + playCount.get(sen_tut));
        } else {
            playCount.put(sen_tut, 1);
        }
    }

    public int getTutorialPlayCount(Pair<String, String> sen_tut) {
        return playCount.get(sen_tut);
    }

    public void usedSoftware(String software, int minutes) {

        softwaresUsed.put(software, minutes);
    }

    public String getSoftwareUsageTime(String software) {
        int hours, minutes;

        if (softwaresUsed.containsKey(software)) {
            hours = (int) TimeUnit.HOURS.convert(softwaresUsed.get(software), TimeUnit.MINUTES);
            minutes = softwaresUsed.get(software) - hours * 60;
        } else {
            return "N/A";
        }

        return hours + "h " + minutes + "m";
    }
    
    public HashMap<String, Boolean> getEnabledSensors() {
        return enabledSensors;
    }

    public HashMap<Pair<String, String>, Integer> getPlayCount() {
        return playCount;
    }

    public HashMap<String, Integer> getSoftwareUsed() {
        return softwaresUsed;
    }
}
