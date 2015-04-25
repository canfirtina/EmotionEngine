package shared;

/**
 * Data structure to keep predetermined emotional state labels.
 */
public enum Emotion {
    JOY(0), DISGUST(1), PEACEFUL(2), FRUSTRATED(3);
	private final int value;
	private Emotion(int val){
		value = val;
	}
	
	public int getValue(){
		return value;
	}
}
