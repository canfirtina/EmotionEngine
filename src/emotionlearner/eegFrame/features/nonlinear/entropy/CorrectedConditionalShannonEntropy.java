package emotionlearner.eegFrame.features.nonlinear.entropy;

import emotionlearner.eegFrame.statisticMeasure.Statistics;
//A. Porta, G. Baselli, D. Liberati, N. Montano, C. Cogliati, T. Gnecchi-Ruscone, et al., “Measuring regularity by means of a corrected conditional entropy in sympathetic outflow,” Biol. Cybern., vol. 78, no. 1, pp. 71–78, 1998.
//PROVJERITI JOŠ
public class CorrectedConditionalShannonEntropy {
	
	public static double calculateCorrectedConditionalShannonEntropy(double [] series, int dim, int division){
		double max = Statistics.maximum(series);
		double min = Statistics.minimum(series);
		double boxSize = (max-min)/division;
		
		int sum;
		// find distribution of points within dim dimensional boxes of size boxSize
		int[] boxes = new int[(int)Math.pow((double)division, (double)dim)]; 
		int[] locator = new int[dim];
		for (int i=0; i<series.length-dim; i++){
			for (int j=0; j<dim; j++){
				locator[j]=(int)Math.ceil((series[i+j]-min)/boxSize)-1;
				if (locator[j]<0){
					locator[j]=0;
				}
			}
			sum = 0;
			for (int j=0, a=dim-1; j<dim; j++, a--){
				sum += (int)locator[j]*Math.pow(division,a); 
			}
			boxes[sum]++;
		}
		double sumEntropy = 0.0;
		double entropy = 0.0;
		// divide and logarithmize in order to obtain Shannon entropy, find percent of those boxes with only one point in them, disregard empty boxes
		double percent = 0.0;
		double log2 = Math.log10(2.0);
		for (int i=0; i<boxes.length; i++){
			if (boxes[i]>1){
				sumEntropy += (double)(boxes[i])/(series.length-dim+1)*Math.log10((double)(boxes[i])/(series.length-dim+1))/log2;
			}
			else if (boxes[i] == 1){
				percent ++;
			}
		}
		entropy = -sumEntropy;
		percent /= (series.length-dim+1);
		
		// repeat for one dimension less
		boxes = new int[(int)Math.pow((double)division, (double)(dim-1))]; 
		locator = new int[dim-1];
		for (int i=0; i<series.length-(dim-1); i++){
			for (int j=0; j<dim-1; j++){
				locator[j]=(int)Math.ceil((series[i+j]-min)/boxSize)-1;
				if (locator[j]<0){
					locator[j]=0;
				}
			}
			sum = 0;
			for (int j=0, a=dim-2; j<dim-1; j++, a--){
				sum += (int)locator[j]*Math.pow(division,a); 
			}
			boxes[sum]++;
		}
		sumEntropy = 0.0;
		// divide and logarithmize in order to obtain Shannon entropy
		for (int i=0; i<boxes.length; i++){
			if (boxes[i]>1){
				sumEntropy += (double)(boxes[i])/(series.length-dim+1)*Math.log10((double)(boxes[i])/(series.length-dim+1))/log2;
			}
		}
		
		
		entropy = entropy - (-sumEntropy);
		sum = 0;
		// repeat for one-dimensional case
		boxes = new int[division];
		for (int i=0; i<series.length-(dim-1); i++){
			sum = (int)Math.ceil((series[i]-min)/boxSize)-1;
			if (sum<0){
				sum = 0;
			}
			boxes[sum]++;
		}
		sumEntropy = 0.0;
		// divide and logarithmize in order to obtain Shannon entropy
		for (int i=0; i<boxes.length; i++){
			if (boxes[i]>1){
				sumEntropy += (double)(boxes[i])/(series.length-dim+1)*Math.log10((double)(boxes[i])/(series.length-dim+1))/log2;
			}
		}
		return (entropy + percent*(-sumEntropy));		
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 20;
}

