package com.pat_eichler.bnn.brain;

public class ConwayNeuronGenetics {
    DiscreteNNLayer preGlobalNN;
    DiscreteNNLayer postGlobalNN;
    DiscreteNNLayer[] stateNN;
    byte[] outputBranchStates;

    public ConwayNeuronGenetics(){
        BrainSettings settings = BrainSettings.getInstance();
        preGlobalNN = new DiscreteNNLayer(settings.neuronSettings.NUM_STATES, settings.geneticSettings.PRE_STATE_NN_INNER_LAYER, DiscreteNNLayer.ActivationFunction.RELU);
        postGlobalNN = new DiscreteNNLayer(settings.neuronSettings.NUM_STATES, settings.geneticSettings.POST_STATE_NN_INNER_LAYER, DiscreteNNLayer.ActivationFunction.RELU);

        outputBranchStates = new byte[settings.neuronSettings.NUM_STATES * 2];
        stateNN = new DiscreteNNLayer[settings.neuronSettings.NUM_STATES];
        int middleLayer = settings.geneticSettings.getMiddleLayerSize();
        for (int i = 0; i < stateNN.length; i++)
            stateNN[i] = new DiscreteNNLayer(middleLayer, 1, DiscreteNNLayer.ActivationFunction.NONE);
    }

    public void parseDNA(DNABuffer buffer){
        preGlobalNN.init(buffer);
        postGlobalNN.init(buffer);
        int stateBitSize = getStateBitSize();
        for (int i = 0; i < stateNN.length; i++) {
            stateNN[i].init(buffer);
            outputBranchStates[i*2] = (byte)buffer.getBits(stateBitSize);
            outputBranchStates[i*2+1] = (byte)buffer.getBits(stateBitSize);
        }
    }

    public int getBitSize(){
        int numBits = 0;
        numBits += preGlobalNN.getBitSize();
        numBits += postGlobalNN.getBitSize();
        int stateBitSize = getStateBitSize();

        for (DiscreteNNLayer layer : stateNN){
            numBits += layer.getBitSize();
            // For each output state branch
            numBits += outputBranchStates.length * stateBitSize;
        }

        return numBits;
    }
    public byte getNeuronStateChange(short[] preNeuronStateCounts, short[] postNeuronStateCounts, byte curState) {
        //TODO: Consider changing data to ints to avoid this copy
        int[] preInput = new int[preNeuronStateCounts.length];
        for (int i = 0; i < preInput.length; i++)
            preInput[i] = preNeuronStateCounts[i];

        int[] postInput = new int[postNeuronStateCounts.length];
        for (int i = 0; i < preInput.length; i++)
            postInput[i] = postNeuronStateCounts[i];

        int[] middleInput = new int[BrainSettings.getInstance().geneticSettings.getMiddleLayerSize()];

        int[] preOutput = preGlobalNN.calculateOutputs(preInput);
        int[] postOutput = postGlobalNN.calculateOutputs(postInput);
        for (int i = 0; i < middleInput.length; i++) {
            if(i < preOutput.length)
                middleInput[i] = preOutput[i];
            else
                middleInput[i] = postOutput[i - preOutput.length];
        }

        int val = stateNN[curState].calculateOutputs(middleInput)[0];
        byte state = val > 0 ? outputBranchStates[curState * 2] : outputBranchStates[curState * 2 + 1];
        //TODO: Probably want to handle this better
        if(state > BrainSettings.getInstance().neuronSettings.NUM_STATES)
            state = curState;

        return state;
    }

    private int getStateBitSize(){
        //TODO: Check if this is right
        return Integer.highestOneBit(BrainSettings.getInstance().neuronSettings.NUM_STATES) + 1;
    }
}
