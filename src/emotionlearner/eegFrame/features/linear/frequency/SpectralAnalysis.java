package emotionlearner.eegFrame.features.linear.frequency;
import emotionlearner.eegFrame.statisticMeasure.Statistics;
import emotionlearner.eegFrame.features.linear.frequencyDomain.operations.Complex;
import emotionlearner.eegFrame.features.linear.frequencyDomain.operations.FFT;


/**
 * Main class for EEG frequency analysis 
 * Currently supports fast Fourier transform 
 * and Burg algorithm based spectral density estimation 
 *X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.
 * @author Alan Jovic
 *
 */

public class SpectralAnalysis {
	private int segmentLengthForFFTSpectrum; 
	private Complex [] fourierTransform;
	private double [] PSD;
	private double [] frequencies;
	private double [] segment;
	private double sampFreq;
	//private double timeSpan = 0.0;
	
	/**
	 * Constructor for spectral analysis
	 * @param segment segment that is to be analyzed in frequency domain
	 */
	public SpectralAnalysis(double [] segment, double sampFreq){
		this.segment = segment;
		this.sampFreq = sampFreq;
//		for (int i=0; i<this.segment.length; i++){
//			timeSpan += segment[i];
//		}
	}
	/**
	 * Method estimates PSD function using fast Fourier transform
	 * @return true if the PSD function has been estimated
	 */
	public boolean calculateSpectrumFourier(int windowFunction){
		// expand to first 2^n greater than the length of the series
		//int pow = 1;
		segmentLengthForFFTSpectrum = 2;
		while (segmentLengthForFFTSpectrum<segment.length){
			segmentLengthForFFTSpectrum *= 2;
			//pow++;
		}
			 
		// transfering to complex numbers to prepare for fast Fourier transform
		Complex [] segmentComplex = new Complex[segmentLengthForFFTSpectrum];
		
		//ZASTO SE NE DIJELI SA SEGMENTLENGTHFORFFTSPECTRUM - 1 ?
		if (windowFunction == SpectralAnalysis.HAMMING_WINDOW){
			for (int i = 0; i<segmentLengthForFFTSpectrum; i++){
				if (i<segment.length){
					segmentComplex[i] = new Complex(segment[i]*(0.56-0.46*Math.cos(2*Math.PI*(double)(i)/segmentLengthForFFTSpectrum)),0);
				}
				else {
					segmentComplex[i] = new Complex(0.0,0);
				}
			}
		}
		//ZASTO SE NE DIJELI SA SEGMENTLENGTHFORFFTSPECTRUM - 1 ?
		else if (windowFunction == SpectralAnalysis.HANN_WINDOW){
			for (int i = 0; i<segmentLengthForFFTSpectrum; i++){
				if (i<segment.length){
					segmentComplex[i] = new Complex(segment[i]*(0.5-0.5*Math.cos(2*Math.PI*(double)(i)/segmentLengthForFFTSpectrum)),0);
				}
				else {
					segmentComplex[i] = new Complex(0.0,0);
				}
			}
		}
		//inaèe obièan rectangular window
		else {
			for (int i = 0; i<segmentLengthForFFTSpectrum; i++){
				if (i<segment.length){
					segmentComplex[i] = new Complex(segment[i],0);
				}
				else {
					segmentComplex[i] = new Complex(0.0,0);
				}
			}
		}
		
		// fast Fourier transform
		fourierTransform = FFT.fft(segmentComplex);
			
		PSD = new double[fourierTransform.length];
		frequencies = new double[fourierTransform.length];
		
//		System.out.println("Rezultat FFT-a je: ");
//		for(int i = 0; i< fourierTransform.length; i++){
//			System.out.println(i+": Re = " + fourierTransform[i].getReal()+ " Im = "+ fourierTransform[i].getImaginary());
//			
//		}
		
		int i;
			
		for (i=0; i<fourierTransform.length; i++){
			PSD[i] = Math.pow(fourierTransform[i].abs(),2.0)/fourierTransform.length;
			frequencies[i] = (double) i*sampFreq/segmentLengthForFFTSpectrum;
		}
		// scaling, one-sided PSD estimate
		double [] scaledFreq = new double[frequencies.length/2];
		double [] scaledPSD = new double[frequencies.length/2];
		for (i=0; i<scaledFreq.length; i++){
			scaledFreq [i] = frequencies[i];
			if (i>0 && i<scaledFreq.length-1){
				scaledPSD[i] = 2*PSD[i];
			}
			else {
				scaledPSD[i] = PSD[i];
			}
		}
		frequencies = scaledFreq;
		PSD = scaledPSD;		
		
		return true;
	}
	
	
	/**
	 * Method estimates PSD function using autoregressive model of specified order using the Burg algorithm
	 * Details for the Burg method can be found in:
	 * 
	 * Burg’s Method, Algorithm and Recursion, Cedrick Collomb 2009;
	 * 
	 * S. de Waele and P. M. T. Broersen, The Burg Algorithm for Segments, IEEE Transactions on Signal Processing, 48(10):2876-2880, Oct 2000.
	 * 
	 * @param order The order of the AR model
	 * @return true if the PSD function has been estimated
	 */
	
