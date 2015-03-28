package emotionlearner.eegFrame.features.linear.timeDomain;

import emotionlearner.eegFrame.statisticMeasure.Statistics;
public class FanoFactor {
	/*
	The Fano factor, which is the variance of the number of events in a specified counting time T 
	divided by the mean number of events in that counting time, is a measure of correlation over different time scales T. 
	This measure is sometimes called the index of dispersion of counts. 
	In terms of the sequence of counts, the Fano factor is simply the variance of {Ni} divided by the mean of {Ni},
	i.e.        Variance(Number_of_events_in_counting_time_T)
		 Fano =	---------------------------------------------
				Mean(Number_of_events_in_counting_time_T)
	
	reference: Heart Rate Variability: Measures and Models
			   Malvin C. Teich, Steven B. Lowen, Bradley M. Jost, and Karin Vibe-Rheymer	
			   Nonlinear Biomedical Signal processing, Vol. II, Dynamic Analysis and Modeling, edited by M.Akay, IEEE Press, New York, 2001, ch.6, pp.159-213
	 */
//PROVJERITI JOS
	public static final double calculateFanoFactorFromTimes(double [] timeAnnots, double timeInSeconds) throws Exception{
		int windowsCount = (int) (timeAnnots.length/timeInSeconds);
		if (windowsCount==0){
			throw new Exception("Unable to calculate Fano factor for this segment.");
		}
		double [] windowsSize = new double[windowsCount];
		
		for (int i=0; i<timeAnnots.length; i++){
			timeAnnots[i] = timeAnnots[i]-timeAnnots[0]; // to start with time 0.00 s
		}
		// series is now e.g.: 0, 0.87, 1.56, 2.22, 3.15, 3.9, 4.75, 5.35, 6.22, 7.09, 7.85, 8.67
		int j=1;
		int count = 0;
		for (int i=0; i<timeAnnots.length; i++){
			if (timeAnnots[i]>j*timeInSeconds){
				if (j-1==windowsSize.length) break;
				windowsSize[j-1]=count;
				j++;
				count=1;
			}
			else {
				count++;
			}
		}
		return Math.pow(Statistics.standardDeviation(windowsSize),2.0)/Statistics.mean(windowsSize);
	}
//	public static final double calculateFanoFactorFromIntervals(double [] RRintervals, double timeInSeconds) throws Exception{
//		// transform into times
//		double [] temp = new double[RRintervals.length+1];
//		
//		temp[0] = 0.0;
//		for (int i=0; i<RRintervals.length; i++){
//			temp[i+1] = temp[i]+RRintervals[i];
//		}
//		
//		int windowsCount = (int) (temp[temp.length-1]/timeInSeconds);
//		if (windowsCount==0){
//			throw new Exception("Unable to calculate Fano factor for this segment.");
//		}
//		double [] windowsSize = new double[windowsCount];
//		
//		// series is now e.g.: 0, 0.87, 1.56, 2.22, 3.15, 3.9, 4.75, 5.35, 6.22, 7.09, 7.85, 8.67
//		int j=1;
//		int count = 0;
//		for (int i=0; i<temp.length; i++){
//			if (temp[i]>j*timeInSeconds){
//				if (j-1==windowsSize.length) break;
//				windowsSize[j-1]=count;
//				j++;
//				count=1;
//			}
//			else {
//				count++;
//			}
//		}
//		return Math.pow(Statistics.standardDeviation(windowsSize),2.0)/Statistics.mean(windowsSize);
//	}
}
