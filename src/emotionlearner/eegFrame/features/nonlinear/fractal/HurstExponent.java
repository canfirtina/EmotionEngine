package emotionlearner.eegFrame.features.nonlinear.fractal;
import emotionlearner.eegFrame.statisticMeasure.Statistics;

public class HurstExponent {
	//M. C. Teich, S. B. Lowen, B. M. Jost, K. Vibe-Rheymer, and C. Heneghan, “Heart-Rate Variability: Measures and Models,” in Dynamic Analysis and Modeling, ser. Nonlinear Biomedical Signal Processing, M. Akay, Ed. New York: IEEE Press, 2001, vol. II, ch. 6, pp. 159–213.
	
	public static final double calculateHurstExponent(double [] segment){
		return calculateHurstExponent(segment, segment.length);
	}
	
	public static final double calculateHurstExponent(double [] segment, int segmentLength){
		if (segmentLength > segment.length){
			segmentLength = segment.length;
		}
		double [] sums = new double[segmentLength];
		double [] C = new double[segmentLength];
		
		double sum = 0.0;
		double standardDeviation = 0.0;
		double min, max;
		
		// calculate sums and R/S ratio
		for (int i=0; i<segmentLength; i++){
			sum += segment[i]-Statistics.mean(segment, 0, i+1);
			sums[i] = sum;
			standardDeviation = Statistics.standardDeviation(segment, 0, i+1);
			min = Statistics.minimum(sums, 0, i+1, false);
			max = Statistics.maximum(sums, 0, i+1, false);
			if (standardDeviation<=1e-08){
				C[i] = 0.0;
			}
			else {
				C[i] = (max - min)/standardDeviation;
			}
		}
		double [] ks = new double[segmentLength];
		for (int i=1; i<=segmentLength; i++){
			ks[i-1] = (double) i;
		}
		for (int k=0; k<segmentLength; k++){
			ks[k] = Math.log10(ks[k]);
			C[k] = Math.log10(C[k]);
		}
		double [] alfaBeta =  Statistics.fitLineThroughXYPointsCoefficients(ks,C,20);
		
		// the slope is the Hurst dimension
		return alfaBeta[0];
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 100;
}
