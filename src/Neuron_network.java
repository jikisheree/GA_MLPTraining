import java.util.List;
import java.util.Random;

public class Neuron_network {

        protected Double minError;
        protected int maxEpoch;
        protected Double bias;
        protected Double learningRate;
        protected Double mmRate;
        private final List<List<Double>> training_dataSet;
        private final List<List<Double>> training_desired;
        private final List<List<Double>> testing_dataSet;
        private final List<List<Double>> testing_desired;
        private Double[][] nodeValue, local_gradient;
        private int[] nodeNum;
        private Weight_store[] weightOfLayer;
        private Weight_store[] changedWeight;
        private Double[] error;
        private int weightLayerNum;
        private int nodeLayerNum;
        private final int dataSet;
        private double av_error;

        public Neuron_network(Double minError, Double learningRate, Double mm, int maxEpoch, Double bias,
                              int[] hidden, int dataSet) {

            this.minError = minError;
            this.learningRate = learningRate;
            this.maxEpoch = maxEpoch;
            this.bias = bias;
            this.mmRate = mm;
            this.dataSet = dataSet;

            // loading the organized lists pf the data set
            this.training_dataSet = Data_Manager.getTrainData(this.dataSet);
            this.training_desired = Data_Manager.getTrainDs(this.dataSet);
            this.testing_dataSet = Data_Manager.getTestData(this.dataSet);
            this.testing_desired = Data_Manager.getTestDs(this.dataSet);

            // build the multi-layer perceptron
            MLPBuilding(hidden);
        }

        public void MLPBuilding(int[] hidden) {

            // store total number of layers
            this.nodeLayerNum = hidden.length+2;
            // store total number of line between layer (weight)
            this.weightLayerNum = nodeLayerNum-1;

            // add an array to store number of nodes in each layer
            this.nodeNum = new int[nodeLayerNum];
            // store number of nodes of each layer into the array
            this.nodeNum[0] = training_dataSet.get(0).size();
            for (int i = 1; i <= hidden.length + 1; i++) {
                if (i == hidden.length + 1) {
                    // add output nodes
                    this.nodeNum[i] = training_desired.get(0).size();
                    this.error = new Double[this.nodeNum[i]];
                } else
                    // add hidden nodes
                    this.nodeNum[i] = hidden[i - 1];
            }

            // matrix to store value of each node in each layer
            this.nodeValue = new Double[nodeLayerNum][];
            // matrix to store local gradient of each node in each layer
            this.local_gradient = new Double[nodeLayerNum][];
            // build the matrices
            for (int i = 0; i < nodeLayerNum; i++) {
                this.nodeValue[i] = new Double[nodeNum[i]];
                this.local_gradient[i] = new Double[nodeNum[i]];
            }

            // matrix of overall neuron network
            this.weightOfLayer = new Weight_store[weightLayerNum];
            this.changedWeight = new Weight_store[weightLayerNum];
            // build the matrices
            for (int i = 0; i < weightLayerNum; i++) {
                weightOfLayer[i] = new Weight_store(nodeNum[i + 1], nodeNum[i], true);
                changedWeight[i] = new Weight_store(nodeNum[i + 1], nodeNum[i], false);
            }

        }

        public void setWeightOfLayer(Weight_store[] weightOfLayer){
            this.weightOfLayer = weightOfLayer;
        }

        public Double[][] getNodeValue(){
            return nodeValue;
        }

        public int[] getNodeNum(){
            return nodeNum;
        }

        public Weight_store[] getWeightOfLayer(){
            return weightOfLayer;
        }

        public void training() {

            System.out.println("================= TRAINING " + (dataSet + 1) + " =================");
            // epoch iteration count
            int n = 0;
            // declaring average error
            double avgError = 10000.0;
            // loading number of input nodes
            int inNodeNum = nodeNum[0];
            // random line of data in a data set
            Random ranDataLine = new Random();

        /* iteration of each epoch in condition if the number of iteration is more than maxEpoch
           or the average error is less than minError, then exit the iteration
        */
            while (n < maxEpoch && avgError > minError) {

                // sum of error in each epoch
                double sum_error = 0.0;

                // iteration of each line of data in a data set
                for (int l = 0; l < training_dataSet.size(); l++) {
                    // insert the value from a randomized line of a data set into input nodes
                    int lineNum = ranDataLine.nextInt(training_dataSet.size());
                    for (int i = 0; i < inNodeNum; i++) {
                        this.nodeValue[0][i] = training_dataSet.get(lineNum).get(i);
                    }

                    // feed forward -> find errors -> calculate new weight by doing back propagation
                    feedForward();
                    errorCalculation(lineNum, true);
                    backPropagation();

                    // console out
                    double d = training_desired.get(lineNum).get(0);
                    double g = nodeValue[nodeLayerNum - 1][0];

                    System.out.println("desired:" + (int)d + " get: "+ g + "\t error_n: " + Math.abs(d-g));

                    // add the mean squared error of each data in this epoch to the summation of error
                    sum_error += 0.5 * Math.pow(error[0], 2);
                }
                // average error of each epoch
                avgError = sum_error / training_dataSet.size();
            System.out.println("N epoch: "+n +"\t"+"Epoch Avg error"+ avgError);
                n++;
            }
            // print avg error in the last epoch
            System.out.println("final Average error: " + avgError);
            this.av_error = avgError;
        }

