
public class PlayerSkeleton {

	static final int NO_FEATURES = 21;

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		if (s.getRowsCleared() % 10000 == 0) {
			System.out.println(s.getRowsCleared());
		}
		Game g = new Game(s);
		double[] w = getValueOfWeight();
		Strategy strategy = new Strategy(w);

		int move = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		Game simulation;

		for(int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(g);
			double score = strategy.calculate(simulation, i);
			simulation.makeMove(i);
			if (simulation.hasLost()) {
				continue;
			}

			if(score > maxScore) {
				maxScore = score;
				move = i;
			}
		}

		/*try {
			Scanner sc = new Scanner(System.in);
			int tmp = sc.nextInt();
		}catch(Exception e) {
		}*/

		return move;
	}

	public static double[] getValueOfWeight() {
double[] w = {-4.098479919581872,1.6967332065682932,-1.3058880449507537,-3.4843437196471303,-4.518121709436123,-1.9063895832088806};
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
