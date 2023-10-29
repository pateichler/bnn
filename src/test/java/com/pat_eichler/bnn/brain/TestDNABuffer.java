package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDNABuffer {
    @Test
    void testDNA(){
        byte[] data = new byte[] {0b01010111, 0b01011111, 0b01111011, 0b01010000};
        int[] dataSize = new int[]{7, 7, 3, 7, 4};
        int[] expectedData = new int[]{43, 87, 6, 123, 5};
        byte b = 0b00101011;
        DNABuffer buffer = new DNABuffer(new DNA(data));
        for (int i = 0; i < dataSize.length; i++) {
            assertEquals(expectedData[i], buffer.getBits(dataSize[i]), "DNA not correctly decoded");
        }
    }

    @Test
    void testDNAGrayCode(){
        byte[] data = new byte[] {0b01010111, 0b01011111, 0b01111011, 0b01010000};
        int[] dataSize = new int[]{7, 7, 3, 7, 4};
        int[] expectedData = new int[]{50-64+1, 101-64, 0, 82-64, 6-8+1};
        byte b = 0b00101011;
        DNABuffer buffer = new DNABuffer(new DNA(data));
        for (int i = 0; i < dataSize.length; i++) {
            assertEquals(expectedData[i], buffer.getGrayCodeBits(dataSize[i]), "DNA not correctly decoded");
        }
    }

    @Test
    void testDNAGrayCodeBounds(){
        byte[] data = new byte[] {2,32, -127, 52, 1, -92, 20, 23, 84, -2};
        DNABuffer buffer = new DNABuffer(new DNA(data));
        for (int i = 0; i < data.length * 4; i++) {
            int val = buffer.getGrayCodeBits(2);
            Assertions.assertTrue(val >= -1 && val <= 1, "Gray code value is outside bounds of 2 bit value: " + val);
        }
    }

    @Test
    void testDNABytes(){
        byte[] data = new byte[] {2,32, -127, 52, 1, -92, 20, 23, 84, -2};
        DNABuffer buffer = new DNABuffer(new DNA(data));
        for (int i = 0; i < data.length; i++) {
            // Throw away fourth iteration to change up
            if(i == 4){
                buffer.getBits(3);
                buffer.getBits(4);
                buffer.getBits(1);
            }else
                assertEquals(data[i], buffer.getBits(8), "DNA retrieved incorrect byte value");
        }
    }
}
