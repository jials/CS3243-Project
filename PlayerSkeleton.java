
public class PlayerSkeleton {

	static final int NO_FEATURES = 21;

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		Game g = new Game(s);
		double[] w = getValueOfWeight();
		Strategy strategy = new Strategy(w);

		int move = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		Game simulation;

		for(int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(g);
			simulation.makeMove(i);
			if (simulation.hasLost()) {
				continue;
			}
			double score = simulation.getRowsCleared() + strategy.calculate(simulation);

			if(score > maxScore) {
				maxScore = score;
				move = i;
			}
		}

		return move;
	}

	public static double[] getValueOfWeight() {
double[] w = {-2.4314434705091954,-4.170703742915406,-1.6855483652653476,-4.467492185839834,-4.0978279947846055,-3.2473988612810585,-4.0832401679564,-3.8201310809251425,-3.3881843924828496,-2.277064870995924,-1.5011607717865298,-0.8738490523768699,-2.247996199862927,-1.4706440374186214,-2.6241133511334653,-1.1212505515403364,-2.393098215478936,-1.2625024690510434,-0.8660445626984148,-1.193076058175646,-4.938496429385502};
		return w;
	}

	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}

}
