import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import java.awt.Color;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerSkeleton {

	static final int NO_FEATURES = 6;

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

		return move;
	}

	public static double[] getValueOfWeight() {
		double[] w = {
			-4.702950520298681,
			2.0706001287265066,
			-1.2880690173841591,
			-3.470570078386892,
			-4.431071922401414,
			-1.9959973216039515
		};
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


/**
 * Strategy
 * 
 * Takes in the set of weights of all the available features. Calculates
 * required values with regards to the current set of weights, such as the total
 * score obtained.
 */
class Strategy {
	// Stores set of weights for all the available features
	private double[] weights;
	public static final double RANGE_BEGIN = -5;
	public static final double RANGE_END = 5;

	/**
	 * Constructor
	 * 
	 * @param w
	 *            : set of weights for all the available features
	 */
	public Strategy(double[] w) {
		weights = Arrays.copyOf(w, w.length);
	}

	public static Strategy createRandom() {
		double[] w = new double[FeaturesScore.NUM_FEATURES];
		Random rnd = new Random();
		for (int i = 0; i < w.length; i++) {
			w[i] = rnd.nextDouble() * (RANGE_END - RANGE_BEGIN) + RANGE_BEGIN;
		}
		return new Strategy(w);
	}

	/**
	 * Calculates the total score of the strategy which has a particular set of
	 * weights for all the available features.
	 * 
	 * @param state
	 * @return sigma(weight[i] * score of feature[i])
	 */
	public double calculate(Game state, int move) {
		FeaturesScore fs = new FeaturesScore(state, move);
		double totalScore = 0.0;

		for (int i = 0; i < fs.getNumberOfFeatures(); i++) {
			totalScore += weights[i] * fs.getScore(i);
		}

		return totalScore;
	}

	public double getWeight(int index) {
		return weights[index];
	}

	/**
	 * Crossover two parent strategies: this (first parent) and
	 * param (second parent) to produce two children strategies.
	 * @param s: second parent strategy
	 * @return array of strategy with 2 elements: two children strategies
	 */
	public Strategy[] crossover(Strategy s) {
		Random random = new Random();
		int seed = random.nextInt(1000) % (weights.length - 2) + 1;
		Strategy[] newStrategies = new Strategy[2];
		double[] newWeights1 = new double[weights.length], newWeights2 = new double[weights.length];

		for (int i = 0; i < seed; i++) {
			newWeights1[i] = this.getWeight(i);
			newWeights2[i] = s.getWeight(i);
		}
		for (int i = seed; i < weights.length; i++) {
			newWeights2[i] = this.getWeight(i);
			newWeights1[i] = s.getWeight(i);
		}

		newStrategies[0] = new Strategy(newWeights1);
		newStrategies[1] = new Strategy(newWeights2);

		return newStrategies;
	}

	/**
	 * Normalize the weights, so that sigma(weights) = 1.0
	 */
	public void normalize() {
		double sum = 0;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
		}
		for (int i = 0; i < weights.length; i++) {
			weights[i] = weights[i] / sum;
		}
	}

	// TODO: Implement
	// Mutate this strategy.
	public void mutate() {
		Random rnd = new Random();
		for (int i = 0; i < weights.length; i++) {
			if (rnd.nextDouble() < 0.006) {
				weights[i] = rnd.nextDouble() * (-5);
			}
		}
	}
}

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
		for (int i = 0; i < 1; i++) {
			Game s = new Game(i);
			while (!s.hasLost()) {
				s.makeMove(pickMove(s, s.legalMoves(), strategy));
				if (s.getRowsCleared() >= 1000) {
					break;
				}
			}
			System.out.println(s.getRowsCleared());
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
	private int pickMove(Game s, int[][] legalMoves, Strategy strategy) {
		int move = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		Game simulation;

		for (int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(s);
			double score = strategy.calculate(simulation, i);

			if (score > maxScore) {
				maxScore = score;
				move = i;
			}
		}
		return move;
	}

	public Integer call() {
		System.out.println("RUN");
		return getScore(strategy);
	}
}

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
		double maxScore = Double.NEGATIVE_INFINITY;
		Game simulation;

		for (int i = 0; i < legalMoves.length; i++) {
			simulation = new Game(s);
			double score = strategy.calculate(simulation, i);

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


class Game {
	public static final int COLS = 10;
	public static final int ROWS = 21;
	public static final int N_PIECES = 7;

	public boolean lost = false;

	public TLabel label;

	//current turn
	private int turn = 0;
	private int cleared = 0;

	//each square in the grid - int means empty - other values mean the turn it was placed
	private int[][] field = new int[ROWS][COLS];
	//top row+1 of each column
	//0 means empty
	private int[] top = new int[COLS];


	//number of next piece
	protected int nextPiece;

	private Random rndGenerator;


	//all legal moves - first index is piece type - then a list of 2-length arrays
	protected int[][][] legalMoves = new int[N_PIECES][][];

	//indices for legalMoves
	public static final int ORIENT = 0;
	public static final int SLOT = 1;

	//possible orientations for a given piece type
	protected static int[] pOrients = {1,2,4,4,4,2,2};

	//the next several arrays define the piece vocabulary in detail
	//width of the pieces [piece ID][orientation]
	protected static int[][] pWidth = {
		{2},
		{1,4},
		{2,3,2,3},
		{2,3,2,3},
		{2,3,2,3},
		{3,2},
		{3,2}
	};
	//height of the pieces [piece ID][orientation]
	private static int[][] pHeight = {
		{2},
		{4,1},
		{3,2,3,2},
		{3,2,3,2},
		{3,2,3,2},
		{2,3},
		{2,3}
	};
	private static int[][][] pBottom = {
		{{0,0}},
		{{0},{0,0,0,0}},
		{{0,0},{0,1,1},{2,0},{0,0,0}},
		{{0,0},{0,0,0},{0,2},{1,1,0}},
		{{0,1},{1,0,1},{1,0},{0,0,0}},
		{{0,0,1},{1,0}},
		{{1,0,0},{0,1}}
	};
	private static int[][][] pTop = {
		{{2,2}},
		{{4},{1,1,1,1}},
		{{3,1},{2,2,2},{3,3},{1,1,2}},
		{{1,3},{2,1,1},{3,3},{2,2,2}},
		{{3,2},{2,2,2},{2,3},{1,2,1}},
		{{1,2,2},{3,2}},
		{{2,2,1},{2,3}}
	};

	//initialize legalMoves
	private void initializeLegalMoves() {
		//for each piece type
		for(int i = 0; i < N_PIECES; i++) {
			//figure number of legal moves
			int n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//number of locations in this orientation
				n += COLS+1-pWidth[i][j];
			}
			//allocate space
			legalMoves[i] = new int[n][2];
			//for each orientation
			n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//for each slot
				for(int k = 0; k < COLS+1-pWidth[i][j];k++) {
					legalMoves[i][n][ORIENT] = j;
					legalMoves[i][n][SLOT] = k;
					n++;
				}
			}
		}
	}

	public int[][] getField() {
		return field;
	}

	public int[] getTop() {
		return top;
	}

	public static int[] getpOrients() {
		return pOrients;
	}

	public static int[][] getpWidth() {
		return pWidth;
	}

	public static int[][] getpHeight() {
		return pHeight;
	}

	public static int[][][] getpBottom() {
		return pBottom;
	}

	public static int[][][] getpTop() {
		return pTop;
	}


	public int getNextPiece() {
		return nextPiece;
	}

	public boolean hasLost() {
		return lost;
	}

	public int getRowsCleared() {
		return cleared;
	}

	public int getTurnNumber() {
		return turn;
	}



	//constructor
	public Game() {
		rndGenerator = new Random();
		nextPiece = randomPiece();
		initializeLegalMoves();
	}

	public Game(long seed) {
		rndGenerator = new Random(seed);
		nextPiece = randomPiece();
		initializeLegalMoves();
	}


	public Game(Game g) {
		this.rndGenerator = new Random();
		this.field = new int[ROWS][COLS];
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				this.field[i][j] = g.getField()[i][j];
			}
		}
		this.top = new int[COLS];
		for(int i = 0; i < COLS; i++) {
			this.top[i] = g.getTop()[i];
		}
		this.nextPiece = g.getNextPiece();
		this.turn = g.getTurnNumber();
		this.cleared = 0;
		initializeLegalMoves();
	}

	public Game(State g) {
		this.field = new int[ROWS][COLS];
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				this.field[i][j] = g.getField()[i][j];
			}
		}
		this.top = new int[COLS];
		for(int i = 0; i < COLS; i++) {
			this.top[i] = g.getTop()[i];
		}
		this.nextPiece = g.getNextPiece();
		this.turn = g.getTurnNumber();
		this.cleared = 0;
		initializeLegalMoves();
	}

	//random integer, returns 0-6
	private int randomPiece() {
		return rndGenerator.nextInt(7);
	}




	//gives legal moves for
	public int[][] legalMoves() {
		return legalMoves[nextPiece];
	}

	//make a move based on the move index - its order in the legalMoves list
	public void makeMove(int move) {
		makeMove(legalMoves[nextPiece][move]);
	}

	//make a move based on an array of orient and slot
	public void makeMove(int[] move) {
		makeMove(move[ORIENT],move[SLOT]);
	}

	//returns false if you lose - true otherwise
	public boolean makeMove(int orient, int slot) {
		turn++;
		//height if the first column makes contact
		int height = top[slot]-pBottom[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}

		//check if game ended
		if(height+pHeight[nextPiece][orient] >= ROWS) {
			lost = true;
			return false;
		}


		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {

			//from bottom to top of brick
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = turn;
			}
		}

		//adjust top
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}

		int rowsCleared = 0;

		//check for full rows - starting at the top
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for(int c = 0; c < COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
				cleared++;
				//for each column
				for(int c = 0; c < COLS; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}
			}
		}


		//pick a new piece
		nextPiece = randomPiece();



		return true;
	}

	public void draw() {
		label.clear();
		label.setPenRadius();
		//outline board
		label.line(0, 0, 0, ROWS+5);
		label.line(COLS, 0, COLS, ROWS+5);
		label.line(0, 0, COLS, 0);
		label.line(0, ROWS-1, COLS, ROWS-1);

		//show bricks

		for(int c = 0; c < COLS; c++) {
			for(int r = 0; r < top[c]; r++) {
				if(field[r][c] != 0) {
					drawBrick(c,r);
				}
			}
		}

		for(int i = 0; i < COLS; i++) {
			label.setPenColor(Color.red);
			label.line(i, top[i], i+1, top[i]);
			label.setPenColor();
		}

		label.show();


	}

	public static final Color brickCol = Color.gray;

	private void drawBrick(int c, int r) {
		label.filledRectangleLL(c, r, 1, 1, brickCol);
		label.rectangleLL(c, r, 1, 1);
	}

	public void drawNext(int slot, int orient) {
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
			for(int j = pBottom[nextPiece][orient][i]; j <pTop[nextPiece][orient][i]; j++) {
				drawBrick(i+slot, j+ROWS+1);
			}
		}
		label.show();
	}

	//visualization
	//clears the area where the next piece is shown (top)
	public void clearNext() {
		label.filledRectangleLL(0, ROWS+.9, COLS, 4.2, TLabel.DEFAULT_CLEAR_COLOR);
		label.line(0, 0, 0, ROWS+5);
		label.line(COLS, 0, COLS, ROWS+5);
	}




}


