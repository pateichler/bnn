package com.pat_eichler.bnn.brain;

import java.util.Random;

public class ConwayGenetics extends GeneticsModel{
    private ConwayNeuronGenetics neuronGenetics;
    private ConwayConnectionGenetics connectionGenetics;

    public ConwayGenetics(Random random) {
        super(random);
        init();
        parseDNA(new DNABuffer(random));
    }

    public ConwayGenetics(DNA dna){
        super(dna);
        init();
        parseDNA(new DNABuffer(this.dna));
    }

    private void parseDNA(DNABuffer buffer){
        neuronGenetics.parseDNA(buffer);
        connectionGenetics.parseDNA(buffer);

        if(this.dna == null)
            this.dna = buffer.getNewDNA();
    }

    void init(){
        //TODO: Init can be moved to constructor
        neuronGenetics = new ConwayNeuronGenetics();
        connectionGenetics = new ConwayConnectionGenetics();
    }

//    public DNA getRandomDNA(Random random) {
//        int numBits = 0;
//        numBits += neuronGenetics.getBitSize();
//        numBits += connectionGenetics.getBitSize();
//
//        byte[] data = new byte[Math.ceilDiv(numBits, 8)];
//        random.nextBytes(data);
//        return new DNA(data);
//    }

    @Override
    public Neuron.NeuronStateChange getNeuronStateChange(short[] preNeuronStateCounts, short[] postNeuronStateCounts, byte curState) {
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
