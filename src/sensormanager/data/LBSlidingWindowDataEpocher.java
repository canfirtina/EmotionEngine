package sensormanager.data;

import java.util.LinkedList;
import java.util.List;


public class LBSlidingWindowDataEpocher extends LengthBasedDataEpocher {

    /**
     * sliding interval
     */
    private long interval;

    /**
     * Keeps how many samples acquired since last epoch
     */
    private int samplesSinceLastEpoch;


    /**
     * constraint is used for epoch and default interval is 1000
     * @param constraint
     */
    public LBSlidingWindowDataEpocher(long constraint) {
        super(constraint);
        this.allData = new LinkedList<TimestampedRawData>();
        this.interval = 1000;

    }

    public LBSlidingWindowDataEpocher(long constraint, long period) {
        this(constraint);
        this.interval = period;
    }

    @Override
    public boolean addData(TimestampedRawData rawData) {
        if(allData.size() > interval)
            return false;

        LinkedList<TimestampedRawData> list = this.allData;
        while(!isNewDataSuitable(rawData))
            list.remove();
        list.add(rawData);
        samplesSinceLastEpoch++;

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
        if(allData.size() >= interval)
            return false;
        return true;
    }

    @Override
    public boolean readyForEpoch() {
        if(allData.size() == 0)
            return false;

        LinkedList<TimestampedRawData> list = this.allData;


        if(samplesSinceLastEpoch >= this.constraint)
            return true;
        return false;
    }

    @Override
    public List<TimestampedRawData> getEpoch() {
        samplesSinceLastEpoch = 0;
        return super.getEpoch();
    }

    @Override
    public void reset(){

    }

}
