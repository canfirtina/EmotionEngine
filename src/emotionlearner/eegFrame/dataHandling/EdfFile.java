/**
 * 
 */
package emotionlearner.eegFrame.dataHandling;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author lsuc
 *
 */
public class EdfFile extends InputFile {
	// TODO calculate appropriate buffer size depending on dataRecord size?
	private final int BUFFER_SIZE_BYTES = 524288; //0.5 megabytes 524288

	private enum HeaderBytesNum {
		
		VERSION_OF_DATA_FORMAT(8),
		LOCAL_PATIENT_IDENTIFICATION(80),
		LOCAL_RECORDING_IDENTIFICATION(80),
		RECORDING_START_DATE(8),
		RECORDING_START_TIME(8),
		HEADER_LENGTH(8),
		RESERVED(44),
		DATA_RECORDS_NUM(8),
		DATA_RECORD_DURATION(8),
		SIGNALS_NUM(4);
		 
		private final int id;
		HeaderBytesNum(int id){
			this.id = id;
		}
		public int getValue(){
			return id; 
		}
	}
	
	private enum SignalParametersBytesNum {
		LABEL(16),
		TRANSDUCER_TYPE(80),
		PHYSICAL_DIMENSION(8),
		PHYSICAL_MIN(8),
		PHYSICAL_MAX(8),
		DIGITAL_MIN(8),
		DIGITAL_MAX(8),
		PREFILTERING(80),
		SAMPLES_NUM_IN_DATA_RECORD(8),
		RESERVED(32);
		
		private final int id;
		SignalParametersBytesNum(int id){
			this.id = id;
		}
		public int getValue(){
			return id; 
		}
	}
	
