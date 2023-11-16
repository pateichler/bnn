package com.pat_eichler.bnn.brain.neuroncontainer;

import com.pat_eichler.bnn.brain.BrainSettings;

public class BrainNeuronContainerBuilder {
    private final NeuronContainer[] containers;

    public BrainNeuronContainerBuilder(){
        containers = new NeuronContainer[BrainSettings.getInstance().neuronSettings.NUM_NEURON_TYPES];
    }

    public BrainNeuronContainerBuilder addContainer(byte neuronType, NeuronContainer container){
        containers[neuronType] = container;
        return this;
    }

    public BrainNeuronContainer createContainer(){
        for (int i = 0; i < containers.length; i++)
            if(containers[i] == null)
                containers[i] = new ListNeuronContainer();

        return new BrainNeuronContainer(containers);
    }
}
