import java.util.Random;

/*
    This class will be used as a matrix that store weight of lines between nodes in
    destination layer(j) and the source layer(i).
    The number of each node in layer(j) is the index number of a row and the number
    of each node in layer(i) is the index number of a column.
    The value of weight(Wji) is the value of matrix(Mji) or weightData[j][i]
*/
public class Weight_store {
    protected int row, column;
    protected Double[][] weightData;

    public Weight_store(int row, int column, boolean ifRandom) {
        // number of rows and columns
        this.row = row;
        this.column = column;

        /* initialize a matrix, then random an initialized weight to a matrix
           or set all weight as 0
        */
        this.weightData = new Double[row][column];
        Random rand = new Random();

        for (int j = 0; j < row; j++) {
            for (int i = 0; i < column; i++) {
                if (ifRandom)
                    weightData[j][i] = rand.nextDouble(-1, 1);
                else
                    weightData[j][i] = 0.0;
            }
        }
    }

    // this function is used to set a weight to a line
    public void setWeight(int row, int column, Double weight) {
        weightData[row][column] = weight;
    }

    // this function is used to get a weight from a line
    public Double getWeight(int row, int column) {
        return weightData[row][column];
    }

    public Double[][] getWeightData(){
        return weightData;
    }

}
