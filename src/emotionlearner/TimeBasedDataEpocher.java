package emotionlearner;

import shared.TimestampedRawData;

public class TimeBasedDataEpocher extends DataEpocher{

	/**
	 * Constructor with time constraint in milliseconds
	 * @param constraint
	 */
	public TimeBasedDataEpocher(long constraint) {
		super(constraint);
	}

	@Override
	public boolean addData(TimestampedRawData data) {
		if(!isNewDataSuitable(data))
			return false;
		
		allData.add(data);
		return true;
	}
	
	/**
	 * Is time constraint is suitable for new data
	 * @param data
	 * @return
	 */
	public boolean isNewDataSuitable(TimestampedRawData data){
		if(allData.size() == 0)
			return true;
		if(data.getTimestamp().getTime() - allData.get(0).getTimestamp().getTime()>= constraint)
			return false;
		return true;
	}

	/**
	 * Returns false because time based epocher cannot decide without new data
	 */
	public boolean readyForEpoch() {
		return false;
	}

}
