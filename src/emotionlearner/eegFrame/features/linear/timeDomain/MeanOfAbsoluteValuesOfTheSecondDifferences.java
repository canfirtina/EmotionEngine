package emotionlearner.eegFrame.features.linear.timeDomain;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

//X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.
//The means of the absolute values of the second differences of the raw signals

public class MeanOfAbsoluteValuesOfTheSecondDifferences {

	public static final double calculateMeanOfSecondDiff(double[] series){
		return Statistics.meanOfAbsoluteValuesOfSecondDifferences(series);
	}
	public static final double calculateMeanOfSecondDiff(double[] series, int intervalBeginning, int intervalEnding){
		return Statistics.meanOfAbsoluteValuesOfSecondDifferences(series, intervalBeginning, intervalEnding);
	}
}
