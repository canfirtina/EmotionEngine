package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffLoader.ArffReader;
import emotionlearner.EEGClassifier;

public class CaseEEG {
	String testFolderPath = "test/Test/";
	String trainFolderPath = "test/Training/";
	
	/**
	 * Training a dataset and test instances one by one
	 * @throws Exception
	 */
	public void runtest2() throws Exception{
		System.out.println("TEST");
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
