package persistentdatamanagement;

import java.util.Map;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class UsersGamesDirectory {
    private Map<String, String> directory;

    /**
     * Reads location of records for each user-game group
     * @param dbFile the loacation of database file
     */
    public UsersGamesDirectory(String dbFile) {

    }

    /**
     * gets the location of records for given user and game
     * @param username
     * @param gameid
     * @return
     */
    public String getGameRecordsDirectory(String username, String gameid) {
        throw new RuntimeException("return gamerecordsdirectory");
    }

    /**
     * creates an entry for location of username-game records
     * @param username
     * @param gameid
     * @param directory
     */
    public void addGameRecordsDirectory(String username, String gameid, String directory) {

    }
}
