package com.pat_eichler.bnn.brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Neuron {

  private final GeneticsModel genetics;
  private final byte type;
  private boolean active;
  private byte state;
//  private byte nextState;
  private boolean stateChanged;
  private boolean receivedInput;
  private int activationCount;
  private NeuronStateChange nextStateChange;
//  private int nextStateDelay = -1;
//  private boolean nextStateFinal;
  private final short[] preNeuronStates;
  public final ArrayList<Connection> connections;
  public final ArrayList<Neuron> backRefNeurons;
  private int coolDown = 0;
  private int deadCount = -1;
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

  public static class NeuronStateChange{
    public final byte nextState;
    public int stateDelay;
    public boolean nextStateFinal;

    public NeuronStateChange(byte nextState, int stateDelay, boolean nextStateFinal) {
      this.nextState = nextState;
      this.stateDelay = stateDelay;
      this.nextStateFinal = nextStateFinal;
    }
  }

  public Neuron(Brain brain, byte type, GeneticsModel genetics, Random rand) {
    BrainSettings.NeuronSettings settings = BrainSettings.getInstance().neuronSettings;
    connections = new ArrayList<>(settings.MAX_CONNECTIONS);
    backRefNeurons = new ArrayList<>(settings.MAX_BACK_REF_NEURONS);
    preNeuronStates = new short[settings.NUM_STATES];
    this.brain = brain;
    this.type = type;
    this.genetics = genetics;
    this.rand = rand;
  }

  public void createConnectedNeuron(byte neuronType, byte connType, byte initState){
    //TODO: Add neuron to list
    Neuron newNeuron = new Neuron(brain, neuronType, genetics, rand);
    newNeuron.setState(initState);
    this.connections.add(new Connection(newNeuron, connType));
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
    if(isDying()){
      deadCount --;
      if(deadCount <= 0)
        die();
      return;
    }

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
    if(isDying())
      return;

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
    // Update our next state
    nextStateChange = genetics.getNeuronStateChange(preNeuronStates, getPostStateNeurons(), this, nextStateChange);

    if(nextStateChange.stateDelay >= 0)
      nextStateChange.stateDelay--;
    // State delay is less than 0 ... set new state
    else{
      setState(nextStateChange.nextState);
      nextStateChange = null;
    }
  }

  void adjustConnections() {
    connections.removeIf(connection -> !connection.adjust(this, genetics));
  }

  public void addConnection(Neuron neuron, byte ntType){
    addConnection(new Connection(neuron, (byte)(ntType - 1)));
  }
  public void addConnection(Connection c){
//    System.out.println("Adding connection to neuron in state: " + c.endNeuron.getState() + " with type " + c.getNtType());
    if(connections.size() >= BrainSettings.getInstance().neuronSettings.MAX_CONNECTIONS)
      throw new RuntimeException("Unable to add connection ... maximum connections reached");

    connections.add(c);
  }

//  public void removeConnection(Connection c){
//    connections.remove(c);
//  }

  short[] getPostStateNeurons(){
    short[] p = new short[BrainSettings.getInstance().neuronSettings.NUM_STATES];
    for (Connection c : connections) {
      //TODO: Possibly put back state change
//      if(c.endNeuron.stateChanged)
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
    if(backRefNeurons.size() >= BrainSettings.getInstance().neuronSettings.MAX_BACK_REF_NEURONS || isDying())
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

  void die(){
    connections.clear();
    backRefNeurons.clear();
    //TODO: Remove from brain list
  }

  public boolean isActive() {
    return active;
  }
  public byte getType(){
    return type;
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
  public void setDying(int deadCount){
    if(deadCount < 0)
      throw new RuntimeException("Can't set dead count to less than 0");
    this.deadCount = deadCount;
  }
  public boolean isDying(){
    return deadCount >= 0;
  }
  public boolean isDead(){
    return deadCount == 0;
  }
}
