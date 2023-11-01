package com.pat_eichler.bnn.brain;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class DNABuffer {
    //Note: This class could probably be optimized
    private final ByteBuffer buffer;
    ArrayList<Byte> newData;
    ArrayList<Integer> segments;
    Random random;
    byte curByte;
    int curBitPos;
    boolean lastByte;

    final int[] leftBitMask = new int[] {0,1,3,7,15,31,63,127,255};

    public DNABuffer(Random random){
        buffer = null;
        newData = new ArrayList<>();
        newData.add((byte) (random.nextInt(256) - 128));
        segments = new ArrayList<>();
        segments.add(0);
        this.random = random;
    }

    public DNABuffer(DNA dna){
        buffer = ByteBuffer.wrap(dna.data);
        curByte = buffer.get();
    }

    private int trimByte(byte b, int startBit, int endBit){
        // Convert byte to unsigned int
        int r = b & 0xFF;
        r = r >> (8 - endBit);
        if(startBit == 0)
            return r;
        return r & leftBitMask[endBit - startBit];
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
        if(buffer == null){
            curByte = (byte) (random.nextInt(256) - 128);
            newData.add(curByte);
        }
        else {
            if (buffer.hasRemaining())
                curByte = buffer.get();
            else
                lastByte = true;
        }
    }

    public void startSegment(){
        if(buffer == null){
            int s = curByte * 8 + curBitPos;
            if(segments.getLast() != s)
                segments.add(s);
        }
    }

    public int getBits(int numBits){
        if(numBits > 32 || numBits <= 0)
            throw new RuntimeException("Invalid number of read bits: " + numBits);

        int val = 0;
        if(curBitPos + numBits >= 8){
            if(lastByte)
                throw new RuntimeException("DNA is out of data!");

            val += trimByte(curByte, curBitPos, 8);
            numBits -= 8 - curBitPos;
            curBitPos = 0;

            while(numBits >= 8){
                if(lastByte)
                    throw new RuntimeException("DNA is out of data!");

                val = val << 8;
                numBits -= 8;
                retrieveNextByte();
                // Add whole unsigned byte to return value
                val += curByte & 0xFF;
            }

            retrieveNextByte();
            if(numBits == 0)
                return val;

            val = val << numBits;
            val += trimByte(curByte, 0, numBits);
            curBitPos = numBits;
        }else{
            val |= trimByte(curByte, curBitPos, curBitPos + numBits);
            curBitPos += numBits;
        }

        return val;
    }

    public DNA getNewDNA(){
        if(buffer != null)
            throw new RuntimeException("New DNA not created");

        byte[] data = new byte[newData.size()];
        for (int i = 0; i < newData.size(); i++)
            data[i] = (byte)newData.get(i);

        int[] segs = new int[segments.size()];
        for (int i = 0; i < segments.size(); i++)
            segs[i] = (int)segments.get(i);

        return new DNA(data, segs);
    }

}
