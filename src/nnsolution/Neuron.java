package nnsolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Neuron {
    private double bias;
    private List<Double> weightsBefore;
    private List<Neuron> neuronsBefore;
    private Double output;

    void setOutput(Double output) {
        this.output = output;
    }

    Neuron(double bias, int numberOfNeuronsBefore) {
        this.bias = bias;

        if (numberOfNeuronsBefore != 0) {
            generateWeights(numberOfNeuronsBefore);
        }
    }

    Neuron(int numberOfNeuronsBefore, String values, List<Neuron> neurons) {
        String[] splitValues = values.split(",");

        this.bias = 0;

        if (numberOfNeuronsBefore == 0)
            return;

        neuronsBefore = neurons;
        this.bias = Double.parseDouble(splitValues[splitValues.length - 1]);
        addWeights(Arrays.copyOfRange(splitValues, 0, splitValues.length - 1));
    }

    private void addWeights(String[] weights) {
        weightsBefore = new ArrayList<>();
        for (String weight :
                weights) {
            weightsBefore.add(Double.parseDouble(weight));
        }
    }

    private void generateWeights(int numberOfNeuronsBefore) {
        Random random = new Random();
        weightsBefore = new ArrayList<>(numberOfNeuronsBefore);
        for (int i = 0; i < numberOfNeuronsBefore; i++) {
            weightsBefore.add(random.nextGaussian() * 0.1);
        }
    }

    void printBefore() {
        for (double d:
             weightsBefore) {
            System.out.print(d + ",");
        }
    }

    void printBias() {
        System.out.print(bias);
    }

    boolean canBePrinted() {
        return weightsBefore != null;
    }

    double calculateLastNeuronOutput() {
        if (output != null) {
            return output;
        }

        return calculateHiddenNeuronOutput();
    }

    private double calculateNeuronOutput() {
        if (output != null) {
            return output;
        }

        double out = calculateHiddenNeuronOutput();
        out = relu(out);
        return out;
    }

    private double relu(double out) {
        out = out < 0 ? 0 : out;
        return out;
    }

    private double calculateHiddenNeuronOutput() {
        double out = bias;
        for (int i = 0; i < neuronsBefore.size(); i++) {
            out += neuronsBefore.get(i).calculateNeuronOutput() * weightsBefore.get(i);
        }

        return out;
    }

    String getValues() {
        String out = "";
        for (Double weight :
                weightsBefore) {
            out += weight + ",";
        }

        out += bias;

        return out;
    }
}
