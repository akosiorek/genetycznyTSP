package swarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route implements Comparable<Route> {

	private List<Integer> data = null;
	private float cost = 0.0f;
	
	// Constructors -----------------------------------------------------
	
	Route(final float cost, final List<Integer> data) {
		
		this.cost = cost;
		this.data = data;
	}
	
	Route(final List<Integer> data) {
		
		this.data = new ArrayList<Integer>();
		for(Integer city : data)
			this.data.add(new Integer(city));
		cost = Float.MAX_VALUE;
	}
	
	Route(final int routeSize) {
		
		data = new ArrayList<Integer>(routeSize);
		cost = Float.MAX_VALUE;
	}
	
	/**
	 * Copy constructor
	 * @param that	a Route to be copied from
	 */
	Route(final Route that) {
		
		this.cost = that.getCost();
		// Deep Copy
		data = new ArrayList<Integer>();
		for(Integer element : that.get()) {
			
			this.data.add(new Integer(element));
		}
	}
	
	
	// Getters and Setters ----------------------------------------------------------------------------
	public float getCost() {
		return cost;
	}
	
	public void setCost(final float cost) {
		this.cost = cost;
	}
	
	public List<Integer> get() {
		return data;
	}
	
	public int get(int i) {
		return i < data.size() ? data.get(i) : -1;
	}
	
	public void set(final ArrayList<Integer> route) {
		
		this.data = route;
		cost = Float.MAX_VALUE;
	}
	
	public boolean set(final int index, final int value) {
		
		if(index < data.size()) {
			data.set(index, value);
			return true;
		}
		return false;
	}
	
	public int size() {
		return data.size();
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	public boolean swap(final int i, final int j) {
		
		if( !(i < data.size() && j < data.size()))
			return false;
		
		Collections.swap(data, i, j);
		return true;					
	}
	
	public int indexOf(final int city) {
		
		return data.indexOf(city);
	}

	@Override
	public int compareTo(Route that) {
		
		if(this.cost > that.cost)
			return 1;
		if(this.cost < that.cost)
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Route: ");
		buffer.append(data.toString());
		buffer.append("Distance: " + cost);
		return buffer.toString();
	}
	
	
	
	
}
