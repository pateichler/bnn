import java.io.IOException;
import java.util.Scanner;

enum UIPanel{
  MENU, WORLD, HELP
}

public class CLInterface implements CommandInterface, Runnable{
  
  private final int PROGRESS_WIDTH = 20;
  
  private boolean stop;
  private boolean stopImmediately;
  
  private int curBrain = 0;
  private int curGeneration = 0;
  private double[] genFitness;
  
  private Scanner scan;
  private World world;
  private UIPanel curPanel;
  
  public CLInterface(){
    scan = new Scanner(System.in);
  }
  
  @Override
  public void run() {
    setPanel(UIPanel.MENU);
    while(true) {
      if(scan.hasNext()) {
        String input = scan.next();
        for(char c : input.toCharArray()) {
          runCommand(c);
        }
      }
      
      // TODO: consider moving UI rendering on separate thread
      updateUI();
    }
  }
  
  void runCommand(char c) {
    switch(curPanel) {
      case MENU:
        menuCommand(c);
      case WORLD:
        worldCommand(c);
      case HELP:
        break;
    }
  }
  
  void menuCommand(char c) {
    switch(c) {
      case 'n':
        newWorld();
        break;
      case 'p':
        previousWorld();
        break;
    }
  }
  
  void worldCommand(char c) {
    switch(c) {
      case 'q':
        stop = true;
        setPanel(UIPanel.MENU);
        break;
      case 'Q':
        stopImmediately = true;
        setPanel(UIPanel.MENU);
        break;
      case 'h':
        setPanel(UIPanel.HELP);
        break;
    }
  }
  
  void setPanel(UIPanel panel) {
    this.curPanel = panel;
    
    clearScreen();
    
    switch(panel) {
      case MENU:
        System.out.println("World Runner CLI\n\nCommand list:\n\t(n) Create new world\n\t(p) "
            + "Load previous world\n\nEnter command: ");
        break;
      case WORLD:
        System.out.println("Running world...");
        break;
      case HELP:
        System.out.println("TODO complete help menu");
        break;
    }
  }
  
  void updateUI() {
    if(curPanel == UIPanel.WORLD) {
      clearScreen();
      
      System.out.print("Brains: ");
      printProgressBar(curBrain, Settings.Instance.POP_SIZE, '-');
      
      System.out.print("Generation: ");
      printProgressBar(curGeneration, Settings.Instance.NUM_GENS, '=');
    }
  }
  
  void showHelpMenu() {
    
  }
  
  void newWorld() {
    setWorld(WorldCreator.getNew());
  }
  
  void previousWorld() {
    while(true) {
      System.out.print("Type world number or b to cancel: ");
      String input = scan.nextLine();
      
      if(input == "b") {
        setPanel(UIPanel.MENU);
        break;
      }
      
      int worldID = 0;
      try {
        worldID = Integer.parseInt(input);
      }catch(NumberFormatException e) {
        System.out.println("Not valid world ID. Please input an integer.");
      }
      
      World w = WorldCreator.getPrevious(worldID);
      if(world == null) {
        System.out.println("World not found!");
        continue;
      }
      
      setWorld(w);
      break;
    }
  }
  
  void setWorld(World w) {
    this.world = w;
    
    curBrain = 0;
    curGeneration = 0;
    genFitness = null;
    
    setPanel(UIPanel.WORLD);
  }
  
  // TODO: All methods below need to be thread safe
  
  @Override
  public World getWorld() {
    // TODO: block execution and wait on world notify
    
    World runWorld = null;
    
    if(world != null) {
      runWorld = world;
      world = null;
    }
    
    return runWorld;
  }
  
  @Override
  public void postGenerationStatus(int completedBrains) {
    this.curBrain = completedBrains;
  }

  @Override
  public void postGenerationResults(double[] fitness) {
    genFitness = fitness;
    this.curGeneration += 1;
  }

  @Override
  public boolean stopWorld() {
    return stop || stopImmediately;
  }

  @Override
  public boolean stopWorldImmediately() {
    return stopImmediately;
  }
  
  void clearScreen() {
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
    try {
      Runtime.getRuntime().exec("clear");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  void printProgressBar(int amount, int total, char loadMarker) {
    String s = "[";
    float per = (float)amount / total;
    
    for(int i = 0; i < PROGRESS_WIDTH; i++) {
      if((float)i / PROGRESS_WIDTH < per)
        s += loadMarker;
      else
        s += " ";
    }
    s += "] (" + amount + "/" + total + "]";
    
    System.out.println(s);
  }

}
