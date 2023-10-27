package com.pat_eichler.bnn.brain;

import java.util.Random;

public class ConwayGenetics extends GeneticsModel{

    public ConwayGenetics(Random random) {
        super(random);
        parseDNA();
    }

    public ConwayGenetics(DNA dna){
        super(dna);
        parseDNA();
    }

    private void parseDNA(){

    }

    @Override
    public DNA getRandomDNA(Random random) {
        return null;
    }

    @Override
    public byte getNeuronStateChange(byte[] preNeuronStateCounts, byte[] postNeuronStateCounts, byte curState) {
        return 0;
    }

    @Override
    public boolean getConnectionIncreaseStrength(byte preNeuronState, byte postNeuronState) {
        return false;
    }

    @Override
    public byte getConnectionCreation(byte preNeuronState, byte postNeuronState) {
        return 0;
    }
}
