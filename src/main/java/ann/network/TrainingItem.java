package ann.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Class "TrainingItem" represent one item from collection of training set
 * It contains inputs and outputs, inputs or output descriptions (names) can be accessed from "NeuralNetwork" class
 */
public class TrainingItem {
    private double[] inputVector;
    private double[] outputVector;

    public double[] getInputVector() {
        return inputVector;
    }

    public void setInputVector(double[] inputVector) {
        this.inputVector = inputVector;
    }

    public double[] getOutputVector() {
        return outputVector;
    }

    public void setOutputVector(double[] outputVector) {
        this.outputVector = outputVector;
    }

    //Assign values when creating setItem
    public TrainingItem() {
    }
}
