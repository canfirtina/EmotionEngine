/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author aliyesilyaprak
 */
public class CaseDatasetTest {
	private String user;
	private String[] videos;
	private int[] offsets;
	
	private void createAllTrainingData(String[] videoNames, int[] videoOffsets, String outName) throws IOException{
		String arffPath = "test/" + user + "/" + "ARFF/";
		Instances instances = null;
		int counter = 0;
		for(String videoName : videoNames){
			System.out.println(arffPath + videoName);
			ArffLoader loader = new ArffLoader();
			loader.setFile(new File(arffPath + videoName));
			if(instances == null)
				instances = loader.getDataSet();
			else {
				Instances instances2 = loader.getDataSet();
				System.out.println(instances2.numInstances());
				for(int i=videoOffsets[counter];i<instances2.numInstances();++i)
					
					instances.add(instances2.instance(i));
				}
			counter++;
			
		}
		System.out.println(instances.numInstances());
		ArffSaver saver = new ArffSaver();
		saver.setFile(new File(arffPath + outName));
		saver.setInstances(instances);
		saver.writeBatch();
	}
	
	private void createOneOut(int index, String name) throws IOException{
		String[] param = new String[videos.length-1];
		int[] param2 = new int[videos.length-1];
		for(int i=0,j=0;i<videos.length;++i){
			if(i==index)
				continue;
			
			param[j] = videos[i];
			param2[j++] = offsets[i];
		}
		createAllTrainingData(param, param2, name+"-out.arff");
	}
	
	private static void extractAli() throws IOException{
		CaseDatasetTest test = new CaseDatasetTest();
		test.user = "ali";
		test.videos = new String[]{"fali boring 1 10.arff",
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
		test.offsets = new int[test.videos.length];
		for(int i=0;i<test.videos.length;++i)
			test.offsets[i] = 4;
		test.offsets[0] = 30;
		test.createAllTrainingData(test.videos, test.offsets, test.user+"trainingall.arff");
		for(int i=0;i<test.videos.length;++i)
			test.createOneOut(i, test.videos[i]);
	}
	
	private static void extractCan() throws IOException{
		CaseDatasetTest test = new CaseDatasetTest();
		test.user = "can";
		test.videos = new String[]{"fcan - disgusting1 - 10.arff",
									"fcan - disgusting2 - 10.arff",
									"fcan - disgusting3 - 8.arff",
									"fcan - disgusting4 - 9.arff",
									"fcan - disgusting5 - 10.arff",
									"fcan - disgusting6 - 9.arff",
									"fcan - disgusting7 - 10.arff",
									"fcan - frustrated1 - cat mario.arff",
									"fcan - frustrated2 - cat mario 2.arff",
									"fcan - peaceful1 - 9.arff",
									"fcan - peaceful2 - 9.arff",
									"fcan - peaceful3 - 7.arff",
									"fcan - peaceful4 - 8.arff",
									"fcan - sexy1 - 7.arff",
									"fcan - sexy2 - sonlari 10.arff",
									"fcan - sexy3 - sonlari 9.arff",
									"fcan - sexy4 - 8.arff"};
		test.offsets = new int[test.videos.length];
		//FIX THE OFFSETS FOR CAN
		for(int i=0;i<test.videos.length;++i)
			test.offsets[i] = 4;
		//test.offsets[7] = 10;
		//test.offsets[11] = test.offsets[12] = 8;
		test.createAllTrainingData(test.videos, test.offsets, test.user+"trainingall.arff");
		for(int i=0;i<test.videos.length;++i)
			test.createOneOut(i, test.videos[i]);
	}
	
	private static void extractMustafa() throws IOException{
		CaseDatasetTest test = new CaseDatasetTest();
		test.user = "mustafa";
		test.videos = new String[]{"fmustafa - disgust1 - 8.arff",
									"fmustafa - disgust2 - 7.arff",
									"fmustafa - disgust3 - 6.arff",
									"fmustafa - disgust4 - 5.arff",
									"fmustafa - disgust5 - 8.arff",
									"fmustafa - frustrated1 - 7.arff",
									"fmustafa - frustrated2 - 6.arff",
									"fmustafa - frustrated3 - 5.arff",
									"fmustafa - frustrated4 - 4.arff",
									"fmustafa - joy1 - 7.arff",
									"fmustafa - joy2 - 8.arff",
									"fmustafa - joy3 - 5.arff",
									"fmustafa - joy4 - 6.arff",
									"fmustafa - peaceful1 - 6.arff",
									"fmustafa - peaceful2 - 8.arff",
									"fmustafa - peaceful3 - 4.arff",
									"fmustafa - peaceful4 - 5.arff"};
		test.offsets = new int[test.videos.length];
		//FIX THE OFFSETS FOR MUSTAFA
		for(int i=0;i<test.videos.length;++i)
			test.offsets[i] = 4;
		
		test.createAllTrainingData(test.videos, test.offsets, test.user+"trainingall.arff");
		for(int i=0;i<test.videos.length;++i)
			test.createOneOut(i, test.videos[i]);
	}
	
	private static void extractAyhun() throws IOException{
		CaseDatasetTest test = new CaseDatasetTest();
		test.user = "ayhun";
		test.videos = new String[]{"fayhun-d-1-8.arff",
									"fayhun-d-2-6.arff",
									"fayhun-d-3and4-5.arff",
									"fayhun-d-5and6-100.arff",
									"fayhun-p-1-7.arff",
									"fayhun-p-2-9.arff",
									"fayhun-p-3-6.arff",
									"fayhun-p-4-8.arff",
									"fayhun-s-1-7.arff",
									"fayhun-s-2-6.arff",
									"fayhun-s-3-7.arff",
									"fayhun-s-4-7.arff"};
		test.offsets = new int[test.videos.length];
		//FIX THE OFFSETS FOR AYHUN
		for(int i=0;i<test.videos.length;++i)
			test.offsets[i] = 4;
		
		test.createAllTrainingData(test.videos, test.offsets, test.user+"trainingall.arff");
		for(int i=0;i<test.videos.length;++i)
			test.createOneOut(i, test.videos[i]);
	}
	
	public static void main(String[] args) throws IOException{
		//extractAli();
		extractCan();
		//extractMustafa();
		//extractAyhun();
	}
	
}
