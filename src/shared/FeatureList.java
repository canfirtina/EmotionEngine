package shared;

import java.sql.Timestamp;
import weka.core.Instance;

/**
 * Keeps features extracted from a single sensor for one sample.
 */
public class FeatureList {

	/**
	 * weka type for fast asynchronous vector implementation 
	 */
    private Instance features;
    //private int timestamp;
    
    /**
     * Emotion corresponding to features
     */
    private Emotion emotion;
	
	/**
	 * timestamp when feature list is extracted
	 */
	private Timestamp timestamp;
    
    /**
     * Creates FeatureList object from double features array
     * @param featuresArray
     */
    public FeatureList(double[] featuresArray){
    	features = new Instance(featuresArray.length);
    	for(int i=0;i<featuresArray.length;++i)
    		features.setValue(i, featuresArray[i]);
		this.timestamp = new Timestamp(0);
    }
	
	/**
	 * Create FeatureList object from double features array and a timestamp
	 * @param featuresArray
	 * @param timestamp 
	 */
	public FeatureList(double[] featuresArray, Timestamp timestamp){
		this(featuresArray);
		this.timestamp = timestamp;
	}
    
	/**
	 * 
	 * @param featuresArray
	 * @param emotion 
	 */
    public FeatureList(double[] featuresArray, Emotion emotion){
    	this(featuresArray);
    	this.emotion = emotion;
		this.features.setClassValue(emotion.getValue());
    }
    
    public int size(){
    	return features.numValues();
    }
    
    public double get(int index){
    	return features.value(index);
    }
    
    public void setEmotion(Emotion emotion){
    	this.emotion = emotion;
		this.features.setClassValue(emotion.getValue());
    }
    
    public Emotion getEmotion(){
    	return emotion;
    }
	
	public void setTimestamp(Timestamp timestamp){
		this.timestamp = timestamp;
	}
	
	public Timestamp getTimestamp(){
		return timestamp;
	}
	
	

}
