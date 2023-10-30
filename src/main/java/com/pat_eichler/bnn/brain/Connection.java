package com.pat_eichler.bnn.brain;

public class Connection {
  
  private GeneticsModel genetics;
  public final Neuron endNeuron;
  
  private int strength;
  private final byte ntType;
  private final BrainSettings.ConnectionSettings connectionSettings;


  public Connection(Neuron endNeuron, byte ntType) {
    if(ntType > BrainSettings.getInstance().connectionSettings.NT_TYPE_NEURON_CHANGE.length)
      throw new RuntimeException("Invalid NT type: " + ntType);

    this.endNeuron = endNeuron;
    this.strength = BrainSettings.getInstance().connectionSettings.START_CONNECTION_STRENGTH;
    this.ntType = ntType;
    connectionSettings = BrainSettings.getInstance().connectionSettings;
  }
  
  public void trigger(Neuron startNeuron) {
    endNeuron.addNT(getPhysicalStrength(), ntType, startNeuron.getState(), true);
  }
  
  public boolean adjust(Neuron startNeuron, GeneticsModel genetics) {
    if(genetics.getConnectionIncreaseStrength(startNeuron.getState(), endNeuron.getState(), ntType))
      setStrength(Math.min(strength + connectionSettings.STRENGTH_INCREASE, connectionSettings.MAX_STRENGTH));
    else
      setStrength(strength << 1);

    return strength > 0;
  }
  
  public int getStrength() {
    return strength;
  }

  void setStrength(int s){
    strength = s;
  }

  public short getPhysicalStrength(){
    return (short)(Common.binlog(strength) + 1);
  }

  public int getNtType(){
    return ntType;
  }
  
  public String toString() {
    return "Physical strength: " + getPhysicalStrength() + ", type: " + ntType;
  }
}
