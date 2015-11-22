import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TweetsKMeans {

	public static int K = 25;
	public static String inputFileName = "Tweets.json";
	public static String initialSeedsFile = "InitialSeeds.txt";	
	public static String outputFileName = "tweets-k-means-output.txt";

	public static void main(String[] args) throws IOException {
		
		K = Integer.parseInt(args[0]);
		initialSeedsFile = args[1];
		inputFileName = args[2];
		outputFileName = args[3];
		
		HashSet<String> initialSeeds = readInitialSeeds(initialSeedsFile);
		List<Tweet> centroids = new ArrayList<Tweet>();		
		List<Tweet> tweets = readFile(inputFileName,centroids,initialSeeds);		
		HashMap<Integer, List<Tweet>> clusterPoints = new HashMap<Integer, List<Tweet>>();
		
		// Initialization
		
		initialize(tweets, centroids, clusterPoints);						
		updateCentroids(centroids, clusterPoints);
		
		int count = 1;
		boolean isConverged;					
		do{
			isConverged = updateCluster(centroids, clusterPoints);
			count++;
		}
		while(count <= 25 && !isConverged);
					
		double SSE = calculateSSE(centroids,clusterPoints);
		System.out.println("SSE: " + SSE);		
		writeToFile(outputFileName, clusterPoints);
		System.out.println("Output written to "+outputFileName);
	}
	
	private static double calculateSSE(List<Tweet> centroids, HashMap<Integer, List<Tweet>> clusterPoints) {		
		double SSE = 0;
		for(int i=0;i<K;i++){
			double temp = 0;
			for(Tweet tweet :clusterPoints.get(i)){
				
				float distance = Tweet.getDistance(tweet, centroids.get(i));
				distance = distance * distance;
				temp +=distance;
			}
			SSE +=temp;
		}				
		return SSE;
	}

	private static HashSet<String> readInitialSeeds(String initialSeedsFile) throws IOException {		
		HashSet<String> initialSeeds = new HashSet<String>();		
		BufferedReader reader = new BufferedReader(new FileReader(initialSeedsFile));
		
		while(true){
			String line = reader.readLine();
			if(line == null){
				break;
			}
			initialSeeds.add(line.replace(",", ""));
		}
		return initialSeeds;
	}

	private static void writeToFile(String outputFileName, HashMap<Integer, List<Tweet>> clusterPoints) throws IOException {
		
		File outputFile = new File(outputFileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		for(int i = 0; i < clusterPoints.size();i++){
			writer.write(i+1 + "\t");
			for(Tweet tweet : clusterPoints.get(i)){
				writer.write(tweet.id + ", ");				
			}
			writer.write("\n");
		}	
		writer.close();
	}

	private static boolean updateCluster(List<Tweet> centroids,
			HashMap<Integer, List<Tweet>> clusterPoints) {
		
		boolean isConverged = true;
		int pointsChangedCount = 0;		
		
		for (int i = 0; i < K; i++) {			
			
			List<Tweet> tweets = new ArrayList<Tweet>(clusterPoints.get(i));
			pointsChangedCount = 0;
			for (Tweet tweet : tweets) {
				int nearestCentroid = getNearestCentroid(tweet, centroids);				
				if (tweet.cluster != nearestCentroid) {
					tweet.setCluster(nearestCentroid);
					clusterPoints.get(i).remove(tweets.indexOf(tweet)-pointsChangedCount);					
					clusterPoints.get(nearestCentroid).add(tweet);
					isConverged = false;
					pointsChangedCount++;
				}
			}
		}
		updateCentroids(centroids, clusterPoints);
		return isConverged;
	}

	private static void initialize(List<Tweet> tweets,
			List<Tweet> centroids,
			HashMap<Integer, List<Tweet>> clusterPoints) {
				
		// Set Random K Points as Centroids
		for (int i = 0; i < K; i++) {
			List<Tweet> cluster = new LinkedList<Tweet>();			
			Tweet t = centroids.get(i);
			t.setCluster(i);			
			cluster.add(t);
			clusterPoints.put(i, cluster);
		}
		// form the initial cluster
		for (Tweet tweet : tweets) {
			int nearestCentroid = getNearestCentroid(tweet, centroids);
			tweet.setCluster(nearestCentroid);
			clusterPoints.get(nearestCentroid).add(tweet);
		}
	}

	private static void updateCentroids(List<Tweet> centroids,
			HashMap<Integer, List<Tweet>> clusterPoints) {
		for (int i = 0; i < K; i++) {
			centroids.set(i, computerCentroid(clusterPoints.get(i)));
		}
	}

	private static Tweet computerCentroid(List<Tweet> list) {
		
		float minAverageDist = Float.MAX_VALUE;
		Tweet minAverageDistTweet = null;
		
		for(Tweet tweet1 : list){
			float temp = 0;
			for(Tweet tweet2 : list){
				temp += Tweet.getDistance(tweet1, tweet2);
			}
			
			if(temp < minAverageDist){
				minAverageDist = temp;
				minAverageDistTweet = tweet1;
			}
		}		
		return minAverageDistTweet;		
	}
	
	public static int getNearestCentroid(Tweet tweet, List<Tweet> centroids) {
		int nearest = 0;
		float nearestDistance = Float.MAX_VALUE;
		for (int i = 0; i < K; i++) {
				
			float temp = Tweet.getDistance(tweet, centroids.get(i));
			if (temp < nearestDistance) {
				nearest = i;
				nearestDistance = temp;
			}
		}
		return nearest;
	}

	public static int randomInt(int min, int max) {
		Random rand = new Random();
		// nextInt is exclusive of the top value, so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	private static List<Tweet> readFile(String inputFileName, List<Tweet> centroids, HashSet<String> initialSeeds)
			throws IOException {

		List<Tweet> tweets = new LinkedList<Tweet>();
		JsonParser parser = new JsonParser();
		BufferedReader reader = new BufferedReader(new FileReader(inputFileName));									
		
		while(true){
			String line = reader.readLine();
			if(line == null)
				break;
			JsonObject object = parser.parse(line).getAsJsonObject();
			Tweet tweet = new Tweet(object.get("id").toString(), object.get("text").toString().replace("\"", ""));
			if(initialSeeds.contains(object.get("id").toString())){
					centroids.add(tweet);
					continue;
			}
			tweets.add(tweet);			
		}	
		return tweets;
	}
}