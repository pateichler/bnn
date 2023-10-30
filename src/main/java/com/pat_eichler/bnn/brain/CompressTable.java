package com.pat_eichler.bnn.brain;

public class CompressTable {
    private final byte[][] table;
    private final int entrySize;
    private final byte[] rowTranslations, colTranslations;
    public CompressTable(byte[][] table, int numEntries){
        this(table, numEntries, new byte[numEntries], new byte[numEntries]);
    }
    public CompressTable(byte[][] table, int numEntries, byte[] rowTranslations, byte[] colTranslations){
        this.table = table;
        entrySize = Integer.highestOneBit(table.length) + 1;

        this.rowTranslations = rowTranslations;
        this.colTranslations = colTranslations;
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