import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Jama.Matrix;

public class Logistic {
	/** the weight to learn */
	private double[] weights;
	private double bias;
	/** the number of iterations */
	private int ITERATIONS = 6000;
	int sizeoffold = 5;
	double lambda = 0.0001;

	public Logistic(int n) {
		weights = new double[n];
		bias = 0;
	}

	private double sigmoid(double z) {
		return 1 / (1 + Math.exp(-z));
	}

	public void train(List<Instance> instances) {
		for (int n = 0; n < ITERATIONS; n++) {
			int z = 0, a = 0;
			double lik = 0.0;
			int lengthofinst = instances.size();
			double[] predictVec = new double[lengthofinst];
			int features = instances.get(0).getDimension();
			int[] classes = new int[lengthofinst];
			Matrix der = new Matrix(features, 1);
			Matrix hessianmatrix = new Matrix(features, features);
			for (int i = 0; i < lengthofinst; i++) {
				double[] x = instances.get(i).getX();
				double predicted = classify(x);
				predictVec[i] = predicted;
				classes[i] = instances.get(i).getLabel();
			}
			while (z < lengthofinst) {
				Instance myinstance = instances.get(z);
				der = der.plus(new Matrix(myinstance.getX(), 1).transpose())
						.times(classes[z] - classify(myinstance.getX()));
				hessianmatrix = hessianmatrix.plus(
						new Matrix(myinstance.getX(), 1).transpose().times(
								new Matrix(myinstance.getX(), 1))).times(
						classify(myinstance.getX())
								* (1 - classify(myinstance.getX())));
				z += 1;
			}
			Matrix identity = Matrix.identity(features, features);
			hessianmatrix = hessianmatrix.minus(identity.times(lambda));
			for (int j = 0; j < weights.length; j++) {
				weights[j] = weights[j]
						- hessianmatrix.inverse().times(der).get(j, 0);
			}

			while (a < lengthofinst) {
				Instance in = instances.get(a);
				double predvalue = 0.0;
				predvalue = classify(in.getX());
				lik += (in.getLabel() * predvalue) - Math.log(1 + predvalue);
				a += 1;
			}
			System.out.println("iteration: " + n + " "
					+ Arrays.toString(weights) + " mle: " + lik);
		}
	}

	private double classify(double[] x) {
		double logit = bias;
		for (int i = 0; i < weights.length; i++) {
			logit += weights[i] * x[i];
		}
		return sigmoid(logit);
	}

	public static void main(String... args) throws FileNotFoundException {
		List<Instance> instances = DataSet.readDataSet("data.txt");
		Logistic logistic = null;
		int foldsize = instances.size() / 5;
		int start = 0, end = foldsize - 1;
		ArrayList<Double> al = new ArrayList<Double>();
		for (int i = 0; i < 5; i++) {
			List<Instance> trainInstances = new ArrayList<Instance>(instances);
			List<Instance> testInstances = new ArrayList<Instance>(
					trainInstances);
			testInstances = testInstances.subList(start, end + 1);
			logistic = new Logistic(instances.get(0).getDimension());
			logistic.train(instances);
			for (int a = 0; a <= (end - start); a++) {
				trainInstances.remove(start);
			}
			double correctness = 0.0;
			for (Instance value : testInstances) {
				int actualvalue = value.getLabel();
				double predictedvalue = logistic.classify(value.getX());
				if (actualvalue == 0 && predictedvalue < 0.5)
					correctness++;
				else if (predictedvalue >= 0.5 && actualvalue == 1)
					correctness++;
			}
			al.add(correctness / (double) testInstances.size());
			start = end + 1;
			end += foldsize;
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
		// toy test
		/*
		 * double[] testPoint =
		 * {63.0278175,22.55258597,39.60911701,40.47523153,98.67291675
		 * ,-0.254399986}; System.out.println("prob(1|testPoint) = " +
		 * logistic.classify(testPoint));
		 */
	}
}
