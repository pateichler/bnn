package com.pat_eichler.bnn.brain;

public class Connection {
  
  private Genetics dna;
  private Neuron startNeuron;
  private Neuron endNeuron;
  
  private int strength;
  private int ntType;
  
  public Connection(Genetics dna, Neuron startNeuron, Neuron endNeuron) {
    this(dna, startNeuron, endNeuron, 0, 0);
  }

  public Connection(Genetics dna, Neuron startNeuron, Neuron endNeuron, int strength, int ntType) {
    this.dna = dna;
    this.startNeuron = startNeuron;
    this.endNeuron = endNeuron;
    this.strength = strength;
    this.ntType = ntType;
  }
  
  public void trigger() {
    if(strength == 0)
      return;
    
    int count = 1 << (strength - 1);
    endNeuron.addNT(count, ntType);
  }
  
  public void adjust() {
    int prevStrength = strength;
    double[] input = new double[startNeuron.neuroCountSegment.length * 2];
    double norm = (1 << BrainSettings.getInstance().connectionSettings.MAX_CONN_STRENGTH) * BrainSettings.getInstance().connectionSettings.CONN_COUNT / 2;
    
    for(int i = 0; i < startNeuron.neuroCountSegment.length; i++)
      input[i] = Math.min(startNeuron.neuroCountSegment[i] / norm, 1.0);
    
    for(int i = 0; i < startNeuron.neuroCountSegment.length; i++)
      input[i + startNeuron.neuroCountSegment.length] = Math.min(endNeuron.neuroCountSegment[i] / norm, 1.0);
    
    strength += dna.getStrengthChange(input);
    strength = Math.min(BrainSettings.getInstance().connectionSettings.MAX_CONN_STRENGTH, Math.max(0, strength));
    
    if(prevStrength == 0 && strength > 0)
      ntType = dna.getNTChange(input);
  }
  
  public int getStrength() {
    return strength;
  }
  
  public int getNtType() {
    return ntType;
  }
  
  public String toString() {
    return "com.pat_eichler.Connection strength: " + strength + ", type: " + ntType;
  }
}
