package swarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MultiTourPSO extends PSO{

	// Algoritm's const parameters
	private static final float SAME_TO_AND_FROM_COST = 1000;//Float.MAX_VALUE;
	private static final float NULL_EDGE_COST = 1000;
	private static final float MULTITOUR_FACTOR = 0.1f;
	
	// Operational parameters
	private int base = 0;
	private float goal = 0;
	
	private List<List<Integer>> bestRoutes;
	private float[] bestCosts;

	/**
	 * Default constructor
	 * @param particleCount a number of swarm's particles
	 */
	public MultiTourPSO(int particleCount) {
		super(particleCount);
	}
	
	public float getGoal() {
		return goal;
	}

	public void setGoal(float goal) {
		this.goal = goal;
	}
	
	@Override
	public float compute() {
		float cost = super.compute();
		bestRoutes = findRoutes(globalBest);
		bestCosts = getSubDistances(globalBest);
		return cost;
	}

	@Override
	protected List<Integer> prepareCities() {
		List<Integer> cities = new ArrayList<Integer>();
		cities.addAll(graph.getVertices());		
		return cities;		
	}

	public void setBase(int base) {
		this.base = base;
	}

	@Override
	protected void shuffleRoute(Route route) {
		Collections.shuffle(route.get());		
	}

	@Override
	protected void randomlyArrange(Route route) {
		int a = random.nextInt(route.size() + 1);
		int b = 0;		
		do {
			b = random.nextInt(route.size() + 1);
		} while (a == b);
		
		if(a == route.size())
			route.get().add(b, base);
		else if(b == route.size())
			if(Collections.frequency(route.get(), base) > 1)
				route.get().remove(route.indexOf(base));
			else
				route.get().add(a, base);
		else
			route.swap(a, b);		
	}

	@Override
	protected void updateTotalDistance(final Route route) {		
	  float distCost = 0;
	  float goalCost = 0;
	  float[] subDistances = getSubDistances(route);
	  
	  for(float distance : subDistances) {		  
		  distCost += distance;
		  //goalCost += (float)Math.expm1(Math.abs(goal - distance));
		  //goalCost += Math.abs(goal - distance);
		  goalCost += Math.pow(goal - distance, 2);
	  }
	  goalCost *= MULTITOUR_FACTOR; 	  
	  route.setCost(distCost + goalCost);
	}
	
	private float[] getSubDistances(final Route route) {		
			
		
		List<List<Integer>> routes = findRoutes(route);
		float[] distances = new float[routes.size()];
		for(int i = 0; i < distances.length; i++)
			distances[i] = getOneRouteCost(routes.get(i));		
		return distances;
	}
	
	private float getOneRouteCost(List<Integer> route) {
		float cost = 0;
		for(int i = 0; i < route.size() - 1; i ++) {
			cost += getDistance(route.get(i), route.get(i + 1));
		}
//		if(route.get(0) != route.get(route.size() - 1))
//			cost += getDistance(route.get(0), route.get(route.size() - 1));
		return cost;
	}
	
	private int[] findBases(final Route route) {		
		int bases[] = new int[Collections.frequency(route.get(), base)];
		int j = 0;
		for(int i = 0; i < route.size(); i++)			
			if(route.get(i) == base)
				bases[j++] = i;
		return bases;
	}
	
	private List<List<Integer>> findRoutes(final Route route) {
		if(Collections.frequency(route.get(), base) == 1)
			return Collections.singletonList(route.get());
		
		List<List<Integer>> routes = new ArrayList<List<Integer>>();
		int[] bases = findBases(route);
		for(int i = 0; i < bases.length - 1; i++)
			routes.add(route.get().subList(bases[i], bases[i + 1] + 1));
		List<Integer> lastRoute = new ArrayList<Integer>();
		lastRoute.addAll(route.get().subList(bases[bases.length - 1], route.size()));
		lastRoute.addAll(route.get().subList(0, bases[0] + 1));
		routes.add(lastRoute);
		return routes;		
	}

	@Override
	protected void crossOver(final Route dest, final Route source) {		
		int startRandomIndex = new Random().nextInt(cityCount);
		int	endRandomIndex = 0;
		do {
			endRandomIndex = new Random().nextInt(cityCount);
		} while( endRandomIndex == startRandomIndex );
		
		if(startRandomIndex > endRandomIndex) {
			int temp = startRandomIndex;
			startRandomIndex = endRandomIndex;
			endRandomIndex = temp;
		}
		
		List<Integer> passedGenes = source.get().subList(startRandomIndex, endRandomIndex);
		for(int i = passedGenes.size() - 1; i >= 0; i--)
			dest.get().add(startRandomIndex, passedGenes.get(i));
		
		for(int i = endRandomIndex - 1; i >= startRandomIndex; i--)
			dest.get().add(startRandomIndex, source.get(i));
			
		for(int i = dest.size() - 1; i >= endRandomIndex; i--)
			if(passedGenes.contains(dest.get(i)))
				if(dest.get(i) != base)
					dest.get().remove(i);
		
		for(int i = startRandomIndex - 1; i >= 0; i--)
			if(passedGenes.contains(dest.get(i)) && dest.get(i) != base)
				dest.get().remove(i);
	}

	@Override
	public List<List<Integer>> getBestRoute() {
		return bestRoutes;
	}

	@Override
	public float[] getBestCost() {
		return bestCosts;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();		
		buffer.append("Shortest Route: \n");
		float cost = 0;
		for(int i = 0; i < bestRoutes.size(); i++) {
			buffer.append("Route: " + bestRoutes.get(i) + " Cost: " + bestCosts[i] + "\n");
			cost += bestCosts[i];
		}
	    buffer.append("Overall cost: " + cost +"\n");	    
		return buffer.toString();
	}

	@Override
	protected float sameToandFromCost() {
		return SAME_TO_AND_FROM_COST;
	}

	@Override
	protected float nullEdgeCost() {
		return NULL_EDGE_COST;

	}
}
