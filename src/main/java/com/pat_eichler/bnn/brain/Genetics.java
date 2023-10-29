package com.pat_eichler.bnn.brain;

import java.util.Random;

public abstract class Genetics{
  public DNA dna;
  public Genetics(Random random){}

  public Genetics(DNA dna){
    this.dna = dna;
  }
}
