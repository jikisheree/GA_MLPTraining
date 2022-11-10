public class Main_run {

    // set a structure to neuron network 1
    private static final Double minError1 = 0.000001;
    private static final int maxEpoch1 = 1000;
    private static final Double bias1 = 1.5;
    private static final Double learningRate1 = 0.02;
    private static final Double mm1 = 0.5;
    private static final int[] hidden1 = {5,4,3};
    private static final int maxIteration1 = 10;

    // set a structure to neuron network 2
    private static final Double minError2 = 0.000001;
    private static final int maxEpoch2 = 2000;
    private static final Double bias2 = 1.0;
    private static final Double learningRate2 = 0.02;
    private static final Double mm2 = 0.6;
    private static final int[] hidden2 = {5,10,10,5};
    private static final int maxIteration2 = 10;

    // run training and testing on neuron network 1
    public static void run_network1() {
        for (int dataSet = 0; dataSet < maxIteration1; dataSet++) {
            Neuron_network nn = new Neuron_network(minError1, learningRate1, mm1, maxEpoch1, bias1, hidden1, dataSet);
            nn.training();
            nn.testing();
        }
    }

    public static void main(String[] args) {

        Data_Manager.getData();

        // flood dataset
        run_network1();
    }

}
