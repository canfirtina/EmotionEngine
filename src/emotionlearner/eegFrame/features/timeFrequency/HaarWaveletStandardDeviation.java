package emotionlearner.eegFrame.features.timeFrequency;

import emotionlearner.eegFrame.statisticMeasure.Statistics;
/**

 * 
 * @author lsuc
 *
 */
public class HaarWaveletStandardDeviation {
	public static final double calculateHaarWaveletSTDEV(double [] segment){
		
		double interval =(int)Math.log(segment.length);
//		double interval =(int)Math.log(segment.length)/ Math.log(2) ;
		int elementNum = (int) Math.pow(2, (int)interval);
		int elements = elementNum/2;
		double[] waves = new double[elementNum];
		
		
		for (int i=0; i < elementNum; i++){
			waves[i] = segment[i];
		}
//		System.out.println("duljina segmenta je " + segment.length);
//		System.out.println("log po bazi 2, tj. interval je "+ Math.log(segment.length));
//		System.out.println("izracunat broj elemenata je " + elementNum);
//		for (int i=0; i < elementNum; i++){
//			System.out.print(waves[i] + " ");
//		}
//		System.out.println();
//		
		double[] pom = new double[elementNum];
		for(int j=0; j < interval; j++){			
			for (int i=0; i < elementNum; i++){
				pom[i] = waves[i];
			}
			for (int i=0; i < elements; i++){
				waves[i] =  waves[i*2] + waves[i*2+1];
				waves[i] /= 2;
				pom[i + elements] = waves[i] - waves[i*2+1];
			}
			
			for(int i=elements; i < elements*2; i++){
				waves[i] = pom[i];
			}
//			for (int i=0; i < elementNum; i++){
//				System.out.print(waves[i] + " ");
//			}
//			System.out.println();
			elements /= 2;
		}
		
		
		return Statistics.standardDeviation(waves);
	}
}