
public class Runner {
  
  private CommandInterface iface;
  
  public Runner(CommandInterface iface) {
    this.iface = iface;
  }
  
  public static void main(String[] args) {    
    // TODO: get command interface type from arguments
    // Default to command line
    
    System.out.println("Running");
    
    new Thread().start();
    CommandInterface f = new CLInterface();
    
    System.out.println("Running runner");
    Runner r = new Runner(f);
    r.run();
  }
  
  public void run() {
    // Start interface if it is a runnable class
    if(iface instanceof Runnable)
      new Thread((Runnable)iface).start();
    
    while(true) {
      World w = iface.getWorld();
      
      if(w == null) {
        continue;
      }
      
      w.setController(iface);
      System.out.println("Starting world!");
      w.run();
    }
  }
}
