package com.pat_eichler.bnn.brain;

import java.io.Serializable;

public class NNGenetics extends GeneticsModel implements Serializable{
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
  @Override
  public int getStrengthChange(double[] input) {
    return strengthNet.predict(input) - 1;
  }
  @Override
  public int getNTChange(double[] input) {
    return typeNet.predict(input);
  }


  @Override
  public Genetics crossGenetics(Genetics otherGenetics, double fitRatio) {
    NNGenetics other = (NNGenetics) otherGenetics;
    try {
      return new NNGenetics(this, other);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public double calculateGenePoolVariation(Genetics[] genePool) {
    double total = 0;
    for(int x = 0; x < 2; x++) {
      boolean strength = x == 0;
      int l = strength ? ((NNGenetics) genePool[0]).strengthNet.getWeightsLength() : ((NNGenetics) genePool[0]).typeNet.getWeightsLength();
      double totStd = 0;

      double[] weights = new double[genePool.length];
      for (int i = 0; i < l; i++) {
        for (int g = 0; g < genePool.length; g++)
          weights[g] = strength ? ((NNGenetics) genePool[g]).strengthNet.getWeights(i) : ((NNGenetics) genePool[g]).typeNet.getWeights(i);

        double mean = 0;
        for (double n : weights)
          mean += n;
        mean /= genePool.length;

        double std = 0;
        for (double n : weights)
          std += Math.pow((n - mean), 2);
        totStd += Math.sqrt(std / genePool.length);
      }

      total += totStd / l;
    }
    return total;
  }
}
