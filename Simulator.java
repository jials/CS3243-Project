import java.util.concurrent.Callable;
class Simulator implements Callable<Integer> {

	Strategy strategy;
	public Simulator(Strategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Run a game of tetris using the passed strategy
	 *
	 * @param strategy
	 * @return the number of rows cleared
	 */
	protected int getScore(Strategy strategy) {
		int res = 0;
		for (int i = 0; i < 5; i++) {
			State s = new State();
			while (!s.hasLost()) {
				s.makeMove(pickMove(s, s.legalMoves(), strategy));
			}
			res += s.getRowsCleared();
		}
		System.out.println(res);
		return res;
	}

	/**
	 * Run simulations for all the next legal move and pick the one that will
	 * give the best possible score according to the strategy
	 *
	 * @param s
	 *            the current state of the game
	 * @param legalMoves
	 * @param strategy
	 *            the strategy being used
	 * @return the index of the move with highest score according to the
	 *         strategy
	 */
	private int pickMove(State s, int[][] legalMoves, Strategy strategy) {
		int move = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		Game simulation;

		for (int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(s);
			simulation.makeMove(i);
			if (simulation.hasLost()) {
				continue;
			}
			double score = simulation.getRowsCleared()
				+ strategy.calculate(simulation);

			if (score > maxScore) {
				maxScore = score;
				move = i;
			}
		}
		return move;
	}

	public Integer call() {
		return getScore(strategy);
	}
}
