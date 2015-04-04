package emotionlearner;


import shared.FeatureList;
import shared.TimestampedRawData;
/**
 * Extract features from GSR signals
 *
 */
public class FeatureExtractorGSR extends FeatureExtractor {

	/**
	 * Sets raw GSR data to be extracted feature from
	 * @param data
	 */
	@Override
	public void appendRawData(TimestampedRawData data) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns Features of the raw GSR data 
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
