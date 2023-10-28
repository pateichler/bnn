package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigClass;
import com.pat_eichler.config.processor.ProcessConfig;

import java.security.PublicKey;

@ConfigClass @ProcessConfig(defaultsFileName = "defaultSettings.json", infoFileName = "webConfigSettings.json")
public class BrainSettings implements AutoCloseable {
  public NTSettings ntSettings;
  public NeuronSettings neuronSettings;
  public ConnectionSettings connectionSettings;
  public GeneticSettings geneticSettings;

  public Integer NEURON_COUNT;
  public String CONN_CONFIG;

  @ConfigClass
  public static class NTSettings{
    public Integer NT_COUNT_EXC;
    public Integer NT_COUNT_INH;
    public Integer NT_COUNT_MOD;

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
    public Integer TRIGGER_COOLDOWN;
    public Integer MAX_CONNECTIONS;
    public Integer STATE_UPDATE_PERIOD;
    public Integer CONN_SEARCH_PERIOD;
  }

  @ConfigClass
  public static class ConnectionSettings{
    public Integer CONN_COUNT;
    public Integer NT_THRESHOLD;
    public Integer CONN_ADJUST_INC;
    public Boolean UNSYNC_CONN_ADJUST;

    public Integer START_CONNECTION_STRENGTH;
    public Integer MAX_STRENGTH;
    public Integer STRENGTH_INCREASE;
  }

  @ConfigClass
  public static class GeneticSettings{
    public Integer[] STRENGTH_NET_IN_LAYERS;
    public Integer[] TYPE_NET_IN_LAYERS;
  }


  private static BrainSettings _instance;
  public static Boolean hasInstance(){
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
