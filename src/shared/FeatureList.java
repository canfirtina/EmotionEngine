package shared;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import java.sql.Timestamp;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.SparseInstance;

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
	private long time;

	private boolean isSetTime;
    
	/**
	 * feature attributes information
	 */
	private FastVector featureAttributes;
	
    /**
     * Creates FeatureList object from double features array
     * @param featuresArray
	 * @param featureAttributes
     */
    public FeatureList(double[] featuresArray, FastVector featureAttributes){
    	features = new SparseInstance(featuresArray.length);
	
		
    	for(int i=0;i<featuresArray.length;++i)
			features.setValue(i, featuresArray[i]);
		this.featureAttributes = featureAttributes;


    }
	
	/**
	 * Create FeatureList object from double features array and a timestamp
	 * @param featuresArray
	 * @param featureAttributes
	 * @param time
	 */
	public FeatureList(double[] featuresArray, FastVector featureAttributes, long time){
		this(featuresArray, featureAttributes);
		this.time = time;
		this.isSetTime = true;
	}
    
	/**
	 * 
	 * @param featuresArray
	 * @param featureAttributes
	 * @param emotion 
	 */
    public FeatureList(double[] featuresArray, FastVector featureAttributes, Emotion emotion){
    	this(featuresArray, featureAttributes);
    	this.emotion = emotion;
    }
    
    public int size(){
    	return features.numValues();
    }
	
	/**
	 * returns weka instance
	 * @return 
	 */
	public Instance getInstance(){
		return features;
	}
    
    public double get(int index){
    	return features.value(index);
    }
    
    public void setEmotion(Emotion emotion){
    	this.emotion = emotion;
    }
    
    public Emotion getEmotion(){
    	return emotion;
    }
	
	public void setTime(long time){
		this.time = time;
		this.isSetTime = true;
	}
	
	public long getTime(){
		return time;
	}
	
	public FastVector getFeatureAttributes(){
		return featureAttributes;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		if(isSetTime){
			builder.append("Time:");
			builder.append(time);
		}
		if(emotion!=null){
			builder.append(" Emotion:");
			builder.append(emotion);
		}
		
		builder.append(" Features");
		builder.append(features);
		
		return builder.toString();
	} 
	

}
