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
	
	public static void main(String[] args) throws IOException{
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
		test.createAllTrainingData(test.videos, test.offsets, "alitrainingall.arff");
		for(int i=0;i<test.videos.length;++i)
			test.createOneOut(i, test.videos[i]);
		
		
		
	}
	
}
