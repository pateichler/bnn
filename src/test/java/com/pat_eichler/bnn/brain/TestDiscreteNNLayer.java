package com.pat_eichler.bnn.brain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDiscreteNNLayer {
    @Test
    void testGetBitSize(){
        DiscreteNNLayer layer = new DiscreteNNLayer(10, 4, DiscreteNNLayer.ActivationFunction.RELU, 4, 8);
        Assertions.assertEquals(10*4*4 + 4*8, layer.getBitSize());
    }

    @Test
    void testCalculateOutputs(){
        int[] weights = new int[] {
                3, 2, 0,
                -2, 3, -1
        };
        int[] biases = new int[] {
                1, -1
        };
        DiscreteNNLayer layer = new DiscreteNNLayer(3, 2, DiscreteNNLayer.ActivationFunction.RELU, 4, 8, weights, biases);

        int[] output = layer.calculateOutputs(new int[]{3,1,0});
        Assertions.assertArrayEquals(new int[]{12,0}, output);
    }

    @Test
    void testCalculateOutputs2(){
        int[] weights = new int[] {
                3, 2, 0,
                -2, 3, -1
        };
        int[] biases = new int[] {
                1, -1
        };
        DiscreteNNLayer layer = new DiscreteNNLayer(3, 2, DiscreteNNLayer.ActivationFunction.NONE, 4, 8, weights, biases);

        int[] output = layer.calculateOutputs(new int[]{3,1,0});
        Assertions.assertArrayEquals(new int[]{12,-4}, output);
    }
}
