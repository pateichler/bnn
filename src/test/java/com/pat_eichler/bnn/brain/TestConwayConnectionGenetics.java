package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestConwayConnectionGenetics {
    private BrainSettings getSettings(){
        BrainSettings settings = new BrainSettings();
        settings.neuronSettings = new BrainSettings.NeuronSettings();
        settings.connectionSettings = new BrainSettings.ConnectionSettings();
        settings.geneticSettings = new BrainSettings.GeneticSettings();

        settings.neuronSettings.NUM_STATES = 6;
        settings.connectionSettings.NT_TYPE_NEURON_CHANGE = new Integer[]{-1,0,1};

        BrainSettings.GeneticSettings.CONN_CHANGE_TABLE = new byte[][]{
                {0, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 1}
        };

        BrainSettings.GeneticSettings.CONN_CHANGE_TABLE = new byte[][]{
                {0, 0, 0, 0},
                {0, 3, 0, 1},
                {0, 0, 2, 0},
                {0, 1, 0, 3}
        };

        return settings;
    }

//    @Test
//    void testGetBitSize(){
//        try(BrainSettings o = getSettings().setContext()) {
//            ConwayConnectionGenetics g = new ConwayConnectionGenetics();
//            Assertions.assertEquals((6 * 4) * 3 + 6 * 4, g.getBitSize());
//        }
//    }
}
