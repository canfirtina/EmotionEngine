package PersistentDataManagement;

import SharedSensorData.Sample;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private String currentUser;
    private int gameId;

    private DataManager() {
    }

    public void saveSample( Sample sample) {

    }

    public Sample[] getGameData( int gameId) {

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

    public void setCurrentGame(int gameId){

    }

    public String getCurrentGame(){
        throw new RuntimeException("get current game from file and return");
        //return "asd";
    }
}
