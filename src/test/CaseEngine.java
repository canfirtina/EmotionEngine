package test;

import emotionlearner.DataEpocher;
import emotionlearner.EmotionEngine;
import emotionlearner.FeatureExtractor;
import emotionlearner.FeatureExtractorEEG;
import emotionlearner.LengthBasedDataEpocher;
import emotionlearner.TimeBasedDataEpocher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import sensormanager.*;
import shared.Emotion;
import shared.FeatureList;
import shared.FeatureListController;
import shared.TimestampedRawData;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.FastVector;

/**
 *
 * @author aliyesilyaprak
 */
public class CaseEngine {
	
	private void testFeatureListFromOBCI() throws Exception{
		
		EmotionEngine engine = EmotionEngine.sharedInstance(null);
		engine.createSensorListener("COM4", SensorListenerEEG.class);
		Thread.sleep(30);
		List<SensorListener> listeners =  engine.getPendingSensors();
		SensorListenerEEG listener = (SensorListenerEEG) listeners.get(0);
		engine.connectionEstablished(listener);
		
		File file = new File("test/ali/ali boring 2 9.txt");
		Scanner sc = new Scanner(file);
		for(int i=0;i<5;++i)
			sc.nextLine();
		TimeBasedDataEpocher epocher = new TimeBasedDataEpocher(4000);
		double [] raw = new double[8];
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] parts = line.split(", ");
			
