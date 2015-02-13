package PersistentDataManagement;

import SharedSensorData.Sample;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void saveSample(String username, int gameId, Sample sample) {

    }

    public Sample[] getSamples(String username, int gameId) {

        return new Sample[0];
    }

    public void saveUser(String username, Object userData) {

    }

    public Object[] loadUsersData() {
        return null;
    }
}
