package solver;

public class Edge {

	static int edgeCount;
	private int id;
	private int from;
	private int to;
	private float weight;
	
	public Edge(float weight, int from, int to) {
		
		id = edgeCount++;
		this.weight = weight;
		this.to = to;
		this.from = from;
	}
	
	public int getId() {
		return id;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}

	public float weight() {
		return weight;
	}	
	
	public String toString() {
		
		String string = "E" + id + " W = " + weight + "; ";
		return string;
	}
}
