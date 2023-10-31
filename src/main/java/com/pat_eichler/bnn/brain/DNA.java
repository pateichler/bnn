package com.pat_eichler.bnn.brain;

import java.io.Serializable;

public class DNA implements Serializable {
    public final byte[] data;
    public DNA(byte[] data){
        this.data = data;
    }
}
