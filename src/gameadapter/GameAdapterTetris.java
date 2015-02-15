package gameadapter;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Mustafa on 15.2.2015.
 */
public class GameAdapterTetris extends GameAdapterGeneric {

    public GameAdapterTetris() {
        super();
    }

    /**
     * calculates appropriate game speed
     * @return game speed
     */
    public int getSpeed() {

        return 0;
    }

    /**
     * determines type of background music
     * @return id of background music to be played
     */
    public int getMusicType() {

        return 0;
    }

    /**
     * calculates how hard it should be to move blocks
     * @return responsiveness of blocks to keyboard commands
     */
    public int getResponsiveness() {

        return 0;
    }

    /**
     * determines if reverse move mode should be activated
     * @return true if reverse mode should be activated, false if not
     */
    public boolean isReverse() {

        return false;
    }
}
