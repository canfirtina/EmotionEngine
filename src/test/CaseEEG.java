package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import shared.FeatureList;
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
	
	/**
	 * reads csv file, epochs, extracts features, writes to csv file (without feature selection)
	 * @throws Exception
	 */
	public void runtest3() throws Exception {
		String fullPath = csvPath+csvFileNamesWithoutExtension[0]+".csv";
		File outFile = new File(projectCSVPath+csvFileNamesWithoutExtension[0]+".csv");
		System.out.println(outFile.getAbsolutePath());
		outFile.createNewFile();
		PrintWriter pw = new PrintWriter(outFile.getAbsolutePath());
		
		
		double [][]M = CSVParser.read(fullPath);
		DataEpocher epocher = new DataEpocher(1024);
		FeatureExtractor extractor = new FeatureExtractorEEG();
		
		for(int i=0;i<M.length;++i){
			epocher.addData(M[i]);
			
			if(epocher.readyForEpoch()){
				ArrayList<double []>epoch = epocher.getEpoch();
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
		trainLoader.setFile(new File(trainFolderPath + "cantrainingFrustrated2Out.arff"));
		ArffLoader testLoader = new ArffLoader();
		testLoader.setFile(new File(testFolderPath  + "fcan - frustrated2 - cat mario 2.arff"));
		
		Instances trainInstances = trainLoader.getDataSet();
		trainInstances.setClassIndex(trainInstances.numAttributes()-1);
		
		String[] options = {"-K", "1"};
		Classifier svmClassifier = (Classifier)Classifier.forName("weka.classifiers.functions.LibSVM", options);
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
		String[] options = {"-K", "1"};
		
		
		
//		svmClassifier.train( "cantrainingDisgusting1Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - disgusting1 - 10.arff");
		
//		svmClassifier.train( "cantrainingDisgusting2Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - disgusting2 - 10.arff");
//		
//		svmClassifier.train( "cantrainingDisgusting3Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - disgusting3 - 8.arff");
//		
//		svmClassifier.train( "cantrainingDisgusting4Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - disgusting4 - 9.arff");
//		
//		svmClassifier.train( "cantrainingDisgusting4Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - disgusting5 - 10.arff");
//		
//		svmClassifier.train( "cantrainingFrustrated1Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - frustrated1 - cat mario.arff");
//		
		svmClassifier.train( trainFolderPath + "cantrainingFrustrated2Out.arff", "weka.classifiers.functions.LibSVM", options);
		svmClassifier.test( testFolderPath+"fcan - frustrated2 - cat mario 2.arff");
//		
//		svmClassifier.train( "cantrainingPeaceful1Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - peaceful1 - 9.arff");
//		
//		svmClassifier.train( "cantrainingPeaceful2Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - peaceful2 - 9.arff");
//		
//		svmClassifier.train( "cantrainingPeaceful3Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - peaceful3 - 7.arff");
//		
		
//		svmClassifier.train(  trainFolderPath + "cantrainingPeaceful4Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( testFolderPath + "fcan - peaceful4 - 8.arff");
//		
//		svmClassifier.train( "cantrainingSexy1Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - sexy1 - 7.arff");
//		
//		svmClassifier.train( "cantrainingSexy2Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - sexy2 - sonlari 10.arff");
//		
//		svmClassifier.train( "cantrainingSexy3Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - sexy3 - sonlari 9.arff");
//		
//		svmClassifier.train( "cantrainingSexy4Out.arff", "weka.classifiers.functions.LibSVM", options);
//		svmClassifier.test( "fcan - sexy4 - 8.arff");
		
//		svmClassifier.trainWithCrossValidation( "cantrainingall.arff", "weka.classifiers.functions.LibSVM", options, 10);
	}
}
