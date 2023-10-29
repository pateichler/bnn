package com.pat_eichler.bnn.brain;

import java.nio.ByteBuffer;

public class DNABuffer {
    //Note: This class could probably be optimized
    private final ByteBuffer buffer;
    byte curByte;
    int curBitPos;
    boolean lastByte;

    final int[] leftBitMask = new int[] {0,1,3,7,15,31,63,127,255};

    public DNABuffer(DNA dna){
        buffer = ByteBuffer.wrap(dna.data);
        curByte = buffer.get();
    }

    private byte trimByte(byte b, int startBit, int endBit){
        int r = b & 0xFF;
        r = r >> (8 - endBit);
        if(startBit == 0)
            return (byte)r;
        return (byte)(r & leftBitMask[endBit - startBit]);
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

    private void retrieveNextByte(){
        if(buffer.hasRemaining())
            curByte = buffer.get();
        else
            lastByte = true;
    }

    public int getBits(int numBits){
        if(numBits > 32 || numBits <= 0)
            throw new RuntimeException("Invalid number of read bits: " + numBits);

        int val = 0;
        if(curBitPos + numBits >= 8){
            if(lastByte)
                throw new RuntimeException("DNA is out of data!");

            val |= trimByte(curByte, curBitPos, 8);
            numBits -= 8 - curBitPos;
            curBitPos = 0;

            while(numBits >= 8){
                if(lastByte)
                    throw new RuntimeException("DNA is out of data!");

                val = val << 8;
                numBits -= 8;
                retrieveNextByte();
                val |= curByte;
            }

            retrieveNextByte();
            if(numBits == 0)
                return val;

            val = val << numBits;
            val |= trimByte(curByte, 0, numBits);
            curBitPos = numBits;
        }else{
            val |= trimByte(curByte, curBitPos, curBitPos + numBits);
            curBitPos += numBits;
        }

        return val;
    }

}
