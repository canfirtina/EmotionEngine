package emotionlearner.eegFrame.features.nonlinear.entropy;
import emotionlearner.eegFrame.statisticMeasure.*;
import java.util.*;

public class SampEn {

	/**
	 * @author Jovic
	 *
	 * @version Ovaj razred raèuna entropiju uzorka putem statièke metode
	 * calculateSampEn, bilo za listu bilo za polje podataka. 
	 * 
	 * J. S. Richman and J. R. Moorman, “Physiological time-series analysis using approximate entropy and sample entropy,” Am. J. Physiol. (Heart Circ. Physiol.), vol. 278, no. 6, pp. 2039–2049, Jun 2000.
	 * 
	*/
	
	
		private static double calculateSampEnSingleStepOfM(double [] series, int m, double r){
			int size = series.length;
			
			double [][] vectors = new double[size-m][m];
			
			for (int i=0; i<vectors.length; i++){
				for (int j=0; j<m; j++){
					vectors[i][j] = series[i+j];
				}
			}
			
			int [] numberNMI = new int[vectors.length];
			double [] numberXMIR = new double[vectors.length];
			
			for (int i=0; i<vectors.length; i++){
				for (int j=0; j<vectors.length; j++){
					
					if (i==j) continue;
					if (DistanceMeasure.maximumAbsoluteDifference(vectors[i],vectors[j])<=r){
						numberNMI[i] ++;
					}
				}
				//usporeðuje se sa svima osim sa sobom, zato vectors.length-1
				numberXMIR[i] = (double)(numberNMI[i])/(vectors.length-1);
			}
			double sum = 0.0;
			for (int i=0; i<vectors.length; i++){
				sum += numberXMIR[i];
			}
			
			return sum/vectors.length;
		}
		/**
		 * Raèuna SampEn za vremensku seriju danu poljem tipa double series.
		 * @param series Vremenski niz podataka dan kao polje tipa double.
		 * @param m Poèetni faktor m za koji se raèuna SampEn.
		 * @param r Definirana udaljenost za SampEn.
		 * @return SampEn.
		 */
		public static double calculateSampEn(double [] series, int m, double r){
			double sampEn;
			
			double Amr = SampEn.calculateSampEnSingleStepOfM(series,m+1,r);
			double Bmr = SampEn.calculateSampEnSingleStepOfM(series,m,r);
			
			if (Math.abs(Amr)<=10e-08 || Math.abs(Bmr)<=10e-08){
				return 0.0;
			}
			sampEn = -Math.log(Amr/Bmr);
			
			return sampEn;
		}
		/**
		 * Raèuna SampEn za vremensku seriju danu listom tipa Double series.
		 * @param series Vremenski niz podataka dan kao lista tipa Double.
		 * @param m Poèetni faktor m za koji se raèuna SampEn.
		 * @param r Definirana udaljenost za SampEn.
		 * @return SampEn.
		 */
		public static double calculateSampEn(List<Double> series, int m, double r){
			double sampEn;
			double [] serie = new double[series.size()];
			for (int i=0; i<series.size(); i++){
				serie[i] = series.get(i).doubleValue();
			}
			
			double Amr = SampEn.calculateSampEnSingleStepOfM(serie,m+1,r);
			double Bmr = SampEn.calculateSampEnSingleStepOfM(serie,m,r);
			
			if (Math.abs(Amr)<=10e-08 || Math.abs(Bmr)<=10e-08){
				return 0.0;
			}
			
			sampEn = -Math.log(Amr/Bmr);
			
			return sampEn;
		}
		
		public static final double [] calculateMaxSampEn(double [] series, int m){
			double rMax = 0.01;
			double sampEnMax = 0.0;
			double sampEn;
			double stdev = Statistics.standardDeviation(series);
			
			for (int i=1; i<=50; i++){
				sampEn = SampEn.calculateSampEn(series, m, 0.01*i*stdev);
				if (sampEn>sampEnMax){
					sampEnMax = sampEn;
					rMax = 0.01*i;
				}
			}
			return new double[]{sampEnMax, rMax};
		}
		
		public static final double [] calculateMaxSampEn(List<Double> series, int m){
			double rMax = 0.01;
			double sampEnMax = 0.0;
			double sampEn;
			double stdev = Statistics.standardDeviation(series);
			
			for (int i=1; i<=50; i++){
				sampEn = SampEn.calculateSampEn(series, m, 0.01*i*stdev);
				if (sampEn>sampEnMax){
					sampEnMax = sampEn;
					rMax = 0.01*i;
				}
			}
			return new double[]{sampEnMax, rMax};
		}
		public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 15;
}
