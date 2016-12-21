package nnsolution4;

import java.util.List;

class NeuralNetworkController {
    private NeuralNetwork4 neuralNetwork;
    private String[] learningSamples;
    private String[] validationSamples;
    private int numberOfEpochs;
    private double braveryFactor;

    NeuralNetworkController(NeuralNetwork4 neuralNetwork, String[] learningSamples,
                            String[] validationSamples, int numberOfEpochs, double braveryFactor) {
        this.neuralNetwork = neuralNetwork;
        this.learningSamples = learningSamples;
        this.validationSamples = validationSamples;
        this.numberOfEpochs = numberOfEpochs;
        this.braveryFactor = braveryFactor;
    }

    String[] getGeneralSquaredErrors() {
        String[] squaredErrors = new String[numberOfEpochs];
        for (int i = 0; i < numberOfEpochs; i++) {
            squaredErrors[i] = calculateSquaredError();
        }
        return squaredErrors;
    }

    private String calculateSquaredError() {
        runNeuralNetworkThroughLearningSamples();
        double error = calculateErrorWithValidationSamples();
        return error + "";
    }

    private double calculateErrorWithValidationSamples() {
        double out = 0;
        int outputSize = 0;

        for (String validationSample :
                validationSamples) {
            List<Double> output = neuralNetwork.generateOutput(validationSample);
            outputSize = output.size();
            out += calculateSquaredOutputErrorAtValidation(output, validationSample);
        }

        out /= validationSamples.length * outputSize;
        return out;
    }

    private void runNeuralNetworkThroughLearningSamples() {
        for (String learningSample :
                learningSamples) {
            List<List<Double>> partialDerivatives = calculatePartialDerivatives(learningSample);

            ListOfDoubleGiver listOfDoubleGiver = new ListOfDoubleGiver(partialDerivatives);
            neuralNetwork.modifyStructure(listOfDoubleGiver);
        }
    }

    private List<List<Double>> calculatePartialDerivatives(String learningSample) {
        System.err.println("CONTROLLER output");
        List<Double> output = neuralNetwork.generateOutput(learningSample);
        System.err.println("CONTROLLER output done");
        System.err.println("CONTROLLER output errors");
        Double[] outputErrors = calculateOutputErrors(output, learningSample);
        System.err.println("CONTROLLER output errors done");
        return neuralNetwork.generatePartialDerivatives(outputErrors);
    }

    private Double[] calculateOutputErrors(List<Double> output, String learningSample) {
        System.err.println("CONTROLLER calculating error*mu*2 product (last neuron delta values):");

        int outputSize = output.size();
        Double[] out = new Double[outputSize];
        Double[] expectedOutputs = getExpectedOutputs(learningSample, outputSize);

        for (int i = 0; i < outputSize; i++) {
            out[i] = (expectedOutputs[i] - output.get(i)) * braveryFactor * 2;
            System.err.println(out[i] + " = (" + expectedOutputs[i] + " - " + output.get(i) + ") * " + braveryFactor + " * 2");
        }
        return out;
    }

    private Double[] getExpectedOutputs(String learningSample, int size) {
        Double[] expectedOutputs = new Double[size];
        String[] splitLearningSample = learningSample.split(",");
        int numberOfInputs = splitLearningSample.length - size;

        for (int i = 0; i < size; i++) {
            int indexOfOutput = i + numberOfInputs;
            expectedOutputs[i] = Double.parseDouble(splitLearningSample[indexOfOutput]);
        }

        return expectedOutputs;
    }

    private double calculateSquaredOutputErrorAtValidation(List<Double> output, String validationSample) {
        double out = 0;

        int outputSize = output.size();
        Double[] expectedOutputs = getExpectedOutputs(validationSample, outputSize);

        for (int i = 0; i < outputSize; i++) {
            double error = expectedOutputs[i] - output.get(i);
            error *= error;
            out += error;
        }

        return out;
    }
}
