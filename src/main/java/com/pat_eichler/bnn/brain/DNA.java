package com.pat_eichler.bnn.brain;

import java.io.Serializable;

public class DNA implements Serializable {
    public final byte[] data;
    public final int[] segments;
    public DNA(byte[] data, int[] segments){
        this.data = data;
        this.segments = segments;
    }
}
