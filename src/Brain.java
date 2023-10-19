import java.util.Random;
import java.util.LinkedList;

public class Brain {
  public Genetics dna;
  public Neuron[] neurons;
  
  private Random rand;
  
  
  
  public Brain(Genetics dna) {
    rand = new Random();
    neurons = new Neuron[Settings.Instance.NEURON_COUNT];
    this.dna = dna;
    
    for(int i = 0; i < neurons.length; i ++)
      neurons[i] = new Neuron();
    
    try {
      createConnections();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  void createConnections() throws Exception {
    if(Settings.Instance.NEURON_COUNT <= Settings.Instance.CONN_COUNT)
      throw new Exception("Neuron count must be greater to connection count");
    
    switch(Settings.Instance.CONN_CONFIG) {
      case "neighbor": 
        createNeighborConnections();
        break;
      case "random":
        createRandomConnections();
        break;
       default:
         throw new Exception("Connection configuration not found: " + Settings.Instance.CONN_CONFIG);
    }
  }
  
  public void step() {
    for(Neuron n : neurons)
      n.step();
    
    for(Neuron n : neurons)
      n.postStep();
  }
  
  void createNeighborConnections() {
    for(int n = 0; n < neurons.length; n++) {
      Connection[] connections = new Connection[Settings.Instance.CONN_COUNT];
      
      for(int k = 0; k < connections.length; k++)
        connections[k] = createConnection(n, (n+k+1) % neurons.length);
      
      neurons[n].setConnections(connections);
    }
  }
  
  void createRandomConnections() {
    for(int n = 0; n < neurons.length; n++) {
      LinkedList<Integer> sel = new LinkedList<Integer>();
      sel.add(n);
      Connection[] connections = new Connection[Settings.Instance.CONN_COUNT];
      
      for(int k = 0; k < connections.length; k++) {
        int i = rand.nextInt(neurons.length - k - 1);
        int sIndex = 0;
        
        for(int s : sel) {
          if(i < s)
            break;
          
          i ++;
          sIndex ++;
        }
        
        sel.add(sIndex, i);
        connections[k] = createConnection(n, i);
      }
      
      neurons[n].setConnections(connections);
    }
  }
  
  Connection createConnection(int n1, int n2) {
    int s = 0, t = 0;
    if(Settings.Instance.RANDOMIZE_CONN_STRENGTH) {
      s = rand.nextInt(Settings.Instance.MAX_CONN_STRENGTH/2);
      t = rand.nextInt(Settings.Instance.totalNTCount());
    }
    
    return new Connection(dna, neurons[n1], neurons[n2], s, t);
  }
  
  public void clearTransmitters() {
   for(Neuron n : neurons)
     n.clearTransmitters();
  }
  
  public void printNumActive() {
    int a = 0;
    for(Neuron n : neurons)
      if(n.isActive())
        a++;
    
    System.out.println("Number active: " + a);
  }
  
  public void printConnectionStrengths() {
    for(Neuron n : neurons) {
      for(Connection c : n.connections)
        System.out.print(c.getStrength());
      System.out.println();
    }
  }
  
  public void printConnectionTypes() {
    for(Neuron n : neurons) {
      for(Connection c : n.connections)
        System.out.print(c.getNtType());
      System.out.println();
    }
  }
}
