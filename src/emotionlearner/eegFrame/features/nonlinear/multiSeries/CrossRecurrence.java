package emotionlearner.eegFrame.features.nonlinear.multiSeries;

import emotionlearner.eegFrame.statisticMeasure.DistanceMeasure;

/** 
 *In order to not under-embed one system, the embedding dimension should be chosen from the system with the higher dimension. 
 *On the other hand, you should also visually inspect the CRP, in order to check the structures. 
 *The optimal CRP should have many diagonal lines and less single dots. The diagonal lines should not be sharply interrupted. 
 *Sometimes it is neccessary to change the embedding parameters in order to get better CRPs.
 
 */
public class CrossRecurrence {
	private int [][] matrixA;
	private int numberOfOnes = 0;
	private int lengthMin = 2;
	private int [] Nl = null;
	
	public CrossRecurrence(double [] segment, double [] probe, int dimension, int lag, double r){
		
		matrixA = new int[segment.length-lag*(dimension-1)][probe.length-lag*(dimension-1)];
		double [] vectTemp1 = new double[dimension];
		double [] vectTemp2 = new double[dimension];
		int i,j,k;
		
		for (i=0; i<matrixA.length; i++){	
			for (k=1; k<=dimension; k++){
				vectTemp1[k-1] = segment[i+(k-1)*lag];
			}
			for(j=0; j<matrixA[0].length; j++){				
				for (k=1; k<=dimension; k++){					 
					vectTemp2[k-1] = probe[j+(k-1)*lag];
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
		
		public double calculateCC(){
			return (double)(this.numberOfOnes)/(matrixA.length*matrixA[0].length);
		}
		
		//Prosjeèna duljina dijagonalnih linija
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
			sum *= 2; //mnoim s 2 jer je izraèunato samo do dijagonale, treba i iznad dijagonale uraèunati
		
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
			sum *= 2;
			//mnoim s 2 jer je izraèunato samo do dijagonale, treba i iznad dijagonale uraèunati
			//zato i svaki N1[i] mnoim s 2
			double shEn = 0.0;
			for (i=0; i<Nl.length; i++){
				if (Nl[i]!=0){
					shEn += (double)(2*Nl[i])/sum*Math.log((double)(2*Nl[i])/sum);
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
			sum *= 2;
			return (double)(sum)/numberOfOnes;
		}
	//PROVJERITI ZA EEG
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 10;
}
