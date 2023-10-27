package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;
import com.pat_eichler.bnn.brain.BrainSettings;

import java.util.Random;

public class SimpleRunner extends BrainRunner {

  private Random rand;
  
  int growthPeriod = 0;
  int teachIterations = 500;
  int testIterations = 50;
  int numOptions = 4;
  
  public SimpleRunner(Brain brain) {
    super(brain);
    rand = new Random();
  }
  
  public Double call() {
    grow();
    
    learn(teachIterations);
        
    return learn(testIterations);
  }
  
  void grow() {
    for(int i = 0; i < growthPeriod; i++)
      brain.step();
  }
  
  double learn(int iterations) {
    int numCorrect = 0;
    
    for(int i = 0; i < iterations; i++) {
      int n = (int)(rand.nextDouble() * numOptions);
      brain.neurons[n].addNT(1 << 10, 0);
      
      brain.step();
      
      boolean correct = false;
      
      outerloop:for(int x = 0; x < 10; x++) {
        for(int o = 0; o < numOptions; o++) {
          boolean state = brain.neurons[brain.neurons.length - numOptions + o].isActive();
          
          // An output neuron is active ... check if it right
          if(state) {
            correct = (o == n);
            
            // com.pat_eichler.Brain choose wrong ... break answer retrieval loop
            if(correct == false)
              break outerloop;
          }
        }
        
        // We have gave the right answer with no other answers ... break out of loop as correct
        if(correct)
          break;
        
        brain.step();
      }
        
      
      if(correct == false)
        brain.neurons[brain.neurons.length - numOptions + n].addNT(1 << 10, BrainSettings.getInstance().ntSettings.totalNTCount() - 1);
      else {
        numCorrect++;
        //Possibly do a reward neurotransmitter here
      }
      
      int learnSteps = 200 + rand.nextInt(100);
      for(int x = 0; x < learnSteps; x++)
        brain.step();
      
//      brain.clearTransmitters();
    }
    
    return (double)numCorrect / iterations;
  }
}
