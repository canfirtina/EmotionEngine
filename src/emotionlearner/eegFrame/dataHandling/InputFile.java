/**
 * 
 */
package emotionlearner.eegFrame.dataHandling;

/**
 * @author lsuc
 *
 */
public abstract class InputFile {

	protected String name;
	protected Metadata metadata;
	protected DataRecord[] dataRecords;
	protected Annotation[] annotations;
	
	public long calculateSignalSamplesNum(int signalIndex){
		return metadata.getDataRecordsNum()* metadata.getSignalParameters()[signalIndex].getSamplesInDataRecordNum();
	}
	
	public long calculateSampleFromTime(double time, int signalIndex){
		return Math.round(time * calculateSignalSamplesNum(signalIndex)/calculateDuration(signalIndex));
	}
	
	public double calculateTimeFromSample(double sample, int signalIndex){
		return sample * calculateDuration(signalIndex) / calculateSignalSamplesNum(signalIndex);
	}
	
	public double calculateDuration(int signalIndex){
		return metadata.getDataRecordsNum()* metadata.getDataRecordDurationInSec();
	}
	public double calculateFrequency(int signalIndex){
		return metadata.getSignalParameters()[signalIndex].getSamplesInDataRecordNum()/ metadata.getDataRecordDurationInSec();
	}
	public void findMinAndMaxSample (int signalIndex) {			
		short maxSample, minSample;;
		maxSample = minSample = dataRecords[0].getSignalData()[signalIndex][0];
		
		for(DataRecord dataRecord : dataRecords ){
			for(short s : dataRecord.getSignalData()[signalIndex]){
				short sample = s;
				if (sample < minSample){
					minSample = sample;
				}
				else if (sample > maxSample){
					maxSample = sample;
				}
			}
		}
		metadata.getSignalParameters()[signalIndex].setPhysicalSampleMax(maxSample);
		metadata.getSignalParameters()[signalIndex].setPhysicalSampleMin(minSample);
	}
	
	public void findMinAndMaxSampleAllSignals(){
		for(int i = 0; i < metadata.getSignalsNum(); i++){
			findMinAndMaxSample(i);
		}
	}
	
	public void calculateSignalCoef(int signalIndex){
		SignalParameterData signal = metadata.getSignalParameters()[signalIndex];
		signal.setCoef((signal.getPhysicalMax()-signal.getPhysicalMin())/(signal.getDigitalMax()-signal.getDigitalMin()));
	}
	
	public void calculateSignalCoefAllSignals(){
		for(int i = 0; i < metadata.getSignalsNum(); i++){
			calculateSignalCoef(i);
		}
	}
	public double[] getSamplesFromInterval(int signalIndex, long startSample, long endSample){
		
		SignalParameterData data = metadata.getSignalParameters()[signalIndex];
		double[] samplesFromInterval = new double[(int)(endSample-startSample)];
		int dataRecordIndex = (int) (startSample /data.getSamplesInDataRecordNum());
		int endDataRecordIndex = (int) (endSample / data.getSamplesInDataRecordNum());
		if(endDataRecordIndex >= getDataRecords().length){
			endDataRecordIndex = getDataRecords().length - 1;
		}
		int currentIndex = (int)startSample;
		int indexInStartDataRecord = (int)(startSample % data.getSamplesInDataRecordNum());
		
		int i, j, k = 0;
		double y;
		for(int index = dataRecordIndex; index <= endDataRecordIndex; index++){
			DataRecord dataRecord = getDataRecords()[index];
			if(dataRecord.getStartTimeOffset() == 0){
				short[] samples = dataRecord.getSignalData()[signalIndex];
				
				for(j = indexInStartDataRecord, i = currentIndex; j < samples.length && i < endSample; i++, j++){ 
					y = data.getPhysicalMin() + (samples[j]-data.getDigitalMin())* data.getCoef();  
					samplesFromInterval[k] = y;
					k++;
		  		}   
				indexInStartDataRecord = 0;
				currentIndex = i;	
			}
		}
		return samplesFromInterval;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	public DataRecord[] getDataRecords() {
		return dataRecords;
	}
	public void setDataRecords(DataRecord[] dataRecords) {
		this.dataRecords = dataRecords;
	}
	public Annotation[] getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}
	public String toString(){
		return name;
	}
}
