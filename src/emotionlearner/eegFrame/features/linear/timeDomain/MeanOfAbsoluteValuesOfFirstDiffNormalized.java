package emotionlearner.eegFrame.features.linear.timeDomain;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

//X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.

public class MeanOfAbsoluteValuesOfFirstDiffNormalized {
	
	public static final double calculateMeanOfFirstDiffNormalized(double[] series){
		double firstDiff = Statistics.meanOfAbsoluteValuesOfFirstDifferences(series);
		double stdDev = Statistics.standardDeviation(series);
		return firstDiff/stdDev;
	}
	public static final double calculateMeanOfFirstDiffNormalized(double[] series, int intervalBeginning, int intervalEnding){
		double firstDiff = Statistics.meanOfAbsoluteValuesOfFirstDifferences(series, intervalBeginning, intervalEnding);
		double stdDev = Statistics.standardDeviation(series, intervalBeginning, intervalEnding);
		return firstDiff/stdDev;
	}

}
