import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class knnAlgorithm {
	private static int findMajorityClass(int[] classes) {
		HashMap<Integer, Integer> finalunique = new HashMap<Integer, Integer>();
		for (int classname : classes) {
			if (finalunique.containsKey(classname))
				finalunique.put(classname, finalunique.get(classname) + 1);
			else
				finalunique.put(classname, 1);
		}
		int maxvalue = -1, maxkey = -1;
		for (int key : finalunique.keySet()) {
			if (maxvalue < finalunique.get(key)) {
				maxvalue = finalunique.get(key);
				maxkey = key;
			}
		}
		return maxkey;
	}

	public static void main(String args[]) throws FileNotFoundException {
		List<Instance> instances = DataSet.readDataSet("data.txt");
		int sizeoffold = instances.size() / 5;
		int start = 0, end = sizeoffold - 1;
		ArrayList<Double> al = new ArrayList<Double>();
		for (int i = 0; i < 5; i++) {
			List<Instance> trainInstances = new ArrayList<Instance>(instances);
			List<Instance> testInstances = new ArrayList<Instance>(
					trainInstances);
			testInstances = testInstances.subList(start, end + 1);

			for (int a = 0; a <= (end - start); a++) {
				trainInstances.remove(start);
			}
			double correctness = 0.0;
			for (Instance value : testInstances) {
				int actualvalue = value.getLabel();
				int predictedvalue = EuclideanDistance(trainInstances, value);
				if (actualvalue == predictedvalue) {
					correctness++;
				}
			}
			al.add(correctness / (double) testInstances.size());
			start = end + 1;
			end += sizeoffold;
			if (i == 4)
				end = instances.size();
		}
		double accuracy = 0.0D;
		double sum = 0.0D;
		for (double d : al) {
			sum += d;
			System.out.println("accuracy: " + d);
		}
		accuracy = sum / 5;
		System.out.println("Average Accuracy %: " + accuracy * 100);
	}

	private static int EuclideanDistance(List<Instance> trainInstances,
			Instance test) {
		List<Result> resultList = new ArrayList<Result>();
		for (Instance train : trainInstances) {
			double dist = 0.0;
			for (int j = 0; j < train.getDimension(); j++) {
				dist += Math.pow(train.getX()[j] - test.getX()[j], 2);
			}
			double distance = Math.sqrt(dist);
			resultList.add(new Result(distance, train.getLabel()));
		}
		Collections.sort(resultList, new DistanceComparator());
		int k = 3; // no of neighbours
		int[] mj = new int[k];
		for (int x = 0; x < k; x++) {
			mj[x] = resultList.get(x).xlabel;
		}
		return findMajorityClass(mj);
	}

	static class Result {
		double distance;
		int xlabel;

		public Result(double distance, int i) {
			this.xlabel = i;
			this.distance = distance;
		}
	}

	static class DistanceComparator implements Comparator<Result> {
		@Override
		public int compare(Result a, Result b) {
			return a.distance < b.distance ? -1 : a.distance == b.distance ? 0
					: 1;
		}
	}
}
