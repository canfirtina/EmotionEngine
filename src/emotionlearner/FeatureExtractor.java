package emotionlearner;

import java.util.ArrayList;

import shared.FeatureList;

/**
 * Blueprint of feature extracting classes. Extracts features from rawData
 *
 */
abstract class FeatureExtractor {
	/**
	 * Raw data window that keeps last N byte[] data
	 */
	private ArrayList<double[]> rawData;
	/**
	 * Sets raw data to be extracted feature from
	 * @param data
	 */
	public abstract void appendRawData(double[] data); 
	
	/**
	 * Returns Features of the raw data 
	 * @return
	 */
	public abstract FeatureList getFeatures();
}
