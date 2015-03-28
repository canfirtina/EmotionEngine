/*
 * Created on 2006.05.23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.nonlinear.entropy;

import java.util.*;

import emotionlearner.eegFrame.statisticMeasure.DistanceMeasure;
import emotionlearner.eegFrame.statisticMeasure.Statistics;

/**
 * This class calculates the approximate entropy of the EEG signal
 * 
 * S. M. Pincus and A. L. Goldberger, “Physiological time-series analysis: what does regularity quantify?” Am. J. Physiol., vol. 266, no. 4, (Heart Circ. Physiol., vol. 35), pp. H1643–H1656, Apr. 1994.
 * 
 * @author Alan Jovic
 *
 */

public class ApEn {
	/**
	 * The private method calculates the number phi(m)
	 * @param series signal given as a field of type double
	 * @param m m factor used in calculating phi(m)
	 * @param r radius for data inclusion
	 * @return phi(m)
	 */
	private static double calculateApEnSingleStepOfM(double [] series, int m, double r){
		int size = series.length;
		
		// Step 1 of ApEn
		double [][] vectors = new double[size-m][m];
		
		for (int i=0; i<vectors.length; i++){
			for (int j=0; j<m; j++){
				vectors[i][j] = series[i+j];
			}
		}
		
		// Steps 2 and 3 of ApEn
		int [] numberNMI = new int[vectors.length];
		double [] numberCMIR = new double[vectors.length];
		for (int i=0; i<vectors.length; i++){
			for (int j=0; j<vectors.length; j++){
				if (DistanceMeasure.maximumAbsoluteDifference(vectors[i],vectors[j])<=r){
					numberNMI[i] ++;
				}
			}
			if (numberNMI[i]==0) numberNMI[i]=1;
		}
		for (int i=0; i<vectors.length; i++){
			numberCMIR[i] = (double)(numberNMI[i]) / vectors.length;
		}
		
		// Step 4 of ApEn
		double numberPHI = 0.0; 
		for (int i=0; i<vectors.length; i++){
			numberPHI += Math.log(numberCMIR[i]);
		}
		numberPHI /= vectors.length;
		
		return numberPHI;
	}
	/**
	 * Calculates approximate entropy for the time series given in the field of type double
	 * @param series signal given as a field of type double
	 * @param m m factor used in calculating phi(m)
	 * @param r radius for data inclusion
	 * @return ApEn
	 */
	public static double calculateApEn(double [] series, int m, double r){
		double apen;
		
		double phi0 = ApEn.calculateApEnSingleStepOfM(series,m,r);
		double phi1 = ApEn.calculateApEnSingleStepOfM(series,m+1,r);
		
		apen = phi0 - phi1;
		
		return apen;
	}
	/**
	 * Calculates approximate entropy for the time series given in the list of type Double
	 * @param series signal given as a field of type double
	 * @param m m factor used in calculating phi(m)
	 * @param r radius for data inclusion
	 * @return ApEn
	 */
	public static double calculateApEn(List<Double> series, int m, double r){
		double apen;
		double [] serie = new double[series.size()];
		for (int i=0; i<series.size(); i++){
			serie[i] = series.get(i).doubleValue();
		}
		
		double phi0 = ApEn.calculateApEnSingleStepOfM(serie,m,r);
		double phi1 = ApEn.calculateApEnSingleStepOfM(serie,m+1,r);
		
		apen = phi0 - phi1;
		
		return apen;
	}
	/**
	 * Calculates the maximum of ApEn dependent of the radius r for the time series given in the field of type double
	 * @param series signal given as a field of type double
	 * @param m m factor used in calculating phi(m)
	 * @return a pair {maximum of ApEn, radius r for the maximum of ApEn}
	 */
	//raèuna ApEn za razne radijuse do 50% standardne devijacije niza i pronaðe najveæu
	public static double [] calculateMaxApEn(double [] series, int m){
		double apenMax = 0.0;
		double maxIndex = 0.0;
		double stdev = Statistics.standardDeviation(series);
		double phi0, phi1, apen;
		for (int i=1; i<=50; i++){
			phi0 = ApEn.calculateApEnSingleStepOfM(series,m,0.01*i*stdev);
			phi1 = ApEn.calculateApEnSingleStepOfM(series,m+1,0.01*i*stdev);
			apen = phi0-phi1;
			if (apen > apenMax){
				apenMax = apen;
				maxIndex = 0.01*i;
			}
		}
		return new double[]{apenMax,maxIndex};
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 15;
}
