package emotionlearner.eegFrame.features.nonlinear.phaseSpace;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import emotionlearner.eegFrame.statisticMeasure.DistanceMeasure;

public class NonlinearPredictionError {
//G. Sugihara and R. M. May, “Non-linear forecasting as a way of distinguishing chaos from measurement error in time series,” Nature, vol. 344, pp. 734–740, 1990.
	
	public static double calculateNonlinearPredictionError(double [] segment, int dimension, int lag, int T) throws Exception{

//		double learningSet [] = new double[size];
//		double testSet [] = new double[size];
		Map<Integer, Double> map;
		double allPointsMatrix[][] = new double[segment.length-(dimension-1)*lag][dimension];
		double xMatrix[][] = new double[allPointsMatrix.length/2][dimension], yMatrix[][] = new double[allPointsMatrix.length/2][dimension]; 
		double predictedValuesMatrix[][] = new double[xMatrix.length][dimension];

//		for(int i = 0; i < size; i++){
//			learningSet[i] = segment[i]; 
//			testSet[i] = segment[size+i];
//		}
		
		//REKONSTRUKCIJA ATRAKTORA
		for(int g = 0; g < segment.length -(dimension-1)*lag; g++){
			for (int n=1; n<=dimension; n++){
				allPointsMatrix[g][n-1] = segment[g+(n-1)*lag];	
//				yMatrix[g][n-1] = learningSet[g+(n-1)*lag];
//				xMatrix[g][n-1] = testSet[g+(n-1)*lag];
			}
		}
		for(int i = 0; i < xMatrix.length; i++){
			for (int n=1; n<=dimension; n++){
				yMatrix[i][n-1] = allPointsMatrix[i][n-1];
				xMatrix[i][n-1] = allPointsMatrix[ xMatrix.length+i][n-1];
			}
		}
		
		for(int i = 0;  i < xMatrix.length; i++){
			map = new HashMap<Integer, Double>();
			double [][] neighbours = locateNearestNeighbours(xMatrix[i], yMatrix, dimension, map);	
//			System.out.println(map.size());
			double [] weights = calculateWeights(xMatrix[i], neighbours);
			double [] predictedX = predictValue(weights, allPointsMatrix, T, map);
			predictedValuesMatrix[i] = predictedX;
		}
		double error = calculateError(xMatrix, predictedValuesMatrix);
		double randomError = calculateRandomPredictionError(xMatrix, yMatrix, allPointsMatrix, dimension, T);

		//returns normalized error
		if(randomError != 0){
			return error/randomError;
		}
		else{
			return 0;
		}
	}
	
	
	public static double[][] locateNearestNeighbours(double [] point, double yMatrix [][], int dimension, Map<Integer, Double> map){
		double[][] neighbours = new double[dimension+1][dimension];
		
		for(int i = 0; i < yMatrix.length; i++){
			double m = DistanceMeasure.euclideanDistance(point, yMatrix[i]);
			map.put(i, m);
			
		}
		map = MapUtil.sortByValue(map);
		
		Object[] s = map.keySet().toArray();
			
		for(int i = 0; i < dimension + 1; i++){			
			neighbours[i] = yMatrix[Integer.parseInt(s[i].toString())];
		}
		return neighbours;
	}
	
	public static double [] calculateWeights(double [] point, double [][] neighbours){
		
		double [] weights = new double[neighbours.length];
		double sum = 0;		
		for(int i = 0; i < neighbours.length; i++){
			sum += Math.pow(DistanceMeasure.euclideanDistance(point, neighbours[i]), -2);
		}
		if(sum != 0){
			for(int i = 0; i < neighbours.length; i++){
				weights[i] = Math.pow(DistanceMeasure.euclideanDistance(point, neighbours[i]), -2)/sum;
			}
		}
		
		return weights;
	}
	
	public static double[] predictValue(double [] weights, double [][] yFutureMatrix, int T,  Map<Integer, Double> map){
		double [] futureX = new double[yFutureMatrix[0].length];
		Object[] s = map.keySet().toArray();

		for(int i = 0; i < futureX.length; i++){
			for(int j = 0; j < weights.length; j++){
				int k = Integer.parseInt(s[i].toString());
				futureX[i] += weights[j]*yFutureMatrix[k+T][i];
			}
		}
		return futureX;
	}

	public static class MapUtil
	{
	    public static <K, V extends Comparable<? super V>> Map<K, V> 
	        sortByValue( Map<K, V> map )
	    {
	        List<Map.Entry<K, V>> list =
	            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	        {
	            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	            {
	                return (o1.getValue()).compareTo( o2.getValue() );
	            }
	        } );

	        Map<K, V> result = new LinkedHashMap<K, V>();
	        for (Map.Entry<K, V> entry : list)
	        {
	            result.put( entry.getKey(), entry.getValue() );
	        }
	        return result;
	    }
	}
	public static double calculateError(double xMatrix[][], double predictedValuesMatrix[][]){
		if(xMatrix.length > 0){
			double[] error = new double[xMatrix.length];
			for(int i = 0; i < error.length; i++){
				error[i] = DistanceMeasure.euclideanDistance(xMatrix[i], predictedValuesMatrix[i]);
			}
			double nlpe = 0;
			for(int i = 0; i < error.length; i++){
				nlpe += error[i]*error[i];
			}
			nlpe /= error.length;
			return nlpe;
		}
		return 0;		
	}
	public static double calculateRandomPredictionError(double xMatrix[][], double yMatrix[][],  double [][] allPointsMatrix, int dimension, int T){
		Set<Integer> randomSet = new HashSet<Integer>();
		double [][] neighbours = getRandomNeighbours(yMatrix, dimension, randomSet);	
//		System.out.println(map.size());
		Random rand = new Random();
		double [] point = xMatrix[rand.nextInt(xMatrix.length)];			 
		double [] weights = calculateWeights(point, neighbours);
		double [] predictedX = predictRandomValues(weights, allPointsMatrix, T, randomSet);
		return calculateError(xMatrix, predictedX);
	}
	
	public static double[][] getRandomNeighbours(double yMatrix[][], int dimension, Set<Integer> randomSet){
		double [][] neighbours = new double[dimension+1][dimension];
		Random r = new Random();

		for(int i = 0; i < dimension+1; i++){
			int next = r.nextInt(yMatrix.length);
			while(randomSet.contains(next)){
				next = r.nextInt(yMatrix.length);
			}
			neighbours[i] = yMatrix[next];
			randomSet.add(next);			
		}
		return neighbours;
	}
	public static double[] predictRandomValues(double [] weights, double [][] yFutureMatrix, int T, Set<Integer> set){
		double [] futureX = new double[yFutureMatrix[0].length];
		Object[] s = set.toArray();
		for(int i = 0; i < futureX.length; i++){
			for(int j = 0; j < weights.length; j++){
				int k = Integer.parseInt(s[i].toString());
				futureX[i] += weights[j]*yFutureMatrix[k+T][i];
			}
		}
		return futureX;
	}
	public static double calculateError(double xMatrix[][], double predictedPoint[]){
		if(xMatrix.length > 0){
			double[] error = new double[xMatrix.length];
			for(int i = 0; i < error.length; i++){
				error[i] = DistanceMeasure.euclideanDistance(xMatrix[i], predictedPoint);
			}
			double nlpe = 0;
			for(int i = 0; i < error.length; i++){
				nlpe += error[i]*error[i];
			}
			nlpe /= error.length;
			return nlpe;
		}
		return 0;		
	}
			
}
