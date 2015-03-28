package emotionlearner.eegFrame.features.linear.timeDomain;

import emotionlearner.eegFrame.statisticMeasure.Statistics;


//[10]	X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.
//The means of the absolute values of the second differences of the normalized signals

public class MeanOfAbsoluteValuesOfSecondDiffNormalized {
	
	public static final double calculateMeanOfSecondDiffNormalized(double[] series){
		double secondDiff = Statistics.meanOfAbsoluteValuesOfSecondDifferences(series);
		double stdDev = Statistics.standardDeviation(series);
		return secondDiff/stdDev;
	}
	public static final double calculateMeanOfSecondDiffNormalized(double[] series, int intervalBeginning, int intervalEnding){
		double secondDiff = Statistics.meanOfAbsoluteValuesOfSecondDifferences(series, intervalBeginning, intervalEnding);
		double stdDev = Statistics.standardDeviation(series, intervalBeginning, intervalEnding);
		return secondDiff/stdDev;
	}

}
