import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KMeansClustering {
	private int totalclusters;
	private static List<Point> mypoints;
	private List<Cluster> clusters;
	String filename;

	public KMeansClustering() {
		mypoints = new ArrayList<Point>();
		this.clusters = new ArrayList<Cluster>();
	}

	public static void main(String[] args) throws IOException {
		KMeansClustering kmeans = new KMeansClustering();
		kmeans.totalclusters = 3;
		mypoints = DataSet.readDataSet("dataset1.txt");
		kmeans.assignInitialClusters(mypoints);
		System.out.println("*********DataSet 1**********");
		kmeans.filename = "dataset1_output.txt";
		kmeans.iterations();
		kmeans.clearall();
		mypoints = DataSet.readDataSet("dataset2.txt");
		kmeans.assignInitialClusters(mypoints);
		System.out.println("*********DataSet 2**********");
		kmeans.filename = "dataset2_output.txt";
		kmeans.iterations();
		kmeans.totalclusters = 2;
		kmeans.clearall();
		mypoints = DataSet.readDataSet("dataset3.txt");
		kmeans.assignInitialClusters(mypoints);
		System.out.println("*********DataSet 3**********");
		kmeans.filename = "dataset3_output.txt";
		kmeans.iterations();
	}

	public void clearall() {
		mypoints.clear();
		clusters.clear();
	}

	public void assignInitialClusters(List<Point> mypoints) {
		for (int i = 1; i <= totalclusters; i++) {
			Cluster cluster = new Cluster(i);
			clusters.add(cluster);
		}
		int counter = 1;
		int clustersize = mypoints.size() / totalclusters;
		int start = 0;
		int end = clustersize - 1;
		while (counter <= totalclusters) {
			List<Point> pointsubList = new ArrayList<Point>();
			pointsubList = mypoints.subList(start, end);
			for (Point sl : pointsubList) {
				clusters.get(counter - 1).addPoint(sl);
			}

			counter += 1;
			start = end + 1;
			end += clustersize;
			if (counter == totalclusters)
				end = mypoints.size();
		}
		newCentroids();
	}

	private void printClusters() throws IOException {
		for (int i = 0; i < totalclusters; i++) {
			Cluster mycluster = clusters.get(i);
			mycluster.printCluster(filename);
		}
	}

	public void iterations() throws IOException {
		boolean flag = false;
		while (!flag) {
			double distance = 0;
			for (Cluster cluster : clusters) {
				cluster.clear();
			}
			List<Point> oldCentroids = getCentroids();
			updateCluster();
			newCentroids();
			List<Point> newCentroids = getCentroids();
			for (int i = 0; i < oldCentroids.size(); i++) {
				distance += calculateDistance(oldCentroids.get(i),
						newCentroids.get(i));
			}
			if (distance == 0) {
				flag = true;
			} // This means there is no change in distance and we can end the
				// iterations.
		}
		printClusters();
		System.out.println("Purity: " + calculatePurity());
		System.out.println("nmi: " + nmi());
	}

	public double nmi() {
		double ho = 0, hc = 0, I = 0 ;
		HashMap<Integer, Integer> w = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> c = new HashMap<Integer, Integer>();
		List<Point> pointsincluster = new ArrayList<Point>();
		double datasize = 0;
		for (int i = 0; i < totalclusters; i++) {
			Cluster mycluster = clusters.get(i);
			pointsincluster.addAll(mycluster.points);
			c.put(i + 1, mycluster.getNumPoints());
			datasize += mycluster.getNumPoints();
		}
		for (Point p : pointsincluster) {
			if (w.containsKey(p.getLabel())) {
				w.put(p.getLabel(), w.get(p.getLabel()) + 1);
			} else
				w.put(p.getLabel(), 1);
		}
		for (Cluster cluster : clusters) {
			int sumlabels = 0;
			HashMap<Integer, Integer> labelvalue = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
			for (Point p : cluster.points) {
				if (labelvalue.containsKey(p.getLabel())) {
					labelvalue.put(p.getLabel(),
							labelvalue.get(p.getLabel()) + 1);
				} else
					labelvalue.put(p.getLabel(), 1);
			}
			for (Integer key : labelvalue.keySet()) {
				sumlabels += labelvalue.get(key);
			}
			counter.put(cluster.getNumber(), sumlabels);
			for (Integer key : labelvalue.keySet()) {
				double divideby = Math.log((datasize * labelvalue.get(key))
						/ (w.get(key) * counter.get(cluster
								.getNumber())))
						/ Math.log(2);
				I += (labelvalue.get(key) / datasize) * divideby;
			}
		}
		for (Integer key : w.keySet()) {
			ho += -(w.get(key) / datasize)
					* (Math.log(w.get(key) / datasize)) / Math.log(2);
		}
		for (Integer key : c.keySet()) {
			hc += -(c.get(key) / datasize)
					* (Math.log(c.get(key) / datasize)) / Math.log(2);
		}
		return I / Math.sqrt(ho * hc);
	}

	private double calculatePurity() {
		int totalsum = 0, totalpoints = 0;
		for (int i = 0; i < totalclusters; i++) {
			Cluster mycluster = clusters.get(i);
			totalsum += mycluster.MajorityClass();
			totalpoints += mycluster.getNumPoints();
		}
		double points = (double) 1 / totalpoints;
		return (points * totalsum);
	}

	public static double calculateDistance(Point mypoint, Point centroid) {
		double distance = Math.pow(
				(centroid.getYPoint() - mypoint.getYPoint()), 2)
				+ Math.pow((centroid.getXPoint() - mypoint.getXPoint()), 2);
		return Math.sqrt(distance);
	}

	private void newCentroids() {
		for (Cluster cluster : clusters) {
			double sumX = 0;
			double sumY = 0;
			List<Point> list = cluster.getPoints();
			for (Point point : list) {
				sumX += point.getXPoint();
				sumY += point.getYPoint();
			}
			Point updatedcentroid = new Point();
			if (list.size() > 0) {
				double newX = sumX / list.size();
				double newY = sumY / list.size();
				updatedcentroid.setXPoint(newX);
				updatedcentroid.setYPoint(newY);
			}
			cluster.setCentroid(updatedcentroid);
		}
	}

	private List<Point> getCentroids() {
		List<Point> centroids = new ArrayList<Point>(totalclusters);
		for (Cluster cluster : clusters) {
			Point cvalue = cluster.getCentroid();
			Point point = new Point(cvalue.getXPoint(), cvalue.getYPoint());
			centroids.add(point);
		}
		return centroids;
	}

	private void updateCluster() {
		double min;
		double max = Double.MAX_VALUE;
		double distance;
		int cluster = 0;
		for (Point point : mypoints) {
			min = max;
			for (int i = 0; i < totalclusters; i++) {
				Cluster mycluster = clusters.get(i);
				distance = calculateDistance(point, mycluster.getCentroid());
				if (distance < min) {
					min = distance;
					cluster = i;
				}
			}
			point.setCluster(cluster);
			clusters.get(cluster).addPoint(point);
		}
	}
}