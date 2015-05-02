package shared;

import weka.core.Attribute;
import weka.core.FastVector;


/**
 * Data structure to keep predetermined emotional state labels.
 */
public enum Emotion {
    JOY(0), BORED(1), PEACEFUL(2), FRUSTRATED(3), DISGUST(4);
	private final int value;
	
	
	private static FastVector labels = null;
	
	
	public static FastVector classAttributes(){
		if(labels == null){
			FastVector vector = new FastVector(4);
			vector.addElement(Emotion.JOY.name());
			vector.addElement(Emotion.DISGUST.name());
			//
			vector.addElement(Emotion.PEACEFUL.name());
			vector.addElement(Emotion.BORED.name());
			
			labels = vector;
		}
		return labels;
	}
	
	
	
	private Emotion(int val){
		value = val;
	}
	
	public int getValue(){
		return value;
	}
	
	public static Emotion emotionWithValue(int value){
		return Emotion.valueOf((String)labels.elementAt(value));
	}
}
