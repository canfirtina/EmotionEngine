package emotionlearner.eegFrame.features.nonlinear.phaseSpace;

import emotionlearner.eegFrame.statisticMeasure.*;

/**
 * This class calculates the correlation dimension D2 based on the Grassberger-Procaccia method
 * 
 * @author Alan Jovic
 *
 */
public class CorrelationDimension {
	/**P. Grassberger and I. Procaccia, “Measuring the strangeness of strange attractors,” Physica D: Nonlinear Phenomena, vol. 9, no. 1–2, pp. 189–208, Oct. 1983.
	 * 
	 * The static method calculates the correlation dimension based on pre-prepared reconstruction matrix
	 * 
	 * @param reconstructionMatrix Reconstruction matrix containing the points of segment of EEG signal
	 * @param finesse Number of pairs (r,C) used in the calculation of the correlation dimension - it should not be less than 20
	 * @return Correlation dimension D2
	 * @throws Exception An exception is thrown if the attractor has two points or less or the finesse is less than 20.
	 */
	public static double calculateCorrelationDimension(double [][]reconstructionMatrix, int finesse) throws Exception{
		double min, max, distance;
		
		distance = DistanceMeasure.euclideanDistance(reconstructionMatrix[0],reconstructionMatrix[1]);
		min = distance;
		max = distance;
		for (int i=0; i<reconstructionMatrix.length-1; i++){
			for (int j=i+1; j<reconstructionMatrix.length; j++){
				distance = DistanceMeasure.euclideanDistance(reconstructionMatrix[i],reconstructionMatrix[j]);
				if (distance>max) {
					max = distance;
				}
				else if (distance<min) {
					min = distance;
				}
			}
		}
		double period;
		if (min == max) throw new Exception("This is a two-point attractor?");
		else if (finesse < 20) throw new Exception("Precision too low.");
		
		period = (max-min)/finesse;
		
		double sum;
		double [] r = new double[finesse];
		double [] C = new double[finesse];
		for (int i=0; i<finesse; i++){
			r[i] = i*period;
		}
		for (int k=0; k<finesse; k++)
		{
			sum = 0.0;
			for (int i=0; i<reconstructionMatrix.length-1; i++){
				for (int j=i+1; j<reconstructionMatrix.length; j++){
					distance = DistanceMeasure.euclideanDistance(reconstructionMatrix[i],reconstructionMatrix[j]);
					if (r[k]-distance>=-10e-08) sum++; 					
				}
			}

			C[k] = 2*sum/(reconstructionMatrix.length*reconstructionMatrix.length);
		}
		for (int i=0; i<finesse; i++){
			r[i] = Math.log(r[i]);
			C[i] = Math.log(C[i]);
		}
		double [] alfaBeta =  Statistics.fitLineThroughXYPointsCoefficients(r,C,20);
		
		return alfaBeta[0];
	}
	public static final int DEFAULT_FINESSE = 20;
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 150;
}