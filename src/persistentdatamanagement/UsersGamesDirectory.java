package persistentdatamanagement;

import java.util.Map;

/**
 * Keeps where sensor and learning recordings of each game for each user is located.
 */
public class UsersGamesDirectory {
    private Map<String, String> directory;

    /**
     * Reads location of records for each user-game group.
     * @param dbFile the loacation of database file
     */
    public UsersGamesDirectory(String dbFile) {

    }

    /**
     * Gets the location of records for given user and game.
     * @param username
     * @param gameid
     * @return
     */
    public String getGameRecordsDirectory(String username, String gameid) {
        throw new RuntimeException("return gamerecordsdirectory");
    }

    /**
     * Creates an entry for location of username-game records.
     * @param username
     * @param gameid
     * @param directory
     */
    public void addGameRecordsDirectory(String username, String gameid, String directory) {

    }
}