	public boolean calculateSpectrumBurg(int order){	
		double [] kReflectionCoefficient = new double[order+1];
		//double [] autoCovarianceFactors = new double[order];
		double [][] fs, bs;
		int N = segment.length-1;
		int i, j;
		
		if (N<=order){
			return false;
		}
		fs = new double[order][N+1];
		bs = new double[order][N+1];
		
		kReflectionCoefficient[0] = 1.0;
		for (i=1; i<=order; i++){
			kReflectionCoefficient[i] = 0.0;
		}
		
		double sum = 0.0;//, lowSum=0.0;
		double [] Ds = new double[order];
		double mju;
		double temp1, temp2;
		
		for (i=0; i<=N; i++){
			fs[0][i] = segment[i];
			bs[0][i] = segment[i];
			sum += fs[0][i]*fs[0][i];
		}
		Ds[0] = sum-fs[0][0]*fs[0][0] + sum-bs[0][N]*bs[0][N];
		
		for (i=0; i<order; i++){
			mju = 0.0;
			for (j=0; j<=N-i-1; j++){
				mju += fs[i][j+i+1]*bs[i][j];
			}
			mju = -2*mju/Ds[i];
			
			
			for (j=0; j<=(i+1)/2; j++){
				temp1 = kReflectionCoefficient[j] + mju*kReflectionCoefficient[i+1-j];
				temp2 = kReflectionCoefficient[i+1-j]+mju*kReflectionCoefficient[j];
				kReflectionCoefficient[j] = temp1;
				kReflectionCoefficient[i+1-j]=temp2;
			}
			/*kReflectionCoefficient[i+1] = mju*1.0;
			*/
			if (i==order-1){
				break;
			}
			for (j=i+1; j<=N; j++){
				fs[i+1][j] = fs[i][j] + mju*bs[i][j-i-1];	
			}
			for (j=0; j<=N-i-1; j++){
				bs[i+1][j] = bs[i][j] + mju*fs[i][j+i+1];
			}
			Ds[i+1] = (1-mju*mju)*Ds[i] - fs[i+1][i+1]*fs[i+1][i+1] - bs[i+1][N-i-1]*bs[i+1][N-i-1];
		}
		
		double [] noise = new double[segment.length-order];
		
		double tempSum = 0.0;
		for (i=order; i<noise.length+order; i++){
			for (j=1; j<=order; j++){
				tempSum += kReflectionCoefficient[j]*segment[i-j];
			}
			noise[i-order] = segment[i]+tempSum;
			tempSum = 0.0;
		}
		double noiseVariance = Statistics.variance(noise, Statistics.mean(noise));
		
		//3. Estimation of the PSD
		PSD = new double[SpectralAnalysis.NUMBER_OF_FREQUENCIES];
		frequencies = new double[SpectralAnalysis.NUMBER_OF_FREQUENCIES];
		
		
		Complex s, h, temp;
		int k=0;
		for (i=0; i<frequencies.length; i++){
			frequencies[i] = (double) (i)*2*Math.PI/frequencies.length;
			
			temp = new Complex(0,0);
			
			s = new Complex(Math.cos(frequencies[i]),Math.sin(frequencies[i])); 
			
			for (j=order, k=0; j>0; j--, k++){
				temp = temp.plus(s.pow(j).times(kReflectionCoefficient[k]));
			}
			temp = temp.plus(kReflectionCoefficient[k]);
			
			h = new Complex(1,0);
			h = h.divide(temp);
			
			PSD[i] = noiseVariance*Math.pow(h.abs(),2.0);
		}
		// scaling, one-sided PSD estimate
		double [] scaledFreq = new double[frequencies.length/2];
		double [] scaledPSD = new double[frequencies.length/2];
		for (i=0; i<scaledFreq.length; i++){
			scaledFreq [i] = frequencies[i]/(2*Math.PI);
			if (i>0 && i<scaledFreq.length-1){
				scaledPSD[i] = 2*PSD[i];
			}
			else {
				scaledPSD[i] = PSD[i];
			}
		}
		frequencies = scaledFreq;
		PSD = scaledPSD;
		return true;
	}
	
	/**
	 * 
	 * @return Frequencies of the spectrum
	 */
	public double [] getFrequencies (){
		return this.frequencies;
	}
	/**
	 * 
	 * @return Power spectral density estimate of the spectrum
	 */
	public double [] getPSDEstimate(){
		return this.PSD;
	}

