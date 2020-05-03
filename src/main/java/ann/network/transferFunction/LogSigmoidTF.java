package ann.network.transferFunction;

public class LogSigmoidTF implements TransferFunction {
    public double calculate(double x, double slope) {
        return 1 / (1 + Math.exp(-x));
    }
}
