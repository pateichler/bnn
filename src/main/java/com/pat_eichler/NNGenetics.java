package com.pat_eichler;

import java.io.Serializable;

public class NNGenetics extends Genetics implements Serializable{
  private static final long serialVersionUID = 6553723454806296985L;
  
  public NeuralNetwork strengthNet;
  public NeuralNetwork typeNet;
  
  public NNGenetics() {
    int inputNodes = Settings.Instance.totalNTCount() * 2;
    
    int[] strengthLayers = new int[Settings.Instance.STRENGTH_NET_IN_LAYERS.length + 2];
    strengthLayers[0] = inputNodes;
    strengthLayers[strengthLayers.length - 1] = 3;
    for(int i = 0; i < Settings.Instance.STRENGTH_NET_IN_LAYERS.length; i++)
      strengthLayers[i+1] = Settings.Instance.STRENGTH_NET_IN_LAYERS[i];
    
    strengthNet = new NeuralNetwork(strengthLayers);
    
    int[] typeLayers = new int[Settings.Instance.TYPE_NET_IN_LAYERS.length + 2];
    typeLayers[0] = inputNodes;
    typeLayers[typeLayers.length - 1] = Settings.Instance.totalNTCount();
    for(int i = 0; i < Settings.Instance.TYPE_NET_IN_LAYERS.length; i++)
      typeLayers[i+1] = Settings.Instance.TYPE_NET_IN_LAYERS[i];
    
    typeNet = new NeuralNetwork(typeLayers);
  }
  
  public NNGenetics(NNGenetics parent1, NNGenetics parent2) throws Exception {
    this(parent1, parent2, 0.5);
  }
  
  public NNGenetics(NNGenetics parent1, NNGenetics parent2, double parent1Ratio) throws Exception {
    strengthNet = new NeuralNetwork(parent1.strengthNet, parent2.strengthNet, parent1Ratio);
    typeNet = new NeuralNetwork(parent1.typeNet, parent2.typeNet, parent1Ratio);
  }
  
  public int getStrengthChange(double[] input) {
    return strengthNet.predict(input) - 1;
  }
  
  public int getNTChange(double[] input) {
    return typeNet.predict(input);
  }
}
