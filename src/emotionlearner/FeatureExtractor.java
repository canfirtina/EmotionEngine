package emotionlearner;

import java.util.ArrayList;

import shared.FeatureList;

/**
 * Blueprint of feature extracting classes. Extracts features from rawData
 *
 */
public abstract class FeatureExtractor {
	/**
	 * Raw data window that keeps last N byte[] data
	 */
	protected ArrayList<double[]> rawData;
	/**
	 * Sets raw data to be extracted feature from
	 * @param data
	 */
	public void appendRawData(double[] data){
		rawData.add(data);
	}
	
	/**
	 * Appends raw data to be extracted feature from 
	 * @param data
	 */
	public void appendRawData(ArrayList<double[]> data){
		for(int i=0;i<data.size();++i)
			rawData.add(data.get(i));
	}
	
	/**
	 * Returns Features of the raw data 
	 * @return
	 */
	public abstract FeatureList getFeatures();
	
	/**
	 * Clears raw data for reusage
	 */
	public void reset(){
		rawData = new ArrayList<double[]>();
	}
	
	//public abstract ();
}
