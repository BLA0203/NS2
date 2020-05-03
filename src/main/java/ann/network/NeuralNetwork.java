package ann.network;

import ann.parser.XMLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class does all work for artificial neural network - learning and testing
 * Contains all parameters necessary for correct initialization after training and test set loading
 * Configuration along with trained ANN (weights) can be exported and reinitialized if needed
 */
public class NeuralNetwork {
    boolean learned = false;
    private Double learningRate;
    private final int layerCount;
    private int inputCount;
    private final List<Integer> neuronsInLayer;
    private final List<String> inputNames;
    private final List<String> outputNames;
    //
    private final List<TrainingItem> trainingSet;
    private final List<TestingItem> testingSet;
    //
    private List<NeuralNetworkLayer> neuralNetworkLayers;
    //
    private final List<Double> errorValues = new ArrayList<Double>();

    //Constructor for testing, no data loaded
    public NeuralNetwork(Double learningRate, int layerCount, List<Integer> neuronsInLayer,
                         List<String> inputNames, List<String> outputNames, List<TrainingItem> trainingSet, List<TestingItem> testingSet) {
        this.learningRate = learningRate;
        this.layerCount = layerCount;
        this.neuronsInLayer = neuronsInLayer;
        this.inputNames = new ArrayList<String>(inputNames);
        this.outputNames = new ArrayList<String>(outputNames);
        this.trainingSet = trainingSet;
        this.testingSet = testingSet;
        errorValues.clear();
        //
        this.inputCount = 5;
        //init Neural Network
        this.neuralNetworkLayers = new ArrayList<NeuralNetworkLayer>();
        for (int i = 0; i < layerCount; i++) {
            //Create neurons for hidden layer
            List<Neuron> h_neurons = new ArrayList<Neuron>();
            for (int j = 0; j < 4; j++) {
                Neuron neuron = new Neuron(4);
                h_neurons.add(neuron);
            }
            NeuralNetworkLayer hidden = new NeuralNetworkLayer(false, true, h_neurons, 0.5);
            this.neuralNetworkLayers.add(hidden);
            //Create neurons for output layer
            List<Neuron> out_neurons = new ArrayList<Neuron>();
            for (int j = 0; j < 4; j++) {
                Neuron neuron = new Neuron(3);
                out_neurons.add(neuron);
            }
            NeuralNetworkLayer output = new NeuralNetworkLayer(true, false, out_neurons, 0.5);
            this.neuralNetworkLayers.add(output);
        }
        //
    }

    //Standard constructor (xml file source)
    public NeuralNetwork(XMLParser sourceData) {
        //Init parameters
        this.learningRate = sourceData.learningRate;
        this.layerCount = sourceData.layerCount;
        this.neuronsInLayer = sourceData.neuronsInLayer;
        this.inputNames = new ArrayList<String>(sourceData.inputNames);
        this.outputNames = new ArrayList<String>(sourceData.outputNames);
        this.trainingSet = sourceData.trainingSet;
        this.testingSet = sourceData.testingSet;
        this.inputCount = sourceData.inputCount;
        errorValues.clear();
        //Init Neural Network
        this.neuralNetworkLayers = new ArrayList<NeuralNetworkLayer>();
        for (int i = 0; i < layerCount; i++) {
            if (i != layerCount - 1) {
                List<Neuron> neurons = new ArrayList<Neuron>();
                //hidden layers
                if (i == 0) {
                    for (int count = 0; count < this.neuronsInLayer.get(i); count++) {
                        Neuron neuron = new Neuron(getRandomWeights(inputCount, 0, 1));
                        neurons.add(neuron);
                    }
                    NeuralNetworkLayer neuralNetworkLayer = new NeuralNetworkLayer(false, true, neurons, 0.5);
                    this.neuralNetworkLayers.add(neuralNetworkLayer);
                } else {
                    for (int count = 0; count < this.neuronsInLayer.get(i); count++) {
                        Neuron neuron = new Neuron(getRandomWeights(neuronsInLayer.get(i - 1), 0, 1));
                        neurons.add(neuron);
                    }
                    NeuralNetworkLayer neuralNetworkLayer = new NeuralNetworkLayer(false, false, neurons, 0.5);
                    this.neuralNetworkLayers.add(neuralNetworkLayer);
                }
            } else {
                List<Neuron> neurons = new ArrayList<Neuron>();
                //output layers
                for (int count = 0; count < this.neuronsInLayer.get(i); count++) {
                    Neuron neuron = new Neuron(getRandomWeights(neuronsInLayer.get(i - 1), 0, 1));
                    neurons.add(neuron);
                }
                NeuralNetworkLayer neuralNetworkLayer = new NeuralNetworkLayer(true, false, neurons, 0.5);
                this.neuralNetworkLayers.add(neuralNetworkLayer);
            }
        }
        //normalize
        for (TrainingItem tr : this.trainingSet) {
            double divider = 0.0;
            double[] newInputVector = new double[inputCount];
            for (int i = 0; i < tr.getInputVector().length; i++) {
                divider += (tr.getInputVector()[i] * tr.getInputVector()[i]);
            }
            divider = Math.sqrt(divider);
            for (int i = 0; i < tr.getInputVector().length; i++) {
                newInputVector[i] = tr.getInputVector()[i] / divider;
            }
            tr.setInputVector(newInputVector);
        }
    }

