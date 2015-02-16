package shared;

/**
 * Keeps label of a sample to be provided to machin learning algorithm.
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

    /**
     * Calculates emotion label from valence, arousal, dominance and liking value.
     * @return Emotion
     */
    public Emotion calculateEmotion() {
        return null;
    }
}