	// TODO RandomAccessFile reading to check if file is corrupted
	public EdfFile (File selectedFile) throws IOException {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(selectedFile));	
			loadHeader(in);
			loadData(in);
		
		} finally {
			if (in != null) {
				in.close();
			}
		}		
	}
	

	public void loadHeader(BufferedInputStream input) throws IOException{ //throws new FormatCorruptedException
		// TODO check EDF/EDF+ compatibility (max digital sample and samples in dataRecords)
		metadata = new Metadata();
		
		String versionOfDataFormat = readBytesToString(input, HeaderBytesNum.VERSION_OF_DATA_FORMAT.getValue());
		metadata.setVersionOfDataFormat(versionOfDataFormat);
		
		String localPatientId = readBytesToString(input, HeaderBytesNum.LOCAL_PATIENT_IDENTIFICATION.getValue());
		metadata.setLocalPatientIdentification(localPatientId);
		
		String localRecordingId = readBytesToString(input, HeaderBytesNum.LOCAL_RECORDING_IDENTIFICATION.getValue());
		metadata.setLocalRecordingIdentification(localRecordingId);
		
		String startDate = readBytesToString(input, HeaderBytesNum.RECORDING_START_DATE.getValue());
		metadata.setRecordingStartDate(startDate);
		
		String startTime = readBytesToString(input, HeaderBytesNum.RECORDING_START_TIME.getValue());
		metadata.setRecordingStartTime(startTime);
		
		String headerLength = readBytesToString(input, HeaderBytesNum.HEADER_LENGTH.getValue());
		metadata.setHeaderLengthInBytes(Long.parseLong(headerLength.trim()));

		String reserved = readBytesToString(input, HeaderBytesNum.RESERVED.getValue());
		metadata.setReserved(reserved);
		
		String dataRecordsNum = readBytesToString(input, HeaderBytesNum.DATA_RECORDS_NUM.getValue());
		metadata.setDataRecordsNum(Long.parseLong(dataRecordsNum.trim()));
		if(metadata.getDataRecordsNum() > Integer.MAX_VALUE){
			// TODO Handle large files, read as two files, or print message and close
			//throw new TooManyDataRecordsException
		}

		String dataRecordDuration = readBytesToString(input, HeaderBytesNum.DATA_RECORD_DURATION.getValue());
		metadata.setDataRecordDurationInSec(Double.parseDouble(dataRecordDuration.trim()));
		
		String signalNum = readBytesToString(input, HeaderBytesNum.SIGNALS_NUM.getValue());
		metadata.setSignalsNum(Integer.parseInt(signalNum.trim()));

		loadParameters(input);
	}

	
	public String readBytesToString(BufferedInputStream input, int byteNum) throws IOException{
		byte[] byteInput = new byte[byteNum];	
		int numByteRead = input.read(byteInput, 0 , byteNum);
		if (numByteRead < byteNum){
			System.out.println("An error occurred while loading file. The file must be closed.");
		}		
		String dataString = new String(byteInput, "US-ASCII");
		return dataString;
	}
	
	public void loadParameters(BufferedInputStream input) throws IOException{ 	
		
		if(metadata.getSignalsNum() <= 0){
			//throw new ErrorInFormatException
			//exit
		}
		SignalParameterData[] signals = new SignalParameterData[metadata.getSignalsNum()];
		
		loadLabels(input, signals);
		loadTransducerTypes(input, signals);
		loadPhysicalDimensions(input, signals);
		loadPhysicalMins(input, signals);
		loadPhysicalMaxs(input, signals);
		loadDigitalMins(input, signals);
		loadDigitalMaxs(input, signals);
		loadPrefilterings(input, signals);
		loadSamplesInDataRecordNums(input, signals);
		loadReserveds(input, signals);
		
		metadata.setSignalParameters(signals);
	}
	
	public void loadLabels (BufferedInputStream input, SignalParameterData[] signals) throws IOException { 			
		for (int sigNum = 0; sigNum < signals.length; sigNum++){
			SignalParameterData signal = new SignalParameterData();		
			String label = readBytesToString(input, SignalParametersBytesNum.LABEL.getValue());
			signal.setLabel(label);
			signals[sigNum] = signal;
		}
	}		
	
	public void loadTransducerTypes(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String transducer = readBytesToString(input, SignalParametersBytesNum.TRANSDUCER_TYPE.getValue());
			signals[sigNum].setTransducerType(transducer);
		}
	}	
	
	public void loadPhysicalDimensions(BufferedInputStream input, SignalParameterData[] signals) throws IOException{
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String physicalDimension = readBytesToString(input, SignalParametersBytesNum.PHYSICAL_DIMENSION.getValue());
			signals[sigNum].setPhysicalDimension(physicalDimension);
		}
	}
	
	public void loadPhysicalMins(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String physicalMin = readBytesToString(input, SignalParametersBytesNum.PHYSICAL_MIN.getValue());
			signals[sigNum].setPhysicalMin(Double.parseDouble(physicalMin.trim()));
		}
	}	
	
	public void loadPhysicalMaxs(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String physicalMax = readBytesToString(input, SignalParametersBytesNum.PHYSICAL_MAX.getValue());
			signals[sigNum].setPhysicalMax(Double.parseDouble(physicalMax.trim()));
		}
	}	
	
	public void loadDigitalMins(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String digitalMin = readBytesToString(input, SignalParametersBytesNum.DIGITAL_MIN.getValue());
			signals[sigNum].setDigitalMin(Double.parseDouble(digitalMin.trim()));
		}
	}	
	
	public void loadDigitalMaxs(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String digitalMax = readBytesToString(input, SignalParametersBytesNum.DIGITAL_MAX.getValue());
			signals[sigNum].setDigitalMax(Double.parseDouble(digitalMax.trim()));
		}
	}	
	
	public void loadPrefilterings(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String prefiltering = readBytesToString(input, SignalParametersBytesNum.PREFILTERING.getValue());
			signals[sigNum].setPrefiltering(prefiltering);
		}
	}	
	
	public void loadSamplesInDataRecordNums(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String samplesNum = readBytesToString(input, SignalParametersBytesNum.SAMPLES_NUM_IN_DATA_RECORD.getValue());
			signals[sigNum].setSamplesInDataRecordNum(Long.parseLong(samplesNum.trim()));
		}
	}	

	public void loadReserveds(BufferedInputStream input, SignalParameterData[] signals) throws IOException{		
		for (int sigNum = 0; sigNum < signals.length; sigNum++){			
			String reserved = readBytesToString(input, SignalParametersBytesNum.RESERVED.getValue());
			signals[sigNum].setReserved(reserved);
		}
	}	

	public long calculateDataRecordBytesNum(){
		long size = 0;
		for(int sigIndex = 0; sigIndex < metadata.getSignalsNum(); sigIndex++){
			size += metadata.getSignalParameters()[sigIndex].getSamplesInDataRecordNum();
		}
		size *= 2; //2 bytes for every sample
		return size;
	}
	
	public long calculateDataRecordsSize(){
		return metadata.getDataRecordsNum() * calculateDataRecordBytesNum();
	}
	
	public void loadData(BufferedInputStream input) throws IOException{
		// TODO  dataRecordNum compare to metadata.dataRecordsNum value before calling this method
		//calculate correct number of dataRecords if -1
		//throw new TooManyDataRecordsException
		
		int signalsNum = metadata.getSignalsNum();
		
		
		short[][] data;
		dataRecords = new DataRecord[(int) metadata.getDataRecordsNum()]; 
		for(int k = 0; k < dataRecords.length; k++){		
			dataRecords[k] = new DataRecord();
			
			data = new short[signalsNum][];		
			for(int sig = 0; sig < signalsNum; sig++){
				data[sig] = new short [(int) metadata.getSignalParameters()[sig].getSamplesInDataRecordNum()];
			}
			dataRecords[k].setSignalData(data);	
		}
		
		byte[] b = new byte[BUFFER_SIZE_BYTES], pom;
		short[] shorts;
		int i = 0, j = 0, dataRecordsNum = 0, length = 0, index = 0;
		long samplesLeft = metadata.getSignalParameters()[i].getSamplesInDataRecordNum();
	    int numByteRead  = -1;
	    while((numByteRead = input.read(b, 0 , b.length)) > -1) {
	    	    	
	    	pom = new byte[numByteRead]; 
	        System.arraycopy(b, 0, pom, 0, numByteRead); 
	        
	        shorts = convertBytesToShorts(pom);
	        
	        index = 0;
	    	while(true){
	    		
	    		if(samplesLeft > Integer.MAX_VALUE){ //TODO handle long number of samples
	    			length = shorts.length; 
	    		}
	    		else{
	    			length = Math.min(shorts.length - index, (int) samplesLeft);
	    		}
	    		
	    		System.arraycopy(shorts, index, dataRecords[dataRecordsNum].getSignalData()[i], j, length);
//	    		if(dataRecordsNum == 0 && i == 0){
//		        	  for(int bla = 0; bla < length; bla++){
//		  	        	System.out.println("ovo su shorts elementi "+shorts[bla] + " a ovo odgovarajuæi dataRecords elementi "+ dataRecords[dataRecordsNum].getSignalData()[i][bla]);
//		  	      
//		  	        }
//		        }
	    		
	    		
	    		samplesLeft -= length;
	    		j += length;
	    		index += length;
	    	
	    		if(samplesLeft == 0){
	    			j = 0;
	    			if(i < signalsNum-1){
	    				i++;
	    				samplesLeft = metadata.getSignalParameters()[i].getSamplesInDataRecordNum();
	    			}
	    			else{
	    				dataRecordsNum++;
	    				i = 0;
	    				samplesLeft = metadata.getSignalParameters()[i].getSamplesInDataRecordNum();
	    			}    			
	    		}
	    		if(index == shorts.length){
	    			break;
	    		}
	    	}
	    }
	}

	public short[] convertBytesToShorts(byte[] bytes){	
		ByteBuffer bb = ByteBuffer.allocate(2);
		int k = 0;
		byte[] byteInput;
		short[] shorts = new short[bytes.length/2];
		for(int i = 0; i < bytes.length-1; i+=2){						
			byteInput = new byte[2];	
			byteInput[0] = bytes[i];
			byteInput[1] = bytes[i+1];
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(byteInput);		
			shorts[k] = bb.getShort(0);		
			
			bb.clear();
			k++;
		}
		return shorts;
	}
}
