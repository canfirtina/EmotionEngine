package emotionlearner.eegFrame.features.nonlinear.multiSeries;

import emotionlearner.eegFrame.features.nonlinear.phaseSpace.CorrelationDimension;

public class MutualDimension {

	//Use of non-linear EEG measures to characterize EEG changes during mental activity.
	//Stam CJ, van Woerkom TC, Pritchard WS.
	public static double D2A = 0, D2B = 0, D2C = 0;
	
	public static double calculateMutualDimension(double [] series1, double [] series2, int dimension1, int dimension2, int lag1, int lag2, int finesse){
		//double D2A = 0, D2B = 0, D2C = 0;
		
		double aMatrix[][] = new double[series1.length-(dimension1-1)*lag1][dimension1];
		for (int g=0; g<series1.length-(dimension1-1)*lag1; g++){
			for (int n=1; n<=dimension1; n++){
				aMatrix[g][n-1] = series1[g+(n-1)*lag1];
			}
		}
		double bMatrix[][] = new double[series2.length-(dimension2-1)*lag2][dimension2];
		for (int g=0; g<series2.length-(dimension2-1)*lag2; g++){
			for (int n=1; n<=dimension2; n++){
				bMatrix[g][n-1] = series2[g+(n-1)*lag2];
			}
		}
		try {
			D2A = CorrelationDimension.calculateCorrelationDimension(aMatrix, finesse);
			D2B = CorrelationDimension.calculateCorrelationDimension(bMatrix, finesse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		int lag = Math.max(lag1, lag2);
		int dimension = Math.max(dimension1, dimension2)*2;
		int N = Math.min(series1.length-(dimension-1)*lag, series2.length-(dimension-1)*lag);
		
		
		
		double cMatrix[][] = new double[N][dimension];
		
		for (int g=0; g<N; g++){
			for (int n=1; n<=dimension/2; n++){
				cMatrix[g][n-1] =  series1[g+(n-1)*lag];
			}
			
		}
		for (int g=0; g<N; g++){
			for (int n=dimension/2+1; n<=dimension; n++){
				cMatrix[g][n-1] = series2[g+(n-1)*lag];
			}
		}
		try {
			D2C = CorrelationDimension.calculateCorrelationDimension(cMatrix, finesse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return D2A+D2B-D2C;
	}
	public static final int FINESSE = 20;
	//U STAMOVOM RADU
	public static final int MAXIMUM_EMBEDDING_DIMENSION = 16;	
}
