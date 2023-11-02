package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;

public class TestRunner extends BrainRunner {

    public TestRunner(Brain brain) {
        super(brain);
    }

    public Double call() {
        byte[] data = brain.genetics.dna.data;

        int val = 0;
        for (int i = 0; i < data.length; i++) {
            if(i % 2 == 0)
                val += Integer.bitCount(data[i] & 0xFF);
            else
                val += 8 - Integer.bitCount(data[i] & 0xFF);
        }

        int t = data.length * 8;
        return (t - val)/(double)t;
    }
}
