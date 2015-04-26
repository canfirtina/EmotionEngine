package gameadapter;

/**
 * Part of EmotionEngine that is implemented in client game Tetris. It provides in game decisions such as speed change, background music.
 */
public class GameAdapterTetris extends GameAdapterGeneric {

    public GameAdapterTetris(String host, int port) {
        super(host,port);
    }

    /**
     * Calculates appropriate game speed.
     * @return game speed
     */
    public int getSpeed() {

        return 0;
    }

    /**
     * Determines type of background music.
     * @return id of background music to be played
     */
    public int getMusicType() {

        return 0;
    }

    /**
     * Calculates how hard it should be to move blocks.
     * @return responsiveness of blocks to keyboard commands
     */
    public int getResponsiveness() {

        return 0;
    }

    /**
     * Determines if reverse move mode should be activated.
     * @return true if reverse mode should be activated, false if not
     */
    public boolean isReverse() {

        return false;
    }
}