			for(int i=1;i<=8;++i)
				raw[i-1] = Double.parseDouble(parts[i].split(",")[0]);
			//System.out.println("raw");
			TimestampedRawData trd = new TimestampedRawData(raw, new Timestamp(new Date().getTime()));
			if(!epocher.addData(trd)){
				List<TimestampedRawData> list = epocher.getEpoch();
				epocher.reset();
				epocher.addData(trd);
				
				listener.setSensorData(list);
				engine.dataArrived(listener);
			}
			Thread.sleep(4);
		}
	}
	
	private void trainEmotionFromOBCI(EmotionEngine engine, String fileName, Emotion emotion, SensorListenerEEG listener) throws Exception{
		int sleeper = 0;
		File file = new File("test/ali/" + fileName);
		Scanner sc = new Scanner(file);
		
		for(int i=0;i<5;++i)
			sc.nextLine();
		TimeBasedDataEpocher epocher = new TimeBasedDataEpocher(4000);
		
		engine.openTrainingSession(emotion);
		double [] raw = new double[8];
		long time = 0;
		int c=0;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] parts = line.split(", ");
			
			for(int i=1;i<=8;++i) {
				raw[i-1] = Double.parseDouble(parts[i].split(",")[0]);
				
			}
			//System.out.println("raw");
			TimestampedRawData trd = new TimestampedRawData(raw, new Timestamp(new Date().getTime()+ time));
			if(!epocher.addData(trd)){
				List<TimestampedRawData> list = epocher.getEpoch();
				epocher.reset();
				epocher.addData(trd);
				
				listener.setSensorData(list);
				engine.dataArrived(listener);
				if((sleeper++)%4 == 0){
					//System.out.println("xxx");
					Thread.sleep(20);
				}
			}
			time += 4;
		}
		engine.closeTrainingSession();
		System.out.println(fileName);
	}
	
	private void testEmotionFromOBCI(EmotionEngine engine, String fileName,  SensorListenerEEG listener) throws Exception{
		int sleeper = 0;
		File file = new File("test/ali/" + fileName);
		Scanner sc = new Scanner(file);
		
		for(int i=0;i<5;++i)
			sc.nextLine();
		TimeBasedDataEpocher epocher = new TimeBasedDataEpocher(4000);
		long time = 0;
		double [] raw = new double[8];
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] parts = line.split(", ");
			
			for(int i=1;i<=8;++i)
				raw[i-1] = Double.parseDouble(parts[i].split(",")[0]);
			
			
			TimestampedRawData trd = new TimestampedRawData(raw, new Timestamp(new Date().getTime()+time));
			if(!epocher.addData(trd)){
				List<TimestampedRawData> list = epocher.getEpoch();
				epocher.reset();
				epocher.addData(trd);
				
				listener.setSensorData(list);
				engine.dataArrived(listener);
				if((sleeper++)%4 == 0){
					//System.out.println("xxx");
					Thread.sleep(20);
				}
			}
			time+=4;
			//Thread.sleep(4);
		}
	}
	 
	private void testOpenTrainingSessionFromOBCI() throws Exception{
		EmotionEngine engine = EmotionEngine.sharedInstance(null);
		engine.createSensorListener("COM4", SensorListenerEEG.class);
		Thread.sleep(30);
		List<SensorListener> listeners =  engine.getPendingSensors();
		SensorListenerEEG listener = (SensorListenerEEG) listeners.get(0);
		engine.connectionEstablished(listener);
		Thread.sleep(50);
		System.out.println("Pending size:" + engine.getPendingSensors().size());
		System.out.println("Established size:" + engine.getConnectedSensors().size());
		
		trainEmotionFromOBCI(engine, "OBCI/ali disgusting 1 9.txt", Emotion.DISGUST, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali boring 1 10.txt", Emotion.BORED, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali boring 2 9.txt", Emotion.BORED, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali disgusting 2 9.txt", Emotion.DISGUST, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali disgusting 3 7.txt", Emotion.DISGUST, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali disgusting 4 7.txt", Emotion.DISGUST, listener);
		//trainEmotionFromOBCI(engine, "OBCI/ali disgusting 6 10.txt", Emotion.DISGUST, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali peaceful 1 9.txt", Emotion.PEACEFUL, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali peaceful 3 8.txt", Emotion.PEACEFUL, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali peaceful 4 7.txt", Emotion.PEACEFUL, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali peaceful 5 8.txt", Emotion.PEACEFUL, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali peaceful 1 9.txt", Emotion.PEACEFUL, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali sexy 1 9.txt", Emotion.JOY, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali sexy 1 9.txt", Emotion.JOY, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali sexy 2 8.txt", Emotion.JOY, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali sexy 3 9.txt", Emotion.JOY, listener);
		trainEmotionFromOBCI(engine, "OBCI/ali sexy 4 8.txt", Emotion.JOY, listener);
		
		
		testEmotionFromOBCI(engine, "OBCI/ali disgusting 6 10.txt"  , listener);
		
		
	}
	
	private void trainOBCIForClassifier(FeatureListController controller, String fileName, Emotion emotion, SensorListenerEEG listener) throws FileNotFoundException{
		File file = new File("test/ali/" + fileName);
		Scanner sc = new Scanner(file);
			System.out.println(fileName);
		
		for(int i=0;i<5;++i)
			sc.nextLine();
		TimeBasedDataEpocher epocher = new TimeBasedDataEpocher(4000);
		long time = 0;
		double [] raw = new double[8];
		FeatureExtractorEEG fe = new FeatureExtractorEEG();
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] parts = line.split(", ");
			
			for(int i=1;i<=8;++i){
				raw[i-1] = Double.parseDouble(parts[i].split(",")[0]);
				System.out.println(raw[i-1]);
			}
			
			TimestampedRawData trd = new TimestampedRawData(raw, new Timestamp(new Date().getTime()+time));
			if(!epocher.addData(trd)){
				List<TimestampedRawData> list = epocher.getEpoch();
				epocher.reset();
				epocher.addData(trd);
				
				listener.setSensorData(list);
				List<TimestampedRawData> rawData = listener.getSensorData();
				fe.appendRawData(rawData);
				FeatureList fl = fe.getFeatures();
				fl.setEmotion(emotion);
				controller.addFeatureList(listener,fl);
				
				
			}
			time+=4;
			//Thread.sleep(4);
		}
		System.out.println(fileName);
	}
	
	private void testOBCIForClassifier(Classifier classifier, Instances instances, String fileName,  SensorListenerEEG listener) throws FileNotFoundException, Exception{
		File file = new File("test/ali/" + fileName);
		Scanner sc = new Scanner(file);
		
		
		for(int i=0;i<5;++i)
			sc.nextLine();
		DataEpocher epocher = new LengthBasedDataEpocher(1024);
		long time = 0;
		double [] raw = new double[8];
		FeatureExtractorEEG fe = new FeatureExtractorEEG();
		int []results = new int[5];
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] parts = line.split(", ");
			
			for(int i=1;i<=8;++i){
				raw[i-1] = Double.parseDouble(parts[i].split(",")[0]);
				//System.out.print("*" + raw[i-1]);
			}
			//System.out.println("");
			
			TimestampedRawData trd = new TimestampedRawData(raw.clone(), new Timestamp(new Date().getTime()+time));
			if(!epocher.addData(trd)){
				List<TimestampedRawData> list = epocher.getEpoch();
				epocher.reset();
				epocher.addData(trd);
				
				listener.setSensorData(list);
				List<TimestampedRawData> rawData = listener.getSensorData();
				fe.appendRawData(rawData);
				FeatureList fl = fe.getFeatures();
				fl.getInstance().setDataset(instances);
				
				double res = classifier.classifyInstance(fl.getInstance());
				results[(int)res]++;
				
				
				
			}
			time+=4;
			//Thread.sleep(4);
		}
		
		for(int i=0;i<5;++i)
			System.out.print(results[i] + " ");
		System.out.println("");
		
	}
	
	private void testClassifierFromOBCI() throws  Exception{
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File("test/ali/ARFF/fali peaceful 3 8.arff-out.arff"));
		Instances ld = loader.getDataSet();
		ld.setClassIndex(ld.numAttributes()-1);
		FastVector fa = new FastVector();
		for(int i=0;i<64;++i)
			fa.addElement(new Attribute("att_eeg_" + i));
		fa.addElement(new Attribute("eeg_class", Emotion.classAttributes()));
		
		ArrayList<FeatureList> features = new ArrayList<>();
		for(int i=0;i<ld.numInstances();++i){
			Instance ins = ld.instance(i);
			ins.setDataset(ld);
			double[] ar = new double[64];
			for(int j=0;j<64;++j){
				ar[j] = ins.value(j);
				//System.out.print(ar[j]);
			}
		
			int v = (int)ins.classValue();
			Emotion e;
			if(v==0)
				e = Emotion.JOY;
			else if(v==1)
				e = Emotion.DISGUST;
			else if(v==2)
				e = Emotion.PEACEFUL;
			else 
				e = Emotion.BORED;
			//System.out.println(e.name());
			
			FeatureList list = new FeatureList(ar, fa);
			list.setEmotion(e);
			features.add(list);
		}
			
		Instances instances = new Instances("eeg", fa, features.size());
		System.out.println(instances.numAttributes());
		instances.setClassIndex(instances.numAttributes()-1);
		System.out.println(fa.size()-1);
		
		for(FeatureList fl : features){
			Instance instance = fl.getInstance();
			instance.setDataset(instances);
			instance.setValue((Attribute)fa.elementAt(fa.size()-1), fl.getEmotion().name());
			//instance.setClassValue(fl.getEmotion().name());
			System.out.println(instance.value(fa.size()-1));
			instances.add(instance);
		}
		
		Classifier classifier = Classifier.forName( "weka.classifiers.functions.SMO", new String[]{"-N", "2"});
		classifier.buildClassifier(instances);
		if(classifier!=null)
			System.out.println("valid");
		
