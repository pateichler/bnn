package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCommon {
    @Test
    void testBinlog(){
        Assertions.assertEquals(4, Common.binlog(24));
        Assertions.assertEquals(2, Common.binlog(7));
        Assertions.assertEquals(6, Common.binlog(64));
        Assertions.assertEquals(0, Common.binlog(1));
        Assertions.assertEquals(0, Common.binlog(0));
    }

    @Test
    void testNumBitsToEncode(){
        Assertions.assertEquals(3, Common.numBitsToEncode(8));
        Assertions.assertEquals(3, Common.numBitsToEncode(5));
        Assertions.assertEquals(2, Common.numBitsToEncode(4));
        Assertions.assertEquals(1, Common.numBitsToEncode(1));
    }
}
