package emotionlearner;

import shared.FeatureWindow;
import shared.Label;

/**
 * Trains windows of EEG features and classifies feature windows
 *
 */
public class FeatureWindowClassifierEEG extends FeatureWindowClassifier{

	/**
	 * Trains an EEG feature window
	 * @param window
	 */
	@Override
	public void trainWindow(FeatureWindow window) {

	}

	/**
	 * Classifies an EEG feature window
	 * @param window
	 * @return
	 */
	@Override
	public Label classifyWindow(FeatureWindow window) {
		
		return null;
	}

}
