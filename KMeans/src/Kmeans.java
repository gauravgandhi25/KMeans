import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class Kmeans {

	public final static int K = 7;
	public static String inputFileName = "test_data.txt";
	public static String outputFileName = "output.txt";

	public static void main(String[] args) throws IOException {

		List<Point> points = readFile(inputFileName);
		HashMap<Integer, List<Point>> clusterPoints = new HashMap<Integer, List<Point>>();
		List<Point> centroids = new ArrayList<Point>();

		// Initialization
		initialize(points, centroids, clusterPoints);
		updateCentroids(centroids, clusterPoints);
		int count = 1;
		boolean isConverged;					
		do{
			isConverged = updateCluster(centroids, clusterPoints);
			count++;
		}
		while(count <= 25 && !isConverged);
				
		//printClusters(clusterPoints);
		System.out.println(count-1);
		
		double SSE = calculateSSE(centroids,clusterPoints);
		System.out.println(SSE);	
	}

	private static double calculateSSE(List<Point> centroids, HashMap<Integer, List<Point>> clusterPoints) {		
		double SSE = 0;
		for(int i=0;i<K;i++){
			double temp = 0;
			for(Point p :clusterPoints.get(i)){
				
				double distance = Point.getDistance(p, centroids.get(i));
				distance = distance * distance;
				temp +=distance;
			}
			SSE +=temp;
		}				
		return SSE;
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

	private static void initialize(List<Point> points,
			List<Point> centroids,
			HashMap<Integer, List<Point>> clusterPoints) {

		// Set Random K Points as Centroids

		for (int i = 0; i < K; i++) {
			List<Point> cluster = new LinkedList<Point>();
			int random = randomInt(0, points.size() - 1);
			Point p = points.remove(random);
			p.setCluster(i);
			centroids.add(p);
			cluster.add(p);
			clusterPoints.put(i, cluster);
		}
		// form the initial cluster
		for (Point p : points) {
			int nearestCentroid = getNearestCentroid(p, centroids);
			p.setCluster(nearestCentroid);
			clusterPoints.get(nearestCentroid).add(p);
		}
	}

	private static void updateCentroids(List<Point> centroids,
			HashMap<Integer, List<Point>> clusterPoints) {
		for (int i = 0; i < K; i++) {
			centroids.set(i, computerCentroid(clusterPoints.get(i)));
		}
	}

	private static Point computerCentroid(List<Point> points) {
		float x = 0, y = 0;
		int count = 0;
		for (Point p : points) {
			x += p.x;
			y += p.y;
			count++;
		}
		x = x / count;
		y = y / count;
		return new Point(-1, x, y);
	}

	private static void printClusters(
			HashMap<Integer, List<Point>> clusterPoints) {
		for (Entry<Integer, List<Point>> entry : clusterPoints.entrySet()) {
			System.out.print(entry.getKey());
			System.out.print(" : ");
			System.out.print(entry.getValue());
			System.out.println("\n");
		}
	}

	public static int getNearestCentroid(Point p, List<Point> centroids) {
		int nearest = 0;
		double nearestDistance = Double.MAX_VALUE;
		for (int i = 0; i < K; i++) {
			double temp = Point.getDistance(p, centroids.get(i));
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

	private static List<Point> readFile(String inputFileName)
			throws IOException {

		BufferedReader reader = new BufferedReader(
				new FileReader(inputFileName));
		reader.readLine();
		String line;

		List<Point> points = new LinkedList<Point>();
		while ((line = reader.readLine()) != null) {
			String[] array = line.split("\t");
			int id = Integer.parseInt(array[0]);
			float x = Float.parseFloat(array[1]);
			float y = Float.parseFloat(array[2]);
			Point entry = new Point(id, x, y);
			points.add(entry);
		}
		return points;
	}

}
