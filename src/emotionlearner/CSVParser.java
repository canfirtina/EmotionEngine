package emotionlearner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author can, ayhun
 *
 */
public class CSVParser {

    public static double[][] read(String src) throws FileNotFoundException {

        ArrayList<ArrayList<Double>> matrixList = new ArrayList<ArrayList<Double>>();
        Scanner scanner = new Scanner(new File(src));

        int currentLine = 0;
        while (scanner.hasNextLine()) {

            Scanner scanner2 = new Scanner(scanner.nextLine());
            scanner2.useDelimiter(",");

            matrixList.add(currentLine, new ArrayList<Double>());
            int currentColumn = 0;
            while (scanner2.hasNext()) {
                matrixList.get(currentLine).add(currentColumn++, Double.parseDouble(scanner2.next()));
            }

            scanner2.close();
            currentLine++;
        }
        scanner.close();

        double[][] matrix = new double[matrixList.size()][matrixList.get(0).size()];
        for (int i = 0; i < matrixList.size(); i++) {
            for (int j = 0; j < matrixList.get(i).size(); j++) {
                matrix[i][j] = matrixList.get(i).get(j);
            }
        }
        return matrix;
    }

    public static void write(String fileName, ArrayList<double[]> list) {

        StringBuilder sb = new StringBuilder();

        for (double[] arr : list) {
            for (int i = 0; i < (arr.length - 1); i++) {
                sb.append(arr[i] + ",");
            }
            sb.append(arr[arr.length - 1]); //there is no delimiter for the last one
            sb.append("\n");
        }

        File file = new File(fileName);
        String content = sb.toString();

        try (FileOutputStream fop = new FileOutputStream(file)) {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
