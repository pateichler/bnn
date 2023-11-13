package com.pat_eichler.bnn.brain;

import com.pat_eichler.bnn.brain.neuroncontainer.NeuronContainer;
import com.pat_eichler.bnn.brain.neuroncontainer.NeuronContainerBuilder;

import java.util.ArrayList;
import java.util.Random;

public class Brain {
  public final BrainSettings settings;
  public final GeneticsModel genetics;
//  public Neuron[] neurons;
  public NeuronContainer[] neuronContainers;
  private int neuronCount = 0;
  private final Random rand;

  //TODO: Probably want to have a class that manages current genetics
  private GeneticsModel getGeneticsModel(DNA dna){
    if(dna == null)
      return new ConwayGenetics(rand);

    return new ConwayGenetics(dna);
  }

  //TODO: Organize constructors better so less repeated code
  public Brain(){
    this((DNA) null, (NeuronContainerBuilder) null);
  }

  public Brain(DNA dna, NeuronContainerBuilder builder){
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

  private void init(NeuronContainerBuilder containerBuilder){
    if(containerBuilder == null)
      containerBuilder = new NeuronContainerBuilder();
    this.neuronContainers = containerBuilder.createContainer();

    Neuron firstNeuron = new Neuron(this, (byte)0, genetics, rand);
    addNeuron(firstNeuron);
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
    for (NeuronContainer container : neuronContainers)
      container.step(false);

    for (NeuronContainer container : neuronContainers)
      container.step(true);

    reduceNeurons(neuronCount - BrainSettings.getInstance().MAX_NEURON_COUNT);
  }

  void reduceNeurons(int numNeurons){
    if(numNeurons <= 0)
      return;

    for (int i = 0; i < numNeurons; i++)
      getKillNeuron().die();
  }

  public void addNeuron(Neuron n){
    neuronContainers[n.getType()].addNeuron(n);
    neuronCount++;
  }

  public void removeNeuron(Neuron n){
    neuronContainers[n.getType()].removeNeuron(n);
    neuronCount--;
  }

  // This method could potentially be improved to try to keep important neurons alive
  Neuron getKillNeuron(){
    for (int i = neuronContainers.length - 1; i >= 0; i--) {
      Neuron n = neuronContainers[i].getKillNeuron(rand);
      if (n != null)
        return n;
    }

    return null;
  }

  public boolean isDead(){
    return neuronCount <= 0;
  }
  public int getNeuronCount(){
    return neuronCount;
  }
}
