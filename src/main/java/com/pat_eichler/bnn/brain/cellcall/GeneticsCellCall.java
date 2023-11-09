package com.pat_eichler.bnn.brain.cellcall;

import com.pat_eichler.bnn.brain.Common;
import com.pat_eichler.bnn.brain.Neuron;

public class GeneticsCellCall {

    CellCall[] calls = new CellCall[]{
            new ReplicationCellCall(),
            new DeathCellCall()
    };

    final int parameterSize;
    final int parameterMask;
    final int callSize;
    final int callMask;

    public GeneticsCellCall(){
        int p = 0;
        for (CellCall c : calls) {
            int size = c.getParameterSize();
            if(size > p)
                p = c.getParameterSize();
        }

        parameterSize = p;
        parameterMask = (1 << parameterSize) - 1;
        callSize = Common.numBitsToEncode(calls.length + 1) + parameterSize;
        callMask = ((1 << callSize) - 1) << parameterSize;
    }

    public int cellCall(Neuron n, int call, byte branchState1, byte branchState2){
        int c = (call & callMask) >> parameterSize;

        // No op call
        if(c == 0 || c > calls.length)
            return -1;

        return calls[c - 1].call(n, call & parameterMask, branchState1, branchState2);
    }

    public int getCallSize(){
        return callSize;
    }
}
