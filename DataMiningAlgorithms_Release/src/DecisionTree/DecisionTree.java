package DecisionTree;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Utils;
import Utility.Utility;

/*Class for constructing an unpruned decision tree based on
 the ID3 algorithm. Can only deal with nominal attributes.
 No missing values allowed. Empty leaves may result in unclassified instances.
 */
public class DecisionTree {
	// The node's successors.
	private DecisionTree[] m_Successors;
	// Attribute used for splitting.
	private Attribute m_Attribute;
	// Class value if node is leaf.
	private double m_ClassValue;
	// Class distribution if node is leaf.
	private double[] m_Distribution;
	// Class attribute of data set.
	private Attribute m_ClassAttribute;
	static boolean flag1 = false;

	public DecisionTree() {
	}

	public DecisionTree(boolean flag) {
		flag1 = flag;
	}

	// Builds decision tree classifier.
	public void buildClassifier(Instances data) throws Exception {
		data = new Instances(data);
		this.makeTree(data);
	}

	private void makeTree(Instances data) throws Exception {
		if (data.numInstances() == 0) {
			this.m_Attribute = null;
			this.m_ClassValue = Instance.missingValue();
			this.m_Distribution = new double[data.numClasses()];
		} else {
			double[] infoGains = new double[data.numAttributes()];

			Attribute splitData;
			for (Enumeration attEnum = data.enumerateAttributes(); attEnum
					.hasMoreElements(); infoGains[splitData.index()] = this
					.computeInfoGain(data, splitData)) {
				splitData = (Attribute) attEnum.nextElement();
			}

			this.m_Attribute = data.attribute(Utils.maxIndex(infoGains));
			if (Utils.eq(infoGains[this.m_Attribute.index()], 0.0D)) {
				this.m_Attribute = null;
				this.m_Distribution = new double[data.numClasses()];

				Instance j;
				for (Enumeration var6 = data.enumerateInstances(); var6
						.hasMoreElements(); ++this.m_Distribution[(int) j
						                                          .classValue()]) {
					j = (Instance) var6.nextElement();
				}

				Utils.normalize(this.m_Distribution);
				this.m_ClassValue = (double) Utils
						.maxIndex(this.m_Distribution);
				this.m_ClassAttribute = data.classAttribute();
			} else {
				Instances[] var7 = this.splitData(data, this.m_Attribute);
				this.m_Successors = new DecisionTree[this.m_Attribute
				                                     .numValues()];

				for (int var8 = 0; var8 < this.m_Attribute.numValues(); ++var8) {
					this.m_Successors[var8] = new DecisionTree();
					this.m_Successors[var8].makeTree(var7[var8]);
				}
			}
		}
	}

	// Classifies a given test instance using the decision tree.
	public double classifyInstance(Instance instance)
			throws NoSupportForMissingValuesException {
		if (instance.hasMissingValue()) {
			throw new NoSupportForMissingValuesException(
					"DecisionTree: no missing values, please.");
		} else {
			return this.m_Attribute == null ? this.m_ClassValue
					: this.m_Successors[(int) instance.value(this.m_Attribute)]
							.classifyInstance(instance);
		}
	}

	public String toString() {
		return this.m_Distribution == null && this.m_Successors == null ? "DecisionTree: No model built yet."
				: "DecisionTree\n\n" + this.toString(0);
	}

	// Computes information gain for an attribute.
	private double computeInfoGain(Instances data, Attribute att)
			throws Exception {
		double infoGain = this.computeEntropy(data);
		Instances[] splitData = this.splitData(data, att);
		double infoNeeded = 0.0D;
		double splitInfo = 0.0D;
		double gainRatio = 0.0D;

		for (Instances myinst : splitData) {
			infoNeeded += ((double) myinst.numInstances() / (double) data
					.numInstances()) * this.computeEntropy(myinst);
			double fraction = (double) myinst.numInstances()
					/ (double) data.numInstances();
			if (!(Double.isNaN(fraction)) && fraction != 0
					&& !(Double.isInfinite(fraction)))
				splitInfo += -(fraction) * (Math.log(fraction) / Math.log(2));
		}
		// System.out.println(splitInfo);
		infoGain = infoGain - infoNeeded;
		if (splitInfo != 0)
			gainRatio = infoGain / splitInfo;

		if (flag1)
			return gainRatio;
		else
			return infoGain;
	}