class GA extends Learning {
	Strategy[] population;
	private static int NUM_POPULATION = 16;
	private static int NUM_NEW_POPULATION = 32;

	/**
	 * Constructor.
	 */
	public GA() {
		population = new Strategy[NUM_POPULATION];
		if (!load()) {
			for (int i = 0; i < NUM_POPULATION; i++) {
				population[i] = Strategy.createRandom();
			}
		}
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
		for (int i = 0; i < iterations; i++) {
			System.out.println("Running iteration " + (i + 1));
			List<Strategy> newPopulation = new ArrayList<Strategy>();
			for (int j = 0; j < NUM_NEW_POPULATION/2; j++) {
				int idx1 = rnd.nextInt(NUM_POPULATION);
				int idx2 = rnd.nextInt(NUM_POPULATION);
				Strategy[] newStrats = population[idx1].crossover(population[idx2]);
				for (int k = 0; k < 2; k++) {
					newStrats[k].mutate();
					newPopulation.add(newStrats[k]);
				}
			}

			ExecutorService service = Executors.newFixedThreadPool(10);


			//Selection
			List<ScoredStrategy> scoredList = new ArrayList<ScoredStrategy>();
			List<Future<Integer> > scores = new ArrayList<Future<Integer> >();
			for (Strategy s : population) {
				ScoredStrategy scoredStrategy = new ScoredStrategy(s, 0);
				scores.add(service.submit(new Simulator(s)));
				scoredList.add(scoredStrategy);
			}
			for (Strategy s : newPopulation) {
				ScoredStrategy scoredStrategy = new ScoredStrategy(s, 0);
				scores.add(service.submit(new Simulator(s)));
				scoredList.add(scoredStrategy);
			}

			service.shutdown();
			try {
				service.awaitTermination(10000, TimeUnit.DAYS);
			for (int j = 0; j < scores.size(); j++) {
				scoredList.get(j).score = scores.get(j).get();
			}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Collections.sort(scoredList);
			for (int j = 0; j < NUM_POPULATION; j++) {
				population[j] = scoredList.get(j).s;
			}

			System.out.println("Best run:" + scoredList.get(0).score);
			store();
		}
	}

	public void store() {
/*		File gaScore = new File("gaScore.json");
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(gaScore));
			GsonBuilder gson = new GsonBuilder();
			String json = gson.create().toJson(population);
			fileWriter.write(json);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public boolean load() {
/*		File gaScore = new File("gaScore.json");
		if(!gaScore.exists()) {
			return false;
		}

		try {
			Scanner fileReader = new Scanner(gaScore);
			String json = "";

			while(fileReader.hasNextLine()) {
				json += fileReader.nextLine();
			}

			GsonBuilder gson = new GsonBuilder();
			JsonParser parse = new JsonParser();

			JsonArray jsonArray = parse.parse(json).getAsJsonArray();

			for (int i = 0; i < jsonArray.size(); i++) {
				Strategy output = gson.create().fromJson(jsonArray.get(i), Strategy.class);
				population[i] = output;
			}
			fileReader.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}*/
		return false;
	}

	public static void main(String[] args) {
		int iterations = 1;
		if (args.length > 0) {
			iterations = Integer.parseInt(args[0]);
		}
		GA ga = new GA();
		ga.run(iterations);
	}
}


class FeaturesScore {
	public static final int COLS = 10;
	public static final int ROWS = 20;

