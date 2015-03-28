package emotionlearner.eegFrame.features.linear.timeDomain;


import emotionlearner.eegFrame.statisticMeasure.Statistics;

//X.-W. Wang, D. Nie, and B.-L. Lu, “EEG-Based Emotion Recognition Using Frequency Domain Features and Support Vector Machines,” Lecture Notes in Computer Science, vol. 7062, pp. 734–743, 2011.
//The means of the raw signal

public class Mean {
	public static final double calculateMean(double[] series){
		return Statistics.mean(series);
	}
	public static final double calculateMean(double[] series, int intervalBeginning, int intervalEnding){
		return Statistics.mean(series, intervalBeginning, intervalEnding);
	}
}