    public double[] getRandomWeights(int dimension, double from, double to) {
        double[] weightVector = new double[dimension];

        for (int index = 0; index < dimension; index++) {
            weightVector[index] = randomDouble(from, to);
        }

        return weightVector;
    }

    public static double randomDouble(double from, double to) {
        Random random = new Random();
        return (double) from + (to - from) * random.nextDouble();
    }

    //Train network and update error throughout the learning
    public void trainNetwork(int epochCount) throws Exception {
        for (int i = 0; i < epochCount; i++) {
            double sumError = 0;
            try {
                sumError = bpStep();
            } catch (Exception e) {
                e.printStackTrace();
            }
            errorValues.add(sumError);
            System.out.println("current global error: " + errorValues.get(i) + " , at epoch: " + i);
            if (sumError < 0.01 || i == epochCount - 1) {
                //Network learned or epoch count reached, give results...
                this.learned = true;
                testNetwork();
                break;
            }
        }
    }

    public void testNetwork() throws Exception {
        for (TestingItem testingItem : testingSet) {
            double[] results;
            System.out.println("\n\n----------------------------------------------");
            System.out.println("\nAnswer of neural network for inputs: ");
            for (double a : testingItem.getInputVector()) {
                System.out.println("Input: " + a);
            }
            results = activationPhase(testingItem);
            for (double a : results) {
                System.out.println("Output: " + Math.round(a));
            }
        }
    }

    //One step in backpropagation
    public double bpStep() throws Exception {
        double globalError = 0;
        for (TrainingItem trainItem : trainingSet) {
            double[] results;
            results = activationPhase(trainItem);
            globalError += adaptionPhase(trainItem);
        }
        return globalError;
    }

    public static double meanSquareError(double[] real, double[] expected) {
        double error = 0;
        for (int index = 0; index < real.length; index++) {
            error += Math.pow(real[index] - expected[index], 2);
            //error += (real[index] - Math.pow(expected[index], 2));
        }
        return error / 2.0;
    }


    //contains steps for activation, transfer function and forward propagate to the very output layer
    private double[] activationPhase(TrainingItem item) throws Exception {
        for (int index = 0; index < neuralNetworkLayers.size(); index++) {
            //if first after input...
            if (neuralNetworkLayers.get(index).isFirst) {
                NeuralNetworkLayer currLayer = neuralNetworkLayers.get(index);
                for (Neuron currNeuron : currLayer.neurons) {
                    currNeuron.setOutput(currNeuron.calculateOutput(item.getInputVector(), 1, false, currLayer.getBias()));
                }
            }
            //any other layer, including last, so i need to provide inputs, which are outputs from previous layer
            else {
                NeuralNetworkLayer currLayer = neuralNetworkLayers.get(index);
                double[] currInputs = neuralNetworkLayers.get(index - 1).getLayerOutputs();
                for (Neuron currNeuron : currLayer.neurons) {
                    if (neuralNetworkLayers.get(index).isOutput) {
                        currNeuron.setOutput(currNeuron.calculateOutput(currInputs, 1, false, currLayer.getBias()));
                    } else {
                        currNeuron.setOutput(currNeuron.calculateOutput(currInputs, 1, false, currLayer.getBias()));
                    }
                }
            }
        }
        //Get outputs generated by output layer
        double[] a = neuralNetworkLayers.get(neuralNetworkLayers.size() - 1).getLayerOutputs();
        return neuralNetworkLayers.get(neuralNetworkLayers.size() - 1).getLayerOutputs();
    }

