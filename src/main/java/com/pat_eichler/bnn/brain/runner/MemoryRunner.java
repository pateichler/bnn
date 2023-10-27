package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;
import com.pat_eichler.bnn.brain.BrainSettings;

import java.util.Random;

public class MemoryRunner extends BrainRunner {
  
  private Random rand;
  
  int growthPeriod = 200;
  int teachIterations = 100;
  int testIterations = 25;
  
  public MemoryRunner(Brain brain) {
    super(brain);
    rand = new Random();
  }
  
  public Double call() {
    grow();
    
    learn(teachIterations);
    
//    System.out.println("com.pat_eichler.Connection status");
//    for(com.pat_eichler.Connection c : brain.neurons[10].connections)
//      System.out.println(c);
    
//    brain.printNumActive();
    
    return learn(testIterations);
  }
  
  void grow() {
    for(int i = 0; i < growthPeriod; i++)
      brain.step();
  }
  
  double learn(int iterations) {
    int numCorrect = 0;
    
    for(int i = 0; i < iterations; i++) {
      int n = rand.nextDouble() > 0.5 ? 1 : 0;
      brain.neurons[n].addNT(1 << 10, 0);
      
      for(int x = 0; x < 50; x++)
        brain.step();
      
      brain.neurons[2].addNT(1 << 10, 0);
      for(int x = 0; x < 30; x++)
        brain.step();
      
      
      brain.step();
      boolean out1 = brain.neurons[brain.neurons.length - 2].isActive() == (n == 0);
      boolean out2 = brain.neurons[brain.neurons.length - 1].isActive() == (n == 1);
      
      boolean correct = out1 && out2;
      
//      if(brain.neurons[brain.neurons.length - 2].isActive())
//        System.out.println("ACTIVE 1");
//      
//      if(brain.neurons[brain.neurons.length - 1].isActive())
//        System.out.println("ACTIVE 2");
      
      if(correct == false)
        brain.neurons[brain.neurons.length - 1 - n].addNT(1 << 10, BrainSettings.getInstance().ntSettings.totalNTCount() - 1);
      else {
        numCorrect++;
//        System.out.println("Correct!!!");
      }
      
      int learnSteps = 200 + rand.nextInt(100);
      for(int x = 0; x < learnSteps; x++)
        brain.step();
      
//      System.out.println("Completed learn iteration: (" + i + "/" + iterations + ")");
    }
    
    return (double)numCorrect / iterations;
  }
}
