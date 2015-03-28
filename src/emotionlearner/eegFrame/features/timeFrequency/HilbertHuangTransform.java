package emotionlearner.eegFrame.features.timeFrequency;

import java.util.ArrayList;
import java.util.Iterator;

import emotionlearner.eegFrame.features.linear.frequencyDomain.operations.Complex;
import emotionlearner.eegFrame.features.linear.frequencyDomain.operations.FFT;
/**
 * An implementation of Hilbert-Huang transform that uses linear interpolation to find intrinsic mode functions
 * @author Alan Jovic
 * 
 * N. E. Huang, Z. Shen, S. R. Long, M. C. Wu, H. H. Shih, Q. Zheng, et al., “The empirical mode decomposition and Hilbert spectrum for nonlinear and non-stationary time series analysis,” in Proc. of the Royal Society A, vol. 454, no. 1971, pp. 903–995, Mar. 1998.
 *
 */
//PROVJERITI JOŠ
public class HilbertHuangTransform {
	private double [] tempBTS, tempBTS2, tempBTS3 = null;
	private ArrayList<double []> intrinsicModeFunctions;
	private ArrayList<Integer > peaks1;
	private double [][] amplitude;
	private double [][] instantaneousFrequency;
	private int[] peaks;
	private int i,j;
	private double sd, sum1, sum2;
	private double [] s1, s2;
	private Iterator<Integer> it;
	private Iterator<double []> it2;
	private double[] tempIntrinsic;
	private Complex[] hilbertTransform;
	private double samplingPeriod;
	
