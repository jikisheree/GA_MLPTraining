import java.util.*;
import java.util.Map.Entry;

public class GA {

    private final Map<Neuron_network, Triplet<Double, Integer, Double>> MLP_list;
    private final List<Double> fitness_list;
    private List<Neuron_network> MLP_selected;
    private List<Neuron_network> MLP_toBeMated;
    private List<Neuron_network> MLP_child;
    private final int mlp_num;
    private Neuron_network best_mlp;

    public GA(int mlp_num){
        MLP_list = new HashMap<>();
        this.mlp_num = mlp_num;
        fitness_list = new ArrayList<>();
        MLP_toBeMated= new LinkedList<>();
        MLP_selected = new LinkedList<>();
        MLP_child = new LinkedList<>();
        init_mlp();

    }

    private Neuron_network init_each_mlp(){
        Double minError = 0.000001;
        int maxEpoch = 1000;
        Double bias = 1.0;
        Double learningRate = 0.02;
        Double mm = 0.6;
        int[] hidden = {20,15,10};
        int dataSet = 1;
        return new Neuron_network(minError, learningRate, mm, maxEpoch, bias, hidden, dataSet);
    }

    private void init_mlp(){

        double min_fitness = 1000000.0;
//        List<Double> old_fitness_list = new ArrayList<>();

        for (int i=0; i<mlp_num; i++){
            Neuron_network mlp = init_each_mlp();
            mlp.training();
            double fitness = mlp.get_avError();

            if(fitness < min_fitness)
                min_fitness = fitness;

            MLP_list.put(mlp, new Triplet<>(fitness));

        }
        fitness_scaling(min_fitness);
    }

    private void fitness_scaling(double min_fitness){
        final int k = 1;

        Set<Entry<Neuron_network, Triplet<Double, Integer, Double>>> setHm = MLP_list.entrySet();

        // eval new fitness value to each mlp
        for (Entry<Neuron_network, Triplet<Double, Integer, Double>> e : setHm) {

                double old_fitness = e.getValue().getFirst();
                double new_fitness = 1/(k+old_fitness-min_fitness);
                e.getValue().setFirst(new_fitness);
                fitness_list.add(new_fitness);
        }
        // rank fitness of each chromosome
        Collections.sort(fitness_list);
    }

    private void selection(){

        double min = 0.5;
        double max = 1.5;

        Set<Entry<Neuron_network, Triplet<Double, Integer, Double>>> setHm = MLP_list.entrySet();

        // insert the rank and select prob to each mlp
        for (int i=0; i<fitness_list.size(); i++) {
            for (Entry<Neuron_network, Triplet<Double, Integer, Double>> e : setHm) {
                if(Objects.equals(e.getValue().getFirst(), fitness_list.get(i))){
                    int rank = i+1;
                    MLP_list.get(e.getKey()).setSecond(i+1);
                    double p = (1.0/mlp_num)*(min+((max-min)*(rank-1.0/mlp_num-1.0)));
                    e.getValue().setThird(p);
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
                MLP_selected.add(e.getKey());
        }

    }
    private void mating(){

        select_into_matingPool();

        for(int i=0; i<MLP_toBeMated.size(); i+=2){
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

        for (int layer=weightLayerNum-1; layer>=0; layer--){
            int numNodeJ = momWeight[layer].getRowNum();
            int numNodeI = momWeight[layer].getColNum();
            childWeight[layer] = new Weight_store(numNodeJ, numNodeI, false);
            for (int j=numNodeJ-1; j>=0; j--){
                for(int i=numNodeI-1; i>=0; i--){
                    double p = 0.5;
                    double q = Math.random();
                    Weight_store parent;
                    if(q < p)
                        parent = momWeight[layer];
                    else
                        parent = dadWeight[layer];
                        Double weight = parent.getWeight(j,i);
                        childWeight[layer].setWeight(j,i,weight);
                }
            }
        }
        child.setWeightOfLayer(childWeight);
        // discard parents from selected pool
        MLP_selected.remove(mom);
        MLP_selected.remove(dad);

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

    private void finalPop(){
        int diff_pop = Math.abs(MLP_list.size()- MLP_child.size());
        int track = 0;
        Random rand = new Random();
        int random_num;

        if(MLP_list.size()>MLP_child.size()) {
            while (track < diff_pop) {
                random_num = rand.nextInt(100);
                if (random_num < 40) {
                    Random r = new Random();
                    int randomIndex = r.nextInt(MLP_selected.size());
                    MLP_child.add(MLP_selected.get(randomIndex));
                }
                track++;
            }
        }else if(MLP_list.size()<MLP_child.size()){
            List<Neuron_network> temp = new LinkedList<>();
            for(int i=0; i<MLP_list.size(); i++){
                temp.add(MLP_child.get(i));
            }
            MLP_child.clear();
            MLP_child.addAll(temp);
        }
    }

    private void setNew_population(){

        double min_fitness = 1000000.0;
//        List<Double> old_fitness_list = new ArrayList<>();

        MLP_list.clear();
        fitness_list.clear();
        MLP_selected.clear();
        MLP_toBeMated.clear();
        for (Neuron_network e : MLP_child) {
            e.training();
            double fitness = e.get_avError();

            MLP_list.put(e, new Triplet<>(fitness));

            if(fitness < min_fitness)
                min_fitness = fitness;
        }
        MLP_child.clear();
        fitness_scaling(min_fitness);
    }

    private void resultOfGen(int i){
        Set<Entry<Neuron_network, Triplet<Double, Integer, Double>>> setHm = MLP_list.entrySet();
        double best_fitness = fitness_list.get(fitness_list.size()-1);

        for (Entry<Neuron_network, Triplet<Double, Integer, Double>> e : setHm) {
                double e_fitness = e.getValue().getFirst();
                if(e_fitness == best_fitness){
                    best_mlp = e.getKey();
                }
        }
        System.out.println("Best MLP result of gen "+i);
        best_mlp.training();
        System.out.println("Sum square error of best mlp is "+best_mlp.get_avError());
    }

    public void start_GA(){

        int i = 0;

        // training each generation
        while (i < 100) {

            System.out.println("gen "+i+" is selecting.....");
            System.out.println("fitness: ");
            fitness_list.forEach(value -> System.out.print(value+ " "));
            selection();
            System.out.println("gen "+i+" is mating.....");
            mating();
            System.out.println("gen "+i+" is fulling population.....");
            finalPop();
            System.out.println("gen "+i+" is setting new population.....");
            setNew_population();
            System.out.println("gen "+i+" is calculating result of .....");
            resultOfGen(i);

            i++;
        }

    }


}
