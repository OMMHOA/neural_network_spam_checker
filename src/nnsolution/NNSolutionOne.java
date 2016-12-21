package nnsolution;

import java.util.Scanner;

public class NNSolutionOne {

    public static void main(String[] args) {
	    Scanner scanner = new Scanner(System.in);
        String row = scanner.nextLine();
        scanner.close();

        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.build(row);

        System.out.println(row);
        neuralNetwork.printWeights();
    }
}
