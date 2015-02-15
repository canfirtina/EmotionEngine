package EmotionLearner;

import java.util.ArrayList;

import shared.FeatureWindow;
import shared.Label;

abstract class FeatureWindowClassifier {
	/**
	 * Feature windows that are used for training
	 */
	private ArrayList<FeatureWindow> windows;
	
	/**
	 * Trains a feature window
	 * @param window
	 */
	public abstract void trainWindow(FeatureWindow window);
	
	/**
	 * Classifies a feature window
	 * @param window
	 * @return
	 */
	public abstract Label classifyWindow(FeatureWindow window);
}
