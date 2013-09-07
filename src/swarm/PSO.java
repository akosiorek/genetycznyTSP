package swarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import solver.Edge;
import edu.uci.ics.jung.graph.Graph;

abstract class PSO {	
	
	// Algorithm's constant parameters
	private static final int MAX_EPOCHS = 1000;
	private static final int SA_ITERATIONS = 100;
	private static final float ALFA = 0.99f;
	private static final int INITIAL_TEMP = 10000;
	private static final int MIN_TEMP = 1;
	
	// Algorithm's changable parameters
	protected int particleCount;
	
	// Helper variables
	protected int cityCount;
	
	// Operational variables
	protected ArrayList<Particle> particles = new ArrayList<Particle>();
	protected Route globalBest = null;
	protected Graph<Integer, Edge> graph = null;
	protected float temperature = 0;
	protected Random random = new Random();
	
	
	/**
	 * Default constructor
	 * @param particleCount number of the swarm's particles
	 */
	public PSO(int particleCount) {
		
		this.particleCount = particleCount;
	}
	
	/**
	 * Performs the optimization process 
	 * @return a cost function value
	 */
	public float compute() {
	
		int epoch = 0;
		initialize();

		while(epoch < MAX_EPOCHS) {
			
			epoch++;
			for(Particle particle : particles) {
				
				simulatedAnnealing(particle);
			}
			updateGlobalBest();
			for(Particle particle : particles) {
				evolveParticle(particle);
			}
			nextTemperature();
		}
		return globalBest.getCost();
	}
	
	/**
	 * Initializes the algorithm
	 */
	private void initialize() {
		
//		Without clearing new particles are added of every run of the algorithm,
//		thus resulting in an improvement of results overtime. We aim at improving 
//		the results in each algorithms execution.
		particles.clear();
	
		temperature = INITIAL_TEMP + MIN_TEMP;
		globalBest = new Route(cityCount);	
		
		List<Integer> cities = prepareCities();
		for(int i = 0; i < particleCount; i++) {
			
		    Particle newParticle = new Particle(cities);
		    shuffleRoute(newParticle);
		    updateTotalDistance(newParticle);
		    particles.add(newParticle);
	  }
	  return;
	}
	
	/**
	 * Prepares an array of cities
	 * @return an int array of cities
	 */
	protected abstract List<Integer> prepareCities();
	
	/**
	 * Changes the route into a random permutation of cities
	 * @param route	a route to be altered
	 */
	protected abstract void shuffleRoute(final Route route);
	
	/**
	 * Randomize the specified route by a small degree
	 * @param route	a route to be randomized
	 */
	protected abstract void randomlyArrange(final Route route);

	/**
	 * Computes a value of route's cost
	 * @param route
	 */
	protected abstract void updateTotalDistance(final Route route);

	/**
	 * Computes a cost of an edge between two cities
	 * @param from a beginning of an edge
	 * @param to an end of an edge
	 * @return a cost value
	 */
	protected float getDistance(final int from, final int to) {		
		if( from == to ) 
			return sameToandFromCost();
		Edge edge = graph.findEdge(from, to);
		if( edge == null ) 
			return nullEdgeCost();
		return edge.weight();
	}	
	
	/**
	 * Performs the Simulated Annealing algorithm on the particle. A search for a local optimum in the particle's
	 * neighbourhood is carried out. The particle's pBest is updated if a better route is found.
	 * @param particle	a particle for which a local optimum is to be found
	 */
	private void simulatedAnnealing(final Particle particle) {
		Route localBest = new Route(particle.getpBest());
		for(int i = 0; i < SA_ITERATIONS; i++) {
			
			Route temp = new Route(localBest);
			randomlyArrange(temp);
			updateTotalDistance(temp);
			if(saProbability(temp, localBest) >= random.nextFloat()){
				localBest = temp;
			}
		}
		if(localBest.compareTo(particle.getpBest()) == -1){
			particle.setpBest(new Route(localBest));
		}
	}
	
	/**
	 * Computes a probability value for the Simulated Annealing algorithm
	 * @param newRoute a new route found by the SA
	 * @param oldRoute a route that has been accepted previously
	 * @return a probability value
	 */
	private float saProbability(Route newRoute, Route oldRoute) {
		
		float delta = newRoute.getCost() - oldRoute.getCost();
		if(delta < 0){
			return 1;
		}		
		return (float)Math.exp(-delta/temperature);		
	}
	
	/**
	 * Updates the global best value
	 */
	private void updateGlobalBest() {
		
		Route minParticle = particles.get(0).getpBest();
		for(Particle particle : particles){
			if(particle.getpBest().compareTo(minParticle) == -1){
				minParticle = particle.getpBest();
			}
		}
		globalBest = minParticle;
	}	
	
	/**
	 * Performs a crossover of the two specified particles
	 * @param dest A particle to be the result of the crossover
	 * @param source A particle to crossed-over with the dest
	 */
	protected abstract void crossOver(final Route dest, final Route source);
	
	/**
	 * Performs a single evolution of the particle
	 * @param particle	a particle to be evolved
	 */
	private void evolveParticle(final Particle particle) {
		
		crossOver(particle, particle.getpBest());
		crossOver(particle, globalBest);
		updateTotalDistance(particle);
	}
	
	/**
	 * Computes the temperature's value in the following iteration
	 */
	private void nextTemperature() {
		
		temperature = (temperature - MIN_TEMP) * ALFA + MIN_TEMP;
	}	
	
	/**
	 * Sets graph on which all the operations are performed.
	 * @param graph	a map of the cities
	 */
	public void setGraph(final Graph<Integer, Edge> graph) {
		this.graph = graph;
		cityCount = graph.getVertexCount();
	}
	
	/**
	 * Gets best solution
	 * @return a best solution
	 */
	public abstract List<List<Integer>> getBestRoute();
	
	/**
	 * Gets best cost
	 * @return a best cost value
	 */
	public abstract float[] getBestCost();
	
	@Override
	public abstract String toString();
	
	/**
	 * Cost of an edge between the same city i.e from city 1 to city 1
	 * @return cost of an edge
	 */
	protected abstract float sameToandFromCost();
	
	/**
	 * Cost of a null edge
	 * @return cost of a null edge
	 */
	protected abstract float nullEdgeCost();
}