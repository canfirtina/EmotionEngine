package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import shared.FeatureList;
import shared.TimestampedRawData;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffLoader.ArffReader;
import emotionlearner.CSVParser;
import emotionlearner.DataEpocher;
import emotionlearner.EEGClassifier;
import emotionlearner.FeatureExtractor;
import emotionlearner.FeatureExtractorEEG;
import emotionlearner.LengthBasedDataEpocher;
import emotionlearner.TimeBasedDataEpocher;

public class CaseEEG {
	private String testFolderPath = "Can Labeled ARFF/Test/";
	private String trainFolderPath = "Can Labeled ARFF/Training/";
	private String csvPath = "Can Labeled ARFF/CSV/";
	private String projectCSVPath = "Can Labeled ARFF/ProjectCSV/";
	private String[] csvFileNamesWithoutExtension; 
	
	public CaseEEG(){
		csvFileNamesWithoutExtension = new String[]{"can - disgusting1 - 10",
				"can - disgusting2 - 10",
				"can - disgusting3 - 8",
				"can - disgusting4 - 9",
				"can - disgusting5 - 10",
				"can - frustrated1 - cat mario",
				"can - frustrated2 - cat mario 2",
				"can - peaceful1 - 9",
				"can - peaceful2 - 9",
				"can - peaceful3 - 7",
				"can - peaceful4 - 8",
				"can - sexy1 - 7",
				"can - sexy2 - sonlari 10",
				"can - sexy3 - sonlari 9",
				"can - sexy4 - 8"};
	}
	/**
	 * same with runtest3 but demonstrates time based epocher
	 * @throws Exception
	 */
	public void runtest4() throws Exception{
		String fullPath = csvPath+csvFileNamesWithoutExtension[0]+".csv";
		File outFile = new File(projectCSVPath+"time"+csvFileNamesWithoutExtension[0]+".csv");
		System.out.println(outFile.getAbsolutePath());
		outFile.createNewFile();
		PrintWriter pw = new PrintWriter(outFile.getAbsolutePath());
		
		int frequency = 256;
		double [][]M = CSVParser.read(fullPath);
		//Time based epocher with 4 seconds
		TimeBasedDataEpocher epocher = new TimeBasedDataEpocher(4000);
		FeatureExtractor extractor = new FeatureExtractorEEG();
		
		for(int i=0;i<M.length;++i){
			TimestampedRawData data = new TimestampedRawData(M[i], (long)(i*((double)1000/frequency)) ) ;
			//System.out.println(i + " " + data.getTimestamp().getTime());
			
			//new data is not in the right time interval or last data
			if(!epocher.isNewDataSuitable(data) || i==M.length-1){
				//System.out.println(i + " " + data.getTimestamp().getTime());
				if(i==M.length-1)
					epocher.addData(data);
				ArrayList<TimestampedRawData> epoch = epocher.getEpoch();
				extractor.appendRawData(epoch);
				
				FeatureList list = extractor.getFeatures();
				pw.print(list.get(0));
				for(int j=1;j<list.size();++j)
					pw.print(","+list.get(j));
				pw.println();
				
				extractor.reset();
				epocher.reset();
			}
			
			if(i!=M.length-1)
				epocher.addData(data);
		}
		pw.close();
	}
	
