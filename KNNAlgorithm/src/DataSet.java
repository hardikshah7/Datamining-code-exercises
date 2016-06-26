import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/*
 * DataSet class has readDataSet method which reads the contents
 * of a file and formats it to an Instance representation which is 
 * use in the knnAlgorithm class. 
 * Empty lines and lines beginning with "@" are ignored. 
 */
public class DataSet {

    public static List<Instance> readDataSet(String file) throws FileNotFoundException {
        List<Instance> dataset = new ArrayList<Instance>();
        Scanner scanner = new Scanner(new File(file));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("@")||line.isEmpty()) {
                continue;
            }
            String[] columns = line.split("	");

            double[] data = new double[columns.length-1];
            int i=0;
            for (i=0; i<columns.length-1; i++) {
                data[i] = Double.parseDouble(columns[i]);
            }
            int label = Integer.parseInt(columns[i]);
            Instance instance = new Instance(label, data);
            dataset.add(instance);
        }
        return dataset;
    }
}
