package com.pat_eichler.bnn.brain;

public class BrainClock {
    private int curNeuronStateUpdate;
    private int curNeuronSearchUpdate;

    private final int statePeriod, searchPeriod;
    private final float stateFreq;
    private float searchFreq;
    public BrainClock(int numNeurons, BrainSettings.NeuronSettings settings){
        this(numNeurons, settings.STATE_UPDATE_PERIOD, settings.CONN_SEARCH_PERIOD);
    }

    public BrainClock(int numNeurons, int statePeriod, int searchPeriod){
        this.statePeriod = statePeriod;
        this.searchPeriod = searchPeriod;
        this.stateFreq = (float) statePeriod / (numNeurons + 1);
    }

    public Neuron.PostNeuronMode getMode(int i){
        //TODO: Double check if this works and also if it can be simplified
        int c = (int) (i / stateFreq);
        boolean updateState = c == curNeuronStateUpdate;
        boolean searchConnections = false;
        if(updateState)
            searchConnections = (i - (int)(c * stateFreq) / searchFreq) == curNeuronSearchUpdate;

        return new Neuron.PostNeuronMode(updateState, searchConnections);
    }

    public void increment(){
        curNeuronStateUpdate++;
        if(curNeuronStateUpdate > statePeriod){
            curNeuronStateUpdate = 0;
            curNeuronSearchUpdate = (curNeuronSearchUpdate + 1) % searchPeriod;
        }

        int chunkSize = (int)((curNeuronStateUpdate+1) / stateFreq) - (int)(curNeuronStateUpdate / stateFreq);
        searchFreq = (float) searchPeriod / (chunkSize + 1);
    }
}
