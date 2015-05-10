package shared;

import weka.core.Attribute;
import weka.core.FastVector;


/**
 * Data structure to keep predetermined emotional state labels.
 */
public enum Emotion {
    JOY(0), DISGUST(1), PEACEFUL(2), BORED(3), NEUTRAL(4);
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
			vector.addElement(Emotion.NEUTRAL.name());
			
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

        public static Emotion emotionForValue( int value){

            switch( value){

                case 0:
                    return Emotion.JOY;
				case 1:
					return Emotion.DISGUST;
				case 2:
					return Emotion.PEACEFUL;
                case 3:
                    return Emotion.BORED;
				case 4:
					return Emotion.NEUTRAL;
            }

            return null;
        }
}
