package com.pat_eichler;// Implemented from https://github.com/SebLague/Neural-Network-Experiments

import java.io.Serializable;
import java.util.Random;

public class NNLayer implements Serializable {
  
  private static final long serialVersionUID = -8188718003272280391L;
  
  private int numNodesIn;
  private int numNodesOut;
  
  public double[] weights;
  public double[] biases;
  
  public NNLayer(int numNodesIn, int numNodesOut, Random rand)
  {
    this.numNodesIn = numNodesIn;
    this.numNodesOut = numNodesOut;
    
    weights = new double[numNodesIn * numNodesOut];
    biases = new double[numNodesOut];
    
    double norm = Math.sqrt(numNodesIn);
    
    for(int i = 0; i < weights.length; i++)
      weights[i] = rand.nextGaussian() / norm;
    
    for(int i = 0; i < biases.length; i++)
      biases[i] = rand.nextGaussian() / norm;
  }
  
  public NNLayer(NNLayer parent1, NNLayer parent2, double parent1Ratio, Random rand) throws Exception {
    if(parent1.numNodesIn != parent2.numNodesIn)
      throw new Exception("Nodes in don't match between parents: " + 
          parent1.numNodesIn + " and " + parent2.numNodesIn);
    
    if(parent1.numNodesOut != parent2.numNodesOut)
      throw new Exception("Nodes out don't match between parents: " + 
          parent1.numNodesOut + " and " + parent2.numNodesOut);
    
    this.numNodesIn = parent1.numNodesIn;
    this.numNodesOut = parent1.numNodesOut;
    
    weights = new double[numNodesIn * numNodesOut];
    biases = new double[numNodesOut];
    
//    createChildAttribute(weights, parent1.weights, parent2.weights, rand);
//    createChildAttribute(biases, parent1.biases, parent2.biases, rand);
    createChild(parent1, parent2, parent1Ratio, rand);
  }
  
  void createChildAttribute(double[] attr, double[] parent1, double[] parent2, Random rand) {
    for(int i = 0; i < attr.length; i++) {
      if (rand.nextDouble() < Settings.Instance.MUTATION_RATE) {
        attr[i] = rand.nextGaussian() / Math.sqrt(numNodesIn);
      }else {
        double diff = parent2[i] - parent1[i];
        double offset = rand.nextGaussian() * Math.abs(diff) / Settings.Instance.GENE_COMB_PRECISION;
        attr[i] = parent1[i] + diff/2 + offset;
      }
    }
  }
  
  void createChild(NNLayer parent1, NNLayer parent2, double parent1Ratio, Random rand) {
    for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++) {
//      boolean isParent1 = rand.nextBoolean();
      boolean isParent1 = rand.nextDouble() < parent1Ratio;
      
      // Set weights
      for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++) {
        int index = nodeOut * numNodesIn + nodeIn; 
        
        if(rand.nextDouble() < Settings.Instance.MUTATION_RATE)
          weights[index] = rand.nextGaussian() / Math.sqrt(numNodesIn);
        else
          weights[index] = isParent1 ? parent1.weights[index] : parent2.weights[index];
      }
      
      // Set bias
      if(rand.nextDouble() < Settings.Instance.MUTATION_RATE)
        biases[nodeOut] = rand.nextGaussian() / Math.sqrt(numNodesIn);
      else
        biases[nodeOut] = isParent1 ? parent1.biases[nodeOut] : parent2.biases[nodeOut];
    }
  }
  
  public double[] CalculateOutputs(double[] inputs) {
    double[] weightedInputs = new double[numNodesOut];

    for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++)
    {
        double weightedInput = biases[nodeOut];

        for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++)
        {
            weightedInput += inputs[nodeIn] * GetWeight(nodeIn, nodeOut);
        }
        weightedInputs[nodeOut] = weightedInput;
    }

    // Apply activation function
    double[] activations = new double[numNodesOut];
    for (int outputNode = 0; outputNode < numNodesOut; outputNode++)
    {
        activations[outputNode] = activate(weightedInputs, outputNode);
    }

    return activations;
  }
  
  double activate(double[] inputs, int index)
  {
    //TODO: switch between activation functions
    return 1.0 / (1 + Math.exp(-inputs[index]));
  }
  
  double GetWeight(int nodeIn, int nodeOut)
  {
      int flatIndex = nodeOut * numNodesIn + nodeIn;
      return weights[flatIndex];
  }
}
