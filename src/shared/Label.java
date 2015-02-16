package shared;

/**
 * Keeps label of a sample to be provided to machin learning algorithm.
 */
public class Label {
    private int valence;
    private int arousal;
    private int dominance;
    private int liking;
    private Emotion emotion;

    public Label(int liking, int valence, int arousal, int dominance, Emotion emotion) {
        this.liking = liking;
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
        this.emotion = emotion;
    }

}
