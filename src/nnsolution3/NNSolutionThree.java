package nnsolution3;

import java.util.List;
import java.util.Scanner;

public class NNSolutionThree {

    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);
        String row = scanner.nextLine();
        String[] architecture = row.split(",");

        int numberOfNeurons = calculateNumberOfNeurons(architecture);

        String[] weights = readWeights(numberOfNeurons, scanner);

        NeuralNetwork3 neuralNetwork = getBuiltNeuralNetwork(architecture, weights);

        int numberOfInputs = scanner.nextInt();
        scanner.nextLine();

        String[] inputs = readInputs(scanner, numberOfInputs);

        scanner.close();

        neuralNetwork.generateOutputs(inputs);

        List<String> partialDerivatives = neuralNetwork.generatePartialDerivatives();

        System.out.println(row);
        partialDerivatives.forEach(System.out::println);
    }

    private static String[] readInputs(Scanner scanner, int numberOfInputs) {
        String[] inputs = new String[numberOfInputs];

        for (int i = 0; i < numberOfInputs; i++) {
            inputs[i] = scanner.nextLine();
        }

        return inputs;
    }

    private static int calculateNumberOfNeurons(String[] architecture) {
        int numberOfNeurons = 0;
        for (int i = 1; i < architecture.length; i++) {
            numberOfNeurons += Integer.parseInt(architecture[i]);
        }
        return numberOfNeurons;
    }

    private static NeuralNetwork3 getBuiltNeuralNetwork(String[] architecture, String[] weights) {
        NeuralNetwork3 neuralNetwork = new NeuralNetwork3();
        neuralNetwork.build2(architecture, weights);
        return neuralNetwork;
    }

    private static String[] readWeights(int numberOfNeurons, Scanner scanner) {
        String[] out = new String[numberOfNeurons];
        for (int i = 0; i < numberOfNeurons; i++) {
            out[i] = scanner.nextLine();
        }
        return out;
    }
}
