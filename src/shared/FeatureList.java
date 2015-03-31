package shared;

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
     * Creates FeatureList object from double features array
     * @param featuresArray
     */
    public FeatureList(double[] featuresArray){
    	features = new Instance(featuresArray.length);
    	for(int i=0;i<featuresArray.length;++i)
    		features.setValue(i, featuresArray[i]);
    }
    
    public int size(){
    	return features.numValues();
    }
    
    public double get(int index){
    	return features.value(index);
    }
    

}