	public HilbertHuangTransform (double []  biomedicalTimeSeries, double samplingPeriod){
		intrinsicModeFunctions = new ArrayList<double []>();
		this.samplingPeriod = samplingPeriod;
		
		tempBTS = new double[biomedicalTimeSeries.length];
		for (i=0; i<tempBTS.length; i++){
			tempBTS[i] = biomedicalTimeSeries[i];
		}
		while (!isMonotonic(tempBTS)){
			tempBTS2 = new double[tempBTS.length];
			tempBTS3 = new double[tempBTS.length];
			tempBTS2 = tempBTS.clone();
			sd = Double.MAX_VALUE;
			while(sd > 0.1 || !isIMF(tempBTS2)){
				s1 = getLinearInterpolation(tempBTS2);
				for (i=0; i<tempBTS2.length; i++){
					tempBTS3[i] = -tempBTS2[i];
				}
				s2 = getLinearInterpolation(tempBTS3);
				for (i=0; i<tempBTS2.length; i++){
					tempBTS3[i] = tempBTS2[i]-(s1[i]-s2[i])/2;
				}
				sum1 = sum2 = 0.0;
				for (i=0; i<tempBTS2.length; i++){
					sum1 += (tempBTS2[i]-tempBTS3[i])*(tempBTS2[i]-tempBTS3[i]);
					sum2 += tempBTS2[i]*tempBTS2[i];
				}
				sd = sum1/sum2;
				for (i=0; i<tempBTS2.length; i++){
					tempBTS2[i] = tempBTS3[i];
				}
			}
			intrinsicModeFunctions.add(tempBTS2);
			for (i=0; i<tempBTS.length; i++){
				tempBTS[i] -= tempBTS2[i];
			}
		}
		intrinsicModeFunctions.add(tempBTS);
		
		amplitude = new double[intrinsicModeFunctions.size()][];
		instantaneousFrequency = new double[intrinsicModeFunctions.size()][];
		double [] tempInstFreq;
		
		it2 = intrinsicModeFunctions.iterator();
		i=-1;
		while (it2.hasNext()){
			i++;
			tempIntrinsic = it2.next();
			/*for (j=0; j<tempIntrinsic.length; j++){
				amplitude[i] += tempIntrinsic[j]*tempIntrinsic[j]; 
			}*/
			hilbertTransform = getHilbertTransform(tempIntrinsic);
			amplitude[i] = new double[hilbertTransform.length];
			instantaneousFrequency[i] = new double[hilbertTransform.length-1];
			for (j=0; j<hilbertTransform.length; j++){
				amplitude[i][j] = hilbertTransform[j].abs();
			}
			tempInstFreq = new double[hilbertTransform.length];
			for (j=0; j<hilbertTransform.length; j++){
				tempInstFreq[j] = hilbertTransform[j].angle(); 
			}
			for (j=0; j<hilbertTransform.length-1; j++){
				instantaneousFrequency[i][j] = (tempInstFreq[j+1]-tempInstFreq[j])/(2*Math.PI)/samplingPeriod;
			}
		}
	}
	private boolean isMonotonic(double[] timeSeries){
		double [] tempTimeSeries = new double[timeSeries.length];
		int u1 = findPeaks(timeSeries).length;
		for (i=0; i<timeSeries.length; i++){
			tempTimeSeries[i] = -timeSeries[i];
		}
		u1 *= findPeaks(tempTimeSeries).length;
		if (u1 > 0) return false;
		return true;
	}
	private double[] getLinearInterpolation(double [] timeSeries){
		int [] tempPeaks = findPeaks(timeSeries);
		int [] tempPeaks2 = new int[tempPeaks.length+2];
		tempPeaks2[0] = 0;
		tempPeaks2[tempPeaks2.length-1] = timeSeries.length-1;
		for (i=1; i<tempPeaks2.length-1; i++){
			tempPeaks2[i] = tempPeaks[i-1];
		}
		int N = timeSeries.length;
		double [] interpolated = new double[timeSeries.length];
		int [] x = new int[timeSeries.length];
		for (i=0; i<timeSeries.length; i++){
			x[i]=i;
		}
		for (i=0; i<N; i++){
			for (j=1; j<tempPeaks2.length; j++){
				if (tempPeaks2[j]==x[i]){
					interpolated[i] = timeSeries[tempPeaks2[j]];
					break;
				}
				if (tempPeaks2[j]>x[i]){
					interpolated[i] = timeSeries[tempPeaks2[j-1]]+(x[i]-tempPeaks2[j-1])*(timeSeries[tempPeaks2[j]]-timeSeries[tempPeaks2[j-1]])/(tempPeaks2[j]-tempPeaks2[j-1]);
					break;
				}
			}
		}
		return interpolated;
	}
	private int[] findPeaks(double [] timeSeries){
		int [] tempPeaks = new int[timeSeries.length-1];
		peaks1 = new ArrayList<Integer>();
		for (i=0; i<tempPeaks.length; i++){
			if (timeSeries[i+1]-timeSeries[i]>1e-05){
				tempPeaks[i]=1;
			}
			else tempPeaks[i]=0;
		}
		// conserve space, do everything with a single array
		for (i=0; i<tempPeaks.length-1; i++){
			if (tempPeaks[i+1]-tempPeaks[i]<0){
				peaks1.add(i);
			}
		}
		peaks = new int[peaks1.size()];
		it = peaks1.iterator();
		i=0;
		while(it.hasNext()){
			peaks[i] = it.next().intValue()+1;
			i++;
		}
		return peaks;
	}
	private boolean isIMF(double [] timeSeries){
		int N = timeSeries.length;
		for (i=0; i<N; i++){
			tempBTS3[i] = -timeSeries[i];
		}
		sum1 = 0.0;
		for (i=0; i<N-1; i++){
			if (timeSeries[i]*timeSeries[i+1]<0){
				sum1++;
			}
		}
		sum2 = findPeaks(timeSeries).length+findPeaks(tempBTS3).length;
		if (Math.abs(sum1-sum2)>1) return false;
		return true;
	}
	private Complex[] getHilbertTransform(double [] timeSeries){
		for (j=0; j<timeSeries.length; j++){
			if (Math.pow(2.0,(double) (j)) > timeSeries.length) break;
		}
		int length = (int) Math.pow(2.0,(double) (j));
		
		Complex[] tempHT = new Complex[length];
		
		for (j=0; j<tempHT.length; j++){
			if (j>=timeSeries.length){
				tempHT[j] = new Complex(0.0, 0.0);
			}
			else tempHT[j] = new Complex(timeSeries[j], 0.0);
		}
		tempHT = FFT.fft(tempHT);
		for (j=0; j<tempHT.length; j++){
			if (j>0 && j<tempHT.length/2){
				tempHT[j] = tempHT[j].times(2.0);
			}
			else if (j>tempHT.length/2){
				tempHT[j] = tempHT[j].times(0.0);
			}
		}
		tempHT = FFT.ifft(tempHT);
		Complex[] tempHT2 = new Complex[timeSeries.length];
		for (j=0; j<timeSeries.length; j++){
			tempHT2[j] = tempHT[j];
		}
		return tempHT2;
		
	}
	public ArrayList<double []> getIntrinsicModeFunctions(){
		return this.intrinsicModeFunctions;
	}
	public double[][] getAmplitudes(){
		return this.amplitude;
	}
	public double[][] getInstantaneousFrequencies(){
		return this.instantaneousFrequency;
	}
	public double getSamplingPeriod(){
		return this.samplingPeriod;
	}
	public double [] getMaximumInstantaneousFrequencies(){
		if (instantaneousFrequency != null) {
			double [] tempMaxFrequencies = new double[this.instantaneousFrequency[0].length];

			for (i=0; i<this.instantaneousFrequency[0].length; i++){
				tempMaxFrequencies[i] = this.instantaneousFrequency[0][i];
				for (j=1; j<this.instantaneousFrequency.length; j++){
					if (instantaneousFrequency[j][i]>tempMaxFrequencies[i]){
						tempMaxFrequencies[i] = instantaneousFrequency[j][i];
					}
				}
			}
			return tempMaxFrequencies;
		}
		else return null;
	}
	public double[] getAmplitudesForMaximumInstantaneousFrequencies(){
		int index = 0;
		if (instantaneousFrequency != null) {
			double [] tempMaxAmplitudes = new double[this.instantaneousFrequency[0].length];

			for (i=0; i<this.instantaneousFrequency[0].length; i++){
				tempMaxAmplitudes[i] = this.instantaneousFrequency[0][i];
				index = 0;
				for (j=1; j<this.instantaneousFrequency.length; j++){
					if (instantaneousFrequency[j][i]>tempMaxAmplitudes[i]){
						tempMaxAmplitudes[i] = instantaneousFrequency[j][i];
						index = j;
					}
				}
				tempMaxAmplitudes[i] = this.amplitude[index][i];
			}
			return tempMaxAmplitudes;
		}
		else return null;
	}
}
