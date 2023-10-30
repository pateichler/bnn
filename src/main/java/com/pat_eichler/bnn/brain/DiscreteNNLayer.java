package com.pat_eichler.bnn.brain;

import java.nio.ByteBuffer;

public class DiscreteNNLayer {

    private final int numInputs, numOutputs, weightBitSize, biasBitSize;
    private final int[] weights, biases;
    private final ActivationFunction activationFunction;
    public enum ActivationFunction{RELU, NONE}

    public DiscreteNNLayer (int numInputs, int numOutputs, ActivationFunction func, int weightBitSize, int biasBitSize){
        this(numInputs, numOutputs, func, weightBitSize, biasBitSize, new int[numInputs * numOutputs], new int[numOutputs]);
    }

    public DiscreteNNLayer (int numInputs, int numOutputs, ActivationFunction func, int weightBitSize, int biasBitSize, int[] weights, int[] biases){
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        this.weightBitSize = weightBitSize;
        this.biasBitSize = biasBitSize;
        this.activationFunction = func;

        this.weights = weights;
        this.biases = biases;
    }

    public void init(DNABuffer buffer){
        for (int i = 0; i < weights.length; i++)
            weights[i] = buffer.getGrayCodeBits(weightBitSize);

        for (int i = 0; i < biases.length; i++)
            biases[i] = buffer.getGrayCodeBits(biasBitSize);
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
        return weights.length * weightBitSize + biases.length * biasBitSize;
    }
}
