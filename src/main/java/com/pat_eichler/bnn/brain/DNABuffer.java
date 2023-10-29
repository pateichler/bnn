package com.pat_eichler.bnn.brain;

import java.nio.ByteBuffer;

public class DNABuffer {

    private final ByteBuffer buffer;
//    private final byte[] data;
    byte curByte;
    int curBitPos;

    final int[] leftBitMask = new int[] {0,1,3,7,15,31,63,127,255};

    public DNABuffer(DNA dna){
        buffer = ByteBuffer.wrap(dna.data);
//        data = dna.data;
    }

    private int trimByte(int b, int startBit, int endBit){
        b = b >> (8 - endBit);
        if(startBit == 0)
            return b;
        return b & leftBitMask[endBit - startBit];
    }
    public int getGrayCodeBits(int numBits){
        int val = getBits(numBits);
        int ret = val;

        while(val > 0){
            val = val  >> 1;
            ret ^= val;
        }

        int i = ret - (1 << (numBits - 1));
        return i >= 0 ? i : i + 1;
    }

    public int getBits(int numBits){
        //TODO: Test this method
        if(numBits > 32 || numBits <= 0)
            throw new RuntimeException("Invalid number of read bits: " + numBits);

        int val = 0;
        if(curBitPos + numBits >= 8){
            val = trimByte(curByte, curBitPos, 8);
            numBits -= 8 - curBitPos;
            curBitPos = 0;

            while(numBits >= 8){
                val = val << 8;
                numBits -= 8;
                curByte = buffer.get();
                val += curByte;
            }

            curByte = buffer.get();
            if(numBits == 0)
                return val;

            val = val << numBits;
            val += trimByte(curByte, 0, numBits);
            curBitPos = numBits;
        }else{
            val = trimByte(curByte, curBitPos, curBitPos + numBits);
            curBitPos += numBits;
        }

        return val;
    }

}
