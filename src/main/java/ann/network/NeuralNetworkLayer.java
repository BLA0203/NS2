package ann.network;

import java.util.List;

/**
 * This class represents layer object in neural network
 * It contains collection of its neurons objects, along with information, if layers is output (for backpropagation alg)
 */
public class NeuralNetworkLayer {
    final boolean isOutput;
    final boolean isFirst;
    public double bias;
    public double biasError = 0.0;

    public List<Neuron> getNeurons() {
        return neurons;
    }

    List<Neuron> neurons;

    public NeuralNetworkLayer(boolean isOutput, boolean isFirst, List<Neuron> neurons, double bias) {
        this.isOutput = isOutput;
        this.isFirst = isFirst;
        this.neurons = neurons;
        this.bias = bias;
    }

    public double[] getLayerOutputs() {
        double[] output = new double[neurons.size()];
        for (int index = 0; index < neurons.size(); index++) {
            Neuron neuron = neurons.get(index);
            output[index] = neuron.getOutput();
        }
        return output;
    }

    /*
    public double[] calculateOutputVector(double[] inputVector, double transFunctionSlope) throws Exception {
        double[] output = new double[neurons.size()];
        for (int index = 0; index < neurons.size(); index++) {
            Neuron neuron = neurons.get(index);
            output[index] = neuron.calculateOutput(inputVector, transFunctionSlope);
        }
        //here output acts as input for next layer or as result, depending if layer is output layer or not
        return output;
    }
     */

    public double[] setInputLayerVector(double[] inputVector, double transFunctionSlope) throws Exception {
        for (int index = 0; index < inputVector.length; index++) {
            Neuron neuron = neurons.get(index);
            neuron.setOutput(inputVector[index]);
        }
        return inputVector;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getBiasError() {
        return biasError;
    }

    public void setBiasError(double biasError) {
        this.biasError = biasError;
    }
}
