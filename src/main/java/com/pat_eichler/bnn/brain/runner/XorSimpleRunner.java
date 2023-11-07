package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;

import java.util.Random;

public class XorSimpleRunner extends BrainRunner {
    private final Random rand;

    int growthPeriod = 0;
    int teachIterations = 500;
    int testIterations = 50;

    boolean init;

    public XorSimpleRunner(Brain brain) {
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
            int b = rand.nextInt(3);
            if(b % 2 == 0)
                brain.neurons[0].addNT((short)20, 2, (byte) 1, false);
            if(b < 2)
                brain.neurons[1].addNT((short)20, 2, (byte) 1, false);

            stepBrain();
            boolean answerActive = (b == 1 || b == 2);
            boolean triggered = false;


            for(int x = 0; x < 10; x++) {
                if(brain.neurons[brain.neurons.length - 1].isActive()){
                    triggered = true;
                    break;
                }

                stepBrain();
            }

            boolean correct = answerActive == triggered;
            if(correct)
                numCorrect++;

            int learnSteps = 60 + rand.nextInt(20);
            for(int x = 0; x < learnSteps; x++) {
//                if(!correct && x < 40)
                if(!correct && x < 40)
                    brain.neurons[brain.neurons.length - 1].addNT((short) 20, 1, (byte) (answerActive ? 2 : 3), false);
                stepBrain();
            }
        }

        return (double)numCorrect / iterations;
    }
    
    void stepBrain(){
        for (int i = 0; i < 2; i++)
            brain.neurons[i].addNT((short) (init ? 1 : 5), 1, (byte) 1, false);

        init = true;
        brain.step();
    }
}
