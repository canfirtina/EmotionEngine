package EmotionLearner;

import shared.FeatureList;

public class FeatureExtractorEEG extends FeatureExtractor{
	/**
	 * Sets raw EEG data to be extracted feature from
	 * @param data
	 */
	@Override
	public void appendRawData(byte[] data) {
	}

	/**
	 * Returns Features of the raw EEG data 
	 * @return
	 */
	@Override
	public FeatureList getFeatures() {
		return null;
	}

}
