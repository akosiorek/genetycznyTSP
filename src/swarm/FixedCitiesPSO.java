package swarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FixedCitiesPSO extends PSO {
	
	// Algoritm's const parameters
	private static final float SAME_TO_AND_FROM_COST = Float.MAX_VALUE;
	private static final float NULL_EDGE_COST = 1000;
	
	// Operational variables
	private ArrayList<Integer> fixedCities;
	private ArrayList<Integer> fixedPositions;
	
	/**
	 * Default constructor
	 * @param particleCount a number of swarm's particles
	 */
	public FixedCitiesPSO(int particleCount) {		
		super(particleCount);
		fixedCities = new ArrayList<Integer>();
		fixedPositions = new ArrayList<Integer>();
		random = new Random();
	}
	
	@Override
	protected List<Integer> prepareCities() {		
		List<Integer> cities = new ArrayList<Integer>();
		cities.addAll(graph.getVertices());		
		
		for(Integer city : fixedCities)
			if(cities.contains(city))
				cities.remove(city);		
		
		for(int i = 0; i < fixedCities.size(); i++) {
			cities.add(fixedPositions.get(i), fixedCities.get(i));
		}
		return cities;		
	}
	
	/**
	 * Computes a number of redundant city instances.
	 * An instance of a city is said to be redundant if there still remains at least one instance of this city
	 * when this instance is removed
	 * @return a number of redudant cities
	 */
	private int redundantCitiesCount() {		
		int redundant = 0;
		ArrayList<Integer> checked = new ArrayList<Integer>();
		for(int city : fixedCities) {
			
			if(checked.contains(city))
				continue;
			checked.add(city);
			redundant += Collections.frequency(fixedCities, city) - 1;
		}
		return redundant;
	}
	
	@Override
	protected void randomlyArrange(final Route route) {	
		int a = 0;
		int b = 0;			
		do {
			a = random.nextInt(cityCount);
			b = random.nextInt(cityCount);
		} while (a == b || fixedPositions.contains(a) || fixedPositions.contains(b));
		route.swap(a, b);
	}
	
	@Override
	protected void shuffleRoute(final Route route) {		
		for(int i = 0; i < cityCount; i++) {
			randomlyArrange(route);
		}
	}
	
	@Override
	protected void updateTotalDistance(final Route route) {		
	  float cost = 0;	  
	  for(int i = 0; i < cityCount - 1; i++) {
		  cost += getDistance(route.get(i), route.get(i + 1));
	  }
	  cost += getDistance(route.get(cityCount - 1), route.get(0));
	  route.setCost(cost);
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
		
		// Genes to be passed from source to dest
		ArrayList<Integer> passedGenes = new ArrayList<Integer>();
		for(int i = startRandomIndex; i < endRandomIndex; i++){
			passedGenes.add(dest.get(i));
		}
		
		// A temporary route used for the exchange process;
		List<Integer> extendedRoute = new ArrayList<Integer>();
		
		// Retain the genes with their index lower than the startRandomIndexif they are
		// not to be passed from the source
		for(int i = 0; i < startRandomIndex; i++)
			if(!passedGenes.contains(dest.get(i)))
			extendedRoute.add(dest.get(i));
		
		// Get all the genes passed from the source
		extendedRoute.addAll(passedGenes);		
		
		for(int i = startRandomIndex; i < cityCount; i++)
		if(!passedGenes.contains(dest.get(i)))
			extendedRoute.add(dest.get(i));	
		
//		Jeśli dane miasto w ścieżce występuje wiele razy, to powyższa procedura eliminuje wszystkie wystąpienia,
//		które znajdują się poza przedzialem [startRandomIndex, endRandomIndex). W takim wypadku można sprawdzić,
//		czy tam gdzie powinno być miasto o zadanym numerze to miasto jest. Jeśli nie, to należy je wstawić. 
//		Wykonanie operacji od lewej do prawej gwarantuje przesuwanie reszty struktury w prawo w miarę dodawania tak,
//		że elementu zawsze nie ma (a więc jest zawsze dodawany) pod właściwym indeksem.		
		for(int i = 0; i < fixedCities.size(); i++) {			
			int ind = fixedPositions.get(i);
			if( extendedRoute.get(ind) != fixedCities.get(i) )
				extendedRoute.add(ind, fixedCities.get(i));
		}
		
		for(int i = 0; i < cityCount; i++)					
			dest.set(i, extendedRoute.get(i));
	}
	
	
	public void setFixedCities(final ArrayList<Integer> fixedCities, final ArrayList<Integer> fixedCitiesPositions) {		
		this.fixedCities = fixedCities;
		this.fixedPositions = fixedCitiesPositions;
		cityCount = redundantCitiesCount() + graph.getVertexCount();
	}
	
	public void addFixedCity(int city, int position) {					
			fixedCities.add(city);
			fixedPositions.add(position);
			
			
//			Jeśli w fixedCity powtarzają się miasta o tym samym numerze, to trzeba zwiększyć cityCount tak, żeby
//			zmieściły się wszystkie miasta oraz powielone instancje określonych miast			
			cityCount = redundantCitiesCount() + graph.getVertexCount();
	}
	
	@Override
	public String toString() {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Shortest Route: ");
		buffer.append(globalBest.get());
	    buffer.append(" Distance: " + globalBest.getCost());
		return buffer.toString();
	}

	@Override
	public List<List<Integer>> getBestRoute() {
		
		return Collections.singletonList(globalBest.get());
	}

	@Override
	public float[] getBestCost() {		
		float[] tmp = new float[1];
		tmp[0] = globalBest.getCost();
		return tmp;
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