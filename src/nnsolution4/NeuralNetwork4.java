package nnsolution4;

import nnsolution.RowGiver;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class NeuralNetwork4 {
    private List<List<Neuron4>> neuronLayers = new ArrayList<>();

    void build2(String[] layerSizes, String[] weights) {
        generateNeuronLayers2(layerSizes, weights);
        System.err.println("Neural network built---------------");
    }

    List<Double> generateOutput(String input) {
        System.err.println("Generating output for input: " + input);

        String[] splitInput = input.split(",");
        initializeFirstLayerNeuronOutputs(splitInput);
        List<Double> out = generateOutputForInput();

        System.err.println("Done generating output for input: " + input);
        System.err.println("The output is: "); out.forEach(d->System.err.print(d + ","));
        System.err.println();
        return out;
    }

    private List<Double> generateOutputForInput() {
        List<Double> out = new ArrayList<>();
        List<Neuron4> lastNeuronLayer = neuronLayers.get(neuronLayers.size() - 1);

        out.addAll(lastNeuronLayer.stream().map(Neuron4::calculateLastNeuronOutput).collect(Collectors.toList()));

        return out;
    }

    List<List<Double>> generatePartialDerivatives(Double[] outputErrors) {
        System.err.println("Generating partial derivatives. Number of neuron layers: " + neuronLayers.size());

        List<List<Double>> partialDerivatives = new ArrayList<>(neuronLayers.size() - 1);
        List<List<Double>> deltas = calculateDeltas(outputErrors);

        for (int i = 1; i < neuronLayers.size(); i++) {
            generatePartialDerivativeInLayer(i, partialDerivatives, deltas);
        }

        System.err.println("Done generating partial derivatives. Generated " + partialDerivatives.size() + " rows.");

        return partialDerivatives;
    }

    private void generatePartialDerivativeInLayer(int layerIndex, List<List<Double>> out, List<List<Double>> deltas) {
        System.err.println("Generating partial derivatives in layer " + layerIndex);
        System.err.println("There are " + out.size() + " rows with partial derivatives generated so far.");

        List<Neuron4> actualLayer = neuronLayers.get(layerIndex);
        int deltaIndexBasedOnLayerIndex = layerIndex - 1;

        for (int i = 0; i < actualLayer.size(); i++) {
            Neuron4 actualNeuron = actualLayer.get(i);
            Double actualDelta = deltas.get(deltaIndexBasedOnLayerIndex).get(i);
            List<Double> actualNeuronDerivatives = actualNeuron.generatePartialDerivatives(actualDelta);

            out.add(actualNeuronDerivatives);
        }

        System.err.println("Generated partial derivatives in layer " + layerIndex + " number of rows: " + out.size());
    }

    private void generateNeuronLayers2(String[] layerSizes, String[] weights) {
        System.err.println("Generating " + layerSizes.length + " neuron layers.(2)");

        RowGiver rowGiver = new RowGiver(weights);

        for (int i = 0; i < layerSizes.length; i++) {
            int layerSize = Integer.parseInt(layerSizes[i]);
            int numberOfNeuronsBefore = getNumberOfNeuronsBeforeLayer(i, layerSizes);

            System.err.println("Starting to generate layer with index " + i);

            if (i == 0)
                rowGiver.disableGiving();
            else if (i == 1)
                rowGiver.enableGiving();

            neuronLayers.add(generateLayer2(layerSize, numberOfNeuronsBefore, rowGiver));


        }

        System.err.println("Generated " + neuronLayers.size() + " neuron layers.(2)");
    }

    private List<Neuron4> generateLayer2(int layerSize, int numberOfNeuronsBefore, RowGiver rowGiver) {
        System.err.println("Generating layer with size " + layerSize + " and " + numberOfNeuronsBefore + " neurons before.");

        List<Neuron4> layer = new ArrayList<>(layerSize);
        for (int i = 0; i < layerSize; i++) {
            List<Neuron4> neuronLayerBefore = getNeuronLayerBefore(numberOfNeuronsBefore);
            layer.add(new Neuron4(numberOfNeuronsBefore, rowGiver.give(), neuronLayerBefore));
        }

        System.err.println("Finished generating layer with size " + layer.size());
        return layer;
    }

    private List<Neuron4> getNeuronLayerBefore(int numberOfNeuronsBefore) {
        return numberOfNeuronsBefore == 0 ? null : neuronLayers.get(neuronLayers.size() - 1);
    }

    private int getNumberOfNeuronsBeforeLayer(int i, String[] layerSizes) {
        return i == 0 ? 0 : Integer.parseInt(layerSizes[i - 1]);
    }

    private void initializeFirstLayerNeuronOutputs(String[] input) {
        List<Neuron4> firstLayer = neuronLayers.get(0);
        for (int i = 0; i < firstLayer.size(); i++) {
            firstLayer.get(i).setFirstNeuronInput(Double.parseDouble(input[i]));
        }
    }

    private double weightBetween(int j, int i, int layer) {
        List<Neuron4> actualNeuronLayer = neuronLayers.get(layer);

        Neuron4 actualNeuron = actualNeuronLayer.get(j);

        return actualNeuron.getWeight(i);
    }

    private List<List<Double>> calculateDeltas(Double[] outputErrors) {
        System.err.println("Calculating deltas.");

        List<List<Double>> deltas = new ArrayList<>();
        int i = neuronLayers.size() - 2;

        List<Double> lastDeltaLayer = calculateLastDelta(outputErrors);
        deltas.add(lastDeltaLayer);
        List<Double> lastCalculatedDeltas = lastDeltaLayer;

        // calc at first layer not needed!
        while (i > 0) {
            List<Double> newCalculatedDeltas = calculateDeltasForLayer(i, lastCalculatedDeltas);

            deltas.add(newCalculatedDeltas);

            lastCalculatedDeltas = newCalculatedDeltas;
            i--;
        }

        Collections.reverse(deltas);

        System.err.println("Done calculating deltas: "); deltas.forEach(System.err::println);
        return deltas;
    }

    private List<Double> calculateDeltasForLayer(int layerIndex, List<Double> lastCalculatedDeltas) {
        System.err.println("Calculating deltas for layer " + layerIndex);

        List<Double> deltasInLayer = new ArrayList<>();
        List<Neuron4> actualLayer = neuronLayers.get(layerIndex);

        for (int j = 0; j < actualLayer.size(); j++) {
            double sum = sumUpDeltasAndWeights(lastCalculatedDeltas, layerIndex, j);
            sum *= actualLayer.get(j).getDerivatedOutput();

            deltasInLayer.add(sum);
        }

        System.err.println("Done calculating deltas for layer " + layerIndex);

        return deltasInLayer;
    }

    private double sumUpDeltasAndWeights(List<Double> lastCalculatedDeltas, int layerBeforeIndex,
                                         int indexOfActualNeuron) {
        System.err.println("Summing up deltas and weights. LayerBeforeIndex: " + layerBeforeIndex + ". Actual neuron: " + indexOfActualNeuron);

        double sum = 0;

        for (int i = 0; i < lastCalculatedDeltas.size(); i++) {
            sum += lastCalculatedDeltas.get(i) * weightBetween(i, indexOfActualNeuron, layerBeforeIndex + 1);
        }

        System.err.println("Done summing up deltas and weights. Sum: " + sum + " layer/neuron: " + layerBeforeIndex + "/" + indexOfActualNeuron);

        return sum;
    }

    private List<Double> calculateLastDelta(Double[] outputErrors) {
        System.err.println("Setting last" + outputErrors.length + " deltas.");

        int size = outputErrors.length;
        List<Double> lastValue = new ArrayList<>(size);

        lastValue.addAll(Arrays.asList(outputErrors));

        System.err.println("Done setting last deltas. There are " + lastValue.size());
        return lastValue;
    }

    void modifyStructure(ListOfDoubleGiver partialDerivativesGiver) {
        System.err.println("Modifying structure.");

        for (List<Neuron4> neuronLayer :
                neuronLayers) {
            modifyLayer(neuronLayer, partialDerivativesGiver);
        }

        System.err.println("Done modifying structure.");
    }

    private void modifyLayer(List<Neuron4> neuronLayer, ListOfDoubleGiver partialDerivativesGiver) {
        System.err.println("Modifying a layer with size of: " + neuronLayer.size());

        for (Neuron4 neuron :
                neuronLayer) {
            neuron.modifyValues(partialDerivativesGiver);
        }

        System.err.println("Done modifying layer with size of: " + neuronLayer.size());
    }

    void printStructure() {
        System.err.println("Printing current structure (to out).");
        for (List<Neuron4> layer :
                neuronLayers) {
            layer.forEach(Neuron4::printValues);
        }
        System.err.println("Done printing current structure (to out).");
    }

    void printStructureToFile(PrintWriter printWriter) {
        for (List<Neuron4> layer :
                neuronLayers) {
            layer.forEach(neuron -> neuron.printValuesToFile(printWriter));
        }
    }
}
