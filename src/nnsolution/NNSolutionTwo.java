package nnsolution;

import java.util.Scanner;

public class NNSolutionTwo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] architecture = scanner.nextLine().split(",");

        int numberOfNeurons = calculateNumberOfNeurons(architecture);

        String[] weights = readWeights(numberOfNeurons, scanner);

        NeuralNetwork neuralNetwork = getBuiltNeuralNetwork(architecture, weights);

        int numberOfInputs = scanner.nextInt();
        scanner.nextLine();

        String[] inputs = readInputs(scanner, numberOfInputs);

        String[] outputs = neuralNetwork.generateOutputs(inputs);

        System.out.println(numberOfInputs);
        for (String output :
                outputs) {
            System.out.println(output);
        }

        scanner.close();
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

    private static NeuralNetwork getBuiltNeuralNetwork(String[] architecture, String[] weights) {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
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
