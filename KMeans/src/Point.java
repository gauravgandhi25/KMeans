
public class Point {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cluster;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (cluster != other.cluster)
			return false;
		if (id != other.id)
			return false;
		return true;
	}

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
		return id + "";
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
