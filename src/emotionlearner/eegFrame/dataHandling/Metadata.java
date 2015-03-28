/**
 * 
 */
package emotionlearner.eegFrame.dataHandling;

/**
 * @author lsuc
 *
 */
public class Metadata {
	private String versionOfDataFormat;                            
	private String localPatientIdentification;
	private String localRecordingIdentification;
	private String recordingStartDate;
	private String recordingStartTime;
	private long headerLengthInBytes;
	private String reserved;
	private long dataRecordsNum;                
	private double dataRecordDurationInSec; 
	private int signalsNum;
	
	private SignalParameterData[] signalParameters;
	
	
	public String getVersionOfDataFormat() {
		return versionOfDataFormat;
	}

	public void setVersionOfDataFormat(String versionOfDataFormat) {
		this.versionOfDataFormat = versionOfDataFormat;
	}

	public String getLocalPatientIdentification() {
		return localPatientIdentification;
	}

	public void setLocalPatientIdentification(String localPatientIdentification) {
		this.localPatientIdentification = localPatientIdentification;
	}

	public String getLocalRecordingIdentification() {
		return localRecordingIdentification;
	}

	public void setLocalRecordingIdentification(String localRecordingIdentification) {
		this.localRecordingIdentification = localRecordingIdentification;
	}

	public String getRecordingStartDate() {
		return recordingStartDate;
	}

	public void setRecordingStartDate(String recordingStartDate) {
		this.recordingStartDate = recordingStartDate;
	}

	public String getRecordingStartTime() {
		return recordingStartTime;
	}

	public void setRecordingStartTime(String recordingStartTime) {
		this.recordingStartTime = recordingStartTime;
	}

	public long getHeaderLengthInBytes() {
		return headerLengthInBytes;
	}

	public void setHeaderLengthInBytes(long headerLengthInBytes) {
		this.headerLengthInBytes = headerLengthInBytes;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public long getDataRecordsNum() {
		return dataRecordsNum;
	}

	public void setDataRecordsNum(long dataRecordsNum) {
		this.dataRecordsNum = dataRecordsNum;
	}

	public double getDataRecordDurationInSec() {
		return dataRecordDurationInSec;
	}

	public void setDataRecordDurationInSec(double dataRecordDurationInSec) {
		this.dataRecordDurationInSec = dataRecordDurationInSec;
	}

	public int getSignalsNum() {
		return signalsNum;
	}

	public void setSignalsNum(int signalsNum) {
		this.signalsNum = signalsNum;
	}

	public SignalParameterData[] getSignalParameters() {
		return signalParameters;
	}

	public void setSignalParameters(SignalParameterData[] signalParameters) {
		this.signalParameters = signalParameters;
	}

	

	
	

}
