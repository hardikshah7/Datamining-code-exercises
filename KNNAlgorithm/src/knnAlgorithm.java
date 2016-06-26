import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class knnAlgorithm {
	/*
	 * findMajorityClass function takes Integer array as input which contains
	 * class values for all the training data points. First, we loop on this
	 * array and store the count of each class in a new HashMap object. After we
	 * finish looping on the array, we run a loop on the HashMap object to
	 * determine the key with the highest count and store it in maxkey integer
	 * object. The function finally returns this integer as the majorityClass
	 * for our training set.
	 */
	private static int findMajorityClass(int[] classes) {
		HashMap<Integer, Integer> finalunique = new HashMap<Integer, Integer>();
		int maxvalue = -1, maxkey = -1; // Initialize to -1 because max will
										// always be greater than 1.
		for (int classname : classes) {
			if (finalunique.containsKey(classname))
				finalunique.put(classname, finalunique.get(classname) + 1);
			else
				finalunique.put(classname, 1);
		}

		for (int key : finalunique.keySet()) {
			if (maxvalue < finalunique.get(key)) {
				maxvalue = finalunique.get(key);
				maxkey = key;
			}
		}
		return maxkey;
	}

	public static void main(String args[]) throws FileNotFoundException {
		/*
		 * Using the readDataSet method from DataSet class, we read the values
		 * from data.txt and store it into a list. We then determine the size of
		 * the fold for cross validating our model For this, we divide the list
		 * into 5 folds. We will use each fold to validate with other folds.
		 */
		List<Instance> instances = DataSet.readDataSet("data.txt");
		int sizeoffold = instances.size() / 5;
		int start = 0, end = sizeoffold - 1;

		ArrayList<Double> accuracyRate = new ArrayList<Double>();
		// Run the loop 5 times as we are using 5-fold cross validation
		for (int fold = 0; fold < 5; fold++) {
			/*
			 * Select the testing dataset as a sublist of training dataset
			 * Exclude the dataset used for testing and utilize the remaining
			 * dataset as training dataset. This happens for each loop by
			 * increasing the start and end values which take a new sublist each
			 * time
			 */
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
				// Compare the training set with a single test dataset instance
				int predictedvalue = EuclideanDistance(trainInstances, value);

				// If the class labels are equal, it means we have an accurate
				// model.
				// Hence, increase the correctness.
				if (actualvalue == predictedvalue) {
					correctness++;
				}
			}
			// Accuracy = Correctness / number of test instances
			accuracyRate.add(correctness / (double) testInstances.size());

			// Increase start and end values to selec tthe sublist for next
			// iteration
			start = end + 1;
			end += sizeoffold;
			if (fold == 4)
				end = instances.size();
		}
		double accuracy = 0.0D;
		double sum = 0.0D;
		// Print Accuracy for each fold and finally the Average Accuracy
		for (double rate : accuracyRate) {
			sum += rate;
			System.out.println("Accuracy: " + rate);
		}
		accuracy = sum / 5;
		System.out.println("Average Accuracy %: " + accuracy * 100);
	}

	private static int EuclideanDistance(List<Instance> trainInstances,
			Instance test) {
		List<Result> resultList = new ArrayList<Result>();
		int neighbours = 3; // no of neighbours
		int[] labelArray = new int[neighbours];
		// Calculate the Euclidean Distance between data points
		// Distance = Math.sqrt((train.x1 - test.x1)^2)
		for (Instance train : trainInstances) {
			double distance = 0.0;
			for (int trainIterator = 0; trainIterator < train.getDimension(); trainIterator++) {
				distance += Math.pow(
						train.getPoint()[trainIterator]
								- test.getPoint()[trainIterator], 2);
			}

			// Add the distance and class label to a List of type Result
			// We will use this list to determine the class labels of the
			// nearest points
			resultList.add(new Result(Math.sqrt(distance), train.getLabel()));
		}
		Collections.sort(resultList, new DistanceComparator());

		// Store all class labels between the nearest points in an array.
		for (int iteration = 0; iteration < neighbours; iteration++) {
			labelArray[iteration] = resultList.get(iteration).xpoint;
		}
		// Call findMajorityClass function to get the majority class label
		return findMajorityClass(labelArray);
	}

	// Custom Object to store Xpoint and distance of that point
	static class Result {
		double distance;
		int xpoint;

		public Result(double distance, int i) {
			this.xpoint = i;
			this.distance = distance;
		}
	}

	static class DistanceComparator implements Comparator<Result> {
		// Return -1, 0 or 1 by comparing distances of 2 Result objects
		@Override
		public int compare(Result a, Result b) {
			return a.distance < b.distance ? -1 : a.distance == b.distance ? 0
					: 1;
		}
	}
}
