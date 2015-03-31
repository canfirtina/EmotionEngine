package emotionlearner;


import features.linear.frequency.SpectralAnalysis;
import features.linear.timeDomain.AutocorrelationCoefficient;
import features.linear.timeDomain.FanoFactor;
import features.linear.timeDomain.MeanOfAbsoluteValuesOfFirstDiffNormalized;
import features.nonlinear.entropy.CorrectedConditionalShannonEntropy;
import features.nonlinear.fractal.HiguchiDimension;
import features.nonlinear.fractal.HurstExponent;
import features.nonlinear.other.AllanFactor;
import features.nonlinear.other.CTMSecondOrderDifferencePlot;
import features.nonlinear.other.LempelZivComplexity;
import features.nonlinear.phaseSpace.LyapunovExponent;
import features.nonlinear.phaseSpace.StandardDeviationRatio;
import features.timeFrequency.HaarWaveletStandardDeviation;
import shared.FeatureList;
/**
 * Extracts features from EEG signals
 *
 */
public class FeatureExtractorEEG extends FeatureExtractor{

	public  FeatureExtractorEEG() {
		reset();	
	}
	

	/**
	 * Returns Features of the raw EEG data 
	 * @return
	 * @throws Exception 
	 */
	@Override
	public FeatureList getFeatures() {
		final int numFeatures = 20;
		
		double[][] rawDataTransposed = new double[rawData.get(0).length][rawData.size()];
		for(int i=0;i<rawDataTransposed.length;++i)
			for(int j=0;j<rawDataTransposed[i].length;++j)
				rawDataTransposed[i][j] = rawData.get(j)[i];
		
		double[] res = new double[rawDataTransposed.length * numFeatures];
		double[] currentFeatures = null;
		for(int i=0;i<rawDataTransposed.length;++i){
			currentFeatures = new double[numFeatures];
			SpectralAnalysis sa = new SpectralAnalysis(rawDataTransposed[i], 256);
			sa.calculateSpectrumFourier(1);

			currentFeatures[0] = sa.getAlpha();
			currentFeatures[1] = sa.getBeta();
			currentFeatures[2] = sa.getTheta();
			currentFeatures[3] = sa.getGamma();
			currentFeatures[4] = sa.getDelta();
			currentFeatures[5] = sa.getTotalPSD();
			currentFeatures[6] = AutocorrelationCoefficient.calculateAutocorrelationCoefficient(rawDataTransposed[i]);
			try {
				currentFeatures[7] = FanoFactor.calculateFanoFactorFromTimes(rawDataTransposed[i], rawDataTransposed[i].length/ 256);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				currentFeatures[7] = 0;
				System.err.println("FanoFactor exception");
			}
			currentFeatures[8] = MeanOfAbsoluteValuesOfFirstDiffNormalized.calculateMeanOfFirstDiffNormalized(rawDataTransposed[i]);
			// parametrelerine bak internet geri gelince.
			currentFeatures[9] = CorrectedConditionalShannonEntropy.calculateCorrectedConditionalShannonEntropy(rawDataTransposed[i], 3, 3);
			// bunun da bak
			currentFeatures[10] = new HiguchiDimension(rawDataTransposed[i], 3).getHiguchiDimension(); 
			currentFeatures[11] = HurstExponent.calculateHurstExponent(rawDataTransposed[i]);
			try {
				currentFeatures[12] = AllanFactor.calculateAllanFactorFromTimes(rawDataTransposed[i], rawDataTransposed[i].length);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				currentFeatures[12] = 0;
				System.err.println("AllanFactor exception");
			}
			// buna da bak
			currentFeatures[13] = CTMSecondOrderDifferencePlot.calculateCTM(rawDataTransposed[i], 100);
			currentFeatures[14] = LempelZivComplexity.calculateLempelZivComplexity(rawDataTransposed[i]);
			currentFeatures[15] = LyapunovExponent.calculateLargestLyapunovExponentStableWolf(rawDataTransposed[i]);
			currentFeatures[16] = new StandardDeviationRatio(rawDataTransposed[i]).getCSI();
			currentFeatures[17] = new StandardDeviationRatio(rawDataTransposed[i]).getCVI();
			currentFeatures[18] = new StandardDeviationRatio(rawDataTransposed[i]).getSD1SD2Ratio();
			currentFeatures[19] = HaarWaveletStandardDeviation.calculateHaarWaveletSTDEV(rawDataTransposed[i]);

			for (int j = 0; j < numFeatures; j++) 
				res[i * numFeatures + j] = currentFeatures[j];
		}
		
		return new FeatureList(res);
	}
	
}
