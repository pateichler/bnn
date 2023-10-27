package com.pat_eichler.bnn.brain.visualizer;

import com.pat_eichler.bnn.brain.Brain;
import com.pat_eichler.bnn.brain.BrainSettings;

import java.util.Random;

// TODO: eliminate this class and maker com.pat_eichler.BrainRunner base class have the ability to visualize
public class SimpleRunnerVisualizer {

  private Random rand;
  private Brain brain;
  public BrainVisualizer br;
  
  int teachIterations = 500;
  int numOptions = 4;
  
  public SimpleRunnerVisualizer(Brain brain) {
    rand = new Random();
    this.brain = brain;
    br = new BrainVisualizer(brain, numOptions, numOptions, 10, true);
  }
  
  public void run() {
    try {
      int n = (int)(rand.nextDouble() * numOptions);
      brain.neurons[n].addNT(1 << 10, 0);
      
      stepBrain();
      
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
        
        stepBrain();
      }
        
      
      if(correct == false)
        brain.neurons[brain.neurons.length - numOptions + n].addNT(1 << 10, brain.settings.ntSettings.totalNTCount() - 1);
      
      System.out.println("Answered " + (correct ? "correct!" : "wrong"));
      
      int learnSteps = 200 + rand.nextInt(100);
      for(int x = 0; x < learnSteps; x++)
        stepBrain();
    } catch (InterruptedException e) {}
  }
  
  void stepBrain() throws InterruptedException {
    brain.step();
    br.visualize();
    Thread.sleep(500);
  }
}
