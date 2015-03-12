
public class PlayerSkeleton {
	
	static final int NO_FEATURES = 21;

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		Game g = new Game(s);
		double[] w = getValueOfWeight();
		Strategy strategy = new Strategy(w);
		
		int move = 0;
		double maxScore = -1;
		Game simulation;
		
		for(int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(g);
			simulation.makeMove(i);
			double score = simulation.getRowsCleared() + strategy.calculate(simulation);
			
			if(score > maxScore) {
				maxScore = score;
				move = i;
			}
		}
		
		return move;
	}
	
	public static double[] getValueOfWeight() {
		double[] w = new double[NO_FEATURES];
		for(int i = 0; i < NO_FEATURES; i++) {
			w[i] = 1;
		}
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
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}