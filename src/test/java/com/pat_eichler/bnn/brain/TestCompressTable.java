package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCompressTable {
    @Test
    void testGetBitSize(){
        CompressTable t = new CompressTable(new byte[8][8], 6);
        Assertions.assertEquals(3*6+3*6, t.getBitSize(), "Wrong bit size of Compress Table");
    }

    @Test
    void testGetEntry(){
        byte[][] testTable = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {8, 9, 10, 11},
                {12, 13, 14, 15}
        };

        byte[] rowTranslation = new byte[]{0, 3, 2, 2, 0, 1};
        byte[] colTranslation = new byte[]{0, 1, 3, 3, 1, 0};
        CompressTable t = new CompressTable(testTable, rowTranslation, colTranslation);

        //[2, 0]
        Assertions.assertEquals(8, t.getTableEntry((byte) 3, (byte) 0));
        //[1, 3]
        Assertions.assertEquals(7, t.getTableEntry((byte) 5, (byte) 2));
        //[3, 1]
        Assertions.assertEquals(13, t.getTableEntry((byte) 1, (byte) 4));
        //[2, 3]
        Assertions.assertEquals(11, t.getTableEntry((byte) 2, (byte) 2));
        //[1, 0]
        Assertions.assertEquals(4, t.getTableEntry((byte) 5, (byte) 5));
    }
}
