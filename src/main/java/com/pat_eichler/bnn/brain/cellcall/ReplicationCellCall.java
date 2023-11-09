package com.pat_eichler.bnn.brain.cellcall;

import com.pat_eichler.bnn.brain.BrainSettings;
import com.pat_eichler.bnn.brain.Common;
import com.pat_eichler.bnn.brain.Neuron;

public class ReplicationCellCall implements CellCall{

    private final int parameterSize;
    private final int typeMask;
    private final int connTypeMask;
    private final int connTypeSize;

    public ReplicationCellCall(){
        connTypeSize = Common.numBitsToEncode(BrainSettings.getInstance().connectionSettings.NT_TYPE_NEURON_CHANGE.length);
        connTypeMask = (1 << connTypeSize) - 1;

        int typeSize = Common.numBitsToEncode(BrainSettings.getInstance().neuronSettings.NUM_NEURON_TYPES);
        typeMask = ((1 << typeSize) - 1) << connTypeSize;


        parameterSize = typeSize + connTypeSize;
    }

    @Override
    public int call(Neuron n, int parameters, byte branchState1, byte branchState2) {
        int type = 0;
        if(n.getType() != 0)
            type = n.getType();
        else{
            type = (parameters & typeMask) >> connTypeSize;
            type = type >= BrainSettings.getInstance().neuronSettings.NUM_NEURON_TYPES ? 0 : type;
        }

        int connType = parameters & connTypeMask;
        if(connType >= BrainSettings.getInstance().connectionSettings.NT_TYPE_NEURON_CHANGE.length)
            connType = 1;

        n.createConnectedNeuron((byte)type, (byte)connType, branchState2);

        return 0;
    }

    @Override
    public int getParameterSize() {
        return parameterSize;
    }
}
