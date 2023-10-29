package com.pat_eichler.bnn.brain;

import java.nio.ByteBuffer;

public class DiscreteNNLayer {

    private final int numInputs, numOutputs;
    private final int[] weights, biases;
    private final ActivationFunction activationFunction;
    public enum ActivationFunction{RELU, NONE}

    public DiscreteNNLayer (int numInputs, int numOutputs, ActivationFunction func){
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;

        weights = new int[numInputs * numOutputs];
        biases = new int[numOutputs];

        this.activationFunction = func;
    }

    public void init(DNABuffer buffer){
        BrainSettings.GeneticSettings settings = BrainSettings.getInstance().geneticSettings;
        for (int i = 0; i < weights.length; i++)
            weights[i] = buffer.getGrayCodeBits(settings.NN_WEIGHT_BITS);

        for (int i = 0; i < biases.length; i++)
            biases[i] = buffer.getGrayCodeBits(settings.NN_BIASES_BITS);
    }

    public int[] calculateOutputs(int[] inputs){
        int[] output = new int[numOutputs];
        for (int nodeOut = 0; nodeOut < numOutputs; nodeOut++) {
            int val = biases[nodeOut];

            for (int nodeIn = 0; nodeIn < numInputs; nodeIn++)
                val += inputs[nodeIn] * getWeight(nodeIn, nodeOut);

            output[nodeOut] = applyActivationFunction(val);
        }

        return output;
    }

    int applyActivationFunction(int val){
        return switch (activationFunction) {
            case RELU -> Math.max(val, 0);
            case NONE -> val;
        };
    }

    int getWeight(int nodeIn, int nodeOut){
        return weights[nodeOut * numInputs + nodeIn];
    }

    public int getBitSize(){
        BrainSettings.GeneticSettings settings = BrainSettings.getInstance().geneticSettings;
        return weights.length * settings.NN_WEIGHT_BITS + biases.length * settings.NN_BIASES_BITS;
    }
}
