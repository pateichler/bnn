package com.pat_eichler.bnn.brain.cellcall;

import com.pat_eichler.bnn.brain.Neuron;

public interface CellCall {
    int call(Neuron n, int parameters, byte branchState1, byte branchState2);

    int getParameterSize();
}
