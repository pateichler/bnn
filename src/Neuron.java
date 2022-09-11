
public class Neuron {
  
  private boolean active;
  
  private int neuroCount;
  public int[] neuroCountSegment;
  
  //TODO: make back to private
  public Connection[] connections;
  
  public Neuron() {
    int t = Settings.Instance.totalNTCount();
    
    neuroCountSegment = new int[t];
  }
  
  public void addNT(int count, int ntType) {
    NTAction action = Settings.Instance.getNTAction(ntType);
    
    if(action == NTAction.EXCITATORY)
      neuroCount += count;
    
    if(action == NTAction.INHIBITORY)
      neuroCount -= count;
    
    neuroCountSegment[ntType] += count;
  }
  
  public void step() {
    if(active == false)
      return;
    
    for(Connection c : connections)
      c.trigger();
  }
  
  public void postStep(boolean adjust) {
    active = neuroCount > Settings.Instance.NT_THRESHOLD;
    neuroCount = 0;
    
    if(adjust)
      this.adjustConnections();
  }
  
  void adjustConnections() {
    for(Connection c : connections)
      c.adjust();
    
    // Reset segment count
    for(int i = 0; i < neuroCountSegment.length; i ++)
      neuroCountSegment[i] = 0;
  }
  
  public void setConnections(Connection[] connections) {
    this.connections = connections;
  }
  
  public boolean isActive() {
    return active;
  }
}
