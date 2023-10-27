package com.pat_eichler.bnn.brain;

import java.io.Serializable;

public class NNGenetics extends Genetics implements Serializable{
  private static final long serialVersionUID = 6553723454806296985L;
  
  public NeuralNetwork strengthNet;
  public NeuralNetwork typeNet;
  
  public NNGenetics() {
    BrainSettings settings = BrainSettings.getInstance();
    int inputNodes = settings.ntSettings.totalNTCount() * 2;
    
    int[] strengthLayers = new int[settings.geneticSettings.STRENGTH_NET_IN_LAYERS.length + 2];
    strengthLayers[0] = inputNodes;
    strengthLayers[strengthLayers.length - 1] = 3;
    for(int i = 0; i < settings.geneticSettings.STRENGTH_NET_IN_LAYERS.length; i++)
      strengthLayers[i+1] = settings.geneticSettings.STRENGTH_NET_IN_LAYERS[i];
    
    strengthNet = new NeuralNetwork(strengthLayers, settings.MUTATION_RATE);
    
    int[] typeLayers = new int[settings.geneticSettings.TYPE_NET_IN_LAYERS.length + 2];
    typeLayers[0] = inputNodes;
    typeLayers[typeLayers.length - 1] = settings.ntSettings.totalNTCount();
    for(int i = 0; i < settings.geneticSettings.TYPE_NET_IN_LAYERS.length; i++)
      typeLayers[i+1] = settings.geneticSettings.TYPE_NET_IN_LAYERS[i];
    
    typeNet = new NeuralNetwork(typeLayers, settings.MUTATION_RATE);
  }
  
  public NNGenetics(NNGenetics parent1, NNGenetics parent2) throws Exception {
    this(parent1, parent2, 0.5);
  }
  
  public NNGenetics(NNGenetics parent1, NNGenetics parent2, double parent1Ratio) throws Exception {
    strengthNet = new NeuralNetwork(parent1.strengthNet, parent2.strengthNet, parent1Ratio, BrainSettings.getInstance().MUTATION_RATE);
    typeNet = new NeuralNetwork(parent1.typeNet, parent2.typeNet, parent1Ratio, BrainSettings.getInstance().MUTATION_RATE);
  }
  
  public int getStrengthChange(double[] input) {
    return strengthNet.predict(input) - 1;
  }
  
  public int getNTChange(double[] input) {
    return typeNet.predict(input);
  }
}
