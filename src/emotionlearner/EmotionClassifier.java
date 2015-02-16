package emotionlearner;

import java.util.ArrayList;

import shared.Label;
import shared.Sample;

/**
 * Trains and classifies samples in order to predict emotional status of the user
 *
 */
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