	public static final int NUM_FEATURES = 6;
	private double[] scores = new double[NUM_FEATURES];

	public FeaturesScore(Game s, int move) {
		Game g = new Game(s);
		g.makeMove(move);
		int[][] grids = g.getField();

		scores[0] = getLandingHeight(s, move);

		scores[1] = getRowsClearedAfterMove(g);

		scores[2] = getRowTransitions(grids);

		scores[3] = getColTransitions(grids);

		scores[4] = getNumberOfHoles(grids);

		scores[5] = getWellSums(grids);
	}

	public double getScore(int index) {
		return scores[index];
	}

	public int getNumberOfFeatures() {
		return NUM_FEATURES;
	}

	/**
	 * @param grids
	 * @return
	 */
	private int getWellSums(int[][] grids) {
		int count = 0;
		for (int j = 1; j < COLS - 1; j++) {
			for (int i = 0; i < ROWS; i++) {
				if (grids[i][j-1] != 0 && grids[i][j] == 0 && grids[i][j+1] != 0) {
					++count;
					for (int k = i - 1; k >= 0; --k) {
						if (grids[k][j] == 0) {
							++count;
						} else {
							break;
						}
					}
				}
			}
		}

		for (int i = 0; i < ROWS; i++) {
			if (grids[i][0] == 0 && grids[i][1] != 0) {
				++count;
				for (int k = i - 1; k >= 0; --k) {
					if (grids[k][0] == 0) {
						++count;
					} else {
						break;
					}
				}
			}
		}

		for (int i = 0; i < ROWS; i++) {
			if (grids[i][COLS - 1] == 0 && grids[i][COLS - 2] != 0) {
				++count;
				for (int k = i - 1; k >= 0; --k) {
					if (grids[k][COLS - 1] == 0) {
						++count;
					} else {
						break;
					}
				}
			}
		}

		return count;
	}

