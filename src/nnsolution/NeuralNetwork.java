package nnsolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NeuralNetwork {
    private List<List<Neuron>> neuronLayers = new ArrayList<>();

    public void build(String architecture) {
        String[] layerSizes = architecture.split(",");

        //System.err.println("Started building NeuralNetwork.");

        generateNeuronLayers(layerSizes);
    }

    void printWeights() {
        neuronLayers.forEach(this::printLayerWeights);
    }

    private void printLayerWeights(List<Neuron> neuronLayer) {
        neuronLayer.forEach(this::printNeuronValues);
    }

    private void printNeuronValues(Neuron neuron) {
        if (neuron.canBePrinted()) {
            neuron.printBefore();
            neuron.printBias();
            System.out.println();
        }
    }

    private void generateNeuronLayers(String[] layerSizes) {
        //System.err.println("Generating neuron layers.");

        for (int i = 0; i < layerSizes.length; i++) {
            int layerSize = Integer.parseInt(layerSizes[i]);
            int numberOfNeuronsBefore = getNumberOfNeuronsBeforeLayer(i, layerSizes);
            neuronLayers.add(generateLayer(layerSize, numberOfNeuronsBefore));
        }

        //System.err.println("Finished generating neuron layers. There are " + neuronLayers.size() + " in the list");
    }

    private int getNumberOfNeuronsBeforeLayer(int i, String[] layerSizes) {
        return i == 0 ? 0 : Integer.parseInt(layerSizes[i - 1]);
    }

    private List<Neuron> generateLayer(int layerSize, int numberOfNeuronsBefore) {
        //System.err.println("Generating layer with size of " + layerSize);

        List<Neuron> layer = new ArrayList<>(layerSize);
        for (int j = 0; j < layerSize; j++) {
            layer.add(new Neuron(0, numberOfNeuronsBefore));
        }

        //System.err.println("Generated layer with size of" + layer.size());

        return layer;
    }

    void build2(String[] layerSizes, String[] weights) {
        generateNeuronLayers2(layerSizes, weights);
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

    private List<Neuron> generateLayer2(int layerSize, int numberOfNeuronsBefore, RowGiver rowGiver) {
        //System.err.println("Generating layer with size " + layerSize + " and " + numberOfNeuronsBefore + " neurons before.");

        List<Neuron> layer = new ArrayList<>(layerSize);
        for (int i = 0; i < layerSize; i++) {
            List<Neuron> neuronLayerBefore = getNeuronLayerBefore(numberOfNeuronsBefore);
            layer.add(new Neuron(numberOfNeuronsBefore, rowGiver.give(), neuronLayerBefore));
        }

        //System.err.println("Finished generating layer with size " + layer.size());
        return layer;
    }

    private List<Neuron> getNeuronLayerBefore(int numberOfNeuronsBefore) {
        return numberOfNeuronsBefore == 0 ? null : neuronLayers.get(neuronLayers.size() - 1);
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

        //System.err.println("Ended generating " + outputs.length + " outputs.");
        return outputs;
    }

    private String generateOutputForInput() {
        //System.err.println("Generating output for particular input.");

        String out = "";
        List<Neuron> lastNeuronLayer = neuronLayers.get(neuronLayers.size() - 1);
        for (Neuron neuron :
                lastNeuronLayer) {
            out += neuron.calculateLastNeuronOutput() + ",";
        }
        out = out.substring(0, out.length() - 1);

        //System.err.println("Generated " + out + " output.");
        return out;
    }

    private void initializeFirstLayerNeuronOutputs(String[] input) {
        List<Neuron> firstLayer = neuronLayers.get(0);
        for (int i = 0; i < firstLayer.size(); i++) {
            firstLayer.get(i).setOutput(Double.parseDouble(input[i]));
        }
    }

    public List<String> getStructure() {
        List<String> out = new ArrayList<>();

        for (int i = 1; i < neuronLayers.size(); i++) {
            addLayerValuesInto(out, neuronLayers.get(i));
        }

        return out;
    }

    private void addLayerValuesInto(List<String> out, List<Neuron> neuronLayer) {
        out.addAll(neuronLayer.stream().map(Neuron::getValues).collect(Collectors.toList()));
    }
}
