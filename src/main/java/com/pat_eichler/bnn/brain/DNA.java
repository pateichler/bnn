package com.pat_eichler.bnn.brain;

public class DNA {
    public final byte[] data;
    public DNA(byte[] data){
        this.data = data;
    }
    public static DNA crossDNA(DNA dna1, DNA dna2, double fitRatio){
        if(dna1.data.length != dna2.data.length)
            throw new RuntimeException("DNA not same length! Can't cross DNA");
        //TODO: Complete
        return new DNA(dna1.data);
    }

    public static double calculateGenePoolVariation(DNA[] genePool){
        //TODO: Complete
        return 0;
    }
}
