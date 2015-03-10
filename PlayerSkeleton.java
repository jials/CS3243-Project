
public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(Game s, int[][] legalMoves) {
		Strategy strategy = new Strategy();
		int move = 0;
		double maxScore = -1;
		Game simulation;
		
		for(int i = 0; i < legalMoves.length; i++) {
			simulation = (Game) s.clone();
			simulation.makeMove(i);
			double score = simulation.getRowsCleared() + strategy.calculate(simulation);
			
			if(score > maxScore) {
				maxScore = score;
				move = i;
			}
		}
		
		return move;
	}
	
	public static void main(String[] args) {
		Game s = new Game();
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