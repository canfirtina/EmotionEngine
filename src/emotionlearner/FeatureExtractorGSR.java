package emotionlearner;
import features.linear.timeDomain.*;

import shared.FeatureList;
/**
 * Extracts features from EEG signals
 *
 */
public class FeatureExtractorGSR extends FeatureExtractor{

	public  FeatureExtractorGSR() {
		reset();	
	}
	

	/**
	 * Returns Features of the raw EEG data 
	 * @return
	 * @throws Exception 
	 */
	@Override
	public FeatureList getFeatures() {
		final int numFeatures = 20;
		
		double[][] rawDataTransposed = new double[rawData.get(0).getData().length][rawData.size()];
		for(int i=0;i<rawDataTransposed.length;++i)
			for(int j=0;j<rawDataTransposed[i].length;++j)
				rawDataTransposed[i][j] = rawData.get(j).getData()[i];
		
		double[] res = new double[rawDataTransposed.length * numFeatures];
		double[] currentFeatures = null;
		for(int i=0;i<rawDataTransposed.length;++i){
			currentFeatures = new double[numFeatures];
			currentFeatures[0] = AutocorrelationCoefficient.calculateAutocorrelationCoefficient(currentFeatures);
			currentFeatures[1] = MeanOfAbsoluteValuesOfFirstDiffNormalized.calculateMeanOfFirstDiffNormalized(currentFeatures);
			currentFeatures[2] = MeanOfAbsoluteValuesOfSecondDiffNormalized.calculateMeanOfSecondDiffNormalized(currentFeatures);
			currentFeatures[3] = StandardDeviation.calculateStandardDeviation(currentFeatures);
			
			
			for (int j = 0; j < numFeatures; j++) 
				res[i * numFeatures + j] = currentFeatures[j];
		}
		
		return new FeatureList(selectFeatures(res));
	}


	/**
	 * To be continued
	 */
	@Override
	protected double[] selectFeatures(double[] features) {
		return features;
	}
	
	
	
}
