import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TweetsKMeans {

	public final static int K = 11;
	public static String inputFileName = "Tweets.json";
	public static String initialSeedsFile = "InitialSeeds.txt";	
	public static String outputFileName = "output.txt";

	public static void main(String[] args) throws IOException {

		List<Tweet> tweets = readFile(inputFileName);
		
		
		HashMap<Integer, List<Tweet>> clusterPoints = new HashMap<Integer, List<Tweet>>();
		List<Tweet> centroids = new ArrayList<Tweet>();

		// Initialization
		initialize(tweets, centroids, clusterPoints);
		updateCentroids(centroids, clusterPoints);
		/*
		int count = 1;
		boolean isConverged;					
		do{
			isConverged = updateCluster(centroids, clusterPoints);
			count++;
		}
		while(count <= 25 && !isConverged);
				
		System.out.println("Converged in " + (count-1) + " steps");
		
		double SSE = calculateSSE(centroids,clusterPoints);
		System.out.println("SSE: " + SSE);
		writeToFile(outputFileName, clusterPoints);
		*/
	}

	private static void writeToFile(String outputFileName, HashMap<Integer, List<Point>> clusterPoints) throws IOException {
		
		File outputFile = new File(outputFileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		for(int i = 0; i < clusterPoints.size();i++){
			writer.write(i+1 + "\t");
			for(Point p : clusterPoints.get(i)){
				writer.write(p.id + ",");				
			}
			writer.write("\n");
		}	
		writer.close();
	}

	private static boolean updateCluster(List<Point> centroids,
			HashMap<Integer, List<Point>> clusterPoints) {
		
		boolean isConverged = true;
		int pointsChangedCount = 0;		
		for (int i = 0; i < K; i++) {			
			List<Point> points = new ArrayList<Point>(clusterPoints.get(i));
			pointsChangedCount = 0;
			for (Point p : points) {
				int nearestCentroid = getNearestCentroid(p, centroids);
				if (p.cluster != nearestCentroid) {
					p.setCluster(nearestCentroid);
					clusterPoints.get(i).remove(points.indexOf(p)-pointsChangedCount);					
					clusterPoints.get(nearestCentroid).add(p);
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
			int random = randomInt(0, tweets.size() - 1);
			Tweet t = tweets.remove(random);
			t.setCluster(i);
			centroids.add(t);
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
		
		int minAverageDist = 0;
		double minAverageDistTweet = Double.MAX_VALUE;
		
		for(Tweet tweet1 : list){
			
			for(Tweet tweet2 : list){
				
			}
		}
		
		
		return new Point(-1, x, y);
	}
	
	public static int getNearestCentroid(Tweet tweet, List<Tweet> centroids) {
		int nearest = 0;
		double nearestDistance = Double.MAX_VALUE;
		for (int i = 0; i < K; i++) {
			double temp = Tweet.getDistance(tweet, centroids.get(i));
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

	private static List<Tweet> readFile(String inputFileName)
			throws IOException {

		List<Tweet> tweets = new LinkedList<Tweet>();
		JsonParser parser = new JsonParser();
		BufferedReader reader = new BufferedReader(new FileReader(inputFileName));									
		
		while(true){
			String line = reader.readLine();
			if(line == null)
				break;
			JsonObject object = parser.parse(line).getAsJsonObject();
			Tweet tweet = new Tweet(object.get("id").toString(), object.get("text").toString());
			tweets.add(tweet);			
		}	
		return tweets;
	}
}
