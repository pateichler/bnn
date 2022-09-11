import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class World {
  private Brain[] brains;
  private double[] brainFitness;
  private int startGen = 1;
  private int id;
  
  private Random rand;
  
  public World(int worldID) {
    id = worldID;
    brainFitness = new double[Settings.Instance.POP_SIZE];
    
    rand = new Random();
  }
  
  public World(int worldID, Genetics[] genePool, int startGen) {
    this(worldID);
    
    brains = new Brain[genePool.length];
    for(int i = 0; i < brains.length; i ++)
      brains[i] = new Brain(genePool[i]);
    
    this.startGen = startGen;
  }
  
  public void run() {
    // Make sure experiment path exists
    createPath();
    
    for(int i = 0; i < Settings.Instance.NUM_GENS; i ++) {
      try {
        createBrains();
        long t = System.currentTimeMillis();
        runGeneration();
        t = System.currentTimeMillis() - t;
        System.out.println("Generation completed in: " + (double)t/1000 + "s");
      } catch (Exception e) {
        System.out.println("Error in generation: " + (i + startGen));
        e.printStackTrace();
        break;
      }
      
      saveBrains();
      printSaveGenerationStats(i + startGen);
    }
  }
  
  void createBrains() throws Exception {
    // Check if we don't have previous brains ... if so create new brains from scratch
    if(brains == null) {
      brains = new Brain[Settings.Instance.POP_SIZE];
      for(int i = 0; i < brains.length; i++)
        brains[i] = new Brain(new Genetics());
      
      return;
    }
    
    // We have brains from previous generation ... re-populate
    Brain[] newBrains = new Brain[Settings.Instance.POP_SIZE];
    for(int i = 0; i < newBrains.length; i ++) {
      int[] mates = getMates();
      double fit1 = brainFitness[mates[0]];
      double fit2 = brainFitness[mates[1]];
      double parent1Ratio = 0.5;
      if(fit1 + fit2 > 0)
        parent1Ratio = Math.min(Settings.Instance.MAX_PARENT_RATIO, Math.max(1 - Settings.Instance.MAX_PARENT_RATIO, fit1 / (fit1 + fit2)));
      
      Genetics dna = new Genetics(brains[mates[0]].dna, brains[mates[1]].dna, parent1Ratio);
      newBrains[i] = new Brain(dna);
    }
    
    brains = newBrains;
  }
  
  void runGeneration() throws InterruptedException {
    //TODO: Run this as a threaded pool
    ExecutorService executor = Executors.newFixedThreadPool(Settings.Instance.THREAD_COUNT);
    
    List<Callable<Double>> callableTasks = new ArrayList<>();
    for(Brain b : brains)
      callableTasks.add(Settings.Instance.createBrainRunner(b)); //TODO: get correct brain runner here
    
    List<Future<Double>> futures = executor.invokeAll(callableTasks);
    
    int i = 0;
    for(Future<Double> f : futures) {
       try {
        brainFitness[i] = f.get();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
       i++;
    }
    
    
//    BrainRunner br = Settings.Instance.getRunner();
//    for(int i = 0; i < brains.length; i ++)
//      brainFitness[i] = br.runBrain(brains[i]);
  }
  
  int[] getMates() {
    // Pick subset of brains
    LinkedList<Integer> l = getSubset(Settings.Instance.TOURN_SIZE, brains.length);
    
    // Returns the two brains with best fitness
    int max1 = 0, max2 = 1;
    int index = 0;
    for(int b : l) {
      if(brainFitness[b] > brainFitness[max1]) {
        max2 = max1;
        max1 = index;
      }
      else if(brainFitness[b] > brainFitness[max2])
       max2 = index;
      
      index++;
    }
    
    return new int[] {l.get(max1), l.get(max2)};
  }
  
  void saveBrains() {
    Genetics[] genePool = new Genetics[brains.length];
    for(int i = 0; i < genePool.length; i++)
      genePool[i] = brains[i].dna;
    
    ObjectOutputStream oos = null;
    try {
      FileOutputStream fout = new FileOutputStream(getFilePath("genePool.ser").toString());
      oos = new ObjectOutputStream(fout);
      oos.writeObject(genePool);
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      if(oos != null)
        try {
          oos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }
  
  void printSaveGenerationStats(int g) {
    double bestFit = 0;
    double worseFit = Double.POSITIVE_INFINITY;
    double meanFit = 0;
    for(double f : brainFitness) {
      if(f > bestFit)
        bestFit = f;
      
      if(f < worseFit)
        worseFit = f;
      
      meanFit += f;
    }
    
    meanFit /= brainFitness.length;
    
    // Possibly pass in gene pool
    Genetics[] genePool = new Genetics[brains.length];
    for(int i = 0; i < genePool.length; i++)
      genePool[i] = brains[i].dna;
    
    double variation = calculateGenePoolVariation(genePool, true);
    
    String stats = String.join(",", String.valueOf(g), String.valueOf(bestFit), 
        String.valueOf(worseFit), String.valueOf(meanFit), String.valueOf(variation));
    
    System.out.println("Gen " + g + ": (" + stats + ")");
    
    String csvString = stats + "\n";
    
    try {
      Files.write(getFilePath("genStats.csv"), csvString.getBytes(), StandardOpenOption.APPEND);
    }catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static double calculateGenePoolVariation(Genetics[] genePool, boolean strength) {
    int l = strength ? genePool[0].strengthNet.getWeightsLength() : genePool[0].typeNet.getWeightsLength();
    double totStd = 0;
    
    double[] weights = new double[genePool.length];
    for(int i = 0; i < l; i++) {
      for(int g = 0; g < genePool.length; g++) 
        weights[g] = strength ? genePool[g].strengthNet.getWeights(i) : genePool[g].typeNet.getWeights(i); 
    
      double mean = 0;
      for(double n : weights)
        mean += n;
      mean /= genePool.length;
      
      double std = 0;
      for(double n : weights)
        std += Math.pow((n - mean), 2);
      totStd += Math.sqrt(std / genePool.length);
    }
    
    return totStd / l;
  }
  
  Path getFilePath(String fileName) {
    return Paths.get("experiments", String.valueOf(id), fileName);
  }
  
  void createPath() {
    // Create folder if doesn't exist
    Path folderPath = getFilePath(".");
    if(Files.exists(folderPath) == false) {
      try {
        Files.createDirectories(folderPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    // Create CSV if doesn't exist
    Path csvPath = getFilePath("genStats.csv");
    if(Files.exists(csvPath) == false) {
      String csvString = String.join(",", "Generation", "Best", "Worst", "Average", "Gene variation") + "\n";
      try {
        Files.write(csvPath, csvString.getBytes(), StandardOpenOption.CREATE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  LinkedList<Integer> getSubset(int size, int total) {
    LinkedList<Integer> sel = new LinkedList<Integer>();
    
    for(int k = 0; k < size; k++) {
      int i = rand.nextInt(total - k);
      int sIndex = 0;
      
      for(int s : sel) {
        if(i < s)
          break;
        
        i ++;
        sIndex ++;
      }
      
      sel.add(sIndex, i);
    }
    
    return sel;
  }
}
