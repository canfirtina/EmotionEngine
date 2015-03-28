package emotionlearner.eegFrame.features.nonlinear.entropy;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

/**
 * This class calculates the Renyi entropy of the EEG signal.
 * 
 * K. Waheed and F. M. Salam, “A Data-Derived Quadratic Independence Measure for Adaptive Blind Source Recovery in Practical Applications,” in Proc. 45th IEEE Int. Midwest Symposium on Circuits and Systems, pp. 473–476, 2002.
 * 
 * @author Alan Jovic
 *
 */

//malo èudni rezultati kod testiranja

public class RenyiEntropy {
	/**
	 * Public static method for calculating Renyi entropy of the specified order for a data series of EEG 
	 * @param segment segment of EEG series
	 * @param order Order of the Renyi entropy
	 * @return Renyi entropy
	 */
	public static double calculateRenyiEntropy(double [] segment, int order){
		double [] probs = new double[segment.length];
		double min, max, step;
		
		// calculate probability distribution (with a final bin size)
		min = Statistics.minimum(segment);
		max = Statistics.maximum(segment);
		
		step = (max - min)/noBins; //step je broj sampleova u jednom binu
		double [] limits = new double[noBins+1];
		double [] bin = new double[noBins];
		int [] binIndex = new int[segment.length]; 
		
		limits[0] = min;
		limits[noBins] = max;
			
		for (int i=1; i<noBins; i++){
			limits[i] = min + i*step;
		}
		for (int i=0; i<segment.length; i++){
			for (int k=0; k<noBins; k++){
				if (segment[i]>=limits[k] && segment[i]<=limits[k+1]){
					bin[k]++;
					binIndex[i] = k;
					break;
				}
			}
		}
		for (int i=0; i<noBins; i++){
			bin[i] /= segment.length;
		}
		for (int i=0; i<segment.length; i++){
			probs[i] = bin[binIndex[i]];
		}
		// call an internal method for calculating the entropy
		return determineRenyiEntropy(probs, order);
	}
	/**
	 * Internal, private method for determining the Renyi entropy
	 * @param probabilities
	 * @param order
	 * @return
	 */
	private static double determineRenyiEntropy(double [] probabilities, int order){
		if (order <= 1){
			return 0.0;
		}
		double sum = 0.0;
		for (int i=0; i<probabilities.length; i++){
			sum += Math.pow(probabilities[i], (double) order);
		}
		return Math.log10(sum)/((1-order)*Math.log10(2));
	}	
	private static final int noBins = 30;
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 100;
}
