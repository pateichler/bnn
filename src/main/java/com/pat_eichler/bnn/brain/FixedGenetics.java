package com.pat_eichler.bnn.brain;

public class FixedGenetics extends Genetics{

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

}
