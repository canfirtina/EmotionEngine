/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import emotionlearner.EEGFeatureExtractor;
import sensormanager.data.TimeBasedDataEpocher;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import sensormanager.data.TimestampedRawData;

/**
 *
 * @author aliyesilyaprak
 */
public class CaseFeatureExtractor {
	
	public static double[][] transposeMatrix(double [][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }
	
	public void extractFeaturesFromOBCI(String fileName) throws Exception{
		Scanner sc = new Scanner(new File(fileName));
		
		for(int i=0;i<5;++i)
			sc.nextLine();
		//TimeBasedDataEpocher epocher = new TimeBasedDataEpocher(4000);
		long time = 0;
		double [] raw = new double[8];
		ArrayList<double[]> matrix = new ArrayList<>();
		int r = 0;
		while(sc.hasNextLine()){
			
			String line = sc.nextLine();
			String[] parts = line.split(", ");
			
			for(int i=1;i<=8;++i)
				raw[i-1] = Double.parseDouble(parts[i].split(",")[0]);
			
			matrix.add(raw.clone());
			
		}
		
		System.out.println(matrix.get(0).length);
		
		double [][]dm = new double[matrix.size()][matrix.get(0).length];
		System.out.println(dm.length + " " + dm[0].length);
		for(int i=0;i<dm.length;++i){
			for(int j=0;j<dm[i].length;++j){
				dm[i][j] = matrix.get(i)[j];
				
				
			}
			
		}
		
		ArrayList<double[]> features = EEGFeatureExtractor.extractFeatures(transposeMatrix(dm));
		System.out.println(features.size());
		for(int i=0;i<features.size();++i){
			for(int j=0;j<features.get(i).length;++j)
				System.out.print(features.get(i)[j] + " ");
			System.out.println("");
		}
						
		
	}
	
	public static void main(String[] args) throws Exception{
		String fn = "test/ali/OBCI/ali peaceful 1 9.txt";
		new CaseFeatureExtractor().extractFeaturesFromOBCI(fn);
	}
	
}
