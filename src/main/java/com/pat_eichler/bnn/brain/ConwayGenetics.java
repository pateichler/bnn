package com.pat_eichler.bnn.brain;

import java.util.Random;

public class ConwayGenetics extends GeneticsModel{
    private ConwayNeuronGenetics neuronGenetics;
    private ConwayConnectionGenetics connectionGenetics;

    public ConwayGenetics(Random random) {
        super(random);
        init();
        this.dna = getRandomDNA(random);
        parseDNA();
    }

    public ConwayGenetics(DNA dna){
        super(dna);
        init();
        parseDNA();
    }

    private void parseDNA(){
        DNABuffer buffer = new DNABuffer(this.dna);
        neuronGenetics.parseDNA(buffer);
        connectionGenetics.parseDNA(buffer);
    }

    void init(){
        neuronGenetics = new ConwayNeuronGenetics();
        connectionGenetics = new ConwayConnectionGenetics();
    }

    public DNA getRandomDNA(Random random) {
        int numBits = 0;
        numBits += neuronGenetics.getBitSize();
        numBits += connectionGenetics.getBitSize();

        byte[] data = new byte[Math.ceilDiv(numBits, 8)];
        random.nextBytes(data);
        return new DNA(data);
    }

    @Override
    public byte getNeuronStateChange(short[] preNeuronStateCounts, short[] postNeuronStateCounts, byte curState) {
        return neuronGenetics.getNeuronStateChange(preNeuronStateCounts, postNeuronStateCounts, curState);
    }

    @Override
    public boolean getConnectionIncreaseStrength(byte preNeuronState, byte postNeuronState, byte ntType) {
        return connectionGenetics.getConnectionIncreaseStrength(preNeuronState, postNeuronState, ntType);
    }

    @Override
    public byte getConnectionCreation(byte preNeuronState, byte postNeuronState) {
        return connectionGenetics.getConnectionCreation(preNeuronState, postNeuronState);
    }
}
