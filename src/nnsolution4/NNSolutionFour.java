package nnsolution4;

import java.util.Scanner;

public class NNSolutionFour {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String emurRow = scanner.nextLine();
        String[] emur = emurRow.split(",");
        int numberOfEpochs = Integer.parseInt(emur[0]);
        double braveryFactor = Double.parseDouble(emur[1]);
        double rate = Double.parseDouble(emur[2]);

        String architectureRow = scanner.nextLine();
        String[] architecture = architectureRow.split(",");

        int numberOfNeurons = calculateNumberOfNeurons(architecture);

        String[] weights = readWeights(numberOfNeurons, scanner);

        int numberOfSamples = scanner.nextInt();
        scanner.nextLine();
        int numberOfLearningSamples = (int) Math.floor(numberOfSamples * rate);
        int numberOfValidationSamples = numberOfSamples - numberOfLearningSamples;

        String[] learningSamples = readSamples(numberOfLearningSamples, scanner);
        String[] validationSamples = readSamples(numberOfValidationSamples, scanner);

        scanner.close();

        NeuralNetwork4 neuralNetwork = getBuiltNeuralNetwork(architecture, weights);

        NeuralNetworkController neuralNetworkController = new NeuralNetworkController(neuralNetwork, learningSamples,
                validationSamples, numberOfEpochs, braveryFactor);

        String[] squaredErrors = neuralNetworkController.getGeneralSquaredErrors();

        for (String squaredError :
                squaredErrors) {
            System.out.println(squaredError);
        }
        System.out.println(architectureRow);
        neuralNetwork.printStructure();
    }

    private static String[] readSamples(int numberOfLearningSamples, Scanner scanner) {
        String[] samples = new String[numberOfLearningSamples];
        for (int i = 0; i < numberOfLearningSamples; i++) {
            samples[i] = scanner.nextLine();
        }
        return samples;
    }

    private static int calculateNumberOfNeurons(String[] architecture) {
        int numberOfNeurons = 0;
        for (int i = 1; i < architecture.length; i++) {
            numberOfNeurons += Integer.parseInt(architecture[i]);
        }
        return numberOfNeurons;
    }

    private static NeuralNetwork4 getBuiltNeuralNetwork(String[] architecture, String[] weights) {
        NeuralNetwork4 neuralNetwork = new NeuralNetwork4();
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
