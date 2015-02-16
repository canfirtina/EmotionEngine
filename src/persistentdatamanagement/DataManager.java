package persistentdatamanagement;

import shared.Sample;

/**
 * Responsible for providing other packages with file input output operations.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    /**
     * Keeps the locations of directories of user-games records.
     */
    private UsersGamesDirectory usersGamesDirectory;
    private String currentUser;
    private String currentGame;

    private DataManager() {
        loadUsersDirectory();
    }

    /**
     * Reads the diary of locations of records from a hardcoded location.
     */
    private void loadUsersDirectory() {
        usersGamesDirectory = new UsersGamesDirectory("dir");
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
     * @param username name of the user
     * @param userData serializable user data
     */
    public void saveUser(String username, Object userData) {

    }

    /**
     * Gets data of all users.
     * @return array of objects
     */
    public Object[] loadUsersData() {
        return null;
    }

    /**
     * Changes active user to userName.
     * @param userName
     */
    public void setCurrentUser(String userName){
    	
    }

    /**
     * Gets active user.
     * @return usrname
     */
    public String getCurrentUser(){
    	throw new RuntimeException("get current user from file and return");
    	//return "asd";
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
