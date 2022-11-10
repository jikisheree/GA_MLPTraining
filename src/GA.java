import java.util.*;
import java.util.Map.Entry;

public class GA {

    private final Map<Neuron_network, Triplet<Double, Integer, Double>> MLP_list;
    private final List<Double> fitness_list;
    private List<Neuron_network> MLP_selected;
    private List<Neuron_network> MLP_toBeMated;
    private List<Neuron_network> MLP_child;
    private final int mlp_num;

    public GA(int mlp_num){
        MLP_list = new HashMap<>();
        this.mlp_num = mlp_num;
        init_mlp();
        fitness_list = new ArrayList<>();
        MLP_toBeMated= new LinkedList<>();
        MLP_selected = new LinkedList<>();
        MLP_child = new LinkedList<>();

    }

    private Neuron_network init_each_mlp(){
        Double minError = 0.000001;
        int maxEpoch = 1000;
        Double bias = 1.0;
        Double learningRate = 0.02;
        Double mm = 0.6;
        int[] hidden = {5,10,10,5};
        int dataSet = 10;
        return new Neuron_network(minError, learningRate, mm, maxEpoch, bias, hidden, dataSet);
    }

    private void init_mlp(){

        double min = 1000000.0;

        for (int i=0; i<mlp_num; i++){
            Neuron_network mlp = init_each_mlp();
            double new_fitness = fitness_scaling(mlp.get_avError());
            MLP_list.put(mlp, new Triplet<>(new_fitness));
            fitness_list.add(new_fitness);


        }
    }

    private double fitness_scaling(double old_fitness){
        final int k = 1;
        double eval =

        return eval;
    }

    private void selection(){

        double min = 0.5;
        double max = 1.5;

        // rank fitness of each chromosome
        Collections.sort(fitness_list);
        Set<Entry<Neuron_network, Triplet<Double, Integer, Double>>> setHm = MLP_list.entrySet();

        // insert the rank and select prob to each mlp
        for (int i=0; i<fitness_list.size(); i++) {
            for (Entry<Neuron_network, Triplet<Double, Integer, Double>> e : setHm) {
                if(Objects.equals(e.getValue().getFirst(), fitness_list.get(i))){
                    int rank = i+1;
                    MLP_list.get(e.getKey()).setSecond(i+1);
                    double p = (1.0/mlp_num)*(min+((max-min)*(rank-1.0/mlp_num-1.0)));
                }
            }
        }
        // at this point, all mlp got its fitness, rank, and prob of selection
        sus();
    }

    private void sus(){

        double ptr = Math.random();
        double sum = 0;
        Set<Entry<Neuron_network, Triplet<Double, Integer, Double>>> setHm = MLP_list.entrySet();

        for (Entry<Neuron_network, Triplet<Double, Integer, Double>> e : setHm) {
            double n = mlp_num*(e.getValue().getThird());
            for(sum+=n; ptr<sum; ptr++)
                MLP_selected.add((Neuron_network) e);
        }

    }
    private void mating(){

        select_into_matingPool();

        for(int i=0; i<MLP_toBeMated.size(); i++){
            Pair<Neuron_network, Neuron_network> mate_pair =
                    new Pair<>(MLP_toBeMated.get(i), MLP_toBeMated.get(i+1));
                    MLP_child.add(crossover(mate_pair));
        }

    }

    private Neuron_network crossover(Pair<Neuron_network, Neuron_network> mate_pair){

        Neuron_network mom = mate_pair.getFirst();
        Neuron_network dad = mate_pair.getSecond();
        Neuron_network child = init_each_mlp();

        int weightLayerNum = mom.getWeightOfLayer().length;

        Weight_store[] momWeight = mom.getWeightOfLayer();
        Weight_store[] dadWeight = dad.getWeightOfLayer();
        Weight_store[] childWeight = new Weight_store[momWeight.length];

        for (int layer=weightLayerNum-1; layer>0; layer--){
            int numNodeJ = momWeight[layer].getWeightData().length;
            for (int j=numNodeJ-1; j>=0; j--){
                int numNodeI = momWeight[layer].getWeightData()[j].length;
                for(int i=numNodeI-1; i>=0; i--){
                    double p = 0.5;
                    double q = Math.random();
                    Weight_store parent;
                    if(q < p)
                        parent = momWeight[layer];
                    else
                        parent = dadWeight[layer];
                        childWeight[layer] = new Weight_store(numNodeJ, numNodeI, false);
                        Double weight = parent.getWeight(j,i);
                        childWeight[layer].setWeight(j,i,weight);
                }
            }
        }
        child.setWeightOfLayer(childWeight);

        return child;
    }

    private void select_into_matingPool(){

        double q;
        double pc = 0.6;
        // select mlp from selected pool into mating pool
        for (Neuron_network selected: MLP_selected) {
            q = Math.random();
            if(q < pc)
                MLP_toBeMated.add(selected);
        }
        // check if number of mlp in mating pool is even
        // if not then add random one from selected pool
        int mate_num = MLP_toBeMated.size();
        if (mate_num%2!=0) {
            Random r = new Random();
            int randomIndex = r.nextInt(MLP_selected.size());
            MLP_toBeMated.add(MLP_selected.get(randomIndex));
        }
    }

    private void add_finalPop(){
        int diff_pop = MLP_list.size()- MLP_child.size();
        int track = 0;
        Random rand = new Random();
        int random_num;

        while(track<diff_pop) {
            random_num = rand.nextInt(100);
            if(random_num < 40) {
                Random r = new Random();
                int randomIndex = r.nextInt(MLP_selected.size());
                MLP_child.add(MLP_selected.get(randomIndex));
            }
            track++;
        }
    }

    private void setNew_population(){
        MLP_list.clear();
        for (Neuron_network e : MLP_child) {
            double new_fitness = fitness_scaling(e.get_avError());
            MLP_list.put(e, new Triplet<>(new_fitness));
            fitness_list.add(new_fitness);
        }
    }

    private void clear_old_population(){
        fitness_list.clear();
        MLP_selected.clear();
        MLP_toBeMated.clear();
        MLP_child.clear();
    }

    public void start_GA(){

        int i = 0;

        // training each generation
        while (i < 200) {
            if(i>0)
                clear_old_population();
            selection();
            mating();
            add_finalPop();
            setNew_population();

            i++;
        }

    }


}
