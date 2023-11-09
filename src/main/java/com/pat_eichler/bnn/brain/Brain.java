package com.pat_eichler.bnn.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.LinkedList;

public class Brain {
  public final BrainSettings settings;
  public final GeneticsModel genetics;
//  public Neuron[] neurons;
  public ArrayList<Neuron>[] neurons;
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
    this((DNA) null);
  }

  public Brain(DNA dna){
    rand = new Random();
    if(BrainSettings.hasInstance()){
      settings = null;
      this.genetics = getGeneticsModel(dna);
      init();
    }else{
      //  TODO: Create brain with default config
      System.out.println("Loading default settings");
      settings = new BrainSettings();
      try(BrainSettings o = BrainSettings.getInstance().setContext()){
        this.genetics = getGeneticsModel(dna);
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
    init();
  }

  @SuppressWarnings("unchecked")
  private void init(){
    //TODO: Init with one neuron
    neurons = (ArrayList<Neuron>[]) new ArrayList[BrainSettings.getInstance().neuronSettings.NUM_NEURON_TYPES];
    for (int i = 0; i < neurons.length; i++)
      neurons[i] = new ArrayList<>();

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
    for (ArrayList<Neuron> types : neurons)
      for (Neuron n : types)
        n.step();

    for (ArrayList<Neuron> types : neurons)
      for (Neuron n : types)
        n.postStep();

    reduceNeurons(neuronCount - BrainSettings.getInstance().MAX_NEURON_COUNT);
  }

  void reduceNeurons(int numNeurons){
    if(numNeurons <= 0)
      return;

    for (int i = 0; i < numNeurons; i++)
      getKillNeuron().die();
  }

  public void addNeuron(Neuron n){
    neurons[n.getType()].add(n);
    neuronCount++;
  }

  public void removeNeuron(Neuron n){
    neurons[n.getType()].remove(n);
    neuronCount--;
  }

  // This method could potentially be improved to try to keep important neurons alive
  Neuron getKillNeuron(){
    for (int i = neurons.length - 1; i >= 0; i--)
      if(!neurons[i].isEmpty())
        return neurons[i].get(rand.nextInt(neurons[i].size()));

    return null;
  }

  public boolean isDead(){
    return neuronCount <= 0;
  }
}
