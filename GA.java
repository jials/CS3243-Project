import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class GA extends Learning {
	Strategy[] population;
	private static int NUM_POPULATION = 200;
	private static int NUM_NEW_POPULATION = 400;

	/**
	 * Constructor.
	 */
	public GA() {
		population = new Strategy[NUM_POPULATION];
		if (!load()) {
			Random rnd = new Random();
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
		File gaScore = new File("gaScore.json");
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(gaScore));
			GsonBuilder gson = new GsonBuilder();
			String json = gson.create().toJson(population);
			fileWriter.write(json);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean load() {
		File gaScore = new File("gaScore.json");
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
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
