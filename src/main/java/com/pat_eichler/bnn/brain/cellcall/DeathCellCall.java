package com.pat_eichler.bnn.brain.cellcall;

import com.pat_eichler.bnn.brain.BrainSettings;
import com.pat_eichler.bnn.brain.Neuron;

public class DeathCellCall implements CellCall{

    private final int countBits = 3;
    private final int countMask;

    public DeathCellCall(){
        countMask = (1 << countBits) - 1;
    }

    @Override
    public int call(Neuron n, int parameters, byte branchState1, byte branchState2) {
        int deadCount = ((parameters & countMask) + 2) * BrainSettings.getInstance().neuronSettings.STATE_UPDATE_PERIOD;

        n.setDying(deadCount);

        return 0;
    }

    @Override
    public int getParameterSize() {
        return countBits;
    }
}
