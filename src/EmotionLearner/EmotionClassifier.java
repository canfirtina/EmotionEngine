package EmotionLearner;

import java.util.ArrayList;

import SharedSensorData.Label;
import SharedSensorData.Sample;

public class EmotionClassifier {
	/**
	 * All window classifiers used for learning
	 */
	private ArrayList<FeatureWindowClassifier> windowClassifiers;
	
	/**
	 * Trains a sample 
	 * @param sample
	 */
	public void trainSample(Sample sample){
		
	}
	
	/**
	 * Classifies a sample
	 * @param sample
	 * @return
	 */
	public Label classifySample(Sample sample){
		return null;
	}
	
}
