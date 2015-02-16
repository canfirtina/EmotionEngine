package persistentdatamanagement;

import shared.Sample;

/**
 * Data structure to keep information about all the sessions of one player in one game.
 */
public class GameData {
    private Sample[] samples;

    public GameData(Sample[] samples) {
        this.samples = samples;
    }

    /**
     * Adds a training sample to active game under active user.
     * @param sample
     */
    public void addSample(Sample sample) {

    }
}
