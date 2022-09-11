import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson; 

public class Runner {

  public static void main(String[] args) {
    Runner r = new Runner();
    r.run(12);
  }
  
  public void run(int worldID) {
    loadConfig();
    
//    Genetics[] genePool = r.loadGenePool(0);
    
    World w = new World(worldID);
    w.run();
  }
  
  void loadConfig() {
    Reader reader = null;
    try {
      reader = Files.newBufferedReader(Paths.get("config.json"));
      
      Gson gson = new Gson();
      gson.fromJson(reader, Settings.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
        try {
          if(reader != null)
            reader.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }
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
  
  static void printVariationFromWorld(int worldID) {
    Runner r = new Runner();
    r.loadConfig();
    Genetics[] genePool = r.loadGenePool(5);
    System.out.println("Strength std: " + World.calculateGenePoolVariation(genePool, true));
    System.out.println("Type std: " + World.calculateGenePoolVariation(genePool, false));
    
    Genetics[] genePoolRandom = new Genetics[genePool.length];
    for(int i = 0; i < genePool.length; i++)
      genePoolRandom[i] = new Genetics();

    System.out.println("===============");
    System.out.println("Strength random std: " + World.calculateGenePoolVariation(genePoolRandom, true));
    System.out.println("Type random std: " + World.calculateGenePoolVariation(genePoolRandom, false));
  }
  
  static void testBrain() {
    Brain b = new Brain(new Genetics());
    System.out.println(b);
    
    try {
      Brain b2 = new Brain(new Genetics(b.dna, b.dna));
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
