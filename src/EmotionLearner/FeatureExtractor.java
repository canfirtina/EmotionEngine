package EmotionLearner;

import SharedSensorData.FeatureList;

abstract class FeatureExtractor {
	public abstract FeatureExtractor instance();
	public abstract void setRawData(byte[] data); 
	public abstract FeatureList getFeatures();
}
