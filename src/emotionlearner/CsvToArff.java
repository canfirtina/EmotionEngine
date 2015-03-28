package emotionlearner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvToArff {

	public static void csv2arff(String filePath, String label) throws IOException {
		File file = new File(filePath);
		if (file.getName().endsWith("csv")) {
			BufferedReader br = null;
			PrintWriter pw = null;

			try {
				br = new BufferedReader(new FileReader(file));

				pw = new PrintWriter(file.getName().substring(0,
						file.getName().lastIndexOf('.'))
						+ ".arff");

				boolean firstExec = true;
				String line;
				while ((line = br.readLine()) != null) {
					if (firstExec) {
						pw.println("@relation emotion\n");
						String[] currLine = line.split(",");
						for (int i = 0; i < currLine.length; i++)
							pw.println("@attribute att_" + i + " numeric");
						pw.println("@attribute 'Class' {'joy', 'disgust', 'peaceful', 'frustrated'}");
						pw.println("\n@data");
						firstExec = false;
					}
					pw.println(line.replaceAll(" ", "") + "," + label);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				pw.close();
				br.close();
			}
		}
	}
}
