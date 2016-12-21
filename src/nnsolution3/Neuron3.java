package nnsolution3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Neuron3 {
    private double bias;
    private List<Double> weightsBefore;
    private List<Neuron3> neuronsBefore;
    private Double firstNeuronInput;
    private double output;

    void setFirstNeuronInput(Double firstNeuronInput) {
        this.firstNeuronInput = firstNeuronInput;
        output = firstNeuronInput;
    }

    Neuron3(int numberOfNeuronsBefore, String values, List<Neuron3> neurons) {
        this.bias = 0;

        if (numberOfNeuronsBefore == 0)
            return;

        String[] splitValues = values.split(",");

        neuronsBefore = neurons;
        this.bias = Double.parseDouble(splitValues[splitValues.length - 1]);
        addWeights(Arrays.copyOfRange(splitValues, 0, splitValues.length - 1));
    }

    private double getOutput() {
        return output;
    }

    double calculateLastNeuronOutput() {
        if (firstNeuronInput != null) {
            return firstNeuronInput;
        }

        return calculateHiddenNeuronOutput();
    }

    double getDerivatedOutput() {
        return output > 0 ? 1 : 0;
    }

    double getWeight(int i) {
        return weightsBefore.get(i);
    }

    String generatePartialDerivatives(Double actualDelta) {
        String out = "";

        for (Neuron3 neuronBefore :
                neuronsBefore) {
            double product = actualDelta * neuronBefore.getOutput();
            out += product + ",";
        }

        out += actualDelta;
        return out;
    }

    private void addWeights(String[] weights) {
        weightsBefore = new ArrayList<>();
        for (String weight :
                weights) {
            weightsBefore.add(Double.parseDouble(weight));
        }
    }

    private double calculateNeuronOutput() {
        if (firstNeuronInput != null) {
            return firstNeuronInput;
        }

        output = relu(calculateHiddenNeuronOutput());
        return output;
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
}