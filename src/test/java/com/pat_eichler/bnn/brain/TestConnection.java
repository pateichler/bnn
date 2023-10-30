package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestConnection {
    @Test
    void testTrigger(){
        try(BrainSettings o = ConfigManager.loadConfigFromResources("testConnectionSettings.json", BrainSettings.class).setContext()){
            Brain b = new Brain();

            Neuron startNeuron = b.neurons[0];
            startNeuron.setState((byte)2);
            Neuron endNeuron = b.neurons[1];

            Connection c = new Connection(endNeuron, (byte)2);
            c.setStrength(24);

            c.trigger(startNeuron);

            Assertions.assertEquals(5, endNeuron.getActivationCount(), "Neuron activation count doesn't match expected");

            int[] expectedPre = new int[]{0,0,5,0,0,0};
            int[] actualPre = new int[6];
            for (int i = 0; i < actualPre.length; i++)
                actualPre[i] = endNeuron.getPreNeuronStateCount(i);

            Assertions.assertArrayEquals(actualPre, expectedPre, "Pre neuron state counts don't match expected");
        }
    }

    @Test
    void testMultiTrigger(){
        try(BrainSettings o = ConfigManager.loadConfigFromResources("testConnectionSettings.json", BrainSettings.class).setContext()){
            Brain b = new Brain();

            Neuron startNeuron = b.neurons[0];
            startNeuron.setState((byte)2);
            Neuron endNeuron = b.neurons[1];

            Connection c = new Connection(endNeuron, (byte)2);

            int[] strengths = new int[]{24, 16, 14, 35};
            for (int strength : strengths) {
                c.setStrength(strength);
                c.trigger(startNeuron);
            }

            Assertions.assertEquals(20, endNeuron.getActivationCount(), "Neuron activation count doesn't match expected");

            int[] expectedPre = new int[]{0,0,20,0,0,0};
            int[] actualPre = new int[6];
            for (int i = 0; i < actualPre.length; i++)
                actualPre[i] = endNeuron.getPreNeuronStateCount(i);

            Assertions.assertArrayEquals(actualPre, expectedPre, "Pre neuron state counts don't match expected");
        }
    }

    @Test
    void testMultiConnectionTrigger(){
        try(BrainSettings o = ConfigManager.loadConfigFromResources("testConnectionSettings.json", BrainSettings.class).setContext()){
            Brain b = new Brain();

            b.neurons[1].setState((byte)1);
            b.neurons[2].setState((byte)5);
            b.neurons[3].setState((byte)5);
            Neuron endNeuron = b.neurons[0];

            Connection c1 = new Connection(endNeuron, (byte)2);
            c1.setStrength(24);
            Connection c2 = new Connection(endNeuron, (byte)1);
            c2.setStrength(3);
            Connection c3 = new Connection(endNeuron, (byte)0);
            c3.setStrength(120);

            c1.trigger(b.neurons[1]);
            c2.trigger(b.neurons[2]);
            c3.trigger(b.neurons[3]);

            Assertions.assertEquals(-2, endNeuron.getActivationCount(), "Neuron activation count doesn't match expected");

            int[] expectedPre = new int[]{0,5,0,0,0,9};
            int[] actualPre = new int[6];
            for (int i = 0; i < actualPre.length; i++)
                actualPre[i] = endNeuron.getPreNeuronStateCount(i);

            Assertions.assertArrayEquals(actualPre, expectedPre, "Pre neuron state counts don't match expected");
        }
    }
}
