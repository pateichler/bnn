package com.pat_eichler.bnn.brain;

import com.pat_eichler.config.ConfigClass;
import com.pat_eichler.config.processor.ConfigProperty;
import com.pat_eichler.config.processor.ProcessConfig;

@ConfigClass @ProcessConfig(defaultsFileName = "defaultSettings.json", infoFileName = "webConfigSettings.json")
public class BrainSettings implements AutoCloseable {
  public NeuronSettings neuronSettings;
  public ConnectionSettings connectionSettings;
  public GeneticSettings geneticSettings;

  @ConfigProperty(defualtValue = "20", comment = "Number of neurons in the brain.")
  public Integer NEURON_COUNT;

  @ConfigClass
  public static class NeuronSettings{
    @ConfigProperty(defualtValue = "10", comment = "Number of neuron states.")
    public Integer NUM_STATES;
    @ConfigProperty(defualtValue = "4", comment = "Number of neuron types.")
    public Integer NUM_NEURON_TYPES;
    @ConfigProperty(defualtValue = "1", comment = "Minimum wait time till neuron can fire again.")
    public Integer TRIGGER_COOL_DOWN;
    @ConfigProperty(defualtValue = "20", comment = "Maximum number of outgoing connections a neuron can have. Neurons can have unlimited incoming connections.")
    public Integer MAX_CONNECTIONS;
    @ConfigProperty(defualtValue = "3", comment = "Maximum number of back reference neurons. Back reference neurons are used in connection creation search algorithm.")
    public Integer MAX_BACK_REF_NEURONS;
    @ConfigProperty(defualtValue = "5", comment = "Period of neuron state update (in steps).")
    public Integer STATE_UPDATE_PERIOD;
    @ConfigProperty(defualtValue = "4", comment = "Period of the connection search update (in state updates). @important Period is based off of state update update period. " +
            "The number of period steps is STATE_UPDATE_PERIOD * CONN_SEARCH_PERIOD")
    public Integer CONN_SEARCH_PERIOD;
    @ConfigProperty(defualtValue = "5", comment = "Number of neuron candidates searched.")
    public Integer CONN_SEARCH_SIZE;
  }

  @ConfigClass
  public static class ConnectionSettings{
    @ConfigProperty(defualtValue = "10", comment = "Threshold of neuron activation.")
    public Integer NT_THRESHOLD;
    @ConfigProperty(defualtValue = "[-1,0,1]", comment = "Connection type neuron count change")
    public Integer[] NT_TYPE_NEURON_CHANGE;
    @ConfigProperty(defualtValue = "8", comment = "Start connection strength of connection.")
    public Integer START_CONNECTION_STRENGTH;
    @ConfigProperty(defualtValue = "2048", comment = "Maximum connection strength.")
    public Integer MAX_STRENGTH;
    @ConfigProperty(defualtValue = "32", comment = "Strength increase per connection strengthen update.")
    public Integer STRENGTH_INCREASE;
    @ConfigProperty(defualtValue = "1", comment = "Strength decrease per connection strengthen update.")
    public Integer STRENGTH_DECREASE;
  }

  @ConfigClass
  public static class GeneticSettings{
    @ConfigProperty(defualtValue = "4", comment = "Number of bits per neural network weight.")
    public Integer NN_WEIGHT_BITS;
    @ConfigProperty(defualtValue = "8", comment = "Number of bits per neural network bias.")
    public Integer NN_BIASES_BITS;
    @ConfigProperty(defualtValue = "3", comment = "Size of the middle layer of pre state neural network.")
    public Integer PRE_STATE_NN_INNER_LAYER;
    @ConfigProperty(defualtValue = "2", comment = "Size of the middle layer of post state neural network.")
    public Integer POST_STATE_NN_INNER_LAYER;
    @ConfigProperty(defualtValue = "3", comment = "Number of bits to encode state delay.")
    public Integer STATE_DELAY_BITS;

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