        public double get_avError(){
            return av_error;
        }

        public void testing() {

            System.out.println("================= TESTING =================");
            double avgError;
            int inNodeNum = nodeNum[0];
            double sum_error = 0.0;

            for (int l = 0; l < testing_dataSet.size(); l++) {
                // insert input value to node
                for (int i = 0; i < inNodeNum; i++) {
                    this.nodeValue[0][i] = testing_dataSet.get(l).get(i);
                }

                // feed forward -> find errors
                feedForward();
                errorCalculation(l, false);

                // console out
                double d = testing_desired.get(l).get(0) * 700;
                double g = nodeValue[nodeLayerNum - 1][0] * 700;
//            System.out.println("desired:" + (int) d + " get: " + g + "\t error_n: " + Math.abs(d - g));

                // add the mean squared error of each data in this epoch to the summation of error
                sum_error += 0.5 * Math.pow((error[0]), 2);
            }
            // average error of each epoch
            avgError = sum_error / testing_dataSet.size();

            System.out.println("Average error: " + avgError);
        }

        public void feedForward() {

            /* calculate the value of each node in all layers
             */

            // iteration of each layer of line between layers of node
            for (int i = 0; i < weightLayerNum; i++) {
                // loading number of node in each layer
                int nodeAfterNum = nodeNum[i + 1];
                int nodeBeforeNum = nodeNum[i];

            /* node in the next layer(j) will be row and previous layer(i) will be column
               calculate the formula: Vj = Σ(i=0-to-m) Wji(n)*Yi(n) ; m = number of nodes in layer(i)
             */

                // for each node in next layer
                for (int row = 0; row < nodeAfterNum; row++) {
                    double sum = 0.0;
                    // for each node in previous layer
                    // Vj = Σ(i=0-to-m) Wji(n)*Yi(n)
                    for (int col = 0; col < nodeBeforeNum; col++) {
                        Double weightOfLine = this.weightOfLayer[i].getWeight(row, col);
                        sum += (weightOfLine * activation(nodeValue[i][col]));
                    }
                    nodeValue[i + 1][row] = sum + bias;
                }
            }
        }

        public void errorCalculation(int lineNum, boolean ifTrain) {

        /* calculate the error of output nodes using formula:
           Ej(n) = Dj(n)-Yj(n)
        */

            Double desired;
            for (int i = 0; i < nodeNum[nodeLayerNum - 1]; i++) {
                // select desired value whether it is training or testing data
                if (ifTrain)
                    desired = training_desired.get(lineNum).get(i);
                else
                    desired = testing_desired.get(lineNum).get(i);
                // Ej(n) = Dj(n)-Yj(n)
                this.error[i] = desired - nodeValue[nodeLayerNum - 1][i];

            /* finding the local gradient of an output node using
               formula: Lj(n) = Ej(n) * ac'(Vj(n)) ; activation function denoted by 'ac'
            */
                // the activation function of output layer is linear, so ac'(Vj(n)) = 1
                this.local_gradient[nodeLayerNum - 1][i] = this.error[i] * 1;
            }
        }

        private void backPropagation() {

        /* finding local gradient of each node in input and hidden layers
           from backward using formula:
           -> Lj(n) = ac'(Vj(n))*Σ(i=0-to-m)(Lk(n)*Wkj(n)) ; m = number of nodes in layer k
           finding delta weight of each line then change the weight from backward
           using formula: -> ΔWji(n) = η*Lj(n)*Yi(n) and Wji(n+1) = Wji(n)+ΔWji(n)
         */

            // each layer of line form backward
            for (int layer = weightLayerNum - 1; layer >= 0; layer--) {
                // each node in layer before weight line (j)
                for (int j = 0; j < nodeNum[layer]; j++) {
                    double sum = 0.0;
                    // each node in layer after weight line (k)

                    // finding the local gradient of each node
                    // Lj(n) = ac'(Vj(n))*Σ(i=0-to-m)(Lk(n)*Wkj(n))
                    for (int k = 0; k < nodeNum[layer + 1]; k++) {
                        sum += local_gradient[layer + 1][k] * weightOfLayer[layer].getWeight(k, j);
                    }
                    local_gradient[layer][j] = activation_diff(nodeValue[layer][j]) * sum;

                    // changing the weight of each line in actual layer
                    for (int k = 0; k < nodeNum[layer + 1]; k++) {

                        // ΔWji(n) = (α*ΔWji(n-1)) + (η*Lj(n)*Yi(n))
                        Double cw = (mmRate * changedWeight[layer].getWeight(k, j)) +
                                (learningRate * local_gradient[layer + 1][k] * activation(nodeValue[layer][j]));
                        changedWeight[layer].setWeight(k, j, cw);

                        // Wji(n+1) = Wji(n)+ΔWji(n)
                        weightOfLayer[layer].setWeight(k, j,
                                (weightOfLayer[layer].getWeight(k, j) + changedWeight[layer].getWeight(k, j)));
                    }
                }
            }
        }

        public double activation(Double value) {
            /* formula of activation function: tangent */
            return (Math.exp(value) - Math.exp(-value)) / (Math.exp(value) + Math.exp(-value));
        }

        public double activation_diff(Double value) {
            /* formula of differential activation function: tangent */
            return 1.0 - Math.pow(activation(value), 2);
        }

}

