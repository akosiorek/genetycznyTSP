package solver;

import java.io.IOException;

import swarm.FixedCitiesPSO;
import swarm.MultiTourPSO;
import edu.uci.ics.jung.graph.Graph;

public class TSPSolver {	
	
	private final static int SWARM_PARTICLES = 100;
	
//	private final static String CITIES_FILE = "/home/adam/workspace/TSP/bin/cities.txt";
	
	public static void main(String[] args) throws NumberFormatException, IOException {		
		
		GraphOrganizer graphOrganizer = new GraphOrganizer(args[0]);
		
		Graph<Integer, Edge> allCities = graphOrganizer.getAllCities();
				
//		FixedCitiesPSO fixedPso = new FixedCitiesPSO(SWARM_PARTICLES);
//		fixedPso.setGraph(allCities);
//		fixedPso.addFixedCity(1, 0);
//		fixedPso.addFixedCity(2,  10);
//		fixedPso.addFixedCity(3,  18);
//		for(int i = 0; i < 10; i++) {
//			fixedPso.compute();
//			System.out.println(fixedPso);
//		}
		
		MultiTourPSO pso = new MultiTourPSO(SWARM_PARTICLES);
		pso.setGraph(allCities);
		pso.setGoal(20);
		pso.setBase(1);
		
		System.out.println("PSO: ");		
		for(int i = 0; i < 100; i++) {
			pso.compute();
			System.out.println(pso); 
		}    	  	
	}
}
