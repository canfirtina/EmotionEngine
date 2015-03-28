package emotionlearner.eegFrame.features.nonlinear.phaseSpace;

import emotionlearner.eegFrame.statisticMeasure.DistanceMeasure;

//J. P. Zbilut, N. Thomasson, and C. L. Webber, “Recurrence quantification analysis as a tool for nonlinear exploration of nonstationary cardiac signals,” Med. Eng. & Phys., vol. 24, no. 1, pp. 53–60, Jan. 2002.

public class RecurrencePlot {
	private int [][] matrixA;
	private int numberOfOnes = 0;
	private int lengthMin = 2;
	private int [] Nl = null;
	
	public RecurrencePlot(double [] segment, int dimension, int lag, double r){ 
		matrixA = new int[segment.length-lag*(dimension-1)][segment.length-lag*(dimension-1)];
		double [] vectTemp1 = new double[dimension];
		double [] vectTemp2 = new double[dimension];
		int i,j,k;
		
		for (i=0; i<matrixA.length; i++){ 
			for (k=1; k<=dimension; k++){
				vectTemp1[k-1] = segment[i+(k-1)*lag]; 
			}
			for (j=0; j<matrixA[0].length; j++){
				if (i==j){
					matrixA[i][j]=1;
					numberOfOnes++;
				}
				else {
					for (k=1; k<=dimension; k++){
						vectTemp2[k-1] = segment[j+(k-1)*lag]; 
					}
					if (DistanceMeasure.euclideanDistance(vectTemp1, vectTemp2)<=r){
						matrixA[i][j]=1;
						numberOfOnes++;
					}
					else {
						matrixA[i][j]=0;
					}
				}
			}
		}
	}
	//avg number of neighbours for one point
	public double calculateAVGNumOfNeighbours(){
		return (double)(this.numberOfOnes)/matrixA.length;
	}
	
	public double calculateRecurrenceRate(){
		return (double)(this.numberOfOnes)/(matrixA.length*matrixA.length);
	}
	
	//Avg length of diagonal lines
	public double calculateLMean(){
		int i,j;
		int lengthMax = 2;
		int lengthTemp = 0;
		boolean foundLengthMin = false;
		
		////// find Lmax
		for (i=1; i<matrixA.length-1; i++){ 
			lengthTemp = 0;
			for (j=0; j<matrixA.length-i; j++){
				if (matrixA[i+j][j]==1){
					lengthTemp++;
				}
				else {
					if (lengthTemp >= lengthMin){
						foundLengthMin = true;
						if (lengthTemp > lengthMax){
							lengthMax = lengthTemp;
						}
					}
					lengthTemp = 0;
				}
			}
		}
		if (!foundLengthMin){
			return 0.0;
		}
		//////////////////
		
		////// calculate mean
		Nl = new int[lengthMax-lengthMin+1];
		for (i=1; i<matrixA.length-1; i++){
			lengthTemp = 0;
			for (j=0; j<matrixA.length-i; j++){
				if (matrixA[i+j][j]==1){
					lengthTemp++;
				}
				else {
					if (lengthTemp >= lengthMin){
						Nl[lengthTemp-lengthMin]++;
					}
					lengthTemp = 0;
				}
			}
		}
		int sum1 = 0;
		int sum2=0;
		for (i=0; i<Nl.length; i++){
			sum1 += (i+lengthMin)*Nl[i]; 
			sum2 += Nl[i];
		}
		return (double)(sum1)/sum2;
	}
	
	
	public double calculateDET(){
		int i=0, j=0;
		if (Nl==null){
			int lengthMax = 2;
			int lengthTemp = 0;
			boolean foundLengthMin = false;
			
			////// find Lmax
			for (i=1; i<matrixA.length-1; i++){ 
				lengthTemp = 0;
				for (j=0; j<matrixA.length-i; j++){
					if (matrixA[i+j][j]==1){
						lengthTemp++;
					}
					else {
						if (lengthTemp >= lengthMin){
							foundLengthMin = true;
							if (lengthTemp > lengthMax){
								lengthMax = lengthTemp;
							}
						}
						lengthTemp = 0;
					}
				}
			}
			if (!foundLengthMin){
				return 0.0;
			}
			//////////////////
			
			////// calculate mean
			Nl = new int[lengthMax-lengthMin+1];
			for (i=1; i<matrixA.length-1; i++){
				lengthTemp = 0;
				for (j=0; j<matrixA.length-i; j++){
					if (matrixA[i+j][j]==1){
						lengthTemp++;
					}
					else {
						if (lengthTemp >= lengthMin){
							Nl[lengthTemp-lengthMin]++;
						}
						lengthTemp = 0;
					}
				}
			}
		}
		int sum = 0;
		for (i=0; i<Nl.length; i++){
			sum += (i+lengthMin)*Nl[i]; 
		}
		return (double)(sum)/numberOfOnes;
	}
	
	
	
