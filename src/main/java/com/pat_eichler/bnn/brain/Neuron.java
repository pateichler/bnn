package com.pat_eichler.bnn.brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Neuron {

  private final GeneticsModel genetics;
  private boolean active;
  private byte state;
  private boolean stateChanged;
  private boolean receivedInput;
  private int activationCount;
  private final short[] preNeuronStates;
  public final ArrayList<Connection> connections;
  public final ArrayList<Neuron> backRefNeurons;
  private int coolDown = 0;
  private final Brain brain;
  private final Random rand;

  public static class PostNeuronMode{
    public boolean updateState;
    public boolean searchConnections;
    public PostNeuronMode(boolean updateState, boolean searchConnections){
      this.updateState = updateState;
      this.searchConnections = searchConnections;
    }
  }

  public Neuron(Brain brain, GeneticsModel genetics, Random rand) {
    BrainSettings.NeuronSettings settings = BrainSettings.getInstance().neuronSettings;
    connections = new ArrayList<>(settings.MAX_CONNECTIONS);
    backRefNeurons = new ArrayList<>(settings.MAX_BACK_REF_NEURONS);
    preNeuronStates = new short[settings.NUM_STATES];
    this.brain = brain;
    this.genetics = genetics;
    this.rand = rand;
  }
  //TODO: To test:
  // Activation, cool down, getSearchNeurons

  public void addNT(short count, int ntType, byte neuronState, boolean fromNeuron) {
    activationCount += count * BrainSettings.getInstance().connectionSettings.NT_TYPE_NEURON_CHANGE[ntType];
    preNeuronStates[neuronState] += count;
    if(fromNeuron)
      receivedInput = true;
  }
  
  public void step() {
    if(coolDown > 0)
      coolDown --;

    if (active && coolDown == 0)
      triggerConnections();
  }

  void triggerConnections(){
    coolDown = BrainSettings.getInstance().neuronSettings.TRIGGER_COOL_DOWN;

    for(Connection c : connections)
      c.trigger(this);
  }
  
  public void postStep(PostNeuronMode mode) {
    active = activationCount > BrainSettings.getInstance().connectionSettings.NT_THRESHOLD;
    activationCount = 0;

    if(mode.updateState){
      adjustState();
      adjustConnections();

      Arrays.fill(preNeuronStates, (short) 0);
    }

    if (mode.searchConnections){
      this.searchConnections();
      backRefNeurons.clear();
      receivedInput = false;
    }
  }

  void adjustState(){
      short[] postNeuronStates =  getPostStateNeurons();
      byte s = genetics.getNeuronStateChange(preNeuronStates, postNeuronStates, state);
      stateChanged = state != s;
      if(stateChanged)
        setState(s);
  }

  void adjustConnections() {
    //TODO: Check if this correctly removes neuron
    connections.removeIf(connection -> !connection.adjust(this, genetics));
  }

  public void addConnection(Neuron neuron, byte ntType){
    if(connections.size() >= BrainSettings.getInstance().neuronSettings.MAX_CONNECTIONS)
      throw new RuntimeException("Unable to add connection ... maximum connections reached");

    //TODO: Check if ntType is correctly set
    Connection c = new Connection(neuron, (byte)(ntType - 1));
    connections.add(c);
  }
//  public void removeConnection(Connection c){
//    connections.remove(c);
//  }

  short[] getPostStateNeurons(){
    short[] p = new short[BrainSettings.getInstance().neuronSettings.NUM_STATES];
    for (Connection c : connections) {
      if(c.endNeuron.stateChanged)
        p[c.endNeuron.state] += c.getPhysicalStrength();
    }

    return p;
  }

  void searchConnections(){
    int maxCount = BrainSettings.getInstance().neuronSettings.MAX_CONNECTIONS;
    if(connections.size() >= maxCount)
      return;

    for (Neuron newNeuron : getSearchNeurons()) {
      if(newNeuron == null || hasConnection(newNeuron))
        continue;

      byte ntType = genetics.getConnectionCreation(state, newNeuron.state);
      if(ntType > 0) {
        addConnection(newNeuron, ntType);
        if(connections.size() >= maxCount)
          return;
      }
    }
  }

  Neuron[] getSearchNeurons(){
    //TODO: Method is not efficent ... it can return duplicate neurons
    if(!connections.isEmpty()){
      // We have a downstream neurons
      Neuron[] searchNeurons = new Neuron[BrainSettings.getInstance().neuronSettings.CONN_SEARCH_SIZE];
      for (int i = 0; i < searchNeurons.length; i++) {
        Neuron conNeuron = getRandomConnectedNeuron();
        Neuron newNeuron = conNeuron.getRandomConnectedNeuron();
        if(newNeuron != null)
          searchNeurons[i] = newNeuron;
        else
          conNeuron.addBackRefNeuron(this);
      }

      return searchNeurons;
    }else{
      // We don't have any downstream neurons ... check to see if we should connect via backref neurons
      if(!backRefNeurons.isEmpty() || receivedInput){
        return backRefNeurons.toArray(new Neuron[0]);
      }else{
        // We don't have any back ref neurons or have received any input, so we are probably a lonely neuron ... find
        // a random neuron to connect to
        return brain.getRandomNeurons(BrainSettings.getInstance().neuronSettings.MAX_BACK_REF_NEURONS);
      }
    }
  }

  Neuron getRandomConnectedNeuron(){
    if(connections.isEmpty())
      return null;

    return connections.get(rand.nextInt(connections.size())).endNeuron;
  }

  void addBackRefNeuron(Neuron neuron){
    if(backRefNeurons.size() >= BrainSettings.getInstance().neuronSettings.MAX_BACK_REF_NEURONS)
      return;

    backRefNeurons.add(neuron);
  }

  boolean hasConnection(Neuron neuron){
    for (Connection connection : connections) {
      if(connection.endNeuron.equals(neuron))
        return true;
    }
    return false;
  }

  public boolean isActive() {
    return active;
  }
  public byte getState(){
    return state;
  }
  void setState(byte state){
    this.state = state;
  }
  public int getActivationCount(){
    return activationCount;
  }
  public short getPreNeuronStateCount(int i){
    return preNeuronStates[i];
  }
}
