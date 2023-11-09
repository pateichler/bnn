package com.pat_eichler.bnn.brain;

import java.util.Random;

public class NeuronClock {
    private int curStateCycle;
    private int curSearchCycle;

    public NeuronClock(){
        curStateCycle = 0;
        curSearchCycle = 0;
    }
    public NeuronClock(Random rand){
        curStateCycle = rand.nextInt(BrainSettings.getInstance().neuronSettings.STATE_UPDATE_PERIOD);
        curSearchCycle = rand.nextInt(BrainSettings.getInstance().neuronSettings.CONN_SEARCH_PERIOD);
    }

    public void increment(){
        curStateCycle = (curStateCycle + 1) % BrainSettings.getInstance().neuronSettings.STATE_UPDATE_PERIOD;
        if(curStateCycle == 0)
            curSearchCycle = (curSearchCycle + 1) % BrainSettings.getInstance().neuronSettings.CONN_SEARCH_PERIOD;
    }

    public boolean isStateCycle(){
        return curStateCycle == 0;
    }

    public boolean isSearchCycle(){
        return curSearchCycle == 0;
    }
}
