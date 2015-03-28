/**
 * 
 */
package emotionlearner.eegFrame.dataHandling;

/**
 * @author lsuc
 *
 */
public class DataRecord {

	private double startTimeOffset;
	private short[][] signalData;

	public double getStartTimeOffset() {
		return startTimeOffset;
	}

	public void setStartTimeOffset(double startTimeOffset) {
		this.startTimeOffset = startTimeOffset;
	}

	public short[][] getSignalData() {
		return signalData;
	}

	public void setSignalData(short[][] signalData) {
		this.signalData = signalData;
	}
}
