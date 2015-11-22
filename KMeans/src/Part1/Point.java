package Part1;

public class Point {
	
	int id;
	float x,y;
	int cluster;

	public Point(int id, float x, float y) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return id +"";
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	
	public static double getDistance(Point p1, Point p2){		
		double distance = 0;		 
		float ycoord = Math.abs (p1.y - p2.y);
		float xcoord = Math.abs (p1.x- p2.x);    
		distance = Math.sqrt((ycoord)*(ycoord) +(xcoord)*(xcoord));		  		
		return distance;
	}
	
	
}
