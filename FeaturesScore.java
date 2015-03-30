
public class FeaturesScore {
	public static final int COLS = 10;
	public static final int ROWS = 19;

	//currently 21 features. To be increased in the future
	public static final int NUM_FEATURES = 21;
	private double[] scores = new double[NUM_FEATURES];
	private int[][] grids;

	private int[] colHeights = new int[COLS];

	public FeaturesScore(Game s, int move) {
		Game g = new Game(s);
		int[][] legalMoves = s.legalMoves();
		int[] pairMove = legalMoves[move];

		int nextPiece = g.getNextPiece();
		int orient = pairMove[0];
		int slot = pairMove[1];
		int pieceHeight = g.getpHeight()[nextPiece][orient];


		//height if the first column makes contact
		int height = g.getTop()[slot]-g.getpBottom()[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < g.getpWidth()[nextPiece][orient];c++) {
			height = Math.max(height,g.getTop()[slot+c]-g.getpBottom()[nextPiece][orient][c]);
		}

		scores[0] = (height + pieceHeight)/2.0;

		g.makeMove(move);
		grids = g.getField();

		scores[1] = g.getRowsCleared();

		scores[2] = 0;
		for (int i = 0; i < ROWS; i++) {
			if (grids[i][0] == 0) scores[2]++;
			if (grids[i][COLS - 1] == 0) scores[2]++;
			for (int j = 0; j < COLS - 1; j++) {
				if (grids[i][j] == 0 ^ grids[i][j+1] == 0) {
					scores[2]++;
				}
			}
		}

		scores[3] = 0;
		for (int j = 0; j < COLS; j++) {
			if (grids[0][j] == 0) {
				++scores[3];
			}
			for (int i = 0; i < ROWS - 1; i++) {
				if (grids[i][j] == 0 ^ grids[i+1][j] == 0) {
					scores[3]++;
				}
			}
		}

		scores[4] = 0;
		for (int i = 0; i < COLS; i++) {
			boolean exist = false;
			for (int j = ROWS - 1; j >= 0; --j) {
				if (exist && grids[j][i] == 0) {
					++scores[4];
				}
				if (grids[j][i] != 0) {
					exist = true;
				}
			}
		}

		scores[5] = 0;
		for (int j = 1; j < COLS - 1; j++) {
			for (int i = 0; i < ROWS; i++) {
				if (grids[i][j-1] != 0 && grids[i][j] == 0 && grids[i][j+1] != 0) {
					++scores[5];
					for (int k = i - 1; k >= 0; --k) {
						if (grids[k][j] == 0) {
							++scores[5];
						} else {
							break;
						}
					}
				}
			}
		}

		for (int i = 0; i < ROWS; i++) {
			if (grids[i][0] == 0 && grids[i][1] != 0) {
				++scores[5];
				for (int k = i - 1; k >= 0; --k) {
					if (grids[k][0] == 0) {
						++scores[5];
					} else {
						break;
					}
				}
			}
		}

		for (int i = 0; i < ROWS; i++) {
			if (grids[i][COLS - 1] == 0 && grids[i][COLS - 2] != 0) {
				++scores[5];
				for (int k = i - 1; k >= 0; --k) {
					if (grids[k][COLS - 1] == 0) {
						++scores[5];
					} else {
						break;
					}
				}
			}
		}
	}
	public double getScore(int index) {
		return scores[index];
	}

	public int getNumberOfFeatures() {
		return NUM_FEATURES;
	}

	//Check from above
	public int checkColHeight(int col) {
		int count = 0;
		while (count <= 19 && grids[ROWS - count][col] == 0) {
			count++;
		}
		return ROWS - count;
	}

	//Check absolute difference between adjacent column heights. This method compares grid[column] with grid[column+1]
	public int checkDiffAdjColHeights(int col) {
		int result = colHeights[col] - colHeights[col + 1];
		return result > 0 ? result: -result; //return absolute value
	}

	public int checkMaxColHeight() {
		int max = colHeights[0];
		for (int i = 1; i < COLS; i++) {
			if (max < colHeights[i])
				max = colHeights[i];
		}
		return max;
	}

	//find the height of the column. Any 0's below the top of the column are considered holes
	public int checkNumWallHoles() {
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
