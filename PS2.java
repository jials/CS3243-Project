import java.util.Scanner;

public class PS2 {

	static final int NO_FEATURES = 21;
//implement this function to have a working system 
	public int pickMove(Game s, int[][] legalMoves) {
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
		double[] w =
{-4.702950520298681,2.0706001287265066,-1.2880690173841591,-3.470570078386892,-4.431071922401414,-1.9959973216039515};
		return w;
	}

	public static void main(String[] args) {
		Game s;
		if (args.length > 0) {
			s = new Game(Long.parseLong(args[0]));
		} else {
			s = new Game();
		}
		PS2 p = new PS2();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}

}
