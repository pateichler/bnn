package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

public class TestConwayNeuronGenetics {

    private final byte[] data = {102, 57, -62, 47, -90, -106, 89, 90, -75, -75, 42, -40, 45, 29, 113, 19, 98, 29, -124, 51, 108, -34, -95, -8, 57, 76, -77, 0, 126, 77, 30};

    private BrainSettings getSettings(){
        BrainSettings settings = new BrainSettings();
        settings.neuronSettings = new BrainSettings.NeuronSettings();
        settings.geneticSettings = new BrainSettings.GeneticSettings();

        settings.neuronSettings.NUM_STATES = 4;

        settings.geneticSettings.PRE_STATE_NN_INNER_LAYER = 3;
        settings.geneticSettings.POST_STATE_NN_INNER_LAYER = 2;
        settings.geneticSettings.NN_WEIGHT_BITS = 4;
        settings.geneticSettings.NN_BIASES_BITS = 8;

        return settings;
    }

//    @Test
//    void testGetBitSize(){
//        try(BrainSettings o = getSettings().setContext()) {
//            ConwayNeuronGenetics g = new ConwayNeuronGenetics();
//            Assertions.assertEquals((4*3*4 + 3*8) + (4*2*4 + 2*8) + (5*4 + 8 + 4)*4, g.getBitSize());
//        }
//    }

    @Test
    void testGetStateChangeBounds(){
        try(BrainSettings o = getSettings().setContext()) {
            ConwayNeuronGenetics g = new ConwayNeuronGenetics();
            DNABuffer buffer = new DNABuffer(new DNA(data, new int[0]));
            g.parseDNA(buffer);


            short[] preCounts = new short[]{10, 0, 3, 20};
            short[] postCounts = new short[]{0, 2, 0, 5};
            for (int i = 0; i < 4; i++) {
                byte b = g.getNeuronStateChange(preCounts, postCounts, (byte) i);
                Assertions.assertTrue(b >= 0 && b < 4);
            }
        }
    }

    byte[] getData(){
        Random r = new Random();
        byte[] b = new byte[31];
        r.nextBytes(b);
        System.out.println(Arrays.toString(b));
        return b;
    }
}
