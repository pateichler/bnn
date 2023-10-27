package com.pat_eichler.bnn.brain;

public abstract class GeneticsModel extends Genetics{
    public abstract int getStrengthChange(double[] input);

    public abstract int getNTChange(double[] input);
}
