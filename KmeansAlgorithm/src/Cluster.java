import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;

public class Cluster extends Point {
	public List<Point> points;
	public Point centroid;
	public int number;

	public Cluster(int number) {
		this.number = number;
		this.points = new ArrayList<Point>();
		this.centroid = null;
	}

	public void addPoint(Point point) {
		points.add(point);
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	public int getNumber() {
		return number;
	}

	public void clear() {
		points.clear();
	}
	public int getNumPoints()
	{
		return points.size();
	}

	public int MajorityClass() {
		HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
		for (Point p : points) {
			if (count.containsKey(p.getLabel()))
				count.put(p.getLabel(), count.get(p.getLabel()) + 1);
			else
				count.put(p.getLabel(), 1);
		}
		int maxvalue = -1;
		for (int key : count.keySet()) {
			if (maxvalue < count.get(key)) {
				maxvalue = count.get(key);
			}
		}
		return maxvalue;
	}

	void printCluster(String filename) throws IOException {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(filename, true)));
			out.println("Cluster number: " + number);
			out.println("Centroid: " + centroid);
			out.println("Points: \n");
			for (Point p : points) {
				out.println(p.toString());
			}
			out.close();
		} catch (Exception e) {
			System.out.println("Error writing to file");
		}
	}
}