	/**
	 * @param grids
	 * @return
	 */
	private int getNumberOfHoles(int[][] grids) {
		int count = 0;
		for (int i = 0; i < COLS; i++) {
			boolean filled = false;
			for (int j = ROWS - 1; j >= 0; --j) {
				if (grids[j][i] != 0) {
					filled = true;
				}
				if (filled && grids[j][i] == 0) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * @param grids
	 * @return
	 */
	private int getColTransitions(int[][] grids) {
		int count = 0;
		for (int j = 0; j < COLS; j++) {
			if (grids[0][j] == 0) {
				count++;
			}
			for (int i = 0; i < ROWS - 1; i++) {
				if (grids[i][j] == 0 ^ grids[i+1][j] == 0) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * @return
	 *
	 */
	private int getRowTransitions(int[][] grids) {
		int count = 0;
		for (int i = 0; i < ROWS; i++) {
			if (grids[i][0] == 0) count++;
			if (grids[i][COLS - 1] == 0) count++;
			for (int j = 0; j < COLS - 1; j++) {
				if (grids[i][j] == 0 ^ grids[i][j+1] == 0) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * @param s
	 * @param move
	 * @return
	 */
	private int getRowsClearedAfterMove(Game g) {
		return g.getRowsCleared();
	}

	/**
	 * @param s
	 * @param move
	 */
	private double getLandingHeight(Game s, int move) {
		int[] pairMove = s.legalMoves()[move];

		int nextPiece = s.getNextPiece();
		int orient = pairMove[0];
		int slot = pairMove[1];
		int pieceHeight = Game.getpHeight()[nextPiece][orient];

		//height if the first column makes contact
		int height = s.getTop()[slot] - Game.getpBottom()[nextPiece][orient][0];

		//for each column beyond the first in the piece
		for(int c = 1; c < Game.getpWidth()[nextPiece][orient];c++) {
			height = Math.max(height,s.getTop()[slot+c] -
						Game.getpBottom()[nextPiece][orient][c]);
		}

		return (height + pieceHeight)/2.0;
	}

	//Check from above
	private int checkColHeight(int col, int[][] grids, int[] colHeights) {
		int count = 0;
		while (count <= 19 && grids[ROWS - count][col] == 0) {
			count++;
		}
		return ROWS - count;
	}

	//Check absolute difference between adjacent column heights. This method compares grid[column] with grid[column+1]
	private int checkDiffAdjColHeights(int col, int[] colHeights) {
		int result = colHeights[col] - colHeights[col + 1];
		return result > 0 ? result: -result; //return absolute value
	}

	private int checkMaxColHeight(int[] colHeights) {
		int max = colHeights[0];
		for (int i = 1; i < COLS; i++) {
			if (max < colHeights[i])
				max = colHeights[i];
		}
		return max;
	}

	//find the height of the column. Any 0's below the top of the column are considered holes
	private int checkNumWallHoles(int[][] grids, int[] colHeights) {
		int count = 0;
		for (int i = 0; i < COLS; i++) {
			int height = colHeights[i];
			for (int j = height - 1; j >= 0; j--) {
				if (grids[j][i] == 0)
					count++;
			}
		}
		return count;
	}
}
