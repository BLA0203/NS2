package ann.network;

import ann.network.transferFunction.LogSigmoidTF;
import ann.network.transferFunction.ReLUTF;
import ann.network.transferFunction.TransferFunction;

import java.util.Random;

/**
 * This class represents single neuron, which is part of layer
 * It contains weights, current output - for next layer as input, error for calculation (adaption phase)
 */
public class Neuron {
    private double[] weights;
    private double output;
    private double error;
    private double[] errors;
    //
    private TransferFunction transferFunction;
    private TransferFunction hiddenTransferFuction;

    public Neuron() {
        this.transferFunction = new LogSigmoidTF();
        this.hiddenTransferFuction = new ReLUTF();
    }

    //Testing constructor - using sigmoid
    public Neuron(int weightVectorSize) {
        Random r = new Random();
        //Create random weights
        this.weights = new double[weightVectorSize];
        this.errors = new double[weights.length];
        for (int i = 0; i < weightVectorSize; i++) {
            this.weights[i] = r.nextDouble();
        }
        //Add random weight for biasWeight
        this.transferFunction = new LogSigmoidTF();
        this.hiddenTransferFuction = new ReLUTF();
    }

    //Standard constructor - using sigmoid
    public Neuron(double[] weights) {
        this.weights = weights;
        this.errors = new double[weights.length];
        this.transferFunction = new LogSigmoidTF();
        this.hiddenTransferFuction = new ReLUTF();
        this.output = 0;
        this.error = 0;
    }

    //Prepared constructor in case of another transfer functions need
    public Neuron(double[] weights, double bias, TransferFunction transferFunction) {
        this.weights = weights;
        this.transferFunction = transferFunction;
    }

    public double calculateOutput(double[] input, double transFunctionSlope, boolean hidden, double bias) throws Exception {
        double sum = 0;

        if (input.length != weights.length) {
            throw new Exception("Input vector is different from weights vector!");
        }

        for (int index = 0; index < weights.length; index++) {
            sum += input[index] * weights[index];
        }
        sum += bias;

        if (hidden) output = hiddenTransferFuction.calculate(sum, transFunctionSlope);
        else output = transferFunction.calculate(sum, transFunctionSlope);
        return output;
    }

    public void randomizeWeights(int weightVectorSize) {
        Random r = new Random();
        //Create random weights
        for (int i = 0; i < weightVectorSize; i++) {
            this.weights[i] = r.nextDouble();
        }
        //Add random weight for biasWeight
        //this.bias = r.nextDouble();
    }

    public TransferFunction getTransferFunction() {
        return transferFunction;
    }

    public void setTransferFunction(TransferFunction transferFunction) {
        this.transferFunction = transferFunction;
    }

    public int getInputSize() {
        return weights.length;
    }

    public double getWeightAt(int index) {
        return weights[index];
    }

    public void setWeightAt(double weight, int index) {
        weights[index] = weight;
    }

    public double getErrorAt(int index) {
        return errors[index];
    }

    public void setErrorAt(double error, int index) {
        errors[index] = error;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

}
