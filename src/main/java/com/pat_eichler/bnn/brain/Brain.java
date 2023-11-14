package com.pat_eichler.bnn.brain;

import com.pat_eichler.bnn.brain.neuroncontainer.BrainNeuronContainer;

import java.util.Random;

public class Brain {
  public final BrainSettings settings;
  public final GeneticsModel genetics;
  public BrainNeuronContainer neuronContainer;
  private Neuron rootNeuron;
  private final Random rand;

  //TODO: Probably want to have a class that manages current genetics
  private GeneticsModel getGeneticsModel(DNA dna){
    if(dna == null)
      return new ConwayGenetics(rand);

    return new ConwayGenetics(dna);
  }

  //TODO: Organize constructors better so less repeated code
  public Brain(){
    this((DNA) null, (BrainNeuronContainer) null);
  }

  public Brain(DNA dna, BrainNeuronContainer builder){
    rand = new Random();
    if(BrainSettings.hasInstance()){
      settings = null;
      this.genetics = getGeneticsModel(dna);
      init(builder);
    }else{
      //  TODO: Create brain with default config
      System.out.println("Loading default settings");
      settings = new BrainSettings();
      try(BrainSettings o = BrainSettings.getInstance().setContext()){
        this.genetics = getGeneticsModel(dna);
        init(builder);
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
      init(null);
    }
  }

  //TODO: Make private
  public Brain(GeneticsModel genetics){
    if(!BrainSettings.hasInstance())
      throw new RuntimeException("No brain settings set");

    this.rand = new Random();
    this.genetics = genetics;
    this.settings = null;
    init(null);
  }

  private void init(BrainNeuronContainer container){
    if(container == null)
      container = new BrainNeuronContainer();
    this.neuronContainer = container;

    setRootNeuron(new Neuron(this, null, (byte)0, genetics, rand));
    addNeuron(getRootNeuron());
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
    neuronContainer.step(false);
    neuronContainer.step(true);

    reduceNeurons(neuronContainer.getNeuronCount() - BrainSettings.getInstance().MAX_NEURON_COUNT);
  }

  void reduceNeurons(int numNeurons){
    if(numNeurons <= 0)
      return;

    for (int i = 0; i < numNeurons; i++)
      neuronContainer.getKillNeuron(rand).die();
  }

  public void addNeuron(Neuron n){
    neuronContainer.addNeuron(n);
  }

  public void removeNeuron(Neuron n){
    neuronContainer.removeNeuron(n);
  }

  public Neuron getRootNeuron(){
    return rootNeuron;
  }

  public void setRootNeuron(Neuron n){
    rootNeuron = n;
  }

  public boolean isDead(){
    return neuronContainer.getNeuronCount() <= 0;
  }
  public int getNeuronCount(){
    return neuronContainer.getNeuronCount();
  }
}
