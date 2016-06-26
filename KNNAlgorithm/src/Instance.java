/* This is a custom class used to hold each Instance as a combination
 * of class label, dataPoint and dimensions
 */
public class Instance {
    public int label;
    public double[] datapoint;
    public int dimension;

    public Instance(int label, double[] datapoint) {
        this.label = label;
        this.datapoint = datapoint;
        dimension = datapoint.length;
    }
    // Returns the class label for a data point
    public int getLabel() {
        return label;
    }
    // Returns the data points as a double array
    public double[] getPoint() {
        return datapoint;
    }
    // Returns the dimensions i.e. length of data points array
    public int getDimension() {
    	return dimension;
    }
}
