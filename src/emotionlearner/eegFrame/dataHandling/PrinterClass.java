/**
 * 
 */
package emotionlearner.eegFrame.dataHandling;

/**
 * @author lsuc
 *
 */
public class PrinterClass {

	private InputFile file;
	
	public PrinterClass(InputFile file){
		this.file = file;
	}
	
	public void printHeader(){
		
		System.out.println("Version of data format: " + file.metadata.getVersionOfDataFormat());
		System.out.println("Local patient identification: " + file.metadata.getLocalPatientIdentification());
		System.out.println("Local recording identification: " + file.metadata.getLocalRecordingIdentification());
		System.out.println("Recording start date: " + file.metadata.getRecordingStartDate());
		System.out.println("Recording start time: " + file.metadata.getRecordingStartTime());
		System.out.println("Header length in bytes: " + file.metadata.getHeaderLengthInBytes());
		System.out.println("File type: " + file.metadata.getReserved());
		System.out.println("Number of data records: " + file.metadata.getDataRecordsNum());
		System.out.println("Data record duration: " + file.metadata.getDataRecordDurationInSec());
		System.out.println("Number of signals: " + file.metadata.getSignalsNum());
		
		this.printLabels();
		this.printTransducerTypes();
		this.printPhysDimensions();
		this.printPhysMins();
		this.printPhysMaxs();
		this.printDigMins();
		this.printDigMaxs();
		this.printPrefilterings();
		this.printSamplesNumInDataRecords();
		this.printReservedFields();
				
	}
	
	public void printLabels(){
		System.out.println("Labels:");		
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getLabel());
		
		}
	}
	public void printTransducerTypes(){
		System.out.println("TransducerTypes:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getTransducerType());
		}
	}
	public void printPhysDimensions(){
		System.out.println("PhysDimensions:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getPhysicalDimension());
		}
	}
	public InputFile getFile() {
		return file;
	}

	public void setFile(InputFile file) {
		this.file = file;
	}

	public void printPhysMins(){
		System.out.println("PhysMins:");		
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getPhysicalMin());
		}
	}
	public void printPhysMaxs(){
		System.out.println("PhysMaxs:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getPhysicalMax());
		}
	}
	public void printDigMins(){
		System.out.println("DigMins:");		
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getDigitalMin());
		}
	}
	public void printDigMaxs(){
		System.out.println("DigMaxs:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getDigitalMax());
		}
	}
	public void printPrefilterings(){
		System.out.println("Prefilterings:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getPrefiltering());
		}
	}
	public void printSamplesNumInDataRecords(){
		System.out.println("SamplesNumInDataRecords:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
			System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getSamplesInDataRecordNum());
		}
	}
	public void printReservedFields(){
		System.out.println("Nekakav reserved ne znam kojeg tipa, stavila sam string:");
		for (int i = 0; i < file.metadata.getSignalsNum(); i++){
		System.out.println("Signal "+ i + ": " + file.metadata.getSignalParameters()[i].getReserved());
		}
	}
	
	public void printDataRecord(int index){
		for(int i = 0; i < file.metadata.getSignalsNum(); i++){
			for(int j = 0; j < file.getDataRecords()[index].getSignalData()[i].length; j++){
				if(i < 5){
				System.out.println("Signal " + i + ", sample " + j + ", value: " + file.getDataRecords()[index].getSignalData()[i][j]);
				}
			}
		}
	}
//	public void printConvertedDataRecord(int index){
//		for(int i = 0; i < file.metadata.getSignalsNum(); i++){
//			for(int j = 0; j < file.getDataRecords()[index].getSignalData()[i].length; j++){
//				if(i < 10){
//					System.out.println("Signal " + i + ", sample " + j + ", value: " + file.getDataRecords()[index].getSignalData()[i][j]*calculateSignalCoef(i));
//				}
//			}
//		}
//	}
//	public double calculateSignalCoef(int index){
//		SignalParameterData data = file.metadata.getSignalParameters()[index];
//		double coef = (data.getPhysicalMax()-data.getPhysicalMin()) / (double)(data.getDigitalMax()-data.getDigitalMin());
//		return coef;
//	}
	
	
}
