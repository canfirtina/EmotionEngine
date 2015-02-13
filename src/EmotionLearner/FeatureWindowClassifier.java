package EmotionLearner;

import java.util.ArrayList;

import SharedSensorData.FeatureWindow;
import SharedSensorData.Label;

abstract class FeatureWindowClassifier {
	private ArrayList<FeatureWindow> windows;
	public abstract void trainWindow(FeatureWindow window);
	public abstract Label classifyWindow(FeatureWindow window);
}
