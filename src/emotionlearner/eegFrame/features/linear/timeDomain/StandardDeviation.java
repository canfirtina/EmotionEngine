package emotionlearner.eegFrame.features.linear.timeDomain;


import emotionlearner.eegFrame.statisticMeasure.Statistics;
//X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.
public class StandardDeviation {
	public static final double calculateStandardDeviation(double[] series){
		return Statistics.standardDeviation(series);
	}
	public static final double calculateStandardDeviation(double[] series, int intervalBeginning, int intervalEnding){
		return Statistics.standardDeviation(series, intervalBeginning, intervalEnding);
	}
}

