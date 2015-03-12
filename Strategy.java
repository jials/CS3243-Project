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
	
  /**
   * Normalize the weights, so that sigma(weights) = 1.0
   */
  public void normalize() {
	  double sum = 0;
	  for(int i = 0; i < weights.length; i++) {
		  sum += weights[i];
	  }
	  for(int i = 0; i < weights.length; i++) {
		  weights[i] = weights[i]/sum;
	  }
  }

  //TODO: Implement
  // Mutate this strategy.
  public void mutate() {
  }
}