    //TESTING - NOT LEARNING
    private double[] activationPhase(TestingItem item) throws Exception {
        for (int index = 0; index < neuralNetworkLayers.size(); index++) {
            //if first after input...
            if (neuralNetworkLayers.get(index).isFirst) {
                NeuralNetworkLayer currLayer = neuralNetworkLayers.get(index);
                for (Neuron currNeuron : currLayer.neurons) {
                    currNeuron.setOutput(currNeuron.calculateOutput(item.getInputVector(), 1, false, currLayer.getBias()));
                }
            }
            //any other layer, including last, so i need to provide inputs, which are outputs from previous layer
            else {
                NeuralNetworkLayer currLayer = neuralNetworkLayers.get(index);
                double[] currInputs = neuralNetworkLayers.get(index - 1).getLayerOutputs();
                for (Neuron currNeuron : currLayer.neurons) {
                    if (neuralNetworkLayers.get(index).isOutput) {
                        currNeuron.setOutput(currNeuron.calculateOutput(currInputs, 1, false, currLayer.getBias()));
                    } else {
                        currNeuron.setOutput(currNeuron.calculateOutput(currInputs, 1, false, currLayer.getBias()));
                    }
                }
            }
        }
        //Get outputs generated by output layer
        double[] a = neuralNetworkLayers.get(neuralNetworkLayers.size() - 1).getLayerOutputs();
        return neuralNetworkLayers.get(neuralNetworkLayers.size() - 1).getLayerOutputs();
    }


    private double adaptionPhase(TrainingItem currTrainingItem) {
        double E = meanSquareError(this.neuralNetworkLayers.get(this.neuralNetworkLayers.size() - 1).getLayerOutputs(), currTrainingItem.getOutputVector());
        //ERROR CALCULATE
        for (int layerIndex = this.layerCount - 1; layerIndex >= 0; layerIndex--) {
            NeuralNetworkLayer currLayer = this.neuralNetworkLayers.get(layerIndex);
            //get all neurons
            if (currLayer.isOutput) {
                for (int neuronIndex = 0; neuronIndex < currLayer.neurons.size(); neuronIndex++) {
                    //For each neuron i need to calculate derivative error
                    Neuron currN = currLayer.neurons.get(neuronIndex);
                    double currError = (currTrainingItem.getOutputVector()[neuronIndex] - currN.getOutput()) * transfer_derivate(currN.getOutput());
                    currN.setError(currError);
                }
            } else {
                double currPredecessor = 0.0;
                for (int neuronIndex = 0; neuronIndex < currLayer.neurons.size(); neuronIndex++) {
                    //get current predecessor
                    currPredecessor = getPredecessor(layerIndex, neuronIndex);
                    Neuron currN = currLayer.neurons.get(neuronIndex);
                    double currError = currPredecessor * (currN.getOutput() * (1 - currN.getOutput()));
                    currN.setError(currError);
                }
            }
        }
        //WEIGHTS UPDATE
        for (int layerIndex = this.layerCount - 1; layerIndex >= 0; layerIndex--) {
            NeuralNetworkLayer currLayer = neuralNetworkLayers.get(layerIndex);
            for (int neuronIndex = 0; neuronIndex < currLayer.neurons.size(); neuronIndex++) {
                Neuron currN = currLayer.neurons.get(neuronIndex);
                for (int weightIndex = 0; weightIndex < currN.getInputSize(); weightIndex++) {
                    double correspondingInput = 0.0;
                    if (layerIndex - 1 < 0) {
                        correspondingInput = currTrainingItem.getInputVector()[weightIndex];
                    } else {
                        correspondingInput = neuralNetworkLayers.get(layerIndex - 1).neurons.get(weightIndex).getOutput();
                    }
                    double newWeightAddition = learningRate * currN.getError() * correspondingInput;
                    currN.setWeightAt(currN.getWeightAt(weightIndex) + newWeightAddition, weightIndex);
                }
            }
            currLayer.setBias(currLayer.getBias() + (learningRate * currLayer.getBiasError()));
        }
        return E;
    }

    public double getPredecessor(int currLayerIndex, int indexOfWeightAndError) {
        NeuralNetworkLayer up = this.neuralNetworkLayers.get(currLayerIndex + 1);
        double output = 0.0;
        for (int upLayerIndex = 0; upLayerIndex < up.neurons.size(); upLayerIndex++) {
            output += up.neurons.get(upLayerIndex).getError() * up.neurons.get(upLayerIndex).getWeightAt(indexOfWeightAndError);
        }
        return output;
    }

    private double transfer_derivate(double output) {
        return output * (1.0 - output);
    }

}
