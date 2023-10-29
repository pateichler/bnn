package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBrainClock {

    void testCycle(int numNeurons, int numSearch, int statePeriod, int searchPeriod){
        int[] neuronStateUpdate = new int[numNeurons];
        int[] neuronSearchUpdate = new int[numNeurons];
        BrainClock clock = new BrainClock(numNeurons, statePeriod, searchPeriod);
        for (int i = 0; i < statePeriod * searchPeriod * numSearch; i++) {
            for (int n = 0; n < numNeurons; n++) {
                Neuron.PostNeuronMode mode = clock.getMode(n);
                neuronStateUpdate[n] += mode.updateState ? 1 : 0;
                neuronSearchUpdate[n] += mode.searchConnections ? 1 : 0;
            }
            clock.increment();
        }

        int[] targetState = new int[numNeurons];
        Arrays.fill(targetState, searchPeriod * numSearch);
        int[] targetSearch = new int[numNeurons];
        Arrays.fill(targetSearch, numSearch);

        //System.out.println(Arrays.toString(neuronStateUpdate));
        //System.out.println(Arrays.toString(neuronSearchUpdate));

        assertArrayEquals(targetState, neuronStateUpdate, "State period not correct");
        assertArrayEquals(targetSearch, neuronSearchUpdate, "Search period not correct");
    }

    @Test
    void testSmallCycle(){
        testCycle(6, 2, 3, 2);
    }

    @Test
    void testSmallCycle2(){
        testCycle(6, 2, 4, 1);
    }

    @Test
    void testSmallOddCycle(){
        testCycle(7, 2, 5, 2);
    }

    @Test
    void testLargeOddCycle(){
        testCycle(37, 3, 7, 4);
    }

    @Test
    void testLargeOddCycle2(){
        testCycle(153, 3, 4, 3);
    }
}
