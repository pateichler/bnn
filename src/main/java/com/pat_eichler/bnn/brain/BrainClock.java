package com.pat_eichler.bnn.brain;

import javax.swing.plaf.synth.SynthTextAreaUI;

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
        this.stateFreq = (float) statePeriod / (numNeurons);
        updateSearchFreq();
    }

    public Neuron.PostNeuronMode getMode(int i){
        int c = (int) (i * stateFreq);
        boolean updateState = c == curNeuronStateUpdate;
        boolean searchConnections = false;
        if(updateState){
            int j = i - (int)(Math.ceil(c / stateFreq));
            searchConnections = (int) (j * searchFreq) == curNeuronSearchUpdate;
        }


        return new Neuron.PostNeuronMode(updateState, searchConnections);
    }

    public void increment(){
        curNeuronStateUpdate++;
        if(curNeuronStateUpdate >= statePeriod){
            curNeuronStateUpdate = 0;
            curNeuronSearchUpdate = (curNeuronSearchUpdate + 1) % searchPeriod;
        }

        updateSearchFreq();
    }

    void updateSearchFreq(){
        int chunkSize = (int) (Math.ceil((curNeuronStateUpdate+1) / stateFreq) - Math.ceil(curNeuronStateUpdate / stateFreq));
        searchFreq = (float) searchPeriod / (chunkSize);
    }
}
