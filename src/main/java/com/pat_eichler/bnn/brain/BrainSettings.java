package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigClass;
import com.pat_eichler.config.processor.ProcessConfig;

@ConfigClass @ProcessConfig(defaultsFileName = "defaultSettings.json", infoFileName = "webConfigSettings.json")
public class BrainSettings implements AutoCloseable {
  public NTSettings ntSettings;
  public NeuronSettings neuronSettings;
  public ConnectionSettings connectionSettings;
  public GeneticSettings geneticSettings;

  //TODO: Remove
  public double MUTATION_RATE;

  public int NEURON_COUNT;
  public String CONN_CONFIG;

  @ConfigClass
  public static class NTSettings{
    public int NT_COUNT_EXC;
    public int NT_COUNT_INH;
    public int NT_COUNT_MOD;

    public int totalNTCount() {
      return NT_COUNT_EXC + NT_COUNT_INH + NT_COUNT_MOD;
    }

    public NTAction getNTAction(int ntType) {
      if(ntType < NT_COUNT_EXC)
        return NTAction.EXCITATORY;

      if(ntType < NT_COUNT_EXC + NT_COUNT_INH)
        return NTAction.INHIBITORY;

      return NTAction.MODULATORY;
    }
  }

  @ConfigClass
  public static class NeuronSettings{
    public int TRIGGER_COOLDOWN;
  }

  @ConfigClass
  public static class ConnectionSettings{
    public int CONN_COUNT;
    public int NT_THRESHOLD;
    public int CONN_ADJUST_INC;
    public boolean UNSYNC_CONN_ADJUST;

    public boolean RANDOMIZE_CONN_STRENGTH;
    public int MAX_CONN_STRENGTH;
  }

  @ConfigClass
  public static class GeneticSettings{
    public int[] STRENGTH_NET_IN_LAYERS;
    public int[] TYPE_NET_IN_LAYERS;
  }


  private static BrainSettings _instance;
  public static boolean hasInstance(){
    return _instance != null;
  }
  public static BrainSettings getInstance() {
    if(_instance == null)
      throw new RuntimeException("Context is not set!");

    return _instance;
  }

  private static void setInstance(BrainSettings i){
    _instance = i;
  }

  public BrainSettings setContext(){
    if(_instance != null)
      throw new RuntimeException("Can not set context ... context already set!");

    setInstance(this);
    return this;
  }

  @Override
  public void close() {
    setInstance(null);
  }
  
}
