package emotionlearner.eegFrame.features.nonlinear.fractal;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

/**
 * This class implements detrended fluctuation analysis (DFA)
 * @author Alan Jovic
 *
 */
//PROVJERITI JOŠ
public class DFA {
	/**
	 * The static method calculates the slopes of the line segments obtained by 
	 * detrending the fluctuations in the EEG series
	 * Long range fluctuations are calculated only if there is enough data present and if the user wants it
	 * @param segment segment of EEG series
	 * @param longRange if long range fluctuation calculation is requested 
	 * @param minimalAnalyzedSegmentLength minimal number of samples of EEG series used in detrending procedure
	 * @param boundForAlphaLongCalculation n for the calculation of long range fluctuation coefficient alphaL
	 * @return slopes alphaS and alphaL of the DFA
	 * @throws Exception if the series is too short
	 * 
	 * C.-K. Peng, S. Havlin, H. E. Stanley, and A. L. Goldberger, “Quantification of scaling exponents and crossover phenomena in nonstationary heartbeat time series,” Chaos, Solitons, & Fractals, vol. 5, no. 1, pp. 82–87, Jan 1995.
	 */
	public static double [] calculateDFA(double [] segment, boolean longRange, int minimalAnalyzedSegmentLength, int boundForAlphaLongCalculation) throws Exception{
		double mean = Statistics.mean(segment);
		double [] integratedSeries = new double [segment.length];
		integratedSeries[0] = segment[0]-mean;
		for (int i=1; i<segment.length; i++){
			integratedSeries[i] += integratedSeries[i-1]+segment[i]-mean;
		}
		// minimalna duljina defaultna serije koju se fita: 5 otkucaja - što staviti za EEG?!
		int maxN = (int) segment.length / minimalAnalyzedSegmentLength;  //broj boxova
		if (maxN < 4){
			throw new Exception("Too short time segment for correct DFA estimation.");
		}
		if (!longRange){
			if (maxN > boundForAlphaLongCalculation){
				maxN = boundForAlphaLongCalculation;
			}
		}
		else {
			if (maxN<boundForAlphaLongCalculation+5){
				System.err.print("Too short time segment for correct DFA alphaL estimation.");
			}
		}
		double [] Fn = new double [maxN-2];
		double [] temp;
		double sum;
		int size;
		int i,j,k;
		double [] xPoints, yPoints;
		for (i=2; i<maxN; i++){
			size = integratedSeries.length/i; //koliko uzoraka sadri jedan box
			temp = new double[size];
			xPoints = new double[size];
			sum = 0.0;
			for (j=0; j<size; j++){
				xPoints[j] = j;
			}
			for (j=0; j<i; j++){ //idem po svim prozorima (boxovima) duljine size
				for (k=0; k<temp.length; k++){ //po svim uzorcima u jednom boxu
					temp[k] = integratedSeries[j*temp.length+k]; //strpa sve uzorke trenutnog prozora u temp
				}
				yPoints = Statistics.fitLineThroughXYPointsData(xPoints, temp, 10);
				
				for (k=(int)(0.1*temp.length); k<(int)(0.9*temp.length); k++){ //izbacim prvih i zadnjih 10% toèaka iz trenutnog boxa
					sum += Math.pow(temp[k]-yPoints[k],2.0); //za trenutni box raèuna F(k)
				}				
				
			}
			//ZAŠTO NEGATIVAN LOGARITAM?
			Fn[i-2] = -Math.log10(Math.sqrt(sum/(0.8*integratedSeries.length))); //0.8 jer je izbacio 20% toèaka
		}
		double [] ns;
		double alphaS = 0.0, alphaL = 0.0;
		if (longRange){
			if (maxN>=boundForAlphaLongCalculation+5){ // calculate alphaL
				temp = new double[maxN-boundForAlphaLongCalculation];
				ns = new double[temp.length];
				for (i=0; i<temp.length; i++){
					temp[i] = Fn[boundForAlphaLongCalculation-2+i];
					ns[i] =  Math.log10(boundForAlphaLongCalculation-2+i);
				}
				alphaL = Statistics.fitLineThroughXYPointsCoefficients(ns, temp, 0)[0];
			}
		}
		// calculate alphaS - KLASICAN
		ns = new double[boundForAlphaLongCalculation-2];
		for (i=0; i<boundForAlphaLongCalculation-2; i++){
			ns[i] = Math.log10((double)(i+2));
		}
		alphaS = Statistics.fitLineThroughXYPointsCoefficients(ns, Fn, 10)[0];
		
		return new double[]{alphaS, alphaL};
		
	}
	/**
	 * The static method calculates the slopes of the line segments obtained by 
	 * detrending the fluctuations in the RR interval series using default parameters
	 * @param RRintervalSeries RR interval series
	 * @return slopes alphaS and alphaL of the DFA
	 * @throws Exception if the series is too short
	 */
	public static double [] calculateDFA(double [] RRintervalSeries) throws Exception{
		return calculateDFA(RRintervalSeries, DFA.DEFAULT_USE_LONG_RANGE_FLUCTUATIONS, DFA.DEFAULT_MINIMAL_ANALYZED_SEGMENT_LENGTH, DFA.DEFAULT_BOUND_FOR_ALPHA_LONG_CALCULATION);
	}
	public static final int DEFAULT_MINIMAL_ANALYZED_SEGMENT_LENGTH = 5;
	public static final int DEFAULT_BOUND_FOR_ALPHA_LONG_CALCULATION = 13;
	public static final boolean DEFAULT_USE_LONG_RANGE_FLUCTUATIONS = false;
}
