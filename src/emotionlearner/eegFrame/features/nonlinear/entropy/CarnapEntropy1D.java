package emotionlearner.eegFrame.features.nonlinear.entropy;

import java.util.Arrays;

/**
 * Class for the calculation of the 1D Carnap entropy of the time series
 * 1D Carnap entropy is the topological Kolmogorov entropy of the first order
 * Its advantage is the possibility to be calculated for any series of data points, regardless of the values distribution.
 * The series does not necessarily has to be a time series.
 * 
 * @author Alan Jovic
 *
 */
public class CarnapEntropy1D {
	/**
	 * Static method for the calculation of the 1D Carnap entropy
	 * Details can be found in:
	 * A. Pudmetzky, Teleonomic Entropy Measuring the Phase-Space of End-Directed System. Applied Mathematics and Computation 162 (2) (2005), 695-705.
	 * F. Jovic, D. Krmpotic, A. Jovic, et al. Information Content of Process Signals in Quality Control, IPSI Transactions on Internet Research, 4(2), 2008. 
	 * @param segment EEG interval series
	 * @return 1D Carnap entropy
	 */
	
	//PROVJERITI EXCEPTION
	public static final double calculateCarnapEntropy1D(double [] segment){
		Arrays.sort(segment);
		
		double min = segment[0];
		double dif = 0;
		int length = segment.length;
		for (int i=0; i<length; i++){
			if (segment[i]>min+10e-8){
				dif = segment[i] - min;
				break;
			}
		}
		double lowerBound, upperBound;
		lowerBound = segment[0]-dif/2;
		upperBound = segment[length-1]+dif/2;
		
		double lineLength = upperBound - lowerBound;
		int last = length-1;
		double [] area = new double[length];
		int i,j;
		boolean isLast = false;
		
		//area[0] = (dataVector[1]-dataVector[0])/2 + (dataVector[0]-lowerBound);
		area[last] = (segment[last]-segment[last-1])/2 + (upperBound-segment[last]);
		
		int count = 1;	// number of repeating numbers in a part of sorted sequence
		boolean flag = false;
		for (i=0; i<last; i++){
			if (Math.abs(segment[i+1]-segment[i])<=10e-8){
				count++;
				if (i+1==last){
					isLast = true;
					flag = true;
				}
				else {
					for (j=i+1; j<last; j++){
						if (Math.abs(segment[j+1]-segment[j])<=10e-8){
							count++;
							if (j+1 == last){
								isLast = true;
								break;
							}
						}
						else {
							break;
						}
					}
				}
				flag = true;
			}
			if (!flag){
				if (i==0){
					area[i] = (segment[0]-lowerBound) + (segment[i+1]-segment[i])/2;
				}
				else {
					area[i] = (segment[i]-segment[i-1])/2 + (segment[i+1]-segment[i])/2;
				}
			}
			else {
				if (i==0){
					for (j=0; j<count; j++){
						area[0+j] = (segment[0]-lowerBound) + (segment[i+count]-segment[i])/2;
					}
				}
				else if (isLast){
					for (j=i;j<=last; j++){
						area [j] = (segment[i]-segment[i-1])/2 + (upperBound-segment[last]);
					}
				}
				else {
					for (j=0; j<count; j++){
						area[i+j] = (segment[i]-segment[i-1])/2 + (segment[i+count]-segment[i])/2;
					}
				}
				i += count-1;
			}
			flag = false;
			count = 1;
		}
		double carnap = 0;
		for (i=0; i<last+1; i++){
			carnap += area[i]/lineLength*Math.log10(area[i]/lineLength)/Math.log10(2);
		}
		return -carnap;
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 10;
}
