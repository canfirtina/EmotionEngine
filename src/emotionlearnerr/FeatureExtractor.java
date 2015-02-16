package emotionlearner;

import java.util.ArrayList;

import shared.FeatureList;

abstract class FeatureExtractor {
	/**
	 * Raw data window that keeps last N byte[] data
	 */
	private ArrayList<byte[]> rawData;
	
	/**
	 * Sets raw data to be extracted feature from
	 * @param data
	 */
	public abstract void appendRawData(byte[] data); 
	
	/**
	 * Returns Features of the raw data 
	 * @return
	 */
	public abstract FeatureList getFeatures();
}
