package emotionlearner.eegFrame.features.linear.timeDomain;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

//[10]	X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.
//The means of the absolute values of the first differences of the raw signals

public class MeanOfAbsoluteValuesOfTheFirstDifferences {
	
	public static final double calculateMeanOfFirstDiff(double[] series){
		return Statistics.meanOfAbsoluteValuesOfFirstDifferences(series);
	}
	public static final double calculateMeanOfFirstDiff(double[] series, int intervalBeginning, int intervalEnding){
		return Statistics.meanOfAbsoluteValuesOfFirstDifferences(series, intervalBeginning, intervalEnding);
	}
}
