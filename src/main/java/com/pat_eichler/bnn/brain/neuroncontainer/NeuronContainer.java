package com.pat_eichler.bnn.brain.neuroncontainer;

import com.pat_eichler.bnn.brain.Neuron;

import java.util.Random;

public interface NeuronContainer {

    void addNeuron(Neuron neuron);
    void removeNeuron(Neuron neuron);
    void step(boolean postStep);
    Neuron getKillNeuron(Random random);
}