	public double calculateShannonEntropyRecurrence(){
		int i=0, j=0;
		if (Nl==null){
			int lengthMax = 2;
			int lengthTemp = 0;
			boolean foundLengthMin = false;
			
			////// find Lmax
			for (i=1; i<matrixA.length-1; i++){ 
				lengthTemp = 0;
				for (j=0; j<matrixA.length-i; j++){
					if (matrixA[i+j][j]==1){
						lengthTemp++;
					}
					else {
						if (lengthTemp >= lengthMin){
							foundLengthMin = true;
							if (lengthTemp > lengthMax){
								lengthMax = lengthTemp;
							}
						}
						lengthTemp = 0;
					}
				}
			}
			if (!foundLengthMin){
				return 0.0;
			}
			//////////////////
			
			////// calculate mean
			Nl = new int[lengthMax-lengthMin+1];
			for (i=1; i<matrixA.length-1; i++){
				lengthTemp = 0;
				for (j=0; j<matrixA.length-i; j++){
					if (matrixA[i+j][j]==1){
						lengthTemp++;
					}
					else {
						if (lengthTemp >= lengthMin){
							Nl[lengthTemp-lengthMin]++;
						}
						lengthTemp = 0;
					}
				}
			}
		}
		int sum = 0;
		for (i=0; i<Nl.length; i++){
			sum += Nl[i];	//ovdje se izraèuna ukupan broj dijagonalnih linija
		}
		
		double shEn = 0.0;
		for (i=0; i<Nl.length; i++){
			if (Nl[i]!=0){
				shEn += (double)(Nl[i])/sum*Math.log((double)(Nl[i])/sum);
			}
		}
		return -shEn;
	}
	public double calculateLaminarity(){
		int i=0, j=0;
		
		int lengthMax = 2;
		int lengthTemp = 0;
		boolean foundLengthMin = false;
			
		////// find Lmax
		for (i=2; i<matrixA.length; i++){ 
			lengthTemp = 0;
			for (j=0; j<i; j++){
				if (matrixA[i][j]==1){
					lengthTemp++;
				}
				else {
					if (lengthTemp >= lengthMin){
						foundLengthMin = true;
						if (lengthTemp > lengthMax){
							lengthMax = lengthTemp;
						}
					}
					lengthTemp = 0;
				}
			}
		}
		if (!foundLengthMin){
			return 0.0;
		}
		//////////////////
			
		////// calculate mean
		Nl = new int[lengthMax-lengthMin+1];
		for (i=2; i<matrixA.length; i++){
			lengthTemp = 0;
			for (j=0; j<i; j++){
				if (matrixA[i][j]==1){
					lengthTemp++;
				}
				else {
					if (lengthTemp >= lengthMin){
						Nl[lengthTemp-lengthMin]++;
					}
					lengthTemp = 0;
				}
			}
		}
		int sum = 0;
		for (i=0; i<Nl.length; i++){
			sum += (i+lengthMin)*Nl[i];
		}
		return (double)(sum)/numberOfOnes;
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 10;
}
