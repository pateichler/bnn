package com.pat_eichler.bnn.brain;

public class ConwayConnectionGenetics {

    private final CompressTable[] changeTables;
    private final CompressTable createTable;

    public ConwayConnectionGenetics(){
        changeTables = new CompressTable[BrainSettings.getInstance().connectionSettings.NT_TYPE_NEURON_CHANGE.length];
        for (int i = 0; i < changeTables.length; i++)
            changeTables[i] = new CompressTable(BrainSettings.GeneticSettings.CONN_CHANGE_TABLE, BrainSettings.getInstance().neuronSettings.NUM_STATES);

        createTable = new CompressTable(BrainSettings.GeneticSettings.CONN_CREATE_TABLE, BrainSettings.getInstance().neuronSettings.NUM_STATES);
    }

    public void parseDNA(DNABuffer buffer){
        for (CompressTable changeTable : changeTables)
            changeTable.parseDNA(buffer);

        createTable.parseDNA(buffer);
    }

    public boolean getConnectionIncreaseStrength(byte preNeuronState, byte postNeuronState, byte ntType) {
        return changeTables[ntType].getTableEntry(preNeuronState, postNeuronState) == 1;
    }

    public byte getConnectionCreation(byte preNeuronState, byte postNeuronState) {
        return createTable.getTableEntry(preNeuronState, postNeuronState);
    }
}
