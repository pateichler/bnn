package com.pat_eichler.bnn.brain;

import java.util.Random;

public abstract class GeneticsModel extends Genetics{

    public GeneticsModel(Random random) {
        super(random);
    }

    public GeneticsModel(DNA dna){
        super(dna);
    }

    public abstract Neuron.NeuronStateChange getNeuronStateChange(short[] preNeuronStateCounts, short[] postNeuronStateCounts, byte curState);
    public abstract boolean getConnectionIncreaseStrength(byte preNeuronState, byte postNeuronState, byte ntType);

    public abstract byte getConnectionCreation(byte preNeuronState, byte postNeuronState);
}
