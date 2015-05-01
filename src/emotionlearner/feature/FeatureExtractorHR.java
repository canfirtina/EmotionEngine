package emotionlearner.feature;

import shared.FeatureList;
import sensormanager.data.TimestampedRawData;
/**
 * Extracts features from Heart Rate data
 *
 */
public class FeatureExtractorHR extends FeatureExtractor {

	/**
	 * Sets raw HR data to be extracted feature from
	 * @param data
	 */
	@Override
	public void appendRawData(TimestampedRawData data) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns Features of the raw HR data 
	 * @return
	 */
	@Override
	public FeatureList getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double[] selectFeatures(double[] features) {
		// TODO Auto-generated method stub
		return null;
	}

}
