package emotionlearner.eegFrame.features.nonlinear.phaseSpace;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

public class StandardDeviationRatio {
	/*
	 * - cited from "Mining Physiological Conditions from Heart Rate Variability Analysis", 
	 * Che-Wei Lin, Jeen-Shing Wang and Pau-Choo Chung, IEEE Computational Intelligence Magazine, 5(1):50-58, Feb 2010
	 * 
	 * 
	 * original article:
	 * 
	 * J Auton Nerv Syst. 1997 Jan 12;62(1-2):79-84.
		A new method of assessing cardiac autonomic function and its comparison with spectral analysis and coefficient of variation of R-R interval.
		Toichi M, Sugiura T, Murai T, Sengoku A.
	 */
	//PROVJERITI JOŠ
	private double SD1, SD2;
	/**
	 * Centar elipse odreðen je prosjeènom duljinom segmenta
	 * SD1 oznaèava standardnu devijaciju udaljenosti tocaka od osi y = x,
	 * a SD2 je standardna devijacija tocaka od osi y = -x + mean,
	 * gdje je mean prosjecna duljina segmenta. SD1 devijacija odredjuje sirinu elipse, dok SD2 odredjuje duljinu elipse.
	 * @param segment
	 */
	public StandardDeviationRatio(double [] segment){
		double mean = Statistics.mean(segment);
		double [] distancesFromYequalsX = new double[segment.length-1];
		double [] distancesFromYequalsMinusXplusMean = new double[segment.length-1];
		
		for (int i=0; i<segment.length-1; i++){
			distancesFromYequalsX[i] = Math.abs(segment[i]-segment[i+1])*Math.sqrt(2)/2.0; // udaljenost tocke (xi,yi) od y=x osi: d1 = |xi - yi|/sqrt(2)
			//Ova ispod formula se ne slae s onom koju sam našla
			distancesFromYequalsMinusXplusMean[i] = Math.abs(segment[i]+segment[i+1]-2*mean)/Math.sqrt(2); // udaljenost tocke (xi,yi) od y = -x + RRm osi: d1 = |xi + yi - RRm|/sqrt(2)
		}
		SD1 = Statistics.standardDeviation(distancesFromYequalsX);
		SD2 = Statistics.standardDeviation(distancesFromYequalsMinusXplusMean);
	}
	/**
	 * Cardiac sympathetic index = SD2/SD1
	 * @return SD2/SD1
	 */
	public double getCSI(){
		return SD2/SD1;
	}
	/**
	 * Cardiac vagal index = log10(16*SD1*SD2)
	 * @return log10(16*SD1*SD2)
	 */
	public double getCVI(){
		return Math.log10(16*SD1*SD2);
	}
	public double getSD1SD2Ratio(){
		return SD1/SD2;
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 5;
}
