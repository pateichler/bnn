package com.pat_eichler.bnn.brain.neuroncontainer;

import com.pat_eichler.bnn.brain.Neuron;

import java.util.ArrayList;
import java.util.Random;

public class ListNeuronContainer implements NeuronContainer{

    private final ArrayList<Neuron> neurons;
    public ListNeuronContainer(){
        neurons = new ArrayList<>();
    }
    @Override
    public void addNeuron(Neuron neuron) {
        neurons.add(neuron);
    }

    @Override
    public void removeNeuron(Neuron neuron) {
        if(neurons.remove(neuron))
            throw new RuntimeException("Neuron was not in container");
    }

    @Override
    public void step(boolean postStep) {
        for (Neuron n : neurons) {
            if(postStep)
                n.postStep();
            else
                n.step();
        }
    }

    @Override
    public Neuron getKillNeuron(Random random) {
        if(neurons.isEmpty())
            return null;

        return neurons.get(random.nextInt(neurons.size()));
    }
}
