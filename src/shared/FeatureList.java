package shared;

import weka.core.Instance;

/**
 * Keeps features extracted from a single sensor for one sample.
 */
public class FeatureList {

    private Instance features;
    private int timestamp;

    public FeatureList(Instance features, int timestamp) {
    	this.features = features;
    	this.timestamp = timestamp;
    }

}
