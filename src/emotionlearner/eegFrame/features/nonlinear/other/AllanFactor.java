package emotionlearner.eegFrame.features.nonlinear.other;

import emotionlearner.eegFrame.statisticMeasure.Statistics;

public class AllanFactor {
	/*
	 * The Allan factor is the ratio of the event-number Allan variance to twice the mean:
	 * 
	 * 					Variance(Difference(Number_of_events_in_counting_time_T,i+1:i))
	 * Allan factor =  ------------------------------------------------------------------
	 * 					Mean(Number_of_events_in_counting_time_T,i+1)
	 * 
	 * references:  1. Heart Rate Variability: Measures and Models
			           Malvin C. Teich, Steven B. Lowen, Bradley M. Jost, and Karin Vibe-Rheymer	
			           Nonlinear Biomedical Signal processing, Vol. II, Dynamic Analysis and Modeling, edited by M.Akay, IEEE Press, New York, 2001, ch.6, pp.159-213
			   
	 * 				2. R. G. Turcott and M. C. Teich, “Fractal character of the electrocardiogram: distinguishing
					   heart-failure and normal patients,” Ann. Biomed. Eng., vol. 24, pp. 269–293, 1996.
	 */
	public static final double calculateAllanFactorFromTimes(double [] timeAnnots, double timeInSeconds) throws Exception{
		int windowsCount = (int) (timeAnnots.length/timeInSeconds);
		if (windowsCount==0){
			throw new Exception("Unable to calculate Allan factor for this segment.");
		}
		double [] windowsSize = new double[windowsCount];
		double [] windowsSizeDiffs = new double[windowsCount-1];
		
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
		for (int i=0; i<windowsCount-1; i++){
			windowsSizeDiffs[i] = windowsSize[i+1] - windowsSize[i];
		}
		
		return Math.pow(Statistics.standardDeviation(windowsSizeDiffs),2.0)/Statistics.mean(windowsSize);
	}
	public static final double calculateAllanFactorFromIntervals(double [] intervals, double timeInSeconds) throws Exception{
		// transform into times
		double [] temp = new double[intervals.length+1];
		
		temp[0] = 0.0;
		for (int i=0; i<intervals.length; i++){
			temp[i+1] = temp[i]+intervals[i];
		}
		
		int windowsCount = (int) (temp[temp.length-1]/timeInSeconds);
		if (windowsCount<2){
			throw new Exception("Unable to calculate Allan factor for this segment.");
		}
		double [] windowsSize = new double[windowsCount];
		
		// series is now e.g.: 0, 0.87, 1.56, 2.22, 3.15, 3.9, 4.75, 5.35, 6.22, 7.09, 7.85, 8.67
		int j=1;
		int count = 0;
		for (int i=0; i<temp.length; i++){
			if (temp[i]>j*timeInSeconds){
				if (j-1==windowsSize.length) break;
				windowsSize[j-1]=count;
				j++;
				count=1;
			}
			else {
				count++;
			}
		}
		double sum = 0.0;
		for (int i=0; i<windowsCount-1; i++){
			sum += (windowsSize[i+1] - windowsSize[i])*(windowsSize[i+1] - windowsSize[i]);
		}
		sum /= (windowsCount-1);
		//return Math.pow(Statistics.standardDeviation(windowsSizeDiffs),2.0)/Statistics.mean(windowsSize);
		return sum/Statistics.mean(windowsSize);
	}
	
}
