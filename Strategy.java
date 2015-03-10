import java.util.Arrays;

public class Strategy {
	private double[] weights;
	
	/*Constructor*/
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
	public double calculate(State state){
		FeaturesScore fs = new FeaturesScore(state);
		double totalScore = 0.0;
		
		for (int i = 0; i < fs.getNumberOfFeatures(); ++i){
			totalScore += weights[i] * fs.getScore(i);
		}
		
		return totalScore;
	}
	
}