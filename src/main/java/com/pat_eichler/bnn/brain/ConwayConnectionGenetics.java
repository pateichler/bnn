package com.pat_eichler.bnn.brain;

public class ConwayConnectionGenetics {

    private final CompressTable[] changeTables;
    private final CompressTable createTable;
    static class CompressTable{
        private final byte[][] table;
        private final int entrySize;
        private final byte[] rowTranslations, colTranslations;
        public CompressTable(byte[][] table, int numEntries){
            this.table = table;
            entrySize = Integer.highestOneBit(table.length) + 1;
            rowTranslations = new byte[numEntries];
            colTranslations = new byte[numEntries];
        }
        public void parseDNA(DNABuffer buffer){
            for (int i = 0; i < rowTranslations.length; i++)
                rowTranslations[i] = (byte)buffer.getBits(entrySize);

            for (int i = 0; i < colTranslations.length; i++)
                colTranslations[i] = (byte)buffer.getBits(entrySize);
        }
        public byte getTableEntry(byte row, byte col){
            return table[rowTranslations[row]][colTranslations[col]];
        }

        public int getBitSize(){
            return rowTranslations.length * entrySize + colTranslations.length * entrySize;
        }
    }

    public ConwayConnectionGenetics(){
        changeTables = new CompressTable[3];
        for (int i = 0; i < changeTables.length; i++)
            changeTables[i] = new CompressTable(BrainSettings.GeneticSettings.CONN_CHANGE_TABLE, BrainSettings.getInstance().neuronSettings.NUM_STATES);

        createTable = new CompressTable(BrainSettings.GeneticSettings.CONN_CREATE_TABLE, BrainSettings.getInstance().neuronSettings.NUM_STATES);
    }
    public void parseDNA(DNABuffer buffer){
        for (CompressTable changeTable : changeTables)
            changeTable.parseDNA(buffer);

        createTable.parseDNA(buffer);
    }

    public int getBitSize(){
        int size = 0;
        for (CompressTable changeTable : changeTables)
            size += changeTable.getBitSize();
        return size + createTable.getBitSize();
    }

    public boolean getConnectionIncreaseStrength(byte preNeuronState, byte postNeuronState, byte connectionType) {
        return changeTables[connectionType+1].getTableEntry(preNeuronState, postNeuronState) == 1;
    }

    public byte getConnectionCreation(byte preNeuronState, byte postNeuronState) {
        return createTable.getTableEntry(preNeuronState, postNeuronState);
    }
}
