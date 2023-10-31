package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestNeuron {
    @Test
    void testNeuronActivation(){
        try(BrainSettings o = ConfigManager.loadConfigFromResources("testNeuronSettings.json", BrainSettings.class).setContext()) {
            Brain b = new Brain();

            Neuron startNeuron = b.neurons[0];
            startNeuron.setState((byte) 2);
            Neuron endNeuron = b.neurons[1];

            startNeuron.addConnection(new Connection(endNeuron, (byte)2, 2000));

            startNeuron.addNT((short)20, 2, (byte)0, false);

            startNeuron.postStep(new Neuron.PostNeuronMode(false, false));
            Assertions.assertTrue(startNeuron.isActive(), "Start neuron did not activate");

            startNeuron.step();
            endNeuron.postStep(new Neuron.PostNeuronMode(false, false));
            Assertions.assertTrue(endNeuron.isActive(), "End neuron did not activate");
        }
    }
}
