abstract class Learning {

	/**
	 * Run a game of tetris using the passed strategy
	 * @param strategy
	 * @return the number of rows cleared
	 */
	protected int getScore(Strategy strategy){
		State s = new State();
		while(!s.hasLost()) {
			s.makeMove(pickMove(s, s.legalMoves(), strategy));
		}
		return s.getRowsCleared();
	}

	private int pickMove(State s, int[][] legalMoves, Strategy strategy) {
		int move = 0;
		double maxScore = -1;
		Game simulation;

		for(int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(s);
			simulation.makeMove(i);
			double score =
					simulation.getRowsCleared() + strategy.calculate(simulation);

			if(score > maxScore) {
				maxScore = score;
				move = i;
			}
		}
		return move;
	}

	protected abstract void store();

	protected abstract void load();
}
