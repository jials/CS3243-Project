
public class FeaturesScore {
	public static final int COLS = 10;
	public static final int ROWS = 19;

	//currently 21 features. To be increased in the future
	public static final int NUM_FEATURES = 21;
	private double[] scores = new double[NUM_FEATURES];
	private int[][] grids;

	private int[] colHeights = new int[COLS];

	public FeaturesScore(Game s) {
		grids = s.getField();

		//First 10 are colHeight
		for (int i = 0; i < COLS; i++) {
			scores[i] = colHeights[i] = checkColHeight(i);
		}

		//The next 9 are adjHeight
		for (int i = 10; i < 10 + COLS - 1; i++) {
			scores[i] = checkDiffAdjColHeights(i - 10);
		}

		//max col height
		scores[19] = checkMaxColHeight();

		scores[20] = checkNumWallHoles();
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
