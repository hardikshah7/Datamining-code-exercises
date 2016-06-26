import LinearRegression.LinearRegression;
import DecisionTree.DecisionTree;

import java.util.Scanner;

public class Algorithms {
    public static void main(String args[]) throws Exception {
        System.out.println("\tAlgorithms");
        System.out.println("1) Linear Regression closed form");
        System.out.println("2) Linear Regression online updating");
        System.out.println("3) Linear Regression Z-score normalization closed form");
        System.out.println("4) Linear Regression Z-score normalization online updating");
        System.out.println("5) Decision Tree");
        System.out.println("6) Average accuracy based on 5-fold cross-validation");
        System.out.println("7) Gain based Decision Tree");
        System.out.println("8) Gain based Decision Tree with 5-fold cross-validation");
        System.out.println("9) Exit\n");
        System.out.println("Enter the number corresponding to the algorithm you want to run:");
        Scanner in = new Scanner(System.in);

        int choice = in.nextInt();
        switch(choice){
            case 1: LinearRegression cf = new LinearRegression(true);
                    cf.linearRegression();
                    break;
            case 2: LinearRegression os = new LinearRegression(false);
            		os.linearRegression();
            		break;
            case 3: LinearRegression lzcf = new LinearRegression(1, true);
            		lzcf.linearRegression();
            		break;
            case 4: LinearRegression lzs = new LinearRegression(1, false);
    				lzs.linearRegression();
    				break;
            case 5: DecisionTree dt = new DecisionTree();
                    dt.decisionTree();
                    break;
            case 6: DecisionTree cv = new DecisionTree();
            		cv.CrossValidation();
            		break;
            case 7: DecisionTree mydt = new DecisionTree(true);
            		mydt.decisionTree();
            		break;
            case 8: DecisionTree mydtcv = new DecisionTree(true);
    				mydtcv.CrossValidation();
    				break;
            case 9: System.exit(0);
        }
    }
}
