package com.pat_eichler.bnn.brain;

public abstract class Genetics{

  public Genetics crossGenetics(Genetics otherGenetics){return crossGenetics(otherGenetics, 1f);}
  public abstract Genetics crossGenetics(Genetics otherGenetics, double fitRatio);

  public abstract double calculateGenePoolVariation(Genetics[] genePool);

//  public abstract int getStrengthChange(double[] input);
//
//  public abstract int getNTChange(double[] input);
}
