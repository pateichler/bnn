package com.pat_eichler.bnn.brain;

import java.util.Random;
import java.util.LinkedList;

public class Brain {
  public final BrainSettings settings;
  public final GeneticsModel genetics;
  public Neuron[] neurons;
  private int curNeuronStateUpdate;
  private int curNeuronSearchUpdate;
  private final Random rand;

  //TODO: Probably want to have a class that manages current genetics
  private GeneticsModel getGenetics(DNA dna){
    if(dna == null)
      return new ConwayGenetics(rand);

    return new ConwayGenetics(dna);
  }

  public Brain(){
    this((DNA) null);
  }

  public Brain(DNA dna){
    rand = new Random();
    if(BrainSettings.hasInstance()){
      settings = null;
      this.genetics = getGenetics(dna);
      init();
    }else{
      //  TODO: Create brain with default config
      System.out.println("Loading default settings");
      settings = new BrainSettings();
      try(BrainSettings o = BrainSettings.getInstance().setContext()){
        this.genetics = getGenetics(dna);
        init();
      }
    }
  }
  public Brain(BrainSettings settings){
    this(null, settings);
  }

  public Brain(DNA dna, BrainSettings settings) {
    rand = new Random();
    this.settings = settings;

    try(BrainSettings o = BrainSettings.getInstance().setContext()){
      this.genetics = getGenetics(dna);
      init();
    }
  }

  private void init(){
    neurons = new Neuron[BrainSettings.getInstance().NEURON_COUNT];

    for(int i = 0; i < neurons.length; i ++)
      neurons[i] = new Neuron();
  }
  
  public void step() {
    if(settings != null){
      try(BrainSettings o = BrainSettings.getInstance().setContext()){
        stepBrain();
      }
    }else
      stepBrain();
  }

  private void stepBrain(){
    for(Neuron n : neurons)
      n.step();

    //TODO: Double check if this works and also if it can be simplified
    float f = (float) settings.neuronSettings.STATE_UPDATE_PERIOD / (neurons.length + 1);
    int chunkSize = (int)((curNeuronStateUpdate+1) / f) - (int)(curNeuronStateUpdate / f);
    float fC = (float) settings.neuronSettings.CONN_SEARCH_PERIOD / (chunkSize + 1);
    for (int i = 0; i < neurons.length; i++) {
      int c = (int) (i / f);
      boolean updateState = c == curNeuronStateUpdate;
      boolean searchConnections = false;
      if(updateState)
        searchConnections = (i - (int)(c * f) / fC) == curNeuronSearchUpdate;

      neurons[i].postStep(updateState, searchConnections);
    }

    curNeuronStateUpdate++;
    if(curNeuronStateUpdate > settings.neuronSettings.STATE_UPDATE_PERIOD){
      curNeuronStateUpdate = 0;
      curNeuronSearchUpdate = (curNeuronSearchUpdate + 1) % settings.neuronSettings.CONN_SEARCH_PERIOD;
    }
  }
  
  public void printNumActive() {
    int a = 0;
    for(Neuron n : neurons)
      if(n.isActive())
        a++;
    
    System.out.println("Number active: " + a);
  }
  
  public void printConnectionStrengths() {
    for(Neuron n : neurons) {
      for(Connection c : n.connections)
        System.out.print(c.getStrength());
      System.out.println();
    }
  }
  
  public void printConnectionTypes() {
    for(Neuron n : neurons) {
      for(Connection c : n.connections)
        System.out.print(c.getNtType());
      System.out.println();
    }
  }
}
