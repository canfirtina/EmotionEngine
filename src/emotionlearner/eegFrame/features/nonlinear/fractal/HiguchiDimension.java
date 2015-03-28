package emotionlearner.eegFrame.features.nonlinear.fractal;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

//Higuchi, T., Approach to an irregular time series on the basis of the fractal theory, Physica D, 31, 277--283, 1988.
public class HiguchiDimension extends FractalDimension{
	private double higuchiDimension;
	
	public HiguchiDimension(double [] segment, int kmax){
		super(segment);
		
		int N = segment.length;
		
		double [][] lengths = new double[kmax][kmax];
		
		for (int k = 1; k <= kmax; k++) {
			for (int m = 1; m <= k; m++) {
				double currLength = 0;
				int z = (N - m) / k;
				
				for (int i = 1; i <= z; i++) {
					currLength += Math.abs(segment[(m-1)+i*k] - segment[(m-1)+(i-1)*k]);
				}
				double ng = (N - 1) / (z * k);
				lengths[m-1][k-1] = (currLength * ng) / k;
			}
		}
		
		double [] lnInversek = new double[kmax];
		double [] lnAverageLength = new double[kmax];
		
		for (int k = 1; k <= kmax; k++) {
			double sum = 0.0;
			
			for (int m = 1; m <= k; m++) {
				sum += lengths[m-1][k-1];
			}
			

			lnInversek[k-1] = Math.log(1.0 / k);
			lnAverageLength[k-1] = Math.log(sum / k);
		}
		
		double [] alfaBeta =  Statistics.fitLineThroughXYPointsCoefficients(lnInversek, lnAverageLength, 20);
		
		// the slope is the Higuchi dimension
		this.higuchiDimension = alfaBeta[0];
	}
	public double getHiguchiDimension(){
		return this.higuchiDimension;
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 100;
}