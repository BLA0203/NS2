package ann.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Class "TestingItem" represent one item from collection of testing set
 * It contains input values for testing purposes
 */
public class TestingItem {
    private double[] inputVector;

    public double[] getInputVector() {
        return inputVector;
    }

    public void setInputVector(double[] inputVector) {
        this.inputVector = inputVector;
    }

    //Assign values when creating setItem
    public TestingItem() {
    }
}
