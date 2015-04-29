package emotionlearner.feature;

import java.util.ArrayList;
import java.util.List;

import shared.FeatureList;
import sensormanager.data.TimestampedRawData;
import weka.core.FastVector;

/**
 * Blueprint of feature extracting classes. Extracts features from rawData
 *
 */
public abstract class FeatureExtractor {
	/**
	 * Raw data window that keeps last N byte[] data
	 */
	protected List<TimestampedRawData> rawData;
	
	/**
	 * total feature count
	 */
	protected int totalFeatureCount;
	
	/**
	 * Fast vector of feature attributes informations
	 */
	protected FastVector featureAttributes;
	
	/**
	 * Sets raw data to be extracted feature from
	 * @param data
	 */
	public void appendRawData(TimestampedRawData data){
		rawData.add(data);
	}
	
	/**
	 * Appends raw data to be extracted feature from 
	 * @param data
	 */
	public void appendRawData(List<TimestampedRawData> data){
		for(int i=0;i<data.size();++i)
			appendRawData(data.get(i));
	}
	
	/**
	 * Returns Features of the raw data 
	 * @return
	 */
	public abstract FeatureList getFeatures();
	
	/**
	 * Selects the best features
	 * @param features
	 * @return
	 */
	protected abstract double[] selectFeatures(double[] features);
	
	/**
	 * Clears raw data for re-usage
	 */
	public void reset(){
		rawData = new ArrayList<TimestampedRawData>();
	}
	
	//public abstract ();
}
