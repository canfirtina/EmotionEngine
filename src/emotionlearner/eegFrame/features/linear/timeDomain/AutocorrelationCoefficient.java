package emotionlearner.eegFrame.features.linear.timeDomain;

import emotionlearner.eegFrame.statisticMeasure.Statistics;
//M. C. Teich, S. B. Lowen, B. M. Jost, K. Vibe-Rheymer, and C. Heneghan, “Heart-Rate Variability: Measures and Models,” in Dynamic Analysis and Modeling, ser. Nonlinear Biomedical Signal Processing, M. Akay, Ed. New York: IEEE Press, 2001, vol. II, ch. 6, pp. 159–213.
public class AutocorrelationCoefficient {
	public static double calculateAutocorrelationCoefficient(double [] segment){
		double mean = Statistics.mean(segment);
		double variance = Statistics.variance(segment, mean);
		
		double sum = 0.0;
		for (int i=0; i<segment.length-1; i++){
			sum += (segment[i+1]-mean)*(segment[i]-mean);
		}
		
		return sum/(variance*segment.length-1);
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 30;
}
