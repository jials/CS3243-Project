abstract class Learning {

	/**
	 * Run a game of tetris using the passed strategy
	 *
	 * @param strategy
	 * @return the number of rows cleared
	 */
	protected int getScore(Strategy strategy) {
		State s = new State();
		while (!s.hasLost()) {
			s.makeMove(pickMove(s, s.legalMoves(), strategy));
		}
		return s.getRowsCleared();
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
		double maxScore = -1;
		Game simulation;

		for (int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(s);
			simulation.makeMove(i);
			double score = simulation.getRowsCleared()
					+ strategy.calculate(simulation);

			if (score > maxScore) {
				maxScore = score;
				move = i;
			}
		}
		return move;
	}

	/**
	 * Store the result of the current run
	 */
	protected abstract void store();

	/**
	 * Load the result of previous run
	 */
	protected abstract boolean load();
}
