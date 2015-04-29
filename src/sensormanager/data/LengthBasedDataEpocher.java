package sensormanager.data;

public class LengthBasedDataEpocher extends DataEpocher{
	/**
	 * Creates Epocher with raw data window size as length
	 * @param epochLength
	 */
	public LengthBasedDataEpocher(long length) {
		super(length);
	}
	
	/**
	 * adds another row
	 * @param data
	 * @return
	 */
	@Override
	public boolean addData(TimestampedRawData data){
		if(allData.size() >= constraint)
			return false;
		
		allData.add(data);
		return true;
	}
	
	/**
	 * returns if enough data is available
	 * @return
	 */
	public boolean readyForEpoch(){
		if(allData.size() >= constraint)
			return true;
		return false;
	}
}
