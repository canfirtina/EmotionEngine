package emotionlearner.eegFrame.dataHandling;

public class SignalParameterData {

	private String label;             
	private String transducerType;         
	private String physicalDimension;      
	private double physicalMin;            
	private double physicalMax;               
	private double digitalMin;                
	private double digitalMax;    
	private String prefiltering;          
	private long samplesInDataRecordNum;    
	private String reserved;
	
	private double physicalSampleMax;
	private double physicalSampleMin;
	private double coef; // (physicalMax - physicalMin) / (digitalMax - digitalMin)
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getTransducerType() {
		return transducerType;
	}
	public void setTransducerType(String transducerType) {
		this.transducerType = transducerType;
	}
	public String getPhysicalDimension() {
		return physicalDimension;
	}
	public void setPhysicalDimension(String physicalDimension) {
		this.physicalDimension = physicalDimension;
	}
	public double getPhysicalMin() {
		return physicalMin;
	}
	public void setPhysicalMin(double physicalMin) {
		this.physicalMin = physicalMin;
	}
	public double getPhysicalMax() {
		return physicalMax;
	}
	public void setPhysicalMax(double physicalMax) {
		this.physicalMax = physicalMax;
	}
	public double getDigitalMin() {
		return digitalMin;
	}
	public void setDigitalMin(double digitalMin) {
		this.digitalMin = digitalMin;
	}
	public double getDigitalMax() {
		return digitalMax;
	}
	public void setDigitalMax(double digitalMax) {
		this.digitalMax = digitalMax;
	}
	public String getPrefiltering() {
		return prefiltering;
	}
	public void setPrefiltering(String prefiltering) {
		this.prefiltering = prefiltering;
	}
	public long getSamplesInDataRecordNum() {
		return samplesInDataRecordNum;
	}
	public void setSamplesInDataRecordNum(long samplesInDataRecordNum) {
		this.samplesInDataRecordNum = samplesInDataRecordNum;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	public double getPhysicalSampleMax() {
		return physicalSampleMax;
	}
	public void setPhysicalSampleMax(double physicalSampleMax) {
		this.physicalSampleMax = physicalSampleMax;
	}
	public double getPhysicalSampleMin() {
		return physicalSampleMin;
	}
	public void setPhysicalSampleMin(double physicalSampleMin) {
		this.physicalSampleMin = physicalSampleMin;
	}
	public double getCoef() {
		return coef;
	}
	public void setCoef(double coef) {
		this.coef = coef;
	}	
}
