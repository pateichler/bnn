package com.pat_eichler.bnn.brain;

import java.util.Random;

public abstract class Genetics{
  public final DNA dna;
  public Genetics(Random random){
    this.dna = getRandomDNA(random);
  }

  public Genetics(DNA dna){
    this.dna = dna;
  }

  public abstract DNA getRandomDNA(Random random);

//  public Genetics crossGenetics(Genetics otherGenetics){return crossGenetics(otherGenetics, 1f);}
//  public abstract Genetics crossGenetics(Genetics otherGenetics, double fitRatio);
//
//  public abstract double calculateGenePoolVariation(Genetics[] genePool);

//  public abstract int getStrengthChange(double[] input);
//
//  public abstract int getNTChange(double[] input);
}
