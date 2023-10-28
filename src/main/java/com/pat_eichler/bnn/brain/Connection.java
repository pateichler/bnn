package com.pat_eichler.bnn.brain;

public class Connection {
  
  private GeneticsModel genetics;
  private Neuron endNeuron;
  
  private int strength;
  private final byte ntType;
  private final BrainSettings.ConnectionSettings connectionSettings;


  public Connection(Neuron endNeuron, byte neuroTransmitterType) {
    this.endNeuron = endNeuron;
    this.strength = BrainSettings.getInstance().connectionSettings.START_CONNECTION_STRENGTH;
    this.ntType = neuroTransmitterType;
    connectionSettings = BrainSettings.getInstance().connectionSettings;
  }
  
  public void trigger(Neuron startNeuron) {
    if(strength == 0)
      return;
    
    int count = 1 << (strength - 1);
    endNeuron.addNT(count, ntType, startNeuron.getState());
  }
  
  public void adjust(Neuron startNeuron, GeneticsModel genetics) {
    if(genetics.getConnectionIncreaseStrength(startNeuron.getState(), endNeuron.getState(), ntType))
      strength = Math.min(strength + connectionSettings.STRENGTH_INCREASE, connectionSettings.MAX_STRENGTH);
    else
      strength = strength << 1;

    if(strength == 0)
      startNeuron.removeConnection(this);

  }
  
  public int getStrength() {
    return strength;
  }

  public int getNtType(){
    return ntType;
  }
  
  public String toString() {
    return "Strength: " + strength + ", type: " + ntType;
  }
}
