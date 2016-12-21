package nnsolution3;

import nnsolution.RowGiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class NeuralNetwork3 {
    private List<List<Neuron3>> neuronLayers = new ArrayList<>();

    void build2(String[] layerSizes, String[] weights) {
        generateNeuronLayers2(layerSizes, weights);
        //System.err.println("Neural network built---------------");
    }

    String[] generateOutputs(String[] inputs) {
        int numberOfInputs = inputs.length;
        //System.err.println("Started generating " + numberOfInputs + " outputs.");

        String[] outputs = new String[numberOfInputs];

        for (int i = 0; i < numberOfInputs; i++) {
            //System.err.println("Setting input to: " + inputs[i]);

            String[] splitInputs = inputs[i].split(",");
            initializeFirstLayerNeuronOutputs(splitInputs);
            outputs[i] = generateOutputForInput();
        }

        //System.err.println("Ended generating " + outputs.length + " outputs.-----------------------");
        return outputs;
    }

    List<String> generatePartialDerivatives() {
        //System.err.println("Generating partial derivatives. Number of neuron layers: " + neuronLayers.size());

        List<String> partialDerivatives = new ArrayList<>(neuronLayers.size() - 1);
        List<List<Double>> deltas = calculateDeltas();

        for (int i = 1; i < neuronLayers.size(); i++) {
            generatePartialDerivativeInLayer(i, partialDerivatives, deltas);
        }

        //System.err.println("Done generating partial derivatives. Generated " + partialDerivatives.size() + " rows.");

        return partialDerivatives;
    }

    private void generatePartialDerivativeInLayer(int layerIndex, List<String> out, List<List<Double>> deltas) {
        //System.err.println("Generating partial derivatives in layer " + layerIndex);
        //System.err.println("There are " + out.size() + " rows with partial derivatives generated so far.");

        List<Neuron3> actualLayer = neuronLayers.get(layerIndex);
        int deltaIndexBasedOnLayerIndex = layerIndex - 1;

        for (int i = 0; i < actualLayer.size(); i++) {
            Neuron3 actualNeuron = actualLayer.get(i);
            Double actualDelta = deltas.get(deltaIndexBasedOnLayerIndex).get(i);
            String row = actualNeuron.generatePartialDerivatives(actualDelta);

            out.add(row);
        }

        //System.err.println("Generated partial derivatives in layer " + layerIndex + " number of rows: " + out.size());
    }

    private void generateNeuronLayers2(String[] layerSizes, String[] weights) {
        //System.err.println("Generating " + layerSizes.length + " neuron layers.(2)");

        RowGiver rowGiver = new RowGiver(weights);

        for (int i = 0; i < layerSizes.length; i++) {
            int layerSize = Integer.parseInt(layerSizes[i]);
            int numberOfNeuronsBefore = getNumberOfNeuronsBeforeLayer(i, layerSizes);

            //System.err.println("Starting to generate layer with index " + i);

            if (i == 0)
                rowGiver.disableGiving();
            else if (i == 1)
                rowGiver.enableGiving();

            neuronLayers.add(generateLayer2(layerSize, numberOfNeuronsBefore, rowGiver));


        }

        //System.err.println("Generated " + neuronLayers.size() + " neuron layers.(2)");
    }

    private List<Neuron3> generateLayer2(int layerSize, int numberOfNeuronsBefore, RowGiver rowGiver) {
        //System.err.println("Generating layer with size " + layerSize + " and " + numberOfNeuronsBefore + " neurons before.");

        List<Neuron3> layer = new ArrayList<>(layerSize);
        for (int i = 0; i < layerSize; i++) {
            List<Neuron3> neuronLayerBefore = getNeuronLayerBefore(numberOfNeuronsBefore);
            layer.add(new Neuron3(numberOfNeuronsBefore, rowGiver.give(), neuronLayerBefore));
        }

        //System.err.println("Finished generating layer with size " + layer.size());
        return layer;
    }

    private List<Neuron3> getNeuronLayerBefore(int numberOfNeuronsBefore) {
        return numberOfNeuronsBefore == 0 ? null : neuronLayers.get(neuronLayers.size() - 1);
    }

    private int getNumberOfNeuronsBeforeLayer(int i, String[] layerSizes) {
        return i == 0 ? 0 : Integer.parseInt(layerSizes[i - 1]);
    }

    private String generateOutputForInput() {
        //System.err.println("Generating output for particular input.");

        String out = "";
        List<Neuron3> lastNeuronLayer = neuronLayers.get(neuronLayers.size() - 1);
        for (Neuron3 neuron :
                lastNeuronLayer) {
            out += neuron.calculateLastNeuronOutput() + ",";
        }
        out = out.substring(0, out.length() - 1);

        //System.err.println("Generated " + out + " output.");
        return out;
    }

    private void initializeFirstLayerNeuronOutputs(String[] input) {
        List<Neuron3> firstLayer = neuronLayers.get(0);
        for (int i = 0; i < firstLayer.size(); i++) {
            firstLayer.get(i).setFirstNeuronInput(Double.parseDouble(input[i]));
        }
    }

    private double weightBetween(int j, int i, int layer) {
        List<Neuron3> actualNeuronLayer = neuronLayers.get(layer);

        Neuron3 actualNeuron = actualNeuronLayer.get(j);

        return actualNeuron.getWeight(i);
    }

    private List<List<Double>> calculateDeltas() {
        //System.err.println("Calculating deltas.");

        List<List<Double>> deltas = new ArrayList<>();
        int i = neuronLayers.size() - 2;

        List<Double> lastDeltaLayer = calculateLastDelta();
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

        //System.err.println("Done calculating deltas: "); deltas.forEach(//System.err::println);
        return deltas;
    }

    private List<Double> calculateDeltasForLayer(int layerIndex, List<Double> lastCalculatedDeltas) {
        //System.err.println("Calculating deltas for layer " + layerIndex);

        List<Double> deltasInLayer = new ArrayList<>();
        List<Neuron3> actualLayer = neuronLayers.get(layerIndex);

        for (int j = 0; j < actualLayer.size(); j++) {
            double sum = sumUpDeltasAndWeights(lastCalculatedDeltas, layerIndex, j);
            sum *= actualLayer.get(j).getDerivatedOutput();

            deltasInLayer.add(sum);
        }

        //System.err.println("Done calculating deltas for layer " + layerIndex);

        return deltasInLayer;
    }

    private double sumUpDeltasAndWeights(List<Double> lastCalculatedDeltas, int layerBeforeIndex,
                                         int indexOfActualNeuron) {
        //System.err.println("Summing up deltas and weights. LayerBeforeIndex: " + layerBeforeIndex + ". Actual neuron: " + indexOfActualNeuron);

        double sum = 0;

        for (int i = 0; i < lastCalculatedDeltas.size(); i++) {
            sum += lastCalculatedDeltas.get(i) * weightBetween(i, indexOfActualNeuron, layerBeforeIndex + 1);
        }

        //System.err.println("Done summing up deltas and weights. Sum: " + sum + " layer/neuron: " + layerBeforeIndex + "/" + indexOfActualNeuron);

        return sum;
    }

    private List<Double> calculateLastDelta() {
        List<Double> lastValue = new ArrayList<>(1);
        lastValue.add(1.0);
        return lastValue;
    }
}
