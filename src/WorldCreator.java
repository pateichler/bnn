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

public class WorldCreator {
  public static World getNew() {
    int worldID = getLastWorldID() + 1;
    loadGlobalSettings().setInstance();;
    
    World w = new World(worldID);
    
    try {
      copySettingsToWorld(worldID);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    
    return w;
  }
  
  public static World getNew(Settings s) {
    int worldID = getLastWorldID();
    
    s.setInstance();;
    
    return new World(worldID);
  }
  
  public static World getPrevious(int worldID) {
    Settings s = loadWorldSettings(worldID);
    
    return getPrevious(worldID, s);
  }
  
  public static World getPrevious(int worldID, Settings s) {
    if(s == null) {
//      System.out.println("Error: no settings given for world ... exiting");
      return null;
    }
    
    s.setInstance();
    
    Genetics[] genePool = loadGenePool(worldID);
    
    return new World(worldID, genePool);
  }
  
  static Settings loadGlobalSettings() {
    return loadSettingsFromPath(Paths.get("config.json"));
  }
  
  static Settings loadWorldSettings(int worldID) {
    return loadSettingsFromPath(Paths.get("experiments", String.valueOf(worldID), "config.json"));
  }
  
  static Settings loadSettingsFromPath(Path path) {
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
  
  static void copySettingsToWorld(int worldID) throws IOException {
    Files.copy(Paths.get("config.json"), Paths.get("experiments", String.valueOf(worldID), "config.json"));
  }
  
  static Genetics[] loadGenePool(int worldID) {
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
  
  static int getLastWorldID() {
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
}