	// Computes the entropy of a dataset.
	private double computeEntropy(Instances data) throws Exception {
		double[] classCounts = new double[data.numClasses()];

		Instance entropy;
		for (Enumeration instEnum = data.enumerateInstances(); instEnum
				.hasMoreElements(); ++classCounts[(int) entropy.classValue()]) {
			entropy = (Instance) instEnum.nextElement();
		}

		double totalEntropy = 0.0D;
		int classNum = data.numClasses();
		double[] classProbVec = new double[classNum];

		for (int j = 0; j < classNum; ++j) {
			if (classCounts[j] > 0.0D) {
				classProbVec[j] = classCounts[j] / data.numInstances();
			} else
				classProbVec[j] = 0;
		}

		for (int i = 0; i < classProbVec.length; i++) {
			if (classProbVec[i] != 0.0)
				totalEntropy += -(classProbVec[i])
				* (Math.log(classProbVec[i]) / Math.log(2));
		}

		return totalEntropy;
	}

	public void CrossValidation() throws Exception {
		BufferedReader file = Utility
				.readFile("data\\decision_tree\\house-votes.arff");
		Instances myinstances = new Instances(file);
		myinstances.setClassIndex(0);
		int sizeoffold = myinstances.numInstances() / 5;
		int start = 0, end = sizeoffold - 1;
		ArrayList<Double> al = new ArrayList<Double>();
		for (int i = 0; i < 5; i++) {
			Instances trainInstances = new Instances(myinstances);
			Instances testInstances = new Instances(myinstances, start,
					(end - start) + 1);
			for (int a = 0; a <= (end - start); a++) {
				trainInstances.delete(start);
			}

			buildClassifier(trainInstances);
			double correctness = 0.0;
			for (int index = 0; index < testInstances.numInstances(); index++) {
				Instance testRowInstance = testInstances.instance(index);
				double prediction = classifyInstance(testRowInstance);
				if (prediction == testRowInstance.classValue()) {
					correctness++;
				}
			}
			al.add(correctness / testInstances.numInstances());
			System.out.println(correctness / testInstances.numInstances());

			start = end + 1;
			end += sizeoffold;
			if (i == (4))
				end = myinstances.numInstances();
		}
		double accuracy = 0.0D;
		double sum = 0.0D;
		for (double d : al)
			sum += d;
		accuracy = sum / 5;
		System.out.println("Average Accuracy: " + accuracy);
	}

	// Splits a dataset according to the values of a nominal attribute.
	private Instances[] splitData(Instances data, Attribute att) {
		Instances[] splitData = new Instances[att.numValues()];

		for (int instEnum = 0; instEnum < att.numValues(); ++instEnum) {
			splitData[instEnum] = new Instances(data, data.numInstances());
		}

		Enumeration var6 = data.enumerateInstances();

		while (var6.hasMoreElements()) {
			Instance i = (Instance) var6.nextElement();
			splitData[(int) i.value(att)].add(i);
		}

		for (int var7 = 0; var7 < splitData.length; ++var7) {
			splitData[var7].compactify();
		}
		return splitData;
	}

	private String toString(int level) {
		StringBuffer text = new StringBuffer();
		if (this.m_Attribute == null) {
			if (Instance.isMissingValue(this.m_ClassValue)) {
				text.append(": null");
			} else {
				text.append(": "
						+ this.m_ClassAttribute.value((int) this.m_ClassValue));
			}
		} else {
			for (int j = 0; j < this.m_Attribute.numValues(); ++j) {
				text.append("\n");

				for (int i = 0; i < level; ++i) {
					text.append("|  ");
				}

				text.append(this.m_Attribute.name() + " = "
						+ this.m_Attribute.value(j));
				text.append(this.m_Successors[j].toString(level + 1));
			}
		}
		return text.toString();
	}

	public void decisionTree() throws Exception {
		BufferedReader file = Utility
				.readFile("data\\decision_tree\\weather-nominal.arff");
		Instances data = new Instances(file);
		int cIdx = data.numAttributes() - 1;
		data.setClassIndex(cIdx);
		buildClassifier(data);
		printOutput(data);
	}

	private void printOutput(Instances data) throws IOException,
	NoSupportForMissingValuesException {
		FileWriter fStream;
		if(!flag1)
		fStream = new FileWriter(
				"output\\decision_tree\\decision-tree-output.txt"); // Output
		else
			fStream = new FileWriter("output\\decision_tree\\output-gain-based.txt");
		// File
		BufferedWriter out = new BufferedWriter(fStream);

		for (int index = 0; index < data.numInstances(); index++) {
			Instance testRowInstance = data.instance(index);
			double prediction = classifyInstance(testRowInstance);
			out.write(data.classAttribute().value((int) prediction));
			out.newLine();
		}
		out.close();
	}
}
