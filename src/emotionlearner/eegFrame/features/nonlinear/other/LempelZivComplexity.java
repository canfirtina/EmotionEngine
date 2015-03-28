package emotionlearner.eegFrame.features.nonlinear.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class implements Lempel-Ziv complexity measure.
 * @author Administrator
 *
 */
//PROVJERITI JOŠ
//X.-S. Zhang, Y.-S. Zhu, N. V. Thakor, and Z.-Z. Wang, “Detecting Ventricular tachycardia and fibrillation by complexity measure,” IEEE Trans. Biomed. Eng., vol. 46, no. 5, pp. 548–555, May 1999.
public class LempelZivComplexity {
	/**
	 * Calculates Lempel-Ziv complexity of binary sequence (of EEG sample changes)
	 * @param segment
	 * @return
	 */
	public static double calculateLempelZivComplexity(double [] segment){
		int cn = 0;
		
		ArrayList<String> foundSubStrings = new ArrayList<String>();
		boolean zeroFound = false, oneFound = false, zeroZeroFound = false, zeroOneFound = false, oneZeroFound  = false, oneOneFound = false;
		
		StringBuilder st = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		int i;
		Iterator<String> it;
		boolean found = false;
		int startLength;
		
		double [] tempArray = Arrays.copyOf(segment, segment.length);
		Arrays.sort(tempArray);
		double threshold = 0.0;
		
		if (tempArray.length % 2 == 0){ // array of even length, take average of the two medium values as median
			threshold = (tempArray[tempArray.length/2-1]+tempArray[tempArray.length/2])/2;
		}
		else { // array of odd length, take medium value as median
			threshold = tempArray[tempArray.length/2];
		}
		tempArray = null;
		
		for (i=0; i<segment.length; i++){
			if (segment[i]>=threshold){
				st.append("1");
			}
			else {
				st.append("0");
			}
		}
		startLength = st.length();
		for (i=0; i<startLength; i++){
			temp.setLength(0);
			found = false;
			temp.append(st.substring(0, 1));
			st.deleteCharAt(0);
			
			if (!(zeroFound && oneFound)){
				it = foundSubStrings.iterator();
				
				while(it.hasNext()){
					if (it.next().equals(temp.toString())){
						found = true;
						break;
					}
				}
				if (!found){
					foundSubStrings.add(temp.toString());
					if (temp.toString().equals("0")){
						zeroFound = true;
					}
					else {
						oneFound = true;
					}
					cn++;
					continue;
				}
			}
			found = false;
			i++;
			if (i==startLength){
				cn++;
				break;
			}
			temp.append(st.substring(0, 1));
			st.deleteCharAt(0);
			if (!(zeroZeroFound && zeroOneFound && oneZeroFound && oneOneFound)){
				it = foundSubStrings.iterator();
				
				while(it.hasNext()){
					if (it.next().equals(temp.toString())){
						found = true;
						break;
					}
				}
				if (!found){
					foundSubStrings.add(temp.toString());
					if (temp.toString().equals("00")){
						zeroZeroFound = true;
					}
					else if (temp.toString().equals("01")){
						zeroOneFound = true;
					}
					else if (temp.toString().equals("10")){
						oneZeroFound = true;
					}
					else {
						oneOneFound = true;
					}
					cn++;
					continue;
				}
			}
			found = false;
			while (!found){
				found = false;
				i++;
				if (i==startLength){
					cn++;
					found = true;
				}
				else {
					temp.append(st.substring(0,1));
					st.deleteCharAt(0);
					it = foundSubStrings.iterator();
					
					while(it.hasNext()){
						if (it.next().equals(temp.toString())){
							found = true;
							break;
						}
					}
					if (!found){
						foundSubStrings.add(temp.toString());
						cn++;
						found = true;
					}
					else {
						found = false;
					}
				}
			}
		}
		return cn*Math.log10(startLength)/(Math.log10(2.0)*startLength);
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 7;
}
