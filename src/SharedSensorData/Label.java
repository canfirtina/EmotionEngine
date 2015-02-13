package SharedSensorData;

/**
 * Created by Mustafa on 13.2.2015.
 */
public class Label {
    private int valence;
    private int arousal;
    private int dominance;
    private int liking;

    public Label(int liking, int valence, int arousal, int dominance) {
        this.liking = liking;
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
    }

    public int calculateEmotion() {
        return 0;
    }
}
