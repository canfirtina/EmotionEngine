package emotionlearner.feature;
import emotionlearner.feature.FeatureExtractor;
import features.linear.timeDomain.*;
import shared.Emotion;

import shared.FeatureList;
import weka.core.Attribute;
import weka.core.FastVector;
/**
 * Extracts features from EEG signals
 *
 */
public class FeatureExtractorGSR extends FeatureExtractor{

	/**
	 * specific properties for gsr feature extractor
	 */
	private static FeatureExtractorProperties properties = null;
	
	public FeatureExtractorGSR() {
        int totalFeatureCount = getProperties().getNumChannels() * getProperties().getNumFeaturesEachChannel();
		reset();
    }
	
	/**
	 * gets properties of gsr feature extractor
	 * @return 
	 */
	public static FeatureExtractorProperties getProperties(){
		
		if(properties == null){
			properties = new FeatureExtractorProperties(1, 4, null);
			int totalFeatureCount = properties.getNumChannels() * properties.getNumFeaturesEachChannel();
			FastVector featureAttributes = new FastVector(totalFeatureCount+1);
			for(int i=0;i<totalFeatureCount;++i)
				featureAttributes.addElement(new Attribute("att_gsr_" + i));
			featureAttributes.addElement(new Attribute("gsr_class", Emotion.classAttributes()));
			properties.setFeatureAttributes(featureAttributes);
		}
		return properties;
	}
	
	@Override
	public FeatureList getFeatures() {
		final int numFeatures = getProperties().getNumFeaturesEachChannel();

        double[][] rawDataTransposed = new double[rawData.get(0).getData().length][rawData.size()];
        for (int i = 0; i < rawDataTransposed.length; ++i) {
            for (int j = 0; j < rawDataTransposed[i].length; ++j) {
                rawDataTransposed[i][j] = rawData.get(j).getData()[i];
            }
        }

        double[] res = new double[rawDataTransposed.length * numFeatures];
        double[] currentFeatures ;
        for (int i = 0; i < rawDataTransposed.length; ++i) {
            currentFeatures = new double[numFeatures];
            
			currentFeatures[0] = features.linear.timeDomain.AutocorrelationCoefficient.calculateAutocorrelationCoefficient(rawDataTransposed[i]);
			currentFeatures[1] = features.linear.timeDomain.Mean.calculateMean(rawDataTransposed[i]);
			currentFeatures[2] = features.linear.timeDomain.MeanOfAbsoluteValuesOfFirstDiffNormalized.calculateMeanOfFirstDiffNormalized(rawDataTransposed[i]);
			currentFeatures[3] = features.linear.timeDomain.StandardDeviation.calculateStandardDeviation(rawDataTransposed[i]);
			
			for (int j = 0; j < numFeatures; j++) {
				res[i * numFeatures + j] = currentFeatures[j];
			}
		}

        return new FeatureList(selectFeatures(res), getProperties().getFeatureAttributes());
	}

	@Override
	protected double[] selectFeatures(double[] features) {
		return features;
	}
	
	
}
