
public interface CommandInterface {
  
  World getWorld();
  
  void postGenerationStatus(int completedBrains);
  
  void postGenerationResults(double[] fitness);
  
  boolean stopWorld();
  
  boolean stopWorldImmediately();
  
  /* 
   * Considerations:
   *    - What happens if response retrieval fails (ex. no response from server)
   *        = Possibility: only return once response is retrieved
   */
}
