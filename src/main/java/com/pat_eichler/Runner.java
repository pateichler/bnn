import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.Gson; 

public class Runner {

  public static void main(String[] args) {
//    System.out.println(Runtime.getRuntime().availableProcessors());

//    Runner r = new Runner();
//    r.loadGlobalSettings();
//    r.visualizeBrain(new FixedGenetics());
    
//    Runner r = new Runner();
//    r.visualizeBestBrain(48);
    
    Runner r = new Runner();
    r.runNew();
//    r.runPrevious(47);
  }
  
  public void runNew() {
    int worldID = getLastWorldID() + 1;
    loadGlobalSettings().setInstance();;
    
    World w = new World(worldID);
    
    try {
      copySettingsToWorld(worldID);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    w.run();
  }
  
  public void runNew(Settings s) {
    int worldID = getLastWorldID();
    
    s.setInstance();;
    
    World w = new World(worldID);
    w.run();
  }
  
  public void runPrevious(int worldID) {
    Settings s = loadWorldSettings(worldID);
    runPrevious(worldID, s);
  }
  
  public void runPrevious(int worldID, Settings s) {
    if(s == null) {
      System.out.println("Error: no settings given for world ... exiting");
      return;
    }
    
    s.setInstance();
    
    Genetics[] genePool = loadGenePool(worldID);
    
    World w = new World(worldID, genePool);
    w.run();
  }
  
  public void visualizeBestBrain(int worldID) {
    Settings s = loadWorldSettings(worldID);
    s.setInstance();
    Genetics g = loadBestBrain(worldID);

    visualizeBrain(g);
  }
  
  void visualizeBrain(Genetics g) {
    Brain b = new Brain(g);
    b.printConnectionStrengths();
    System.out.println("===============");
    b.printConnectionTypes();
    
    SimpleRunner sr = new SimpleRunner(b);
    double fit = sr.call();
    System.out.println("Trained with fitness: " + fit);
    
    SimpleRunnerVisualizer v = new SimpleRunnerVisualizer(b);
    System.out.println("Brain in last state:");
    v.br.visualize();

    System.out.println("====================");
    b.printConnectionStrengths();
    System.out.println("===============");
    b.printConnectionTypes();
    
//    b.clearTransmitters();
    v.run();
  }
  
  Settings loadGlobalSettings() {
    return loadSettingsFromPath(Paths.get("config.json"));
  }
  
  Settings loadWorldSettings(int worldID) {
    return loadSettingsFromPath(Paths.get("experiments", String.valueOf(worldID), "config.json"));
  }
  
  Settings loadSettingsFromPath(Path path) {
    Reader reader = null;
    try {
      reader = Files.newBufferedReader(path);
      
      Gson gson = new Gson();
      return gson.fromJson(reader, Settings.class);
    } catch (IOException e) { }
    finally {
        try {
          if(reader != null)
            reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    
    return null;
  }
  
  void copySettingsToWorld(int worldID) throws IOException {
    Files.copy(Paths.get("config.json"), Paths.get("experiments", String.valueOf(worldID), "config.json"));
  }
  
  Genetics[] loadGenePool(int worldID) {
    String path = Paths.get("experiments", String.valueOf(worldID), "genePool.ser").toString();
    ObjectInputStream oi = null;
    
    try {
      FileInputStream fi = new FileInputStream(new File(path));
      oi = new ObjectInputStream(fi);
      return (Genetics[]) oi.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }finally {
      if(oi != null) {
        try {
          oi.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
    
    return null;
  }
  
  Genetics loadBestBrain(int worldID) {
    String path = Paths.get("experiments", String.valueOf(worldID), "bestGenes.ser").toString();
    ObjectInputStream oi = null;
    
    try {
      FileInputStream fi = new FileInputStream(new File(path));
      oi = new ObjectInputStream(fi);
      return (Genetics) oi.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }finally {
      if(oi != null) {
        try {
          oi.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
    
    return null;
  }
  
  int getLastWorldID() {
    File file = new File("experiments");
    String[] directories = file.list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return new File(current, name).isDirectory();
      }
    });
    
    int maxID = 0;
    for(String d : directories) {
      try {
        int w = Integer.parseInt(d);
        if(w > maxID)
          maxID = w;
        
      }catch(NumberFormatException e) { }
    }
    
    return maxID;
  }
  
  static void printVariationFromWorld(int worldID) {
    Runner r = new Runner();
    r.loadWorldSettings(worldID).setInstance();
    Genetics[] genePool = r.loadGenePool(5);
    System.out.println("Strength std: " + World.calculateGenePoolVariation(genePool, true));
    System.out.println("Type std: " + World.calculateGenePoolVariation(genePool, false));
    
    Genetics[] genePoolRandom = new Genetics[genePool.length];
    for(int i = 0; i < genePool.length; i++)
      genePoolRandom[i] = new NNGenetics();

    System.out.println("===============");
    System.out.println("Strength random std: " + World.calculateGenePoolVariation(genePoolRandom, true));
    System.out.println("Type random std: " + World.calculateGenePoolVariation(genePoolRandom, false));
  }
  
  static void testBrain() {
    Brain b = new Brain(new NNGenetics());
    System.out.println(b);
    
    try {
      Brain b2 = new Brain(new NNGenetics((NNGenetics)b.dna, (NNGenetics)b.dna));
      System.out.println(b2);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  static void testNeuralNet() {
    int inputNodes = Settings.Instance.totalNTCount() * 2;
    
    int[] strengthLayers = new int[Settings.Instance.STRENGTH_NET_IN_LAYERS.length + 2];
    strengthLayers[0] = inputNodes;
    strengthLayers[strengthLayers.length - 1] = 3;
    
    NeuralNetwork strengthNet = new NeuralNetwork(strengthLayers);
    
    System.out.println(strengthNet.predict(new double[] {0,0,0,0,0})-1);
    
    try {
      NeuralNetwork newNet = new NeuralNetwork(strengthNet, strengthNet, 0.5);
      System.out.println(newNet.predict(new double[] {0,0,0,0,0})-1);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