	/**
	 * reads csv file, epochs, extracts features, writes to csv file (without feature selection) (length based epoc)
	 * @throws Exception
	 */
	public void runtest3() throws Exception {
		String fullPath = csvPath+csvFileNamesWithoutExtension[0]+".csv";
		File outFile = new File(projectCSVPath+csvFileNamesWithoutExtension[0]+".csv");
		System.out.println(outFile.getAbsolutePath());
		outFile.createNewFile();
		PrintWriter pw = new PrintWriter(outFile.getAbsolutePath());
		
		
		double [][]M = CSVParser.read(fullPath);
		DataEpocher epocher = new LengthBasedDataEpocher(1024);
		FeatureExtractor extractor = new FeatureExtractorEEG();
		
		for(int i=0;i<M.length;++i){
			epocher.addData(new TimestampedRawData(M[i]));
			
			if(epocher.readyForEpoch()){
				ArrayList<TimestampedRawData>epoch = epocher.getEpoch();
				extractor.appendRawData(epoch);
				
				FeatureList list = extractor.getFeatures();
				pw.print(list.get(0));
				for(int j=1;j<list.size();++j)
					pw.print(","+list.get(j));
				pw.println();

				extractor.reset();
				epocher.reset();
			}
			
		}
		pw.close();
	}
	
	/**
	 * Training a dataset and test instances one by one
	 * @throws Exception
	 */
	public void runtest2() throws Exception{
		ArffLoader trainLoader= new ArffLoader();
		trainLoader.setFile(new File(trainFolderPath + trainFolderPath + "cantrainingFrustrated2Out.arff"));
		ArffLoader testLoader = new ArffLoader();
		testLoader.setFile(new File(testFolderPath  + testFolderPath + "fcan - frustrated2 - cat mario 2.arff"));
		
		Instances trainInstances = trainLoader.getDataSet();
		trainInstances.setClassIndex(trainInstances.numAttributes()-1);
		
		String[] options = {"-K", "1"};
		Classifier svmClassifier = (Classifier)Classifier.forName("weka.classifiers.mi.CitationKNN", options);
		svmClassifier.buildClassifier(trainInstances);
		
		Instances testInstances = testLoader.getStructure();
		testInstances.setClassIndex(testInstances.numAttributes()-1);
		
		Instance instance = null;
		while((instance = testLoader.getNextInstance(testInstances))!=null){
			double prediction = svmClassifier.classifyInstance(instance);
			System.out.println("Prediction:" + prediction + " Actual:" + instance.classValue());
		}
	}
	
	/**
	 * Training a dataset and test instances as a whole and getting accuracy
	 * @throws Exception
	 */
	public void runtest1() throws Exception {
//		String fileName = "can - frustrated2 - cat mario 2.csv";
//		CSVParser.write( "f" + fileName, EEGFeatureExtractor.extractFeatures(CSVParser.read(fileName)));
//		CsvToArff.csv2arff("f" + fileName, "frustrated");
		
		EEGClassifier svmClassifier = new EEGClassifier();
		String[] options = {"-R", "1", "-C", "1", "-H", "1"};
		
		svmClassifier.train( trainFolderPath + "cantrainingDisgusting1Out.arff", "weka.classifiers.mi.CitationKNN", options);
		svmClassifier.test( testFolderPath + "fcan - disgusting1 - 10.arff");

//		svmClassifier.train( trainFolderPath + "cantrainingDisgusting2Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - disgusting2 - 10.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingDisgusting3Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - disgusting3 - 8.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingDisgusting4Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - disgusting4 - 9.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingDisgusting4Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - disgusting5 - 10.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingFrustrated1Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - frustrated1 - cat mario.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingFrustrated2Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - frustrated2 - cat mario 2.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingPeaceful1Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - peaceful1 - 9.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingPeaceful2Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - peaceful2 - 9.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingPeaceful3Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - peaceful3 - 7.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingPeaceful4Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - peaceful4 - 8.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingSexy1Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - sexy1 - 7.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingSexy2Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - sexy2 - sonlari 10.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingSexy3Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - sexy3 - sonlari 9.arff");
//		
//		svmClassifier.train( trainFolderPath + "cantrainingSexy4Out.arff", "weka.classifiers.mi.CitationKNN", options);
//		svmClassifier.test( testFolderPath + "fcan - sexy4 - 8.arff");
		
		svmClassifier.trainWithCrossValidation( trainFolderPath + "cantrainingall.arff", "weka.classifiers.mi.CitationKNN", options, 10);
	}
}
