
public class FeaturesScore {
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

	//getMaxHeight
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
	
	// ----------- Heights ------------- //
	
	/**
	 * getMeanHeight(g.getTop());
	 * getMinHeight(g.getTop());
	 * 
	 * These methods can be made faster
	 * by saving the previously computed max, mean, and min height to variables, 
	 * and passing it to the methods (slightly edit the parameters)
	 * 
	 * getMaxMeanHeight(g.getTop());
	 * getMeanMinHeight(g.getTop());
	 * getMaxHeightDiff(g.getTop());
	 * getVariationMaxHeight(s.getTop(), g.getTop());
	 * getVariationOfHeightDiff(s.getTop(), g.getTop());
	 * getVariationMeanHeight(s.getTop(), g.getTop());
	 */
	
	private double getMeanHeight(int[] colHeights) {
		int sum = 0;
		for(int i = 0; i < COLS; i++) {
			sum += colHeights[i];
		}
		return (double)sum / COLS;
	}
	
	private int getMinHeight(int[] colHeights) {
		int min = colHeights[0];
		for(int i = 1; i < COLS; i++) {
			if (min > colHeights[i]) {
				min = colHeights[i];
			}
		}
		return min;
	}
	
	private double getMaxMeanHeight(int[] colHeights) {
		return checkMaxColHeight(colHeights) - getMeanHeight(colHeights);
	}
	
	private double getMeanMinHeight(int[] colHeights) {
		return getMeanHeight(colHeights) - getMinHeight(colHeights);
	}
	
	private int getMaxHeightDiff(int[] colHeights) {
		return checkMaxColHeight(colHeights) - getMinHeight(colHeights);
	}
	 
	private int getVariationMaxHeight(int[] prevHeights, int[] curHeights) {
		int prevMax = checkMaxColHeight(prevHeights);
		int curMax = checkMaxColHeight(curHeights);
		
		return Math.abs(curMax - prevMax);
	}

	//variation of height differences
	private int getVariationOfHeightDiff(int[] prevHeights, int[] curHeights) {
		int prevDiff = getMaxHeightDiff(prevHeights);
		int curDiff = getMaxHeightDiff(curHeights);
		
		return Math.abs(curDiff - prevDiff);
	}

	private double getVariationMeanHeight(int[] prevHeights, int[] curHeights) {
		double prevMean = getMeanHeight(prevHeights);
		double curMean = getMeanHeight(curHeights);
		
		return Math.abs(curMean - prevMean);
	}
	
}
