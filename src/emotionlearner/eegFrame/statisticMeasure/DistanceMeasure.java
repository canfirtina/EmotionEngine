/*
 * Created on 2006.05.23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.statisticMeasure;

/**
 * @author Jovic
 *
 * @version Pomoæni razred koji slui odreðivanju razlièitih mjera udaljenosti izmeðu vektora podataka.
 * Zasad su implementirane metode za apsolutnu maksimalnu razliku te za euklidsku udaljenost.
 * 
 */
public class DistanceMeasure {
	/**
	 * Apsolutna maksimalna razlika izmeðu komponenata dvaju vektora
	 * @param xi prvi vektor
	 * @param xj drugi vektor
	 * @return Maksimalnu razliku
	 */
	public static double maximumAbsoluteDifference(double [] xi, double [] xj){
		double maxDiff = Math.abs(xi[0]-xj[0]);
		double diff;
		
		for (int i=1; i<xi.length; i++){
			diff = Math.abs(xi[i]-xj[i]);
			if (diff>maxDiff) maxDiff = diff; 
		}
		return maxDiff;
	}
	/**
	 * Euklidska udaljenost izmeð dvaju vektora
	 * @param xi prvi vektor
	 * @param xj drugi vektor
	 * @return Euklidska udaljenost
	 */
	public static double euclideanDistance(double [] xi, double [] xj){
		double distance = 0.0;
		
		for (int i=0; i<xi.length; i++){
			distance += (xi[i] - xj[i])*(xi[i] - xj[i]);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	/**
	 * Manhattan distance between two vectors 
	 * @param xi first vektor
	 * @param xj second vektor
	 * @return Manhattan distance
	 */
	public static double manhattanDistance(double [] xi, double [] xj){
		double distance = 0.0;
		
		for (int i=0; i<xi.length; i++){
			distance += Math.abs(xi[i] - xj[i]);
		}
		return distance;
	}
}