	public double getAlpha(){
		return this.getPSDForFrequencyBand(DEFAULT_ALPHA_BAND_LOWER_BOUND, DEFAULT_ALPHA_BAND_UPPER_BOUND);
	}
	public double getBeta(){
		return this.getPSDForFrequencyBand(DEFAULT_BETA_BAND_LOWER_BOUND, DEFAULT_BETA_BAND_UPPER_BOUND);
	}
	public double getGamma(){
		return this.getPSDForFrequencyBand(DEFAULT_GAMMA_BAND_LOWER_BOUND, DEFAULT_GAMMA_BAND_UPPER_BOUND);
	}
	public double getDelta(){
		return this.getPSDForFrequencyBand(DEFAULT_DELTA_BAND_LOWER_BOUND, DEFAULT_DELTA_BAND_UPPER_BOUND);
	}
	public double getTheta(){
		return this.getPSDForFrequencyBand(DEFAULT_THETA_BAND_LOWER_BOUND, DEFAULT_THETA_BAND_UPPER_BOUND);
	}
	
	public double getTotalPSD(){
		/*double sum = 0.0;
		for (int i=0; i<this.frequencies.length; i++){
			sum += PSD[i];
		}
		return sum;*/
		return this.getPSDForFrequencyBand(0.0, sampFreq/2); //Nyquist
	}
	
	/**
	 * A method for getting PSD within a defined frequency band 
	 * @param lowerFrequency Lower bound of the band
	 * @param upperFrequency Upper bound of the band
	 * @return Power spectral density estimate
	 */
	public double getPSDForFrequencyBand(double lowerFrequency, double upperFrequency){
		if(upperFrequency > sampFreq/2){
			upperFrequency = sampFreq/2; //Nyquist
		}
		double sum = 0.0;
		int minIndex = 0;
		int maxIndex = 0;
		int i;
		for (i=0; i<this.frequencies.length; i++){
			if (frequencies[i] >= lowerFrequency){
				break;
			}
		}
		minIndex = i;
		for (i=minIndex; i<this.frequencies.length; i++){
			if (frequencies[i] > upperFrequency){
				break;
			}
		}
		maxIndex = i-1;
		
		for (i=minIndex; i<=maxIndex; i++){
			sum += PSD[i];
		}
		return sum;
	}
	
	public double getFrequencyBand(double lowerFrequency, double upperFrequency){
		double sum = 0.0;
		int minIndex = 0;
		int maxIndex = 0;
		int i;
		for (i=0; i<this.frequencies.length; i++){
			if (frequencies[i] >= lowerFrequency){
				break;
			}
		}
		minIndex = i;
		for (i=minIndex; i<this.frequencies.length; i++){
			if (frequencies[i] > upperFrequency){
				break;
			}
		}
		maxIndex = i;
		
		double[] bandFrequencies = new double[maxIndex - minIndex];
		for (i = minIndex; i < maxIndex; i++){
			bandFrequencies[i] = this.frequencies[i];
		}
		return sum;
	}
	/**
	 * 
	 * @return Total power spectral density of the signal up to the upper HF band
	 */

	/**
	 * 
	 * @return Fourier transform of the signal
	 */
	public Complex [] getFourierTransform(){
		return this.fourierTransform;
	}
	/**
	 * 
	 * @return Segment length cut to the nearest power of 2 to be able to perform a fast Fourier transform
	 */
	public int getSegmentLengthForFFTSpectrum(){
		return this.segmentLengthForFFTSpectrum;
	}

	
	public static final double DEFAULT_ALPHA_BAND_LOWER_BOUND = 8;
	public static final double DEFAULT_ALPHA_BAND_UPPER_BOUND = 13;
	public static final double DEFAULT_BETA_BAND_LOWER_BOUND = 13;
	public static final double DEFAULT_BETA_BAND_UPPER_BOUND = 30;
	public static final double DEFAULT_GAMMA_BAND_LOWER_BOUND = 30;
	public static final double DEFAULT_GAMMA_BAND_UPPER_BOUND = 100;
	public static final double DEFAULT_DELTA_BAND_LOWER_BOUND = 4;
	public static final double DEFAULT_DELTA_BAND_UPPER_BOUND = 8;
	public static final double DEFAULT_THETA_BAND_LOWER_BOUND = 0.5;
	public static final double DEFAULT_THETA_BAND_UPPER_BOUND = 4;
	
	public static final int FAST_FOURIER_TRANSFORM = 1;
	public static final int BURG_METHOD = 2;
	private static final int NUMBER_OF_FREQUENCIES = 256;
	public static final int NO_WINDOW = 0;
	public static final int HAMMING_WINDOW = 1;
	public static final int HANN_WINDOW = 2;
}
