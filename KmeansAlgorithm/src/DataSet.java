	import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
	
	public class DataSet extends Point{
	    public static List<Point> readDataSet(String file) throws FileNotFoundException {
	        List<Point> dataset = new ArrayList<Point>();
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
	            Point mypoint = new Point(data[0],data[1]);
	            mypoint.label = label;
	            dataset.add(mypoint);
	        }
	        return dataset;
	    }
	}
