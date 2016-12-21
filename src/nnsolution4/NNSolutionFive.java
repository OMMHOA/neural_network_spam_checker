package nnsolution4;

import nnsolution.NeuralNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class NNSolutionFive {
    private static String architectureRow = "57,5,4,1";

    public static void main(String[] args) {
        List<String> randomStructure = generateRandomStructure();
        String[] weights = randomStructure.toArray(new String[randomStructure.size()]);
        printRows(weights);

        String[] architecture = architectureRow.split(",");

        double rate = 0.8;
        int numberOfSamples = 4500;
        double braveryFactor = 0.01;
        int numberOfEpochs = 15;

        int numberOfLearningSamples = (int) Math.floor(numberOfSamples * rate);
        int numberOfValidationSamples = numberOfSamples - numberOfLearningSamples;

        String filename = "spambase_train.csv";
        Scanner scanner = getScanner(filename);

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

        try {
            PrintWriter printWriter = new PrintWriter("nn_solution_five.txt");
            printWriter.println(architectureRow);
            neuralNetwork.printStructureToFile(printWriter);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Scanner getScanner(String filename) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return scanner;
    }

    private static void printRows(String[] weights) {
        System.out.println("Starting structure: ");
        for (String weight : weights) {
            System.out.println(weight);
        }
        System.out.println("Done writing structure out.");
    }

    private static List<String> generateRandomStructure() {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.build(architectureRow);
        return neuralNetwork.getStructure();
    }

    private static String[] readSamples(int numberOfLearningSamples, Scanner scanner) {
        String[] samples = new String[numberOfLearningSamples];
        for (int i = 0; i < numberOfLearningSamples; i++) {
            samples[i] = scanner.nextLine();
        }
        return samples;
    }

    private static NeuralNetwork4 getBuiltNeuralNetwork(String[] architecture, String[] weights) {
        NeuralNetwork4 neuralNetwork = new NeuralNetwork4();
        neuralNetwork.build2(architecture, weights);
        return neuralNetwork;
    }
}
