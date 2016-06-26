public class Point {
	private double x = 0;
	private double y = 0;
	private int cluster_number = 0;
	public int label = 0;

	public Point() {
	}
	
	public String toString() {
		return x + "," + y;
	}

	public Point(double x, double y) {
		this.setXPoint(x);
		this.setYPoint(y);
	}

	public void setXPoint(double x) {
		this.x = x;
	}

	public double getXPoint() {
		return this.x;
	}

	public void setYPoint(double y) {
		this.y = y;
	}

	public double getYPoint() {
		return this.y;
	}

	public void setCluster(int no) {
		this.cluster_number = no;
	}

	public int getCluster() {
		return this.cluster_number;
	}
	
	public int getLabel() {
		return this.label;
	}
}