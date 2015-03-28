package emotionlearner.eegFrame.features.nonlinear.multiSeries;

import java.util.ArrayList;

import emotionlearner.eegFrame.statisticMeasure.DistanceMeasure;

//Synchronization likelihood: an unbiased measure of generalized synchronization in multivariate data sets
//C.J. Stam,B.W. van Dijk

public class SynchronizationLikelihood {

	public static double[][] calculateSynchronizationLikelihood(ArrayList<double[]> signalList, int startSample, int seriesLength, int dimension, int lag, int recNum, double roRef, double epsMax){
		ArrayList<double[][]> reconstructedSignals = new ArrayList<double[][]>();
		double W1 = 2*lag*(dimension-1);
		double W2 = recNum/roRef + W1 - 1;		
		
		for(int i = 0; i < signalList.size(); i++){
			
			double [][] reconstructionMatrix = new double[seriesLength-(dimension-1)*lag][dimension];
			
			for (int g=0; g<seriesLength-(dimension-1)*lag; g++){
				for (int n=1; n<=dimension; n++){
					reconstructionMatrix[g][n-1] = signalList.get(i)[g+(n-1)*lag];
				}
			}
			reconstructedSignals.add(reconstructionMatrix);
		}
	
		double epsCrit[][] = new double[reconstructedSignals.size()][];
		
		for(int k = 0; k < reconstructedSignals.size(); k++){
			//toèke atraktora k-te vremenske serije			
			double [][] kAttractorPoints = reconstructedSignals.get(k);
			//System.out.println(kAttractorPoints.length);
			epsCrit[k] = new double[kAttractorPoints.length]; 
			
			//i-ta toèka atraktora k-te vremenske serije
			for(int i = 0; i < kAttractorPoints.length; i++){
				for(double eps = epsMax; eps > 0.0001; eps /= 2){
					double P = probFunc(kAttractorPoints, W1, W2, eps, i);
				
					epsCrit[k][i] = epsMax;
					// sada za svaki k i svaki i treba naæi "critical distance eps" tako da je ProbFunc = pRef
					if(P - roRef >= 1e-06 && P < 2*roRef ){ //IDE LI TU <= ili ==
						epsCrit[k][i] = eps;
						break;
					}	
				
				}			
			}
		}
//		Vector<Integer> jIndices = new Vector<Integer>();
//		for(int i = 0; i < epsCrit[0].length; i++){
//			
//			for(int j = 0; j < epsCrit[0].length; j++){				
//				if(Math.abs(i-j) > W1 && Math.abs(i-j) < W2 && !jIndices.contains(j)){
//					jIndices.add(j);
//				}						
//			}
//		}
		double[][] H = new double[epsCrit[0].length][epsCrit[0].length]; //RIJETKA MATRICA
		for(int i = 0; i < epsCrit[0].length; i++){		
			for(int j = 0; j < epsCrit[0].length; j++){
				if(Math.abs(i-j) > W1 && Math.abs(i-j) < W2){
					double dist = 0;
					double sum = 0;
					for(int k = 0; k < epsCrit.length; k++){	
						double [][] kAttractorPoints = reconstructedSignals.get(k);				
					
						dist = DistanceMeasure.euclideanDistance(kAttractorPoints[i], kAttractorPoints[j]);
						if(epsCrit[k][i] - dist >= 1e-06){
							sum += 1;
						}
					}
					H[i][j] = sum;
				}
				
			}
		
		}
					
//		double[][] H = new double[epsCrit[0].length][jIndices.size()];
//		for(int i = 0; i < epsCrit[0].length; i++){		
//			for(int j = 0; j < jIndices.size(); j++){
//				double dist = 0;
//				double sum = 0;
//				for(int k = 0; k < epsCrit.length; k++){	
//					double [][] kAttractorPoints = reconstructedSignals.get(k);				
//				
//					dist = DistanceMeasure.euclideanDistance(kAttractorPoints[i], kAttractorPoints[jIndices.get(j)]);
//					if(epsCrit[k][i] - dist >= 1e-06){
//						sum += 1;
//					}
//				}
//				H[i][j] = sum;
//			}
//		
//		}
		double[][][] Skij = new double[epsCrit.length][epsCrit[0].length][epsCrit[0].length];
		for(int k = 0; k < epsCrit.length; k++){
			double [][] kAttractorPoints = reconstructedSignals.get(k);	
			for(int i = 0; i < epsCrit[0].length; i++){		
				for(int j = 0; j < epsCrit[0].length; j++){
					if(Math.abs(i-j) > W1 && Math.abs(i-j) < W2){	
						double dist = DistanceMeasure.euclideanDistance(kAttractorPoints[i], kAttractorPoints[j]);
						if(epsCrit[k][i] - dist >= 1e-06){
							Skij[k][i][j] = (H[i][j] - 1) / (epsCrit.length-1);
						}
						else{
							Skij[k][i][j] = 0; 
						}
					}
				}				
			}		
		}
		
//		double[][][] Skij = new double[epsCrit.length][epsCrit[0].length][jIndices.size()];
//		for(int k = 0; k < epsCrit.length; k++){
//			double [][] kAttractorPoints = reconstructedSignals.get(k);	
//			for(int i = 0; i < epsCrit[0].length; i++){		
//				for(int j = 0; j < jIndices.size(); j++){
//									
//					double dist = DistanceMeasure.euclideanDistance(kAttractorPoints[i], kAttractorPoints[jIndices.get(j)]);
//					if(epsCrit[k][i] - dist >= 1e-06){
//						Skij[k][i][j] = (H[i][j] - 1) / (epsCrit.length-1);
//					}
//					else{
//						Skij[k][i][j] = 0; 
//					}
//				}				
//			}		
//		}
		double[][] Ski = new double [epsCrit.length][epsCrit[0].length];
		for(int k = 0; k < epsCrit.length; k++){
			for(int i = 0; i < epsCrit[0].length; i++){		
				for(int j = 0; j < epsCrit[0].length; j++){
					Ski[k][i] += Skij[k][i][j];
				}
				Ski[k][i] /= 2*(W2-W1);
			}
		}
//		double[][] Ski = new double [epsCrit.length][epsCrit[0].length];
//		for(int k = 0; k < epsCrit.length; k++){
//			for(int i = 0; i < epsCrit[0].length; i++){		
//				for(int j = 0; j < jIndices.size(); j++){
//					Ski[k][i] += Skij[k][i][j];
//				}
//				Ski[k][i] /= 2*(W2-W1);
//			}
//		}
		return Ski;
		
	}
	
	static double probFunc(double [][] points, double W1, double W2, double eps, int i){
		double sum = 0.0;
		double dist = 0;
		for(int j = 0; j < points.length; j++){
	
			if(Math.abs(i-j) > W1 && Math.abs(i-j) < W2 ){
				dist = DistanceMeasure.euclideanDistance(points[i], points[j]);
				if(eps - dist >= 1e-06){
					sum += 1;
				}
			}
		}
		return  sum / (2 * (W2 - W1));						
    }
}	