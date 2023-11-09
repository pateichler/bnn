package com.pat_eichler.bnn.brain;

import com.pat_eichler.bnn.brain.cellcall.GeneticsCellCall;

public class ConwayNeuronGenetics {
    DiscreteNNLayer preGlobalNN;
    DiscreteNNLayer postGlobalNN;
    DiscreteNNLayer[] stateNN;
    GeneticsCellCall cellCall;
    byte[] outputBranchStates;
    int[] outputBranchStatesDelay;
    int[] stateCellCalls;

    public ConwayNeuronGenetics(){
        BrainSettings settings = BrainSettings.getInstance();
        int weightBitSize = settings.geneticSettings.NN_WEIGHT_BITS, biasBitSize = settings.geneticSettings.NN_BIASES_BITS;
        preGlobalNN = new DiscreteNNLayer(settings.neuronSettings.NUM_STATES, settings.geneticSettings.PRE_STATE_NN_INNER_LAYER, DiscreteNNLayer.ActivationFunction.RELU, weightBitSize, biasBitSize);
        postGlobalNN = new DiscreteNNLayer(settings.neuronSettings.NUM_STATES, settings.geneticSettings.POST_STATE_NN_INNER_LAYER, DiscreteNNLayer.ActivationFunction.RELU, weightBitSize, biasBitSize);

        outputBranchStates = new byte[settings.neuronSettings.NUM_STATES * 2];
        outputBranchStatesDelay = new int[settings.neuronSettings.NUM_STATES * 2];
        stateNN = new DiscreteNNLayer[settings.neuronSettings.NUM_STATES];
        int middleLayer = settings.geneticSettings.getMiddleLayerSize();
        for (int i = 0; i < stateNN.length; i++)
            stateNN[i] = new DiscreteNNLayer(middleLayer, 1, DiscreteNNLayer.ActivationFunction.NONE, weightBitSize, biasBitSize);

        stateCellCalls = new int[settings.neuronSettings.NUM_STATES];
        cellCall = new GeneticsCellCall();
    }

    public ConwayNeuronGenetics(DiscreteNNLayer preGlobalNN, DiscreteNNLayer postGlobalNN, DiscreteNNLayer[] stateNN, byte[] outputBranchStates, int[] outputBranchStatesDelay) {
        this.preGlobalNN = preGlobalNN;
        this.postGlobalNN = postGlobalNN;
        this.stateNN = stateNN;
        this.outputBranchStates = outputBranchStates;
        this.outputBranchStatesDelay = outputBranchStatesDelay;
    }

    public void parseDNA(DNABuffer buffer){
        preGlobalNN.init(buffer);
        postGlobalNN.init(buffer);
        int stateBitSize = getStateBitSize();
        int delayBitSize = BrainSettings.getInstance().geneticSettings.STATE_DELAY_BITS;
        for (int i = 0; i < stateNN.length; i++) {
            stateNN[i].init(buffer);
            buffer.startSegment();
            outputBranchStates[i*2] = (byte)buffer.getBits(stateBitSize);
            outputBranchStatesDelay[i*2] = buffer.getGrayCodeBits(delayBitSize);
            buffer.startSegment();
            outputBranchStates[i*2+1] = (byte)buffer.getBits(stateBitSize);
            outputBranchStatesDelay[i*2+1] = buffer.getGrayCodeBits(delayBitSize);

            stateCellCalls[i] = buffer.getBits(cellCall.getCallSize());
        }
    }

    int[] getMiddleLayer(short[] preNeuronStateCounts, short[] postNeuronStateCounts){
        //TODO: Consider changing data to ints to avoid this copy
        int[] preInput = new int[preNeuronStateCounts.length];
        for (int i = 0; i < preInput.length; i++)
            preInput[i] = preNeuronStateCounts[i];

        int[] postInput = new int[postNeuronStateCounts.length];
        for (int i = 0; i < preInput.length; i++)
            postInput[i] = postNeuronStateCounts[i];

        int[] preOutput = preGlobalNN.calculateOutputs(preInput);
        int[] postOutput = postGlobalNN.calculateOutputs(postInput);

        return Common.combineArray(preOutput, postOutput);
    }

    boolean isDelayFinal(int delay, byte curState){
        return delay <= Math.min(outputBranchStates[curState*2], outputBranchStates[curState*2+1]);
    }

    Neuron.NeuronStateChange getNNStateChange(short[] preNeuronStateCounts, short[] postNeuronStateCounts, byte curState, Neuron.NeuronStateChange curChange){
        int val = stateNN[curState].calculateOutputs(getMiddleLayer(preNeuronStateCounts, postNeuronStateCounts))[0];
        int branch = val > 0 ? curState * 2 : curState * 2 + 1;
        byte state = outputBranchStates[branch];

        //TODO: Probably want to handle this better
        if(state >= BrainSettings.getInstance().neuronSettings.NUM_STATES)
            state = curState;

        int delay = outputBranchStatesDelay[branch];
        if(curChange != null && curChange.stateDelay <= delay){
            curChange.nextStateFinal = isDelayFinal(curChange.stateDelay, curState);
            return curChange;
        }

        return new Neuron.NeuronStateChange(state, outputBranchStatesDelay[branch], isDelayFinal(delay, curState));
    }

    public Neuron.NeuronStateChange getNeuronStateChange(short[] preNeuronStateCounts, short[] postNeuronStateCounts, Neuron neuron, Neuron.NeuronStateChange curChange) {
        // Next state is final ... return state change unchanged
        if(curChange != null && curChange.nextStateFinal)
            return curChange;

        byte curState = neuron.getState();

        // Only call cell calls if just activated state
        if(curChange == null) {
            int c = cellCall.cellCall(neuron, stateCellCalls[curState], outputBranchStates[curState * 2], outputBranchStates[curState * 2 + 1]);
            if (c >= 0)
                return new Neuron.NeuronStateChange(outputBranchStates[curState * 2 + c], outputBranchStatesDelay[curState * 2 + c], true);
        }

        // Cell call did not override next state selection ... use NN to decide next state
        return getNNStateChange(preNeuronStateCounts, postNeuronStateCounts, curState, curChange);
    }

    private int getStateBitSize(){
        return Common.numBitsToEncode(BrainSettings.getInstance().neuronSettings.NUM_STATES);
    }
}
