package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;
import com.pat_eichler.bnn.brain.Neuron;

import java.util.Random;

public class SimpleRunner extends BrainRunner {
    private final Random rand;

    int growthPeriod = 0;
    int teachIterations = 500;
    int testIterations = 50;
    int numOptions = 4;

    boolean init;

    public SimpleRunner(Brain brain) {
        super(brain);
        rand = new Random();
    }

    public Double call() {
        grow();

        learn(teachIterations);

        return learn(testIterations);
    }

    void grow() {
        for(int i = 0; i < growthPeriod; i++)
            stepBrain();
    }

    double learn(int iterations) {
        int numCorrect = 0;

        for(int i = 0; i < iterations; i++) {
            int n = (int)(rand.nextDouble() * numOptions);
            brain.neurons[n].addNT((short)20, 2, (byte) 1, false);

            stepBrain();

            boolean correct = false;

            outerLoop:for(int x = 0; x < 10; x++) {
                for(int o = 0; o < numOptions; o++) {
                    boolean state = brain.neurons[brain.neurons.length - numOptions + o].isActive();

                    // An output neuron is active ... check if it right
                    if(state) {
                        correct = (o == n);

                        // Brain choose wrong ... break answer retrieval loop
                        if(!correct)
                            break outerLoop;
                    }
                }

                // We have given the right answer with no other answers ... break out of loop as correct
                if(correct)
                    break;

                stepBrain();
            }

            if(correct)
                numCorrect++;

            int learnSteps = 60 + rand.nextInt(20);
            for(int x = 0; x < learnSteps; x++) {
//                if(!correct && x < 40)
                if(x < 40)
                    brain.neurons[brain.neurons.length - numOptions + n].addNT((short) 20, 1, (byte) 2, false);
                stepBrain();
            }
        }

        return (double)numCorrect / iterations;
    }
    
    void stepBrain(){
        for (int i = 0; i < numOptions; i++)
            brain.neurons[i].addNT((short) (init ? 1 : 5), 1, (byte) 1, false);

        init = true;
        brain.step();
    }
}
