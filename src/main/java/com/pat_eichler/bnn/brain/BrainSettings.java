package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigClass;
import com.pat_eichler.config.processor.ConfigProperty;
import com.pat_eichler.config.processor.ProcessConfig;

@ConfigClass @ProcessConfig(defaultsFileName = "defaultSettings.json", infoFileName = "webConfigSettings.json")
public class BrainSettings implements AutoCloseable {
  public NeuronSettings neuronSettings;
  public ConnectionSettings connectionSettings;
  public GeneticSettings geneticSettings;

  public Integer NEURON_COUNT;

  @ConfigClass
  public static class NeuronSettings{
    public Integer NUM_STATES;
    public Integer TRIGGER_COOL_DOWN;
    public Integer MAX_CONNECTIONS;
    public Integer MAX_BACK_REF_NEURONS;
    public Integer STATE_UPDATE_PERIOD;
    public Integer CONN_SEARCH_PERIOD;
    public Integer CONN_SEARCH_SIZE;
  }

  @ConfigClass
  public static class ConnectionSettings{
    public Integer NT_THRESHOLD;

    public Integer START_CONNECTION_STRENGTH;
    public Integer MAX_STRENGTH;
    public Integer STRENGTH_INCREASE;
  }

  @ConfigClass
  public static class GeneticSettings{
    @ConfigProperty(defualtValue = "4")
    public Integer NN_WEIGHT_BITS;
    @ConfigProperty(defualtValue = "8")
    public Integer NN_BIASES_BITS;
    public Integer PRE_STATE_NN_INNER_LAYER;
    public Integer POST_STATE_NN_INNER_LAYER;

    public static byte[][] CONN_CHANGE_TABLE= {
            {0, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 1, 0},
            {0, 1, 1, 1}
    };
    public static byte[][] CONN_CREATE_TABLE= {
            {0, 0, 0, 0},
            {0, 3, 0, 1},
            {0, 0, 2, 0},
            {0, 1, 0, 3}
    };
    public Integer getMiddleLayerSize(){
      return PRE_STATE_NN_INNER_LAYER + POST_STATE_NN_INNER_LAYER;
    }
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
