
public class Settings {
  public static Settings Instance;
  
  public Settings() {
    Instance = this;
  }
  
  // Run
  public int THREAD_COUNT;
  
  // Neurotransmitters
  public int NT_COUNT_EXC;
  public int NT_COUNT_INH;
  public int NT_COUNT_MOD;
  
  // World
  public int POP_SIZE;
  public int NUM_GENS;
  public int TOURN_SIZE;
  public String BRAIN_RUNNER;
  
  // Brain
  public int NEURON_COUNT;
  public int CONN_COUNT;
  public String CONN_CONFIG;
  
  // Connection
  public int NT_THRESHOLD;
  public int CONN_ADJUST_INC;
  public boolean UNSYNC_CONN_ADJUST;
  
  public boolean RANDOMIZE_CONN_STRENGTH;
  public int MAX_CONN_STRENGTH;
  
  // Genetics
  public int[] STRENGTH_NET_IN_LAYERS;
  public int[] TYPE_NET_IN_LAYERS;
  public double MUTATION_RATE;
  public double GENE_COMB_PRECISION;
  public double MAX_PARENT_RATIO;
  
  public int totalNTCount() {
    return NT_COUNT_EXC + NT_COUNT_INH + NT_COUNT_MOD;
  }
  
  public NTAction getNTAction(int ntType) {
    if(ntType < NT_COUNT_EXC)
      return NTAction.EXCITATORY;
    
    if(ntType < NT_COUNT_EXC + NT_COUNT_INH)
      return NTAction.INHIBITORY;
    
    return NTAction.MODULATORY;
  }
  
  public BrainRunner createBrainRunner(Brain b) {
    switch(BRAIN_RUNNER) {
      case "simple":
        return new SimpleRunner(b);
      case "memory":
        return new MemoryRunner(b);
      default:
        return null;
    }
  }
  
}
