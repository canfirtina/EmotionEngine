package shared;

/**
 * Keeps label of a sample to be provided to machin learning algorithm.
 */
public class Label {
    private double valence;
    private double arousal;
    private double dominance;
    private double liking;
    private Emotion emotion;

    public Label(double liking, double valence, double arousal, double dominance, Emotion emotion) {
        this.liking = liking;
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
        this.emotion = emotion;
    }

}
