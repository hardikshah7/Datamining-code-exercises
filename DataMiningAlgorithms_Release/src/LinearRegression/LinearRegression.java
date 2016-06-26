package LinearRegression;

import Jama.Matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Simple Linear Regression implementation
 */
public class LinearRegression {
	static boolean flagset = true;
	static int zscore1 = 0;

	public LinearRegression(int val, boolean myflag) {
		zscore1 = val;
		flagset = myflag;
	}

	public LinearRegression(boolean flag) {
		flagset = flag;
	}

	public static void linearRegression() throws Exception {
		Matrix trainingData = MatrixData
				.getDataMatrix("data\\linear_regression\\linear-regression-train.csv");
		// getMatrix(Initial row index, Final row index, Initial column index,
		// Final column index)
		Matrix train_x = trainingData.getMatrix(0,
				trainingData.getRowDimension() - 1, 0,
				trainingData.getColumnDimension() - 2);
		Matrix train_y = trainingData.getMatrix(0,
				trainingData.getRowDimension() - 1,
				trainingData.getColumnDimension() - 1,
				trainingData.getColumnDimension() - 1);

		Matrix testData = MatrixData
				.getDataMatrix("data\\linear_regression\\linear-regression-test.csv");
		Matrix test_x = testData.getMatrix(0, testData.getRowDimension() - 1,
				0, testData.getColumnDimension() - 2);
		Matrix test_y = testData.getMatrix(0, testData.getRowDimension() - 1,
				testData.getColumnDimension() - 1,
				testData.getColumnDimension() - 1);

		/* Linear Regression */
		/* 2 step process */
		// 1) find beta
		if(zscore1 == 1)
		{
			train_x = zscore(train_x);
			test_x = zscore(test_x);
		}
		Matrix beta = getBeta(train_x, train_y);
		// 2) predict y for test data using beta calculated from train data
		Matrix predictedY = test_x.times(beta);
		getmeansquareerror(test_x, test_y, beta);
		// Output
		printOutput(predictedY);
		
	}

	private static Matrix zscore(Matrix matrixx) {
		double[] computevalues = new double[matrixx.getRowDimension()];
		Matrix zscorematrix = new Matrix(matrixx.getRowDimension(),
				matrixx.getColumnDimension());
		double mean = 0.0, sd = 0.0;
		double zscore = 0.0;
		for (int j = 0; j < matrixx.getColumnDimension(); j++) {
			for (int i = 0; i < matrixx.getRowDimension(); i++)
				computevalues[i] = matrixx.get(i, j);

			mean = calculatemean(computevalues);
			sd = calculatesd(computevalues, mean);
			if (sd != 0) {
				for (int k = 0; k < computevalues.length; k++) {
					zscore = (computevalues[k] - mean) / sd;
					zscorematrix
							.set(k, j, Math.round(zscore * 10000D) / 10000D);
				}
			} else {
				for (int k = 0; k < computevalues.length; k++) {
					zscorematrix.set(k, j, computevalues[k]);
				}
			}
		}
		return zscorematrix;
	}

	private static double calculatemean(double[] computevalues) {
		double total = 0.0;
		for (double c : computevalues)
			total += c;
		return total / computevalues.length;
	}

	private static double calculatesd(double[] computevalues, double meanvalue) {
		double result = 0.0;
		for (double c : computevalues)
			result += Math.pow((c - meanvalue), 2);
		double variance = result / computevalues.length;
		return Math.sqrt(variance);
	}

	private static void getmeansquareerror(Matrix test_x, Matrix test_y,
			Matrix beta) throws Exception {
		Matrix xbetaminusy = (test_x.times(beta)).minus(test_y);
		Matrix xbetatranspose = xbetaminusy.transpose();
		Matrix mse = xbetatranspose.times(xbetaminusy);
		FileWriter fStream;
		if (flagset)
			fStream = new FileWriter(
					"output\\linear_regression\\mean-square-error-closedform.txt"); // Output
		else
			fStream = new FileWriter(
					"output\\linear_regression\\mean-square-error-online.txt");
		BufferedWriter out = new BufferedWriter(fStream);
		for (int row = 0; row < mse.getRowDimension(); row++) {
			double finalvalue = mse.get(row, 0) / test_x.getRowDimension();
			out.write(String.valueOf(finalvalue));
			out.newLine();
		}
		out.close();
	}

	/**
	 * @params: X and Y matrix of training data returns value of beta calculated
	 *          using the formula beta = (X^T*X)^ -1)*(X^T*Y)
	 */
	private static Matrix getBeta(Matrix trainX, Matrix trainY)
			throws Exception {
		if (flagset)
			return closedform(trainX, trainY);
		else
			return onlinesolution(trainX, trainY);

	}

	private static Matrix onlinesolution(Matrix trainX, Matrix trainY)
			throws Exception {
		double eta = 0.001;
		int i = 0, a = 0;
		Random rand = new Random();
		double randomValue = (-1) + (1 - (-1)) * rand.nextDouble();
		double[] value = new double[trainX.getColumnDimension()];
		Arrays.fill(value, randomValue);
		Matrix beta = new Matrix(value, value.length);
		int[] rows = new int[1], cols = new int[trainX.getColumnDimension()];
		for (a = 0; a < trainX.getColumnDimension(); a++) {
			cols[a] = a;
		}
		for (i = 0; i < trainX.getRowDimension(); i++) {
			rows[0] = i;
			double slopeofmatrix = trainY.get(i, 0)
					- trainX.getMatrix(rows, cols).times(beta).get(0, 0);
			beta = beta.plus(trainX.getMatrix(rows, cols).transpose()
					.times(2 * eta * slopeofmatrix));
		}
		displaybeta(beta, 2);
		return beta;
	}

	private static void displaybeta(Matrix beta, int val) throws Exception {
		FileWriter fStream; // Output File
		if (val == 1)
			fStream = new FileWriter(
					"output\\linear_regression\\beta-closedform.txt");
		else
			fStream = new FileWriter(
					"output\\linear_regression\\beta-onlinesolution.txt");
		BufferedWriter out = new BufferedWriter(fStream);
		for (int row = 0; row < beta.getRowDimension(); row++) {
			out.write(String.valueOf(beta.get(row, 0)));
			out.newLine();
		}
		out.close();

	}

	private static Matrix closedform(Matrix trainX, Matrix trainY)
			throws Exception {
		Matrix leftside = ((trainX.transpose()).times(trainX)).inverse();
		Matrix rightside = (trainX.transpose()).times(trainY);
		Matrix beta = leftside.times(rightside);
		displaybeta(beta, 1);
		return beta;
	}

	/**
	 * @params: predicted Y matrix outputs the predicted y values to the text
	 *          file named "linear-regression-output"
	 */
	public static void printOutput(Matrix predictedY) throws IOException {
		FileWriter fStream = new FileWriter(
				"output\\linear_regression\\linear-regression-output.txt"); // Output
		// File
		BufferedWriter out = new BufferedWriter(fStream);
		for (int row = 0; row < predictedY.getRowDimension(); row++) {
			out.write(String.valueOf(predictedY.get(row, 0)));
			out.newLine();
		}
		out.close();
	}
}
