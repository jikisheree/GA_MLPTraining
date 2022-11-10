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
    protected static double mean;
    protected static double sd;

    static {
        try {
            data = new Data_Manager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Data_Manager() throws IOException {

        FileReader fr1 = new FileReader(inFile);
        BufferedReader reader1 = new BufferedReader(fr1);
        {
            String d;
            double sum = 0;
            int countInput = 0;
            List<Double> input_list = new LinkedList<>();

            // mean
            while ((d = reader1.readLine()) != null) {
                String[] eachLine = d.split(",");

                for (int i=2; i<eachLine.length; i++) {

                    double eachValue = Double.parseDouble(eachLine[i]);
                    input_list.add(eachValue);
                    sum+=eachValue;
                    countInput++;

                }

            }
            mean = sum/countInput;

            sum = 0;
            // std
            for (double e:input_list) {

                double v = Math.pow(Math.abs(e-mean), 2);
//                System.out.println("v: "+v);
                sum += v;

            }
            System.out.println("sum: "+sum);
            sd = Math.sqrt(sum/countInput);

            System.out.println("mean: "+mean+"sd: "+sd);
        }

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
                        double normValue = 0;
                        
                        if(i==1) {
                            if(eachLine[i].equals("M"))
                                Desired_line.add(0.0);
                            else
                                Desired_line.add(1.0);
                        }else if(i>1)
                            normValue = normalize(Double.parseDouble(eachLine[i]));
                            System.out.println(normValue);
                            Input_line.add(normValue);
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
            }

        }
    }


    private double normalize(double value){

        return (value-mean)/sd;
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


