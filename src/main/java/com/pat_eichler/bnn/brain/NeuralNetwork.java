package com.pat_eichler.bnn.brain;// Implemented from https://github.com/SebLague/Neural-Network-Experiments

import java.io.Serializable;
import java.util.Random;

public class NeuralNetwork implements Serializable {
  
  private static final long serialVersionUID = -8485634800997941752L;
  
  private NNLayer[] layers;
  
  public NeuralNetwork(int[] layerSizes, double mutationRate) {
    Random rand = new Random(); 
    
    layers = new NNLayer[layerSizes.length - 1];
    
    for(int i = 0; i < layers.length; i++)
      layers[i] = new NNLayer(layerSizes[i], layerSizes[i + 1], rand, mutationRate);
  }
  
  public NeuralNetwork(NeuralNetwork parent1, NeuralNetwork parent2, double parent1Ratio, double mutationRate) throws Exception {
    if(parent1.layers.length != parent2.layers.length)
      throw new Exception("Number of layers between neural networks don't match: " +
          parent1.layers.length + " and " + parent2.layers.length);
    
    Random rand = new Random(); 
    
    layers = new NNLayer[parent1.layers.length];
    
    for(int i = 0; i < layers.length; i++)
      layers[i] = new NNLayer(parent1.layers[i], parent2.layers[i], parent1Ratio, rand, mutationRate);
  }
  
  public int predict(double[] inputs)
  {
    double[] outputs = calculateOutputs(inputs);
    
    int index = 0;
    double max = outputs[0];
    for(int i = 1; i < outputs.length; i ++) {
      if(outputs[i] > max) {
        index = i;
        max = outputs[i];
      }
    }
    
    return index;
  }

  
  double[] calculateOutputs(double[] inputs)
  {
    for (NNLayer layer : layers)
      inputs = layer.CalculateOutputs(inputs);
    
    return inputs;
  }
  
  public double getWeights(int i) {
    for(NNLayer l : layers) {
      if(i < l.weights.length)
        return l.weights[i];
      i -= l.weights.length;
    }
    
    throw new IndexOutOfBoundsException("Index is out of bounds");
  }
  
  public double getBiases(int i) {
    for(NNLayer l : layers) {
      if(i < l.biases.length)
        return l.biases[i];
      i -= l.biases.length;
    }
    
    throw new IndexOutOfBoundsException("Index is out of bounds");
  }
  
  public int getWeightsLength() {
    int c = 0;
    for(NNLayer l : layers)
      c += l.weights.length;
    
    return c;
  }
  
  public int getBiasesLength() {
    int c = 0;
    for(NNLayer l : layers)
      c += l.biases.length;
    
    return c;
  }
}