//		loader = new ArffLoader();
//		loader.setFile(new File("test/ali/ARFF/fali disgusting 3 7.arff"));
//		Instances testInstances = loader.getDataSet();
//		testInstances.setClassIndex(testInstances.numAttributes()-1);
//		for(int i=0;i<testInstances.numInstances();++i)
//			System.out.println(classifier.classifyInstance(testInstances.instance(i)));
		
		
		SensorListenerEEG listener = new SensorListenerEEG("com4");
		testOBCIForClassifier(classifier, instances, "OBCI/ali peaceful 3 8.txt" , listener);

		
		/*
		SensorListenerEEG listener = new SensorListenerEEG("com4");
		FeatureListController c = new FeatureListController();
		c.registerSensorListener(listener);
		trainOBCIForClassifier(c, "OBCI/ali boring 2 9.txt", Emotion.BORED, listener);
		trainOBCIForClassifier(c, "OBCI/ali disgusting 1 9.txt", Emotion.DISGUST, listener);
		//trainOBCIForClassifier(c, "OBCI/ali disgusting 2 9.txt", Emotion.DISGUST, listener);
		trainOBCIForClassifier(c, "OBCI/ali disgusting 3 7.txt", Emotion.DISGUST, listener);
		trainOBCIForClassifier(c, "OBCI/ali disgusting 4 7.txt", Emotion.DISGUST, listener);
		trainOBCIForClassifier(c, "OBCI/ali disgusting 6 10.txt", Emotion.DISGUST, listener);
		trainOBCIForClassifier(c, "OBCI/ali peaceful 1 9.txt", Emotion.PEACEFUL, listener);
		trainOBCIForClassifier(c, "OBCI/ali peaceful 3 8.txt", Emotion.PEACEFUL, listener);
		trainOBCIForClassifier(c, "OBCI/ali peaceful 4 7.txt", Emotion.PEACEFUL, listener);
		trainOBCIForClassifier(c, "OBCI/ali peaceful 5 8.txt", Emotion.PEACEFUL, listener);
		trainOBCIForClassifier(c, "OBCI/ali peaceful 1 9.txt", Emotion.PEACEFUL, listener);
		trainOBCIForClassifier(c, "OBCI/ali sexy 1 9.txt", Emotion.JOY, listener);
		trainOBCIForClassifier(c, "OBCI/ali sexy 1 9.txt", Emotion.JOY, listener);
		trainOBCIForClassifier(c, "OBCI/ali sexy 2 8.txt", Emotion.JOY, listener);
		trainOBCIForClassifier(c, "OBCI/ali sexy 3 9.txt", Emotion.JOY, listener);
		trainOBCIForClassifier(c, "OBCI/ali sexy 4 8.txt", Emotion.JOY, listener);
		
		//training now
		List<FeatureList> lists = c.getLastNFeatureList(listener, -1);
		Instances instances = new Instances("eeg", lists.get(0).getFeatureAttributes(), lists.size());
		System.out.println(instances.numAttributes());
		instances.setClassIndex(instances.numAttributes()-1);
		
		for(FeatureList fl : lists){
			Instance instance = fl.getInstance();
			instance.setDataset(instances);
			instances.add(instance);
			instance.setClassValue(fl.getEmotion().getValue());
		}
		
		Classifier classifier = new NaiveBayes();
		classifier.buildClassifier(instances);
		
		//test
		testOBCIForClassifier(classifier, instances, "OBCI/ali disgusting 2 9.txt" , listener);
				*/
		
	}
	
	private void testClassificationWithDataManager() throws Exception{
		EmotionEngine engine = EmotionEngine.sharedInstance(null);
		engine.createSensorListener("COM4", SensorListenerEEG.class);
		Thread.sleep(30);
		List<SensorListener> listeners =  engine.getPendingSensors();
		SensorListenerEEG listener = (SensorListenerEEG) listeners.get(0);
		engine.connectionEstablished(listener);
		Thread.sleep(5000);
		System.out.println("Pending size:" + engine.getPendingSensors().size());
		System.out.println("Established size:" + engine.getConnectedSensors().size());
		
		testEmotionFromOBCI(engine, "OBCI/ali disgusting 6 10.txt"  , listener);
		
		
	}
	
	public static void main(String[] args) throws Exception{
		//new CaseEngine().testClassifierFromOBCI();
		new CaseEngine().testClassificationWithDataManager();
		
	}
	
}