package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import emotionlearner.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sensormanager.SensorListener;
import sensormanager.SensorListenerEEG;
import sensormanager.SensorListenerGSR;
import shared.FeatureList;
import shared.TimestampedRawData;
import usermanager.User;
import usermanager.UserManager;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffLoader.ArffReader;

public class CaseEEG {
	private String testFolderPath = "test/Test/";
	private String trainFolderPath = "test/Training/";
	private String csvPath = "test/CSV/";
	private String projectCSVPath = "test/ProjectCSV/";
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

	public void runtest6(){
		EmotionEngine engine = EmotionEngine.sharedInstance(null);
		engine.createSensorListener("COM4", SensorListenerEEG.class);
		//engine.createSensorListener("COM3", SensorListenerGSR.class);
	}

	/**
	 * concurrency test
	 */
	public void runtest5() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable<Integer> callable = new Callable<Integer>(){
				public Integer call(){
					System.out.print(csvPath);
					return 5;
				}
		};
		
		Future<Integer> future = executor.submit(callable);
		try {
			System.out.println(future.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				ArrayList<TimestampedRawData> epoch = (ArrayList<TimestampedRawData>)epocher.getEpoch();
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
				ArrayList<TimestampedRawData>epoch = (ArrayList<TimestampedRawData>)epocher.getEpoch();
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
		testLoader.setFile(new File(testFolderPath + testFolderPath + "fcan - frustrated2 - cat mario 2.arff"));
		
		Instances trainInstances = trainLoader.getDataSet();
		trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
		
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
		String[] options ={"-K", "1", "-R", "1.0", "-D", "1"};
		
		String arffFolder = "test/ali/ARFF/";
		
		svmClassifier.train( arffFolder + "alidisgust1out.arff", "weka.classifiers.functions.LibSVM", options);
		svmClassifier.test( arffFolder + "fali disgusting 1 9.arff");

//		svmClassifier.train( trainFolderPath + "cantrainingDisgusting2Out.arff", "weka.classifiers.functions.LibSVM", options);
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
		
//		svmClassifier.trainWithCrossValidation( "test/ali/ARFF/" + "alialltraining.arff", "weka.classifiers.functions.LibSVM", options, 10);
	}

	public void testUserManager() {
		UserManager um = UserManager.getInstance();
		new Scanner(System.in).nextLine();

		User user = um.getCurrentUser();

		boolean oldumu = um.newUser("ayhun", "babako");

		user = um.getCurrentUser();

		oldumu = um.login("ayhun", "babako");

		user = um.getCurrentUser();

	}
	
	public void crossVal(String path, String classPath, String[] options){
		try {
			EEGClassifier svmClassifier = new EEGClassifier();
			svmClassifier.trainWithCrossValidation( path, classPath, options, 10);
		} catch (Exception ex) {
			Logger.getLogger(CaseEEG.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void testOneOut(String path, String classPath, String[] options ){
		try {
			EEGClassifier svmClassifier = new EEGClassifier();
			svmClassifier.train( path+"-out.arff", classPath, options);
			svmClassifier.test( path);
		} catch (Exception ex) {
			Logger.getLogger(CaseEEG.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void testAli(){
		String classifierClassPath = "weka.classifiers.functions.LibSVM";
		String[] options ={"-K", "1", "-R", "1.0", "-D", "1"};
		boolean isCrossVallAll = false;
		int outIndex = 4;
		
		String arffPath = "test/ali/ARFF/";
		String all = "alitrainingall.arff";
		String[] names = new String[]{"fali boring 1 10.arff",
									"fali boring 2 9.arff",
									"fali disgusting 1 9.arff",
									"fali disgusting 2 9.arff",
									"fali disgusting 3 7.arff",
									"fali disgusting 4 7.arff",
									"fali disgusting 6 10.arff",
									"fali peaceful 1 9.arff",
									"fali peaceful 3 8.arff",
									"fali peaceful 4 7.arff",
									"fali peaceful 5 8.arff",
									"fali sexy 1 9.arff",
									"fali sexy 2 8.arff",
									"fali sexy 3 9.arff",
									"fali sexy 4 8.arff"};
		
		if(isCrossVallAll)
			crossVal(arffPath + all, classifierClassPath, options);
		else {
			testOneOut(arffPath + names[outIndex], classifierClassPath, options);
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		
		
		new CaseEEG().testAli();
	}
}
