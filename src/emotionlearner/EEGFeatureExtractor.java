package emotionlearner;

/**
 * 
 */
import java.util.ArrayList;
import java.util.Arrays;

import features.linear.frequency.SpectralAnalysis;
import features.linear.timeDomain.AutocorrelationCoefficient;
import features.linear.timeDomain.FanoFactor;
import features.linear.timeDomain.Mean;
import features.linear.timeDomain.MeanOfAbsoluteValuesOfFirstDiffNormalized;
import features.linear.timeDomain.MeanOfAbsoluteValuesOfSecondDiffNormalized;
import features.linear.timeDomain.MeanOfAbsoluteValuesOfTheFirstDifferences;
import features.linear.timeDomain.MeanOfAbsoluteValuesOfTheSecondDifferences;
import features.linear.timeDomain.StandardDeviation;
import features.nonlinear.entropy.CorrectedConditionalShannonEntropy;
import features.nonlinear.entropy.RenyiEntropy;
import features.nonlinear.fractal.DFA;
import features.nonlinear.fractal.HiguchiDimension;
import features.nonlinear.fractal.HurstExponent;
import features.nonlinear.other.AllanFactor;
import features.nonlinear.other.CTMSecondOrderDifferencePlot;
import features.nonlinear.other.LempelZivComplexity;
import features.nonlinear.phaseSpace.LyapunovExponent;
import features.nonlinear.phaseSpace.StandardDeviationRatio;
import features.timeFrequency.HaarWaveletStandardDeviation;

/**
 * @author ayhun, can
 *
 */
public class EEGFeatureExtractor {
	final static int epochLength = 1024;
	static boolean first = true;

	public static ArrayList<double[]> extractFeatures(double[][] matrix) throws Exception {
		int numEpoch = (int) Math.floor(matrix[0].length / epochLength);
		int numChannel = matrix.length;
		double[][] currentEpoch;
		ArrayList<double[]> features = new ArrayList<double[]>();

		for (int i = 0; i < numEpoch; i++) {
			currentEpoch = new double[numChannel][];
			for (int j = 0; j < numChannel; j++) {
				int start = i * epochLength;
				int end = (i + 1) * epochLength;
				currentEpoch[j] = Arrays.copyOfRange(matrix[j], start, end);
			}

			features.add(extract(currentEpoch));
		}
		
		return features;
	}

