import java.util.Arrays;

/**
 * Strategy
 * 
 * Takes in the set of weights of all the available features.
 * Calculates required values with regards to the current set of
 * weights, such as the total score obtained.
 */
public class Strategy {
	//Stores set of weights for all the available features
	private double[] weights;
	
	/**
	 * Constructor
	 * @param w: set of weights for all the available features
	 */
	public Strategy(double[] w){
		weights = Arrays.copyOf(w, w.length);
	}
	
	/**
	 * Calculates the total score of the strategy
	 * which has a particular set of weights for
	 * all the available features.
	 * @param state
	 * @return sigma(weight[i] * score of feature[i])
	 */
	public double calculate(Game state){
		FeaturesScore fs = new FeaturesScore(state);
		double totalScore = 0.0;
		
		for (int i = 0; i < fs.getNumberOfFeatures(); i++){
			totalScore += weights[i] * fs.getScore(i);
		}
		
		return totalScore;
	}

  //TODO: Implement
  // Returns the two strategies created from crossovers
  public Strategy[] crossover(Strategy s) {
    return null;
  }
	
  //TODO: Implement
  // Normalize this strategy.
  public void normalize() {
  }

  //TODO: Implement
  // Mutate this strategy.
  public void mutate() {
  }
}
