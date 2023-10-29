package com.pat_eichler.bnn.brain.runner;

import com.pat_eichler.bnn.brain.Brain;
import java.lang.reflect.Constructor;

public class RunnerLoader {
    public static BrainRunner getBrainRunnerFromClassString(String className, Brain b){
        try {
            Class<?> c = Class.forName(RunnerLoader.class.getPackageName() + "." + className);
            Constructor<?> ctor = c.getConstructor(Brain.class);
            return (BrainRunner) ctor.newInstance(b);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
