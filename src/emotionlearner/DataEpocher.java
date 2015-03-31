package emotionlearner;

import java.util.ArrayList;

/**
 * epoches raw data when enough time is passed 
 * @author aliyesilyaprak
 *
 */
public class DataEpocher {
	/**
	 * window size of raw data
	 */
	private int epochLength;
	
	/**
	 * raw data 
	 */
	private ArrayList<double[]> allData;
	
	/**
	 * Creates Epocher with raw data window size as length
	 * @param epochLength
	 */
	public DataEpocher(int length){
		this.epochLength = length;
		allData = new ArrayList<double[]>();
	}
	
	/**
	 * returns if enough data is available
	 * @return
	 */
	public boolean readyForEpoch(){
		if(allData.size() >= epochLength)
			return true;
		return false;
	}
	
	/**
	 * adds another row
	 * @param data
	 * @return
	 */
	public boolean addData(double[] data){
		if(allData.size() >= epochLength)
			return false;
		
		allData.add(data);
		return true;
	}
	
	/**
	 * get current epoch data
	 * @return
	 */
	public ArrayList<double[]> getEpoch(){
		return allData;
	}
	
	/**
	 * reset data for reuse
	 */
	public void reset(){
		allData = new ArrayList<double[]>();
	}
	
}
