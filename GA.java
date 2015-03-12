import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
public class GA extends Learning {
  Strategy[] population;
	/**
	 * Constructor
	 */
	public GA() {
		load();
	}
	

	class ScoredStrategy implements Comparable<ScoredStrategy> {
		private Strategy s;
		private double score;

		public ScoredStrategy(Strategy s, double score) {
			this.s = s;
			this.score = score;
		}
		
		public int compareTo(ScoredStrategy s) {
			if (s.score < this.score) {
				return -1;
			} else if (s.score == this.score) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	/**
	 * Run some iterations of the genetic algorithm to refine the population. 
	 * @param iterations The number of iterations.
	 */
	public void run(int iterations) {
		Random rnd = new Random();
    for (int i = 0; i < seed; i++) {
			List<Strategy> newPopulation = new ArrayList<Strategy>();
			for (int j = 0; j < 21; j++) {
				int idx1 = rnd.nextInt(16);
				int idx2 = rnd.nextInt(16);
				Strategy[] newStrats = population[idx1].crossover(population[idx2]);
				newStrats[0].mutate();
				newStrats[1].mutate();
				newStrats[0].normalize();
				newStrats[1].normalize();
				newPopulation.add(newStrats[0]);
				newPopulation.add(newStrats[1]);
			}

			//Selection
			List<ScoredStrategy> scoredList = new ArrayList<ScoredStrategy>();
			for (Strategy s : population) {
				scoredList.add(new ScoredStrategy(s, getScore(s)));
			}
			for (Strategy s : newPopulation) {
				scoredList.add(new ScoredStrategy(s, getScore(s)));
			}

			Collections.sort(scoredList);
			for (int j = 0; j < 16; j++) {
				population[i] = scoredList.get(i).s;
			}
		}
	}
	
	public void store() {
		
	}
	
	public void load() {
		
	}
}
