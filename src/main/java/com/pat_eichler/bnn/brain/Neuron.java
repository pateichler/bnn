package com.pat_eichler.bnn.brain;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Arrays;
import java.util.Random;

public class Neuron {

  private GeneticsModel genetics;
  private boolean active;
  private byte state;
  private boolean stateChanged;
  private int neuroCount;
  private int[] preNeuronStates;
  public final Connection[] connections;
  private int coolDown = 0;

  public static class PostNeuronMode{
    public boolean updateState;
    public boolean searchConnections;
    public PostNeuronMode(boolean updateState, boolean searchConnections){
      this.updateState = updateState;
      this.searchConnections = searchConnections;
    }
  }

  public Neuron() {
    connections = new Connection[BrainSettings.getInstance().neuronSettings.MAX_CONNECTIONS];
  }
  
  public void addNT(int count, int ntType, byte neuronState) {
    neuroCount += count * ntType;
    preNeuronStates[neuronState] += count;
  }
  
  public void step() {
    if(coolDown > 0)
      coolDown --;
    
    if(!active || coolDown > 0)
      return;
    
    coolDown = BrainSettings.getInstance().neuronSettings.TRIGGER_COOLDOWN;
    
    for(Connection c : connections)
      c.trigger(this);
  }
  
  public void postStep(PostNeuronMode mode) {
    active = neuroCount > BrainSettings.getInstance().connectionSettings.NT_THRESHOLD;
    neuroCount = 0;

    if(mode.updateState){
      adjustState();
      adjustConnections();

      Arrays.fill(preNeuronStates, 0);
    }

    if (mode.searchConnections){
      this.searchConnections();
    }
  }

  void adjustState(){
      //TODO: Complete
      int[] postNeuronStates;
  }

  void adjustConnections() {
    for(Connection c : connections)
      c.adjust(this, genetics);
  }

  void searchConnections(){
    //TODO: Complete
  }

  public void removeConnection(Connection connection){
    //TODO: Complete
  }
  
  public boolean isActive() {
    return active;
  }
  public byte getState(){
    return state;
  }
}
