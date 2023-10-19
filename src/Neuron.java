import java.util.Random;

public class Neuron {
  
  private boolean active;
   
  private int neuroCount;
  public int[] neuroCountSegment;
  
  //TODO: make back to private
  public Connection[] connections;
  
  private int curStep = 0;
  private int coolDown = 0;
  
  public Neuron() {
    int t = Settings.Instance.totalNTCount();
    
    neuroCountSegment = new int[t];
    
    if(Settings.Instance.UNSYNC_CONN_ADJUST)
      curStep = new Random().nextInt(Settings.Instance.CONN_ADJUST_INC);
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
    if(coolDown > 0)
      coolDown --;
    
    if(active == false || coolDown > 0)
      return;
    
    coolDown = Settings.Instance.TRIGGER_COOLDOWN;
    
    for(Connection c : connections)
      c.trigger();
  }
  
  public void postStep() {
    active = neuroCount > Settings.Instance.NT_THRESHOLD;
    neuroCount = 0;
    
    curStep++;
    if(curStep >= Settings.Instance.CONN_ADJUST_INC) {
      curStep = 0;
      this.adjustConnections();
    }
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
  
  public void clearTransmitters() {
    active = false;
    neuroCount = 0;
    for(int i = 0; i < neuroCountSegment.length; i++)
      neuroCountSegment[i] = 0;
  }
}
