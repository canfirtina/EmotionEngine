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

    public void saveSample(int gameId, Sample sample) {

    }

    public Sample[] getSamples(String username, int gameId) {

        return new Sample[0];
    }

    public void saveUser(String username, Object userData) {

    }

    public Object[] loadUsersData() {
        return null;
    }
    
    public void setCurrentUser(String userName){
    	
    }
    
    public String getCurrentUser(){
    	throw new RuntimeException("get current user from file and return");
    	//return "asd";
    }
    
}
