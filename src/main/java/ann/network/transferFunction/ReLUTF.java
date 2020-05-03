package ann.network.transferFunction;

public class ReLUTF implements TransferFunction {
    @Override
    public double calculate(double x, double slope) {
        return Math.max(0.0f, x);
    }
}
