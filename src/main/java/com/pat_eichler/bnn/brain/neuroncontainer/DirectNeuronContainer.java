package com.pat_eichler.bnn.brain.neuroncontainer;

import com.pat_eichler.bnn.brain.Neuron;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class DirectNeuronContainer implements NeuronContainer{
    private int neuronCount;
    private final int numNeurons;
    private final Neuron[] neurons;
    private final Queue<Neuron> extraNeurons;

    public DirectNeuronContainer(int numNeurons){
        this.numNeurons = numNeurons;
        this.neurons = new Neuron[numNeurons];
        this.extraNeurons = new LinkedList<>();
    }

    public void addNeuron(Neuron neuron){
        if(neuronCount < numNeurons) {
            for (int i = 0; i < neurons.length; i++)
                if (neurons[i] == null)
                    neurons[i] = neuron;

            neuronCount++;
        }else
            extraNeurons.add(neuron);
    }
    public void removeNeuron(Neuron neuron){
        for (int i = 0; i < neurons.length; i++) {
            if(neurons[i].equals(neuron)) {
                if(!extraNeurons.isEmpty())
                    neurons[i] = extraNeurons.poll();
                else {
                    neurons[i] = null;
                    neuronCount--;
                }
                return;
            }
        }

        if(!extraNeurons.remove(neuron))
            throw new RuntimeException("Neuron was not in container");
    }

    public void addInput(int n, short count, int ntType, byte neuronState){
        if(n > numNeurons)
            throw new RuntimeException("Neuron greater than max neurons");

        if(neurons[n] != null)
            neurons[n].addNT(count, ntType, neuronState, false);
    }

    public boolean getOutput(int n){
        if(n > numNeurons)
            throw new RuntimeException("Neuron greater than max neurons");

        if(neurons[n] != null)
            return neurons[n].isActive();

        return false;
    }

    public void step(boolean postStep){
        for (Neuron n : neurons)
            stepBrain(n, postStep);
        for (Neuron n : extraNeurons)
            stepBrain(n, postStep);
    }

    @Override
    public Neuron getKillNeuron(Random random) {
        if(!extraNeurons.isEmpty())
            return extraNeurons.peek();

        if(neuronCount > 0)
            for (Neuron n : neurons)
                if(n != null)
                    return  n;

        return null;
    }

    void stepBrain(Neuron n, boolean postStep){
        if(postStep)
            n.postStep();
        else
            n.step();
    }
}
