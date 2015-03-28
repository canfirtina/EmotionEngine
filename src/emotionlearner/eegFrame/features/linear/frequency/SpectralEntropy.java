package emotionlearner.eegFrame.features.linear.frequency;

import emotionlearner.eegFrame.features.linear.frequencyDomain.operations.Complex;

public class SpectralEntropy {
	/**
	* Spectral entropy estimation based on:
    * Ermes, M., Pärkkä, J. & Cluitmans, L. 2008. Advancing from Offline to
    * Online Activity Recognition with Wearable Sensors. Proceedings of the
	* 30th Annual International Conference of the IEEE Engineering in Medicine
	* and Biology Society. Pp. 4451–4454. 
	* 
	*  A. Rezek and S. J. Roberts, “Stochastic Complexity Measures for Physiological Signal Analysis,” IEEE Trans. Biomed. Eng., vol. 45, no. 9, pp. 1186–1191, Sep. 1998.
	*
	* Entropy is calculated using fast Fourier transform
    * @param segment EEG interval segment 
	* @param lowerFrequencyBound Lower bound for spectral entropy
	* @param upperFrequencyBound Higher bound for spectral entropy
	* @return spectral entropy
	*/
	public static final double calculateSpectralEntropy(double [] segment, double sampFreq, double lowerFrequencyBound, double upperFrequencyBound) throws Exception{
		SpectralAnalysis sAnalysis = new SpectralAnalysis(segment, sampFreq);
		//sAnalysis.calculateSpectrumFourier();
		if (!sAnalysis.calculateSpectrumBurg(16)){
			throw new Exception("Unable to calculate spectral entropy.");
		}
		
		//return SpectralEntropy.calculateSpectralEntropy(sAnalysis.getFourierTransform(), sAnalysis.getSegmentLengthForFFTSpectrum(), lowerFrequencyBound, upperFrequencyBound);
		
		double spectEntropy = 0;
		double [] psd = sAnalysis.getPSDEstimate();
		double [] frequency = sAnalysis.getFrequencies();
		int i;
		
		int minIndex = 0, maxIndex = 0;
		for (i=0; i<frequency.length; i++){
			if (frequency[i]>=lowerFrequencyBound){
				break;
			}
		}
		minIndex = i;
		
		for (i=minIndex; i<frequency.length; i++){
			if (frequency[i]>upperFrequencyBound){
				break;
			}
		}
		maxIndex = i-1;
		
		for (i=minIndex; i<=maxIndex; i++){
			spectEntropy += psd[i]*Math.log10(psd[i]);
		}
		spectEntropy = -spectEntropy/Math.log10((double)(maxIndex-minIndex));
		
		return spectEntropy;
		
	}
	
	/**
	 * Spectral entropy estimation based on:
	 * Ermes, M., Pärkkä, J. & Cluitmans, L. 2008. Advancing from Offline to
     * Online Activity Recognition with Wearable Sensors. Proceedings of the
     * 30th Annual International Conference of the IEEE Engineering in Medicine
     * and Biology Society. Pp. 4451–4454.
	 * @param fastFT Complex numbers containing fast fourier transform of EEG interval segment
	 * @param segmentLength Length of EEG interval segment after the cut to the nearest power of 2 
	 * @param lowerFrequencyBound Lower bound for spectral entropy
	 * @param upperFrequencyBound Higher bound for spectral entropy
	 * @return spectral entropy
	 */
	public static final double calculateSpectralEntropy(Complex [] fourierTransform, int segmentLength, double lowerFrequencyBound, double upperFrequencyBound){
		double spectEntropy = 0;
		double [][] psd = new double[2][fourierTransform.length];
		int i;
		
		for (i=0; i<fourierTransform.length; i++){
			psd[1][i] = Math.pow(fourierTransform[i].abs(),2.0)/fourierTransform.length;
			psd[0][i] = (double) i/segmentLength*1; // frequencies, sampling period set to 1
		}
		
		int minIndex = 0, maxIndex = 0;
		for (i=0; i<fourierTransform.length; i++){
			if (psd[0][i]>=lowerFrequencyBound){
				break;
			}
		}
		minIndex = i;
		
		for (i=minIndex; i<fourierTransform.length; i++){
			if (psd[0][i]>upperFrequencyBound){
				break;
			}
		}
		maxIndex = i-1;
		
		for (i=minIndex; i<=maxIndex; i++){
			spectEntropy += psd[1][i]*Math.log10(psd[1][i]);
		}
		spectEntropy = -spectEntropy/Math.log10((double)(maxIndex-minIndex));
		
		return spectEntropy;
	}
}
