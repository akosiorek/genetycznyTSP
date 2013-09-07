package solver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.VoltageClusterer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class GraphOrganizer{
	
	private Graph<Integer, Edge> allCities;
	
	public Graph<Integer, Edge> getAllCities() {
		return allCities;
	}

	public GraphOrganizer(String cityMatrixFile) throws NumberFormatException, IOException{
		
		allCities = readGraphFromFile(cityMatrixFile);		
	}
	
	private Graph<Integer, Edge> readGraphFromFile(String filepath) throws NumberFormatException, IOException {
		
		Graph<Integer, Edge> fileCities = new DirectedSparseGraph<Integer, Edge>();
		FileInputStream is = new FileInputStream(filepath);
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));
		ArrayList<float[]> lines = new ArrayList<float[]>();		
		
		String line = null;		
		while( (line = br.readLine()) != null ) {
			String[] sValues = line.split(" ");
			if( sValues.length < 2) {
				continue;
			}
			float[] fValues = new float[sValues.length];
			for( int i = 0; i < sValues.length; i++ ){
				
				fValues[i] = Float.parseFloat(sValues[i]);
			}
			lines.add(fValues);
		}
		br.close();

		for( int j = 0; j < lines.size(); j++ ) {
			for( int i = 0; i < lines.size(); i++ ){
				
				float value = lines.get(j)[i];
				if( i == j || value == 0) {
					continue;
				}
				fileCities.addEdge(new Edge(value, j, i), j, i);
			}
		}
		return fileCities;
	}
	
	public ArrayList<Graph<Integer,Edge>> cluster(int clusterAmount) {
		
		// KMeans based
		VoltageClusterer<Integer, Edge> vClusterer = new VoltageClusterer<Integer, Edge>(this.allCities, 800);
		
		Collection<Set<Integer>> clusters = vClusterer.cluster(clusterAmount);
		ArrayList<Graph<Integer,Edge>> subGraphs = new ArrayList<Graph<Integer,Edge>>();
		
		System.out.println("Subgraphs count = " + clusters.size());
		for( Set<Integer> cluster : clusters) {
			System.out.println("Subgraph = " + cluster.toString());
			subGraphs.add(getSubGraph(cluster));
		}
		return subGraphs;
	}
	
	private Graph<Integer,Edge> getSubGraph(Set<Integer> cluster){
		
		Graph<Integer,Edge> subGraph = new DirectedSparseGraph<Integer,Edge>();
		/* take connected vertices from cluster and create for them and allCities new graph */
		
		Object[] vertex = cluster.toArray();
		
		for(int i = 0; i < cluster.size(); i++ ){
			for(int j = 0; j < cluster.size(); j++){
				if(i == j) {
					continue;
				}
				Edge edge = allCities.findEdge((Integer)vertex[i], (Integer)vertex[j]);
				if( edge != null) {
					
					subGraph.addEdge( edge, edge.from(), edge.to());
				}
			}
		}
		
		return subGraph;
	}
	
	public ArrayList<Edge> getVertex(int i) {
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.addAll(allCities.getInEdges(i));
		edges.addAll(allCities.getOutEdges(i));
		return edges;
	}
	
	public ArrayList<Graph<Integer,Edge>> addStartingPoint(ArrayList<Graph<Integer, Edge>> graphs, int i) {
		
		ArrayList<Edge> edges = getVertex(i);
		for( Graph<Integer, Edge> graph : graphs) {			
			if( graph.containsVertex(i) )
				continue;
			
			graph.addVertex(i);
			for( Edge edge : edges) {				
				if( !graph.containsVertex(edge.to()) || !graph.containsVertex(edge.from() ))					
					continue;
				
				graph.addEdge(edge, edge.from(), edge.to());
			}
		}
		return graphs;
	}
	
	@Override
	public String toString() {
		
		return allCities.toString();
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		GraphOrganizer graphOrganizer = new GraphOrganizer("/home/adam/workspace/TSP/bin/cities.txt");
		ArrayList<Graph<Integer,Edge>> graphs = graphOrganizer.cluster(5);
		graphs = graphOrganizer.addStartingPoint(graphs, 7);
		for(Graph<Integer,Edge> graph  : graphs){
			System.out.println();
			System.out.println(graph);
		}
		System.out.println();
		System.out.println(graphOrganizer);		
	}
	
}