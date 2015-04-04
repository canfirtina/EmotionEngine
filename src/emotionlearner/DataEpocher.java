package emotionlearner;

import java.util.ArrayList;

import shared.TimestampedRawData;

/**
 * epoches raw data when enough X is passed 
 * @author aliyesilyaprak
 *
 */
public abstract class DataEpocher {
	/**
	 * constraint that is used to epoch
	 */
	protected long constraint;
	
	/**
	 * raw data 
	 */
	protected ArrayList<TimestampedRawData> allData;
	
	/**
	 * Creates Epocher with raw data window with constraint
	 * @param epochLength
	 */
	public DataEpocher(long constraint){
		this.constraint = constraint;
		allData = new ArrayList<TimestampedRawData>();
	}
	
	/**
	 * Creates Epocher with raw data window w
	 * @param epochLength
	 */
	public abstract boolean addData(TimestampedRawData rawData);
	
	/**
	 * returns if it is ready to epoch
	 * @return
	 */
	public abstract boolean readyForEpoch();
	
	/**
	 * get current epoch data
	 * @return
	 */
	public ArrayList<TimestampedRawData> getEpoch(){
		return allData;
	}
	
	/**
	 * reset data for reuse
	 */
	public void reset(){
		allData = new ArrayList<TimestampedRawData>();
	}
	
}
