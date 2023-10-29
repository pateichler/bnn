package com.pat_eichler.bnn.brain;

public class BrainClock {
    private int curNeuronStateUpdate;
    private int curNeuronSearchUpdate;

    private final int statePeriod, searchPeriod, numNeurons;
    private int stateChunkSize, stateChunkStart;
    public BrainClock(int numNeurons, BrainSettings.NeuronSettings settings){
        this(numNeurons, settings.STATE_UPDATE_PERIOD, settings.CONN_SEARCH_PERIOD);
    }

    public BrainClock(int numNeurons, int statePeriod, int searchPeriod){
        this.statePeriod = statePeriod;
        this.searchPeriod = searchPeriod;
        this.numNeurons = numNeurons;
        updateStateChunk();
    }

    public Neuron.PostNeuronMode getMode(int i){
        boolean updateState =  Math.floorDiv(i * statePeriod, numNeurons) == curNeuronStateUpdate;
        boolean searchConnections = false;

        if(updateState){
            int j = i - stateChunkStart;
            searchConnections = Math.floorDiv (j * searchPeriod, stateChunkSize) == curNeuronSearchUpdate;
        }

        return new Neuron.PostNeuronMode(updateState, searchConnections);
    }

    public void increment(){
        curNeuronStateUpdate++;
        if(curNeuronStateUpdate >= statePeriod){
            curNeuronStateUpdate = 0;
            curNeuronSearchUpdate = (curNeuronSearchUpdate + 1) % searchPeriod;
        }

        updateStateChunk();
    }

    void updateStateChunk(){
        // There might be a better way to calculate state chunk size
        stateChunkSize = Math.ceilDiv((curNeuronStateUpdate+1) * numNeurons, statePeriod) - Math.ceilDiv(curNeuronStateUpdate * numNeurons, statePeriod);
        stateChunkStart = Math.ceilDiv(curNeuronStateUpdate * numNeurons, statePeriod);
    }
}
