package emotionlearner;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * @author can
 *
 */

public class EEGClassifier {
	
	Instances trainInstance;
	Instances testInstance;
	Classifier model;
	int seed = 1000;
	
	public EEGClassifier(){
		
		trainInstance = null;
		testInstance = null;
		model = null;
	}
	
	public void saveModel(Classifier c, String name, File path) throws Exception {

	    ObjectOutputStream oos = null;
	    try {
	        oos = new ObjectOutputStream(new FileOutputStream(name + ".model"));

	    } catch (FileNotFoundException e1) {
	        e1.printStackTrace();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	    
	    oos.writeObject(c);
	    oos.flush();
	    oos.close();
	}
	
	public Classifier loadModel(String name, File path) throws Exception {
		
		Classifier classifier;
		
	    FileInputStream fis = new FileInputStream(name + ".model");
	    ObjectInputStream ois = new ObjectInputStream(fis);

	    classifier = (Classifier) ois.readObject();
	    ois.close();

	    return classifier;
	}
	
	public void train( String traininDataLocation, String classifier, String[] options) throws Exception{
		DataSource trainSource = new DataSource( traininDataLocation);
		trainInstance = trainSource.getDataSet();
	    
	    if (trainInstance.classIndex() == -1)
	    	trainInstance.setClassIndex(trainInstance.numAttributes() - 1);
	    
	    model = Classifier.forName( classifier, options);
	    Random rand = new Random( seed);
	    trainInstance.randomize( rand);
	    
	    model.buildClassifier( trainInstance);
	}
	
	public void trainWithCrossValidation( String traininDataLocation, String classifier, String[] options, int folds) throws Exception{
		DataSource trainSource = new DataSource( traininDataLocation);
		trainInstance = trainSource.getDataSet();
	    
	    if (trainInstance.classIndex() == -1)
	    	trainInstance.setClassIndex(trainInstance.numAttributes() - 1);
	    
	    model = Classifier.forName( classifier, options);
	    Random rand = new Random( seed);
	    trainInstance.randomize( rand);
	    if (trainInstance.classAttribute().isNominal())
	    	trainInstance.stratify(folds);

	      // perform cross-validation
	      Evaluation eval = new Evaluation(trainInstance);
	      for (int n = 0; n < folds; n++) {
	        Instances train = trainInstance.trainCV(folds, n);
	        Instances test = trainInstance.testCV(folds, n);

	        // build and evaluate classifier
	        Classifier clsCopy = Classifier.makeCopy( model);
	        clsCopy.buildClassifier(train);
	        eval.evaluateModel(clsCopy, test);
	      }
	    
	      // output evaluation
	      System.out.println();
	      System.out.println("=== Setup ===");
	      System.out.println("Classifier: " + model.getClass().getName() + " " + Utils.joinOptions(model.getOptions()));
	      System.out.println("Dataset: " + trainInstance.relationName());
	      System.out.println("Folds: " + folds);
	      System.out.println("Seed: " + seed);
	      System.out.println();
	      System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
	}
	
	public void test( String input) throws Exception{
		
		DataSource testSource = new DataSource( input);
		testInstance = testSource.getDataSet();
	    
	    if (testInstance.classIndex() == -1)
	    	testInstance.setClassIndex( trainInstance.classIndex());
	    
	    Evaluation eval = new Evaluation( trainInstance);
	    eval.evaluateModel( model, testInstance);
	    
	    // output evaluation
	    System.out.println();
	    System.out.println("=== Setup ===");
	    System.out.println("Classifier: " + model.getClass().getName() + " " + Utils.joinOptions(model.getOptions()));
	    System.out.println("Dataset: " + testInstance.relationName());
	    System.out.println();
	    System.out.println(eval.toSummaryString(false));
	}
}
