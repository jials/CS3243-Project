import java.util.Scanner;

public class PS2 {

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
		double[] w ={-4.197522873578402,0.7448884715097055,-1.2662380046953858,-4.445836416127795,-2.6618254253024256,-1.079545707988129};
		return w;
	}

	public static void main(String[] args) {
		State s = new State();
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}

}
