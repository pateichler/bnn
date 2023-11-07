package com.pat_eichler.bnn.brain;

import java.util.Random;
import java.util.LinkedList;

public class Brain {
  public final BrainSettings settings;
  public final GeneticsModel genetics;
  public Neuron[] neurons;
  public final BrainClock clock;
  private final Random rand;

  //TODO: Probably want to have a class that manages current genetics
  private GeneticsModel getGeneticsModel(DNA dna){
    if(dna == null)
      return new ConwayGenetics(rand);

    return new ConwayGenetics(dna);
  }

  //TODO: Organize constructors better so less repeated code
  public Brain(){
    this((DNA) null);
  }

  public Brain(DNA dna){
    rand = new Random();
    if(BrainSettings.hasInstance()){
      settings = null;
      this.genetics = getGeneticsModel(dna);
      this.clock = new BrainClock(BrainSettings.getInstance().NEURON_COUNT, BrainSettings.getInstance().neuronSettings);
      init();
    }else{
      //  TODO: Create brain with default config
      System.out.println("Loading default settings");
      settings = new BrainSettings();
      try(BrainSettings o = BrainSettings.getInstance().setContext()){
        this.genetics = getGeneticsModel(dna);
        this.clock = new BrainClock(BrainSettings.getInstance().NEURON_COUNT, BrainSettings.getInstance().neuronSettings);
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
      this.genetics = getGeneticsModel(dna);
      this.clock = new BrainClock(BrainSettings.getInstance().NEURON_COUNT, BrainSettings.getInstance().neuronSettings);
      init();
    }
  }

  //TODO: Make private
  public Brain(GeneticsModel genetics){
    if(!BrainSettings.hasInstance())
      throw new RuntimeException("No brain settings set");

    this.rand = new Random();
    this.genetics = genetics;
    this.settings = null;
    this.clock = new BrainClock(BrainSettings.getInstance().NEURON_COUNT, BrainSettings.getInstance().neuronSettings);
    init();
  }

  private void init(){
    neurons = new Neuron[BrainSettings.getInstance().NEURON_COUNT];

    for(int i = 0; i < neurons.length; i ++)
      neurons[i] = new Neuron(this, genetics, rand);
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

    for (int i = 0; i < neurons.length; i++)
      neurons[i].postStep(clock.getMode(i));

    clock.increment();
  }

  public Neuron[] getRandomNeurons(int numNeurons){
    Neuron[] n = new Neuron[numNeurons];
    //TODO: Could make better to avoid repeats
    for (int i = 0; i < numNeurons; i++)
      n[i] = neurons[rand.nextInt(neurons.length)];

    return n;
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
