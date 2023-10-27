package com.pat_eichler.bnn.brain;

public class FixedGenetics extends GeneticsModel{
@Override
  public int getStrengthChange(double[] input) {
    if(input[0] > 0 && input[BrainSettings.getInstance().ntSettings.totalNTCount() + BrainSettings.getInstance().ntSettings.NT_COUNT_EXC + BrainSettings.getInstance().ntSettings.NT_COUNT_INH] > 0) {
      return 1;
    }
    return 0;
  }
  @Override
  public int getNTChange(double[] input) {
    return 0;
  }

  @Override
  public Genetics crossGenetics(Genetics otherGenetics, double fitRatio) {
    return new FixedGenetics();
  }

  @Override
  public double calculateGenePoolVariation(Genetics[] genePool) {
    return 0;
  }
}
