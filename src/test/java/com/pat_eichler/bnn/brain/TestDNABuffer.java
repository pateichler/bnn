package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDNABuffer {
    @Test
    void testDNA(){
        byte[] data = new byte[] {0b01010111, 0b01011111, 0b01111011, 0b01010000};
        int[] dataSize = new int[]{7, 7, 3, 7, 4};
        int[] expectedData = new int[]{43, 87, 6, 123, 5};

        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        for (int i = 0; i < dataSize.length; i++) {
            assertEquals(expectedData[i], buffer.getBits(dataSize[i]), "DNA not correctly decoded");
        }
    }

    @Test
    void testDNALarge(){
        //AF3B49A1D87162C906DF939C
        //101011110 011101101001001 101 00001 110110000111 000 1011 00 01 01 1001 00100 0001101101111 110010 011 1001 1100
        byte[] data = new byte[] {(byte)0xAF, (byte)0x3B, (byte)0x49, (byte)0xA1, (byte)0xD8, (byte)0x71,
                (byte)0x62, (byte)0xC9, (byte)0x06, (byte)0xDF, (byte)0x93, (byte)0x9C};
        int[] dataSize = new int[]{9, 15, 3, 5, 12, 3, 4, 2, 2, 2, 4, 5, 13, 6, 3, 4, 4};
        int[] expectedData = new int[]{350, 15177, 5, 1, 3463, 0, 11, 0, 1, 1, 9, 4, 879, 50, 3, 9, 12};

        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        int[] actualData= new int[expectedData.length];
        for (int i = 0; i < dataSize.length; i++)
            actualData[i] = buffer.getBits(dataSize[i]);

        Assertions.assertArrayEquals(expectedData, actualData, "DNA not correctly decoded");
    }
    @Test
    void testDNALarge2(){
        //27323FD8E8533EBB45773294F2CB3640
        //00100111001100100011111111011000 111010000 101001 1001111 1010111011010 0010101 11011100110010100101001111001 0110010 1100 110 11001 000000
        byte[] data = new byte[] {(byte)0x27, (byte)0x32, (byte)0x3F, (byte)0xD8, (byte)0xE8, (byte)0x53, (byte)0x3E,
                (byte)0xBB, (byte)0x45, (byte)0x77, (byte)0x32, (byte)0x94, (byte)0xF2, (byte)0xCB, (byte)0x36, (byte)0x40};
        int[] dataSize = new int[]{32, 9, 6, 7, 13, 7, 29, 7, 4, 3, 5, 6};
        int[] expectedData = new int[]{657604568, 464, 41, 79, 5594, 21, 463030905, 50, 12, 6, 25, 0};
        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        int[] actualData= new int[expectedData.length];
        for (int i = 0; i < dataSize.length; i++)
            actualData[i] = buffer.getBits(dataSize[i]);

        Assertions.assertArrayEquals(expectedData, actualData, "DNA not correctly decoded");
    }

    @Test
    void testDNAInts(){
        int[] expectedData = { 21425, -422142, 342214124, 42, 423, 51, -26346, 523, 42393 };

        ByteBuffer byteBuffer = ByteBuffer.allocate(expectedData.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(expectedData);

        byte[] data = byteBuffer.array();
        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        int[] actualData = new int[expectedData.length];
        for (int i = 0; i < actualData.length; i++)
            actualData[i] = buffer.getBits(32);

        Assertions.assertArrayEquals(expectedData, actualData, "DNA not correctly decoded");
    }

    @Test
    void testDNAGrayCode(){
        byte[] data = new byte[] {0b01010111, 0b01011111, 0b01111011, 0b01010000};
        int[] dataSize = new int[]{7, 7, 3, 7, 4};
        int[] expectedData = new int[]{50-64+1, 101-64, 0, 82-64, 6-8+1};
        byte b = 0b00101011;
        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        for (int i = 0; i < dataSize.length; i++) {
            assertEquals(expectedData[i], buffer.getGrayCodeBits(dataSize[i]), "DNA not correctly decoded");
        }
    }

    @Test
    void testDNAGrayCodeBounds(){
        byte[] data = new byte[] {2,32, -127, 52, 1, -92, 20, 23, 84, -2};
        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        for (int i = 0; i < data.length * 4; i++) {
            int val = buffer.getGrayCodeBits(2);
            Assertions.assertTrue(val >= -1 && val <= 1, "Gray code value is outside bounds of 2 bit value: " + val);
        }
    }

    @Test
    void testDNABytes(){
        byte[] data = new byte[] {2,32, -127, 52, 1, -92, 127, 23, -128, -2};
        DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
        for (int i = 0; i < data.length; i++) {
            // Throw away fourth iteration to change up
            if(i == 4){
                buffer.getBits(3);
                buffer.getBits(4);
                buffer.getBits(1);
            }else
                assertEquals(data[i], (byte)buffer.getBits(8), "DNA retrieved incorrect byte value");
        }
    }
}
