import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Data_Manager {

    // setting path of source data set
    protected static String inFile = "src/data.txt";
    // lists that store all data sets
    protected static List<List<List<Double>>> training_dataSet = new LinkedList<>();
    protected static List<List<List<Double>>> training_desired = new LinkedList<>();
    protected static List<List<List<Double>>> testing_dataSet = new LinkedList<>();
    protected static List<List<List<Double>>> testing_desired = new LinkedList<>();
    protected static Data_Manager data;

    static {
        try {
            data = new Data_Manager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Data_Manager() throws IOException {

        for (int dataSet = 0; dataSet < 10; dataSet++) {
            FileReader fr = new FileReader(inFile);
            BufferedReader reader = new BufferedReader(fr);
            {
                // lists that store each data set
                List<List<Double>> sub_training_dataSet = new LinkedList<>();
                List<List<Double>> sub_training_desired = new LinkedList<>();
                List<List<Double>> sub_testing_dataSet = new LinkedList<>();
                List<List<Double>> sub_testing_desired = new LinkedList<>();

                // count lines
                int lines = 0;
                String data;

                /* splitting data into each type of list which are training dataset,
                   training desired output, testing dataset, and testing desired output
                 */
                while ((data = reader.readLine()) != null) {
                    lines++;

                    String[] eachLine = data.split(",");

                    List<Double> Input_line = new LinkedList<>();
                    List<Double> Desired_line = new LinkedList<>();

                    for (int i=0; i<eachLine.length; i++) {
                        if(i==1) {
                            if(eachLine[i].equals("M"))
                                Desired_line.add(1.0);
                            else
                                Desired_line.add(2.0);
                        }else if(i>1)
                            Input_line.add(Double.parseDouble(eachLine[i]));
                    }

                    if (lines % (10) == dataSet) {
                        sub_testing_dataSet.add(Input_line);
                        sub_testing_desired.add(Desired_line);
                    } else {
                        sub_training_dataSet.add(Input_line);
                        sub_training_desired.add(Desired_line);
                    }
                }

                // insert a data set into a list of each type of data
                training_dataSet.add(sub_training_dataSet);
                training_desired.add(sub_training_desired);
                testing_dataSet.add(sub_testing_dataSet);
                testing_desired.add(sub_testing_desired);
                System.out.println("hi");
            }

        }
    }

    private void normalize(){

    }

    public void setPath(String path) {
        inFile = path;
    }

    public static Data_Manager getData() {
        return data;
    }

    /* these function below will be used for accessing data set the neuron network */
    public static List<List<Double>> getTrainData(int dataSet) {
        return training_dataSet.get(dataSet);
    }

    public static List<List<Double>> getTrainDs(int dataSet) {
        return training_desired.get(dataSet);
    }

    public static List<List<Double>> getTestData(int dataSet) {
        return testing_dataSet.get(dataSet);
    }

    public static List<List<Double>> getTestDs(int dataSet) {
        return testing_desired.get(dataSet);
    }

    public static void main(String[] args) {

        Data_Manager.getData();
    }
}


