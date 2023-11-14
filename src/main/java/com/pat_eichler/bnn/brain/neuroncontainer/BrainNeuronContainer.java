package com.pat_eichler.bnn.brain.neuroncontainer;

import com.pat_eichler.bnn.brain.BrainSettings;
import com.pat_eichler.bnn.brain.Neuron;

import java.util.Random;

public class BrainNeuronContainer implements NeuronContainer{

    public final NeuronContainer[] neuronContainers;
    private int neuronCount = 0;

    public BrainNeuronContainer(){
        neuronContainers = new NeuronContainer[BrainSettings.getInstance().neuronSettings.NUM_NEURON_TYPES];
    }

    public BrainNeuronContainer(NeuronContainer[] neuronContainers){
        this.neuronContainers = neuronContainers;
    }

    @Override
    public void addNeuron(Neuron neuron) {
        neuronContainers[neuron.getType()].addNeuron(neuron);
        neuronCount++;
    }

    @Override
    public void removeNeuron(Neuron neuron) {
        neuronContainers[neuron.getType()].removeNeuron(neuron);
        neuronCount--;
    }

    @Override
    public void step(boolean postStep) {
        for (NeuronContainer container : neuronContainers)
            container.step(false);

        for (NeuronContainer container : neuronContainers)
            container.step(true);
    }

    // This method could potentially be improved to try to keep important neurons alive
    @Override
    public Neuron getKillNeuron(Random random) {
        for (int i = neuronContainers.length - 1; i >= 0; i--) {
            Neuron n = neuronContainers[i].getKillNeuron(random);
            if (n != null)
                return n;
        }

        return null;
    }

    public int getNeuronCount(){
        return neuronCount;
    }
}
