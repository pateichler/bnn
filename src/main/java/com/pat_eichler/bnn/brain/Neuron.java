package com.pat_eichler.bnn.brain;

import java.nio.file.attribute.UserPrincipalLookupService;
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
    int t = BrainSettings.getInstance().ntSettings.totalNTCount();
    
    neuroCountSegment = new int[t];
    
    if(BrainSettings.getInstance().connectionSettings.UNSYNC_CONN_ADJUST)
      curStep = new Random().nextInt(BrainSettings.getInstance().connectionSettings.CONN_ADJUST_INC);
  }
  
  public void addNT(int count, int ntType) {
    NTAction action = BrainSettings.getInstance().ntSettings.getNTAction(ntType);
    
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
    
    coolDown = BrainSettings.getInstance().neuronSettings.TRIGGER_COOLDOWN;
    
    for(Connection c : connections)
      c.trigger();
  }
  
  public void postStep() {
    active = neuroCount > BrainSettings.getInstance().connectionSettings.NT_THRESHOLD;
    neuroCount = 0;
    
    curStep++;
    if(curStep >= BrainSettings.getInstance().connectionSettings.CONN_ADJUST_INC) {
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
