/*
 * Created on 2006.05.23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.statisticMeasure;
import java.util.*;
/**
 * @author Jovic
 *
 * @version Razred koji sadri neke postupke vezane za izraèun statistièkih velièina kao što
 * su srednja vrijednost i standardna devijacija.
 * 
 */
public class Statistics {
	/**
	 * Postupak raèuna aritmetièku srednju vrijednost serije
	 * @param series Niz numerièkih podataka
	 * @return Srednja vrijednost
	 */
	public static double mean(double [] series){
		double mean = 0.0;
		for (int i = 0; i<series.length; i++){
			mean += series[i];
		}
		if(series.length > 0){
			mean /= series.length;
		}
		return mean;
	}
	/**
	 * 
	 * @param series
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	public static double mean(double [] series, int lowerBound, int upperBound){
		double mean = 0.0;
		for (int i = lowerBound; i<upperBound; i++){
			mean += series[i];
		}
		mean /= (upperBound-lowerBound);
		return mean;
	}
	/**
	 * Postupak raèuna standardno odstupanje (devijaciju) aritmetièke sredine
	 * @param series Niz numerièkih podataka
	 * @return Standardno odstupanje
	 */
	public static double standardDeviation(double [] series){
		double sd = 0.0;
		double mean = Statistics.mean(series);
		for (int i=0; i<series.length; i++){
			sd += (series[i]-mean)*(series[i]-mean);
		}
		sd /= (series.length - 1);
		sd = Math.sqrt(sd);
		return sd;
	}
	/**
	 * 
	 * @param series
	 * @param range
	 * @return
	 */
	public static double standardDeviation(double [] series, int lowerBound, int upperBound){
		double sd = 0.0;
		if (upperBound-lowerBound<=1) return 0.0;
		double mean = Statistics.mean(series,lowerBound,upperBound);
		for (int i=lowerBound; i<upperBound; i++){
			sd += (series[i]-mean)*(series[i]-mean);
		}
		sd /= (upperBound - lowerBound - 1);
		sd = Math.sqrt(sd);
		return sd;
	}
	//provjeriti intervale lower i upper bound
	//Dodano-Lea
	//Chai Tong Yuen- Classification of human emotions from EEG using statistical features and neural network
	public static double meanOfAbsoluteValuesOfFirstDifferences(double [] series, int lowerBound, int upperBound){
		double mean = 0.0;
		for (int i = lowerBound; i<upperBound-1; i++){
			double absDiff = Math.abs(series[i+1] - series[i]);
			mean += absDiff;
		}
		mean /= (upperBound-lowerBound-1);
		return mean;
	}
	//Dodano-Lea
	public static double meanOfAbsoluteValuesOfFirstDifferences(double [] series){
		double mean = 0.0;
		for (int i = 0; i<series.length-1; i++){
			double absDiff = Math.abs(series[i+1] - series[i]);
			mean += absDiff;
		}
		mean /= (series.length-1);
		return mean;
	}
	//Dodano-Lea
	public static double meanOfAbsoluteValuesOfSecondDifferences(double [] series, int lowerBound, int upperBound){
		double mean = 0.0;
		for (int i = lowerBound; i<upperBound-2; i++){
			double absDiff = Math.abs(series[i+2] - series[i]);
			mean += absDiff;
		}
		mean /= (upperBound-lowerBound-2);
		return mean;
	}
	//Dodano-Lea
	public static double meanOfAbsoluteValuesOfSecondDifferences(double [] series){
		double mean = 0.0;
		for (int i = 0; i<series.length-2; i++){
			double absDiff = Math.abs(series[i+2] - series[i]);
			mean += absDiff;
		}
		mean /= (series.length-2);
		return mean;
	}
	/**
	 * Postupak raèuna srednju kvadratnu pograšku niza podataka
	 * @param series Niz numerièkih podataka
	 * @return Srednja kvadratna pogreška
	 */
	public static double meanSquareError(double [] series){
		double sd = Statistics.standardDeviation(series);
		double mse = sd / (Math.sqrt(series.length));
		return mse;
	}
	/**
	 * Postupak raèuna aritmetièku srednju vrijednost serije podataka u listi
	 * @param series Lista numerièkih podataka
	 * @return Srednju vrijednost
	 */
	public static double mean(List<Double> series){
		double mean = 0.0;
		for (int i = 0; i<series.size(); i++){
			mean += series.get(i).doubleValue();
		}
		mean /= series.size();
		return mean;
	}
	/**
	 * Postupak raèuna standardno odstupanje (devijaciju) aritmetièke sredine za listu podataka
	 * @param series Lista numerièkih podataka
	 * @return Standardno odstupanje
	 */
	public static double standardDeviation(List<Double> series){
		double sd = 0.0;
		double mean = Statistics.mean(series);
		
		if (series.size()<=1) return 0.0;
		for (int i=0; i<series.size(); i++){
			sd += (series.get(i).doubleValue()-mean)*(series.get(i).doubleValue()-mean);
		}
		sd /= (series.size() - 1);
		sd = Math.sqrt(sd);
		return sd;
	}
	public static double variance(double [] series, double mean){
		double var = 0.0;

		if (series.length<=1) return 0.0;
		for (int i=0; i<series.length; i++){
			var += (series[i]-mean)*(series[i]-mean);
		}
		var /= (series.length - 1);
		
		return var;
	}
	public static double rootMeanSquare(double [] series){
		double sum = 0.0;
		
		for (int i = 0; i<series.length; i++){
			sum += Math.pow(series[i],2);
		}
		sum /= series.length;
		
		return Math.sqrt(sum);
	}
	/**
	 * Postupak raèuna srednju kvadratnu pograšku liste podataka
	 * @param series Lista numerièkih podataka
	 * @return Srednja kvadratna pogreška
	 */
	public static double meanSquareError(List<Double> series){
		double sd = Statistics.standardDeviation(series);
		double mse = sd / (Math.sqrt(series.size()));
		return mse;
	}
	/**
	 * Postupak raèuna najmanju vrijednost u nizu podataka
	 * @param series Niz numerièkih podataka
	 * @return Najmanja vrijednost
	 */
	public static double minimum(double [] series){
		return Statistics.minimum(series,0,series.length,false);
	}
	/**
	 * Postupak raèuna najmanju vrijednost u nizu podataka izmeðu startIndexa i endIndexa
	 * @param series Niz numerièkih podataka
	 * @param startIndex poèetni indeks u nizu (ukljuèen)
	 * @param endIndex završni indeks u nizu (iskljuèen)
	 * @param absolute Ako se raèuna najmanja apsolutna vrijednost, onda treba biti true
	 * @return Najmanja vrijednost
	 */
	public static double minimum(double [] series, int startIndex, int endIndex, boolean absolute){
		double min;
		if (!absolute){
			min = series[startIndex];
			for (int i=startIndex+1; i<endIndex; i++){
				if (series[i] < min) min = series[i];
			}
		}
		else {
			min = Math.abs(series[startIndex]);
			for (int i=startIndex+1; i<endIndex; i++){
				if (Math.abs(series[i]) < min) min = Math.abs(series[i]);
			}
		}
		return min;
	}
	/**
	 * Postupak raèuna najveæu vrijednost u nizu podataka
	 * @param series Niz numerièkih podataka
	 * @return Najveæa vrijednost
	 */
	public static double maximum(double [] series){
		return Statistics.maximum(series,0,series.length,false);
	}
	/**
	 * Postupak raèuna najveæu vrijednost u nizu podataka izmeðu startIndexa i endIndexa
	 * @param series Niz numerièkih podataka
	 * @param startIndex poèetni indeks u nizu (ukljuèen)
	 * @param endIndex završni indeks u nizu (iskljuèen)
	 * @param absolute Ako se raèuna najveæa apsolutna vrijednost, onda treba biti true
	 * @return Najveæa vrijednost
	 */
	public static double maximum(double [] series, int startIndex, int endIndex, boolean absolute){
		double max;
		if (!absolute){
			max = series[startIndex];
			for (int i=startIndex+1; i<endIndex; i++){
				if (series[i] > max) max = series[i];
			}
		}
		else {
			max = Math.abs(series[startIndex]);
			for (int i=startIndex+1; i<endIndex; i++){
				if (Math.abs(series[i]) > max) max = Math.abs(series[i]);
			}
		}
		return max;
	}
	public static int maximum (int [] series, boolean absolute){
		int max = 0;
		if (!absolute){
			max = series[0];
			for (int i=1; i<series.length; i++){
				if (series[i]>max) {
					max = series[i];
				}
			}
		}
		else {
			max = Math.abs(series[0]);
			for (int i=1; i<series.length; i++){
				if (Math.abs(series[i])>max) {
					max = series[i];
				}
			}
		}
		return max;
	}
	/**
	 * Postupak raèuna najmanju vrijednost u listi podataka
	 * @param series Lista numerièkih podataka
	 * @return Najmanja vrijednost
	 */
	public static double minimum(List<Double> series){
		return Statistics.minimum(series,0,series.size(),false);
	}
	/**
	 * Postupak raèuna najmanju vrijednost u listi podataka izmeðu startIndexa i endIndexa
	 * @param series Lista numerièkih podataka
	 * @param startIndex poèetni indeks u listi (ukljuèen)
	 * @param endIndex završni indeks u listi (iskljuèen)
	 * @param absolute Ako se raèuna najmanja apsolutna vrijednost, onda treba biti true
	 * @return Najmanja vrijednost
	 */
	public static double minimum(List<Double> series, int startIndex, int endIndex, boolean absolute){
		double min;
		if (!absolute){
			min = series.get(startIndex).doubleValue();
			for (int i=startIndex+1; i<endIndex; i++){
				if (series.get(i).doubleValue() < min) min = series.get(i).doubleValue();
			}
		}
		else {
			min = Math.abs(series.get(startIndex).doubleValue());
			for (int i=startIndex+1; i<endIndex; i++){
				if (Math.abs(series.get(i).doubleValue()) < min) min = Math.abs(series.get(i).doubleValue());
			}
		}
		return min;
	}
	/**
	 * Postupak raèuna najveæu vrijednost u listi podataka
	 * @param series Lista numerièkih podataka
	 * @return Najveæa vrijednost
	 */
	public static double maximum(List<Double> series){
		return Statistics.maximum(series,0,series.size(),false);
	}
	/**
	 * Postupak raèuna najveæu vrijednost u listi podataka izmeðu startIndexa i endIndexa
	 * @param series Lista numerièkih podataka
	 * @param startIndex poèetni indeks u listi (ukljuèen)
	 * @param endIndex završni indeks u listi (iskljuèen)
	 * @param absolute Ako se raèuna najveæa apsolutna vrijednost, onda treba biti true
	 * @return Najveæa vrijednost
	 */
	public static double maximum(List<Double> series, int startIndex, int endIndex, boolean absolute){
		double max;
		if (!absolute){
			max = series.get(startIndex).doubleValue();
			for (int i=startIndex; i<endIndex; i++){
				if (series.get(i).doubleValue() > max) max = series.get(i).doubleValue();
			}
		}
		else {
			max = Math.abs(series.get(startIndex).doubleValue());
			for (int i=startIndex; i<endIndex; i++){
				if (Math.abs(series.get(i).doubleValue()) > max) max = Math.abs(series.get(i).doubleValue());
			}
		}
		return max;
	}
	/**
	 * Postupak raèuna najmanju razliku n-tog reda za seriju podataka
	 * @param series Niz numerièkih podataka
	 * @param n Razmak izmeðu elemenata za koje se raèuna razlika
	 * @param startIndex poèetni indeks u nizu podataka (ukljuèen)
	 * @param endIndex završni indeks u nizu podataka (iskljuèen)
	 * @param absolute Ako se raèuna najmanja apsolutna razlika, onda treba biti true 
	 * @param order Uzlazni ili silazni poredak, dakle da li je x(1)-x(2) ili x(2)-x(1)
	 * @return Najmanja razlika
	 */
	public static double getDifferenceMinimumNthOrder(double [] series, int n, int startIndex, int endIndex, boolean absolute, int order){
		double min = 0.0;
		if (!absolute){
			if (order == Statistics.ASCENDING){
				min = series[startIndex]-series[startIndex+n];
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series[i] - series[i+n] < min) min = series[i] - series[i+n];
				}
			}
			else {
				min = series[startIndex+n]-series[startIndex];
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series[i+n] - series[i] < min) min = series[i+n] - series[i];
				}
			}
			
		}
		else {	// Ako je apsolutno, redoslijed je nebitan (uzima se silazni)
			min = Math.abs(series[startIndex+n]-series[startIndex]);
			for (int i=startIndex+1; i<endIndex-n-1; i++){
				if (Math.abs(series[i+n] - series[i]) < min) min = Math.abs(series[i+n] - series[i]);
			}
		}
		return min;
	}
	/**
	 * Postupak raèuna najmanju razliku n-tog reda za seriju podataka
	 * @param series Lista numerièkih podataka
	 * @param n Razmak izmeðu elemenata za koje se raèuna razlika
	 * @param startIndex poèetni indeks u listi podataka (ukljuèen)
	 * @param endIndex završni indeks u listi podataka (iskljuèen)
	 * @param absolute Ako se raèuna najmanja apsolutna razlika, onda treba biti true 
	 * @param order Uzlazni ili silazni poredak, dakle da li je x(1)-x(2) ili x(2)-x(1)
	 * @return Najmanja razlika
	 */
	public static double getDifferenceMinimumNthOrder(List<Double> series, int n, int startIndex, int endIndex, boolean absolute, int order){
		double min;
		if (!absolute){
			if (order == Statistics.ASCENDING){
				min = series.get(startIndex).doubleValue()-series.get(startIndex+n).doubleValue();
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series.get(i).doubleValue() - series.get(i+n).doubleValue() < min) min = series.get(i).doubleValue() - series.get(i+n).doubleValue();
				}
			}
			else {
				min = series.get(startIndex+n).doubleValue()-series.get(startIndex).doubleValue();
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series.get(i+n).doubleValue() - series.get(i).doubleValue() < min) min = series.get(i+n).doubleValue() - series.get(i).doubleValue();
				}
			}
		}
		else {	// Ako je apsolutno, redoslijed je nebitan (uzima se silazni)
			min = Math.abs(series.get(startIndex + n).doubleValue()-series.get(startIndex).doubleValue());
			for (int i=startIndex+1; i<endIndex-n-1; i++){
				if (Math.abs(series.get(i+n).doubleValue() - series.get(i).doubleValue()) < min) min = Math.abs(series.get(i+n).doubleValue() - series.get(i).doubleValue());
			}
		}
		return min;
	}
	/**
	 * Postupak raèuna najveæu razliku n-tog reda za seriju podataka
	 * @param series Niz numerièkih podataka
	 * @param n Razmak izmeðu elemenata za koje se raèuna razlika
	 * @param startIndex poèetni indeks u nizu podataka (ukljuèen)
	 * @param endIndex završni indeks u nizu podataka (iskljuèen)
	 * @param absolute Ako se raèuna najmanja apsolutna razlika, onda treba biti true 
	 * @param order Uzlazni ili silazni poredak, dakle da li je x(1)-x(2) ili x(2)-x(1)
	 * @return Najveæa razlika
	 */
	public static double getDifferenceMaximumNthOrder(double [] series, int n, int startIndex, int endIndex, boolean absolute, int order){
		double max;
		if (!absolute){
			if (order == Statistics.ASCENDING){
				max = series[startIndex]-series[startIndex+n];
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series[i] - series[i+n] > max) max = series[i] - series[i+n];
				}
			}
			else {
				max = series[startIndex+n]-series[startIndex];
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series[i+n] - series[i] > max) max = series[i+n] - series[i];
				}
			}
			
		}
		else {	// Ako je apsolutno, redoslijed je nebitan (uzima se silazni)
			max = Math.abs(series[startIndex+n]-series[startIndex]);
			for (int i=startIndex+1; i<endIndex-n-1; i++){
				if (Math.abs(series[i+n] - series[i]) > max) max = Math.abs(series[i+n] - series[i]);
			}
		}
		return max;
	}
	/**
	 * Postupak raèuna najveæu razliku n-tog reda za seriju podataka
	 * @param series Lista numerièkih podataka
	 * @param n Razmak izmeðu elemenata za koje se raèuna razlika
	 * @param startIndex poèetni indeks u listi podataka (ukljuèen)
	 * @param endIndex završni indeks u listi podataka (iskljuèen)
	 * @param absolute Ako se raèuna najmanja apsolutna razlika, onda treba biti true 
	 * @param order Uzlazni ili silazni poredak, dakle da li je x(1)-x(2) ili x(2)-x(1)
	 * @return Najveæa razlika
	 */
	public static double getDifferenceMaximumNthOrder(List<Double> series, int n, int startIndex, int endIndex, boolean absolute, int order){
		double max;
		if (!absolute){
			if (order == Statistics.ASCENDING){
				max = series.get(startIndex).doubleValue()-series.get(startIndex+n).doubleValue();
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series.get(i).doubleValue() - series.get(i+n).doubleValue() > max) max = series.get(i).doubleValue() - series.get(i+n).doubleValue();
				}
			}
			else {
				max = series.get(startIndex+n).doubleValue()-series.get(startIndex).doubleValue();
				for (int i=startIndex+1; i<endIndex-n-1; i++){
					if (series.get(i+n).doubleValue() - series.get(i).doubleValue() > max) max = series.get(i+n).doubleValue() - series.get(i).doubleValue();
				}
			}
		}
		else {	// Ako je apsolutno, redoslijed je nebitan (uzima se silazni)
			max = Math.abs(series.get(startIndex + n).doubleValue()-series.get(startIndex).doubleValue());
			for (int i=startIndex+1; i<endIndex-n-1; i++){
				if (Math.abs(series.get(i+n).doubleValue() - series.get(i).doubleValue()) > max) max = Math.abs(series.get(i+n).doubleValue() - series.get(i).doubleValue());
			}
		}
		return max;
	}
	public static final double [] removeOutliers(double [] series, double factor){
		double [] changedSeries;
		Arrays.sort(series);
		double observingFactor = 0.05; // gleda se prvih 5% serije s oba kraja da mogu biti outliersi
		int observingIndexLow = (int) (series.length * observingFactor);
		int observingIndexHigh = (int) (series.length * (1.0-observingFactor));
		int locationLow = 0, locationHigh=series.length-1;
		double dif1, dif2;
		for (int i=0; i<observingIndexLow; i++){
			dif1 = series[i+1] - series[i];
			dif2 = series[i+2]-series[i+1];
			if (dif1<=10e-04 || dif2<=10e-04 ){
				continue;
			}
			if (dif1>=factor*dif2){
				locationLow = i+1;
				break;
			}
		}
		for (int i=observingIndexHigh; i<series.length-2; i++){
			dif1 = series[i+1] - series[i];
			dif2 = series[i+2]-series[i+1];
			if (dif1<=10e-04 || dif2<=10e-04 ){
				continue;
			}
			if (dif2>=factor*dif1){
				locationHigh = i+1;
				break;
			}
		}
		if (locationLow == 0 && locationHigh == series.length-1) return series;
		else {
			changedSeries = new double[locationHigh-locationLow+1];
			for (int i=locationLow; i<=locationHigh; i++){
				changedSeries[i-locationLow] = series[i];
			}
			return changedSeries;
		}
	}
	/**
	 * Fits a regression line y=alfa*x+beta through a series of points and returns slope and shift
	 * @param xPoints x coordinates of points
	 * @param yPoints y coordinates of points
	 * @param omitFirstAndLastPercent percentage of data to be ommited from regression line, usually due to some calculations' outliers
	 * @return coefficients {alfa,beta}, i.e. slope and displacement of the regression line
	 */
	public static final double [] fitLineThroughXYPointsCoefficients(double [] xPoints, double [] yPoints, int omitFirstAndLastPercent){
		double [] xOperable, yOperable;
		
		if (omitFirstAndLastPercent == 0){
			xOperable = new double[xPoints.length];
			yOperable = new double[xPoints.length];
			for (int i=0; i<xPoints.length; i++){
				xOperable[i] = xPoints[i];
				yOperable[i] = yPoints[i];
			}
		}
		else {
			double prop = 0.01*omitFirstAndLastPercent*xPoints.length;
			xOperable=new double[xPoints.length-2*(int)(prop)];
			yOperable=new double[xPoints.length-2*(int)(prop)];
			for (int i=(int)prop; i<xPoints.length-(int)prop; i++){
				xOperable[i-(int)prop] = xPoints[i];
				yOperable[i-(int)prop] = yPoints[i];
			}
		}
		
		double meanX = Statistics.mean(xOperable);
		double meanY = Statistics.mean(yOperable);
		double [] xy = new double[xOperable.length];
		double [] xSquare = new double[xOperable.length];
		for (int i=0; i<xOperable.length; i++){
			xy[i] = xOperable[i]*yOperable[i];
			xSquare[i] = xOperable[i]*xOperable[i];
		}
		double meanXY = Statistics.mean(xy);
		double meanXSquare = Statistics.mean(xSquare);
		
		double alfa = (meanXY-meanX*meanY)/(meanXSquare-meanX*meanX);
		double beta = meanY - alfa*meanX;
		
		return new double[]{alfa,beta};
	}
	/**
	 * Fits a regression line y=alfa*x+beta through a series of points and returns regression data
	 * @param xPoints x coordinates of points
	 * @param yPoints y coordinates of points
	 * @param omitFirstAndLastPercent percentage of data to be ommited from regression line, usually due to some calculations' outliers
	 * @return coefficients {alfa,beta}, i.e. slope and displacement of the regression line
	 */
	public static final double [] fitLineThroughXYPointsData(double [] xPoints, double [] yPoints, int omitFirstAndLastPercent){
		double [] xOperable, yOperable;
		
		if (omitFirstAndLastPercent == 0){
			xOperable = new double[xPoints.length];
			yOperable = new double[xPoints.length];
			for (int i=0; i<xPoints.length; i++){
				xOperable[i] = xPoints[i];
				yOperable[i] = yPoints[i];
			}
		}
		else {
			double prop = 0.01*omitFirstAndLastPercent*xPoints.length;
			xOperable=new double[xPoints.length-2*(int)(prop)];
			yOperable=new double[xPoints.length-2*(int)(prop)];
			for (int i=(int)prop; i<xPoints.length-(int)prop; i++){
				xOperable[i-(int)prop] = xPoints[i];
				yOperable[i-(int)prop] = yPoints[i];
			}
		}
		
		double meanX = Statistics.mean(xOperable);
		double meanY = Statistics.mean(yOperable);
		double [] xy = new double[xOperable.length];
		double [] xSquare = new double[xOperable.length];
		for (int i=0; i<xOperable.length; i++){
			xy[i] = xOperable[i]*yOperable[i];
			xSquare[i] = xOperable[i]*xOperable[i];
		}
		double meanXY = Statistics.mean(xy);
		double meanXSquare = Statistics.mean(xSquare);
		
		double alfa = (meanXY-meanX*meanY)/(meanXSquare-meanX*meanX);
		double beta = meanY - alfa*meanX;
		
		double [] regressionLinePoints = new double[yPoints.length];
		for (int i=0; i<yPoints.length; i++){
			regressionLinePoints[i] = alfa*xPoints[i] + beta;
		}
		return regressionLinePoints;
	}
	/**
	 * Uzlazni redoslijed
	 */
	public static final int ASCENDING = 0;
	/**
	 * Silazni redoslijed
	 */
	public static final int DESCENDING = 1;
}
