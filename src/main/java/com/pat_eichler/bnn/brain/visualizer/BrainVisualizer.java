package com.pat_eichler.bnn.brain.visualizer;

import com.pat_eichler.bnn.brain.Brain;
import com.pat_eichler.bnn.brain.Neuron;

public class BrainVisualizer {
  
  String ANSI_RESET = "\u001B[0m";
  String ANSI_RED = "\u001B[31m";
  String ANSI_GREEN = "\u001B[32m";
  
  private Brain brain;
  private int numInput, numOutput;
  private int width;
  
  public BrainVisualizer(Brain brain, int inputNeurons, int outputNeurons, int width, boolean useColor) {
    this.brain = brain;
    this.numInput = inputNeurons;
    this.numOutput = outputNeurons;
    this.width = width;
    
    if(useColor == false) {
      ANSI_RESET = "";
      ANSI_RED = "";
      ANSI_GREEN = "";
    } 
  }
  
  public void visualize() {
    
    int inputSpacing = (width - numInput)/2;
    for(int i = 0; i < inputSpacing; i++)
      System.out.print(' ');
    
    for(int i = 0; i < numInput; i++)
      printNeuron(brain.neurons[i]);
    
    for(int i = numInput; i < brain.neurons.length - numOutput; i++) {
      if((i - numInput) % width == 0) {
        System.out.println("");
        
        int numLeft = brain.neurons.length - numOutput - i;
        if(numLeft < width) {
          int lastSpacing = (width - numLeft)/2;
          for(int s = 0; s < lastSpacing; s++)
            System.out.print(' ');
        }
      }
      
      printNeuron(brain.neurons[i]);
    }
    
    System.out.println("");
    
    int outputSpacing = (width - numOutput)/2;
    for(int i = 0; i < outputSpacing; i++)
      System.out.print(' ');
    
    for(int i = brain.neurons.length - numOutput; i < brain.neurons.length; i++)
      printNeuron(brain.neurons[i]);
    
    System.out.println("\n");
  }
  
  void printNeuron(Neuron n) {
    char nonActive = n.neuroCountSegment[0] > 0 ? 'Z' : 'X';
    
    System.out.print(n.isActive() ? (ANSI_GREEN + "O" + ANSI_RESET) : nonActive);
  }
}
