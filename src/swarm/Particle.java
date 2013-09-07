package swarm;

import java.util.List;

 public class Particle extends Route {
	 
    private Route pBest;

    // Constructors ------------------------------------------------------
    
    public Particle(final List<Integer> route) {
    	super(route);
    	pBest = new Route(route);
    }
    
    /**
     * Copy constructor
     * @param that a Particle to be copied from
     */
    public Particle(final Particle that) {
    	
    	super(that);
    	this.pBest = new Route(that.getpBest());
    }
    
    // -------------------------------------------------------
    
    public Route getpBest() {
		return pBest;
	}

	public void setpBest(Route pBest) {
		this.pBest = pBest;
	}

	public Particle(final int cityCount) {
    	
    	super(cityCount);
    }   
}