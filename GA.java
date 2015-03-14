import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


public class GA extends Learning {
  Strategy[] population;
	/**
	 * Constructor
	 */
	public GA() {
		
	}
	
	/**
	 * 
	 * @param seed
	 */
	public void run(int seed) {
		
	}
	
	public void store() {
		File gaScore = new File("gaScore.json");
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(gaScore));
			GsonBuilder gson = new GsonBuilder();
			String json = gson.create().toJson(population);
			fileWriter.write(json);
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
	}
}
