package nnsolution4;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Neuron4 {
    private double bias;
    private List<Double> weightsBefore;
    private List<Neuron4> neuronsBefore;
    private Double firstNeuronInput;
    private double output;

    void setFirstNeuronInput(Double firstNeuronInput) {
        this.firstNeuronInput = firstNeuronInput;
        output = firstNeuronInput;
    }

    Neuron4(int numberOfNeuronsBefore, String values, List<Neuron4> neurons) {
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

    List<Double> generatePartialDerivatives(Double actualDelta) {
        List<Double> out = new ArrayList<>(neuronsBefore.size());

        for (Neuron4 neuronBefore :
                neuronsBefore) {
            double product = actualDelta * neuronBefore.getOutput();
            out.add(product);
        }

        out.add(actualDelta);
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
        return out < 0 ? 0 : out;
    }

    private double calculateHiddenNeuronOutput() {
        double out = bias;
        for (int i = 0; i < neuronsBefore.size(); i++) {
            out += neuronsBefore.get(i).calculateNeuronOutput() * weightsBefore.get(i);
        }
        return out;
    }

    void modifyValues(ListOfDoubleGiver listOfValuesGiver) {
        if (weightsBefore == null)
            return;

        List<Double> listOfValues = listOfValuesGiver.give();
        for (int i = 0; i < weightsBefore.size(); i++) {
            weightsBefore.set(i, weightsBefore.get(i) + listOfValues.get(i));
        }

        bias += listOfValues.get(listOfValues.size() - 1);
    }

    void printValues() {
        if (weightsBefore == null)
            return;
        for (Double d :
                weightsBefore) {
            System.out.print(d + ",");
        }
        System.out.println(bias);
    }

    void printValuesToFile(PrintWriter printWriter) {
        if (weightsBefore == null)
            return;
        for (Double d :
                weightsBefore) {
            printWriter.print(d + ",");
        }
        printWriter.println(bias);
    }
}