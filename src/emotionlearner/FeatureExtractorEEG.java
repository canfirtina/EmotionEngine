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
import shared.Emotion;
import shared.FeatureList;
import weka.core.Attribute;
import weka.core.FastVector;

/**
 * Extracts features from EEG signals
 *
 */
public class FeatureExtractorEEG extends FeatureExtractor {
	
	/**
	 * specific properties for eeg feature extractor
	 */
	private static FeatureExtractorProperties properties = null;
	
    public FeatureExtractorEEG() {
        int totalFeatureCount = getProperties().getNumChannels() * getProperties().getNumFeaturesEachChannel();
		reset();
    }
	
	/**
	 * gets properties of eeg feature extractor
	 * @return 
	 */
	public static FeatureExtractorProperties getProperties(){
		
		if(properties == null){
			properties = new FeatureExtractorProperties(8, 8, null);
			int totalFeatureCount = properties.getNumChannels() * properties.getNumFeaturesEachChannel();
			FastVector featureAttributes = new FastVector(totalFeatureCount+1);
			for(int i=0;i<totalFeatureCount;++i)
				featureAttributes.addElement(new Attribute("att_eeg_" + i));
			featureAttributes.addElement(new Attribute("eeg_class", Emotion.classAttributes()));
			properties.setFeatureAttributes(featureAttributes);
		}
		return properties;
	}

    /**
     * Returns Features of the raw EEG data
     *
     * @return
     */
    @Override
    public FeatureList getFeatures() {
        final int numFeatures = getProperties().getNumChannels();

        double[][] rawDataTransposed = new double[rawData.get(0).getData().length][rawData.size()];
        for (int i = 0; i < rawDataTransposed.length; ++i) {
            for (int j = 0; j < rawDataTransposed[i].length; ++j) {
                rawDataTransposed[i][j] = rawData.get(j).getData()[i];
            }
        }

        double[] res = new double[rawDataTransposed.length * numFeatures];
        double[] currentFeatures ;
        for (int i = 0; i < rawDataTransposed.length; ++i) {
            currentFeatures = new double[numFeatures];
            SpectralAnalysis sa = new SpectralAnalysis(rawDataTransposed[i], 256);
            sa.calculateSpectrumFourier(1);

            currentFeatures[0] = sa.getAlpha();
            currentFeatures[1] = sa.getBeta();
            currentFeatures[2] = sa.getTheta();
            currentFeatures[3] = sa.getGamma();
            currentFeatures[4] = sa.getDelta();

            currentFeatures[5] = AutocorrelationCoefficient.calculateAutocorrelationCoefficient(rawDataTransposed[i]);
			currentFeatures[6] = CorrectedConditionalShannonEntropy.calculateCorrectedConditionalShannonEntropy(rawDataTransposed[i], 3, 3);
			currentFeatures[7] = HaarWaveletStandardDeviation.calculateHaarWaveletSTDEV(rawDataTransposed[i]);
			
			for (int j = 0; j < numFeatures; j++) {
				res[i * numFeatures + j] = currentFeatures[j];
			}
		}

        return new FeatureList(selectFeatures(res), getProperties().getFeatureAttributes());
    }

    /**
     * To be continued
     */
    @Override
    protected double[] selectFeatures(double[] features) {
        return features;
    }

	
	
	
	//old code
	/*
	public FeatureList getFeatures() {
        final int numFeatures = numFeaturesEachChannel;

        double[][] rawDataTransposed = new double[rawData.get(0).getData().length][rawData.size()];
        for (int i = 0; i < rawDataTransposed.length; ++i) {
            for (int j = 0; j < rawDataTransposed[i].length; ++j) {
                rawDataTransposed[i][j] = rawData.get(j).getData()[i];
            }
        }

        double[] res = new double[rawDataTransposed.length * numFeatures];
        double[] currentFeatures ;
        for (int i = 0; i < rawDataTransposed.length; ++i) {
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
//            try {
//                currentFeatures[7] = FanoFactor.calculateFanoFactorFromTimes(rawDataTransposed[i], rawDataTransposed[i].length / 256);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                currentFeatures[7] = 0;
//                System.err.println("FanoFactor exception");
//            }
			
            currentFeatures[7] = MeanOfAbsoluteValuesOfFirstDiffNormalized.calculateMeanOfFirstDiffNormalized(rawDataTransposed[i]);
            /*
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
            currentFeatures[15] = 0;//LyapunovExponent.calculateLargestLyapunovExponentStableWolf(rawDataTransposed[i]); TODO
            currentFeatures[16] = new StandardDeviationRatio(rawDataTransposed[i]).getCSI();
            currentFeatures[17] = new StandardDeviationRatio(rawDataTransposed[i]).getCVI();
            currentFeatures[18] = new StandardDeviationRatio(rawDataTransposed[i]).getSD1SD2Ratio();
            currentFeatures[19] = HaarWaveletStandardDeviation.calculateHaarWaveletSTDEV(rawDataTransposed[i]);

            for (int j = 0; j < numFeatures; j++) {
                res[i * numFeatures + j] = currentFeatures[j];
            }
        }

        return new FeatureList(selectFeatures(res), featureAttributes);
    }*/
}
