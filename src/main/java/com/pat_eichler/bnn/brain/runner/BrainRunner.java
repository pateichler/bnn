package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;

import java.util.concurrent.Callable;

public abstract class BrainRunner implements Callable<Double> {
  final Brain brain;
  
  public BrainRunner(Brain brain) {
    this.brain = brain;
  }
  
  public abstract Double call();
}