	public static double[] extract(double[][] epoch) throws Exception {
		/*
		 * DataSource source = new DataSource("ayhun.arff"); Instances data =
		 * source.getDataSet(); Attribute a = data.attribute(2);
		 */
		final int numFeatures = 20;
		double[] res = new double[epoch.length * numFeatures];
		double[] currentFeatures;
		
		// forun d���nda multiseries featurelar ��kart�labilir belki
		// check it out samtayms
		for (int i = 0; i < epoch.length; i++) {
			currentFeatures = new double[numFeatures];
			SpectralAnalysis sa = new SpectralAnalysis(epoch[i], 256);
			sa.calculateSpectrumFourier(1);

			currentFeatures[0] = sa.getAlpha();
			currentFeatures[1] = sa.getBeta();
			currentFeatures[2] = sa.getTheta();
			currentFeatures[3] = sa.getGamma();
			currentFeatures[4] = sa.getDelta();
			currentFeatures[5] = sa.getTotalPSD();
			currentFeatures[6] = AutocorrelationCoefficient.calculateAutocorrelationCoefficient(epoch[i]);
			currentFeatures[7] = FanoFactor.calculateFanoFactorFromTimes(epoch[i], epochLength / 256);
			currentFeatures[8] = MeanOfAbsoluteValuesOfFirstDiffNormalized.calculateMeanOfFirstDiffNormalized(epoch[i]);
			// parametrelerine bak internet geri gelince.
			currentFeatures[9] = CorrectedConditionalShannonEntropy.calculateCorrectedConditionalShannonEntropy(epoch[i], 3, 3);
			// bunun da bak
			currentFeatures[10] = new HiguchiDimension(epoch[i], 3).getHiguchiDimension(); 
			currentFeatures[11] = HurstExponent.calculateHurstExponent(epoch[i]);
			currentFeatures[12] = AllanFactor.calculateAllanFactorFromTimes(epoch[i], epochLength / 256);
			// buna da bak
			currentFeatures[13] = CTMSecondOrderDifferencePlot.calculateCTM(epoch[i], 100);
			currentFeatures[14] = LempelZivComplexity.calculateLempelZivComplexity(epoch[i]);
			currentFeatures[15] = LyapunovExponent.calculateLargestLyapunovExponentStableWolf(epoch[i]);
			currentFeatures[16] = new StandardDeviationRatio(epoch[i]).getCSI();
			currentFeatures[17] = new StandardDeviationRatio(epoch[i]).getCVI();
			currentFeatures[18] = new StandardDeviationRatio(epoch[i]).getSD1SD2Ratio();
			currentFeatures[19] = HaarWaveletStandardDeviation.calculateHaarWaveletSTDEV(epoch[i]);

			for (int j = 0; j < numFeatures; j++) {
				res[i * numFeatures + j] = currentFeatures[j];
			}
		}

		if (first) {
			double[] asd = epoch[2];

			/* features.linear.frequency */{
				SpectralAnalysis sa = new SpectralAnalysis(asd, 256);
				sa.calculateSpectrumFourier(1);
				System.out.println("alpha: " + sa.getAlpha() + "\nbeta: "
						+ sa.getBeta() + "\ntheta: " + sa.getTheta()
						+ "\ndelta: " + sa.getDelta() + "\ngamma: "
						+ sa.getGamma());

				// SpectralEntropy.calculateSpectralEntropy(asd, 250,
				// lowerFrequencyBound, upperFrequencyBound);
			}

			/* features.linear.frequencyDomain.operations */{
				// System.out.println("\nSurrogate Series: " +
				// SurrogateSeries.calculateSurrogateSeries(Arrays.copyOfRange(asd,
				// 1, 16384)));
			}

			/* features.linear.timeDomain */{
				System.out.println("\nAuto Correlation Coefficient: "
						+ AutocorrelationCoefficient
								.calculateAutocorrelationCoefficient(asd));
				System.out.println("\nFano Factors: "
						+ FanoFactor.calculateFanoFactorFromTimes(asd, 80));
				System.out.println("\nMean: " + Mean.calculateMean(asd));
				System.out
						.println("\nMeanOfAbsoluteValuesOfFirstDiffNormalized: "
								+ MeanOfAbsoluteValuesOfFirstDiffNormalized
										.calculateMeanOfFirstDiffNormalized(asd));
				System.out
						.println("\nMeanOfAbsoluteValuesOfSecondDiffNormalized: "
								+ MeanOfAbsoluteValuesOfSecondDiffNormalized
										.calculateMeanOfSecondDiffNormalized(asd));
				System.out
						.println("\nMeanOfAbsoluteValuesOfTheFirstDifferences: "
								+ MeanOfAbsoluteValuesOfTheFirstDifferences
										.calculateMeanOfFirstDiff(asd));
				System.out
						.println("\nMeanOfAbsoluteValuesOfTheSecondDifferences: "
								+ MeanOfAbsoluteValuesOfTheSecondDifferences
										.calculateMeanOfSecondDiff(asd));
				System.out.println("\nStandard Deviation: "
						+ StandardDeviation.calculateStandardDeviation(asd));
			}

			/* features.linear.timeDomain */{
				System.out.println("\nAuto Correlation Coefficient: "
						+ AutocorrelationCoefficient
								.calculateAutocorrelationCoefficient(asd));
			}

			/* features.nonlinear.entropy */{
				// System.out.println("\n: " + ApEn.calculateApEn(asd, m, r));
				// System.out.println("\nCarnap Entropy: " +
				// CarnapEntropy1D.calculateCarnapEntropy1D(asd));
				System.out
						.println("\nConditional Shannon Entropy (parametre var ogren onu): "
								+ CorrectedConditionalShannonEntropy
										.calculateCorrectedConditionalShannonEntropy(
												asd, 1, 3));
				System.out
						.println("\nRenyi Entropy (parametre var ogren onu): "
								+ RenyiEntropy.calculateRenyiEntropy(asd, 1));
				// System.out.println("\n: " + SampEn.calculateSampEn(asd, m,
				// r));
			}

			/* features.nonlinear.fractal */{
				System.out.println("\nDFA: "
						+ Arrays.toString(DFA.calculateDFA(asd)));

				System.out
						.println("\nHiguchi Dimension: "
								+ (new HiguchiDimension(asd, 50))
										.getHiguchiDimension());
				System.out.println("\nHurst Exponent: "
						+ HurstExponent.calculateHurstExponent(asd));
			}

			/* wavelet */{
				System.out.println("\nWavelet: "
						+ HaarWaveletStandardDeviation
								.calculateHaarWaveletSTDEV(asd));
			}
			System.out.println("\nConditional Shannon Entropy: "
					+ CorrectedConditionalShannonEntropy
							.calculateCorrectedConditionalShannonEntropy(asd,
									1, 3));
			first=false;
		}
		return res;

		// System.out.println("\nAllan Factor: " +
		// AllanFactor.calculateAllanFactorFromIntervals(asd, 60));

	}
}
