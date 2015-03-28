package emotionlearner.eegFrame.features.nonlinear.phaseSpace;
import java.util.Random;
import emotionlearner.eegFrame.statisticMeasure.DistanceMeasure;
import emotionlearner.eegFrame.statisticMeasure.Statistics;

/**
 * This class calculates the largest Lyapunov exponent for EEG segment
 * 
 * 
 * @author Alan Jovic
 *
 */
//PROVJERITI JOŠ
public class LyapunovExponent {
	
	/**
	 * Implementation of the reliable and accurate method for estimating Largest Lyapunov exponent, as proposed by Rosenstien 1993, used for small number of points
	 * This method should NOT be used for number of points that exceeds 1000 due to N^2 numeric complexity!
	 * 
	 * M. Rosenstien, J. J. Colins, and C. J. de Luca, “A practical method for calculating largest Lyapunov exponents from small data sets,” Physica D: Nonlinear Phenomena, vol. 65, no. 1–2, pp. 117–134, May 1993.
	 * @param segment EEG segment used in the LLE calculation
	 * @param phaseSpaceDimension The embedding dimension of the phase space
	 * @param trajectoryLength The length of the trajectory between points for which calculation of the distance is pursued. It should not exceed 1/10-th the number of points in the observed segment
	 * @return LLE
	 */
	public static final double calculateLargestLyapunovExponentRosenstien(double [] segment, int phaseSpaceDimension, int trajectoryLength) throws Exception{
		// construct the phase space
		int pointCount = segment.length - phaseSpaceDimension + 1;
		
		if (pointCount<= trajectoryLength+10){
			throw new Exception("Unable to calculate LLE for this segment.");
		}
		
		double [][] points = new double[pointCount][phaseSpaceDimension];
		
		for(int i=0; i<pointCount; i++){
			for (int j=0; j<phaseSpaceDimension; j++){
				points[i][j] = segment[i+j];
			}
		}
		
		// determine average rate of change between two values in the signal
		/*
		double averageChange = 0.0;
		for (int i=0; i<RRintervals.length-1; i++){
			averageChange += Math.abs(RRintervals[i+1]-RRintervals[i]);
		}
		averageChange /= (RRintervals.length-1);*/
		
		
		// determine the closest points
		double minDistance;
		int [] closestPoints = new int[pointCount];
		double distance;
		for (int i=0; i<pointCount; i++){
			minDistance = Double.MAX_VALUE;
			for (int j=0; j<pointCount; j++){
				if (i==j) continue;
				distance = DistanceMeasure.euclideanDistance(points[i], points[j]);
				if (distance<minDistance && distance>1e-03){
					minDistance = distance;
					closestPoints[i] = j;
				}
			}
		}
		// determine average distances between points after "trajectoryLength" changes from starting neighbor points
		double [] averageDistances = new double[trajectoryLength];
		int closestPoint;
		int tempTrajectLength;
		for (int i=0; i<trajectoryLength; i++){
			for (int j=0; j<pointCount-i; j++){
				closestPoint = closestPoints[j]+i;
				if (closestPoint >= pointCount){
					tempTrajectLength = pointCount-1 - closestPoints[j];
				}
				else {
					tempTrajectLength = i;
				}
				averageDistances[i] += DistanceMeasure.euclideanDistance(points[j+tempTrajectLength], points[closestPoints[j]+tempTrajectLength]);
			}
			averageDistances[i] /= (pointCount-i);
		}
		
		double [] trajectoryLengths = new double[trajectoryLength];
		for (int i=0; i<trajectoryLength; i++){
			trajectoryLengths[i] = (double) i;
		}
		
		for (int k=0; k<trajectoryLength; k++){
			trajectoryLengths[k] = Math.log(trajectoryLengths[k]);
			averageDistances[k] = Math.log(averageDistances[k]);
		}
		double [] alfaBeta =  Statistics.fitLineThroughXYPointsCoefficients(trajectoryLengths,averageDistances,20);
		
		// the slope is the Lyapunov exponent
		return alfaBeta[0];
	}
	
	
	
	
	/**
	 * Method calculates the largest Lyapunov exponent (LLE) based on a more stable modification of the Wolf algorithm
	 * that calculates the averaged largest Lyapunov exponent
	 *  
	 * @param segment EEG segment used in the LLE calculation
	 * @param phaseSpaceDimension  The embedding dimension of the phase space
	 * @param trajectoryLength The length of the trajectory, in points
	 * @param averagingCount Number of LLE used for the averaged LLE
	 * @param marginOfDistance The maximum number of times that the next distance is larger than the previous before discarding the point
	 * @return Largest Lyapunov exponent - a positive LLE indicates chaotic behavior
	 */
	public static final double calculateLargestLyapunovExponentStableWolf(double [] segment, int phaseSpaceDimension, int trajectoryLength, int averagingCount, double marginOfDistance){
		double averagedLLE = 0.0;
		Random r = new Random(System.currentTimeMillis());
		int [] startingIndexes = new int[averagingCount];
		int currentIndex = 0;
		int secondIndex = 1;
		int j, k, l;
		double minDistance = 0.0;
		double distance = 0.0;
		double lastDistance;
		double [] iVector = new double[phaseSpaceDimension];
		double [] jVector = new double[phaseSpaceDimension];
		double tempLLE;
		double time;
		
		// select random collection of starting points
		for(int i=0; i<averagingCount; i++){
			startingIndexes[i] = r.nextInt(segment.length-trajectoryLength-phaseSpaceDimension);
		}
		
		for(int i=0; i<averagingCount; i++){
			currentIndex = startingIndexes[i];
			tempLLE = 0.0;
			time = 0.0;
			
			// Determine starting closest point in phase space 
			for (k=0; k<phaseSpaceDimension; k++){
				iVector[k] = segment[currentIndex+k];
				jVector[k] = segment[currentIndex+1+k];
			}
			minDistance = DistanceMeasure.euclideanDistance(iVector, jVector);
			for (k=0; k<segment.length-trajectoryLength-phaseSpaceDimension; k++){
				if (k==currentIndex) continue;
				
				for (j=0; j<phaseSpaceDimension; j++){
					jVector[j] = segment[k+j];
				}
				distance = DistanceMeasure.euclideanDistance(iVector, jVector);
				if (distance <= 1e-06){
					continue;
				}
				else if (distance >= minDistance){
					continue;
				}
				else {
					minDistance = distance;
					secondIndex = k;
				}
			}
			///////
			lastDistance = minDistance;
			
			// continue with the calculation
			for (j=currentIndex+1; j<currentIndex+trajectoryLength; j++){
				// determine points
				for (k=0; k<phaseSpaceDimension; k++){
					iVector[k] = segment[j+k];
					jVector[k] = segment[secondIndex+k];
				}
				// calculate distance between points
				distance = DistanceMeasure.euclideanDistance(iVector, jVector);

				// if distance equals zero, special care should be taken - last distance should remain unchanged (not zero)
				if (distance <= 1e-06){
					tempLLE += 0;
					secondIndex++;
					time += segment[j];
				}
				// if the distance is rather small, use it for the LLE calculation
				if (distance <= marginOfDistance*lastDistance){
					tempLLE += Math.log(distance/lastDistance);
					lastDistance = distance;
					secondIndex++;
					time += segment[j];
				}
				else { // critical part - if the distance is greater than MARGIN_OF_DISTANCE times the last distance, a new point has to be chosen (this should be avoided as often as possible)
					minDistance = distance; 
					for (k=0; k<segment.length-trajectoryLength-phaseSpaceDimension; k++){
						if (k==j) continue;
						
						for (l=0; l<phaseSpaceDimension; l++){
							jVector[l] = segment[k+l];
						}
						distance = DistanceMeasure.euclideanDistance(iVector, jVector);
						if (distance <= 1e-06){
							continue;
						}
						else if (distance >= minDistance){
							continue;
						}
						else {
							minDistance = distance;
							secondIndex = k;
						}
					}
					tempLLE += Math.log(minDistance/lastDistance);
					lastDistance = minDistance;
					secondIndex++;
					time += segment[j];
				}
			}
			averagedLLE += tempLLE/time;
		}
		averagedLLE /= averagingCount;
		
		return averagedLLE;
	}
	/**
	 * Method calculates the largest Lyapunov exponent (LLE) based on a more stable modification of the Wolf algorithm
	 * that calculates the averaged largest Lyapunov exponent.
	 * LLE is calculated by using default parameters.
	 *  
	 * @param segment EEG segment used in the LLE calculation
	 * @return Largest Lyapunov exponent - a positive LLE indicates chaotic behavior
	 */
	public static final double calculateLargestLyapunovExponentStableWolf(double [] segment){
		return LyapunovExponent.calculateLargestLyapunovExponentStableWolf(segment, DEFAULT_PHASE_DIMENSION, DEFAULT_TRAJECTORY_LENGTH, DEFAULT_AVERAGING_COUNT, DEFAULT_MARGIN_OF_DISTANCE);
	}
	public static final int DEFAULT_PHASE_DIMENSION = 2;
	public static final int DEFAULT_TRAJECTORY_LENGTH = 100;
	public static final int SMALL_TRAJECTORY_LENGTH = 20;
	public static final int DEFAULT_AVERAGING_COUNT = 10;
	public static final double DEFAULT_MARGIN_OF_DISTANCE = 5.0;
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 30;
	
}
