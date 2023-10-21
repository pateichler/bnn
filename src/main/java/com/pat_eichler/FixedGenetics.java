package com.pat_eichler;

public class FixedGenetics extends Genetics{

  @Override
  public int getStrengthChange(double[] input) {
    if(input[0] > 0 && input[Settings.Instance.totalNTCount() + Settings.Instance.NT_COUNT_EXC + Settings.Instance.NT_COUNT_INH] > 0) {
      return 1;
    }
    return 0;
  }

  @Override
  public int getNTChange(double[] input) {
    return 0;
  }

}
