package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;
import java.util.*;

import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.Pair;

public class State implements Serializable{
	private List<Unit> allUnits;//TODO - find a more efficient way of storing these (maybe HashMap of IDs to units?)s
	private Map<Integer,List<Unit>> unitsByAgent;
	private List<Resource> resources;
	private int turnNumber;
	private Map<Pair<Integer,Resource.Type>,Integer> currentResources;
	private int xextent;
	private int yextent;
	public State() {
		allUnits = new ArrayList<Unit>();
		unitsByAgent = new HashMap<Integer,List<Unit>>();
		resources = new ArrayList<Resource>();
		currentResources = new HashMap<Pair<Integer,Resource.Type>,Integer>();
	}
	public int getTurnNumber() { return turnNumber; }
	public List<Unit> getUnits() {
		return Collections.unmodifiableList(allUnits);
	}
	public Unit getUnit(int unitId) {
		for(Unit u : allUnits)
		{
			if(unitId == u.hashCode())
				return u;
		}
		return null;
	}
	public void setSize(int x, int y) {
		xextent = x;
		yextent = y;
	}
	public List<Unit> getUnits(int agent) {
		if(unitsByAgent.get(agent) == null)
			return null;
		return Collections.unmodifiableList(unitsByAgent.get(agent));
	}
	public void addUnit(Unit u) {
		int player = u.getPlayer();
		if(!allUnits.contains(u))
		{
			List<Unit> list = unitsByAgent.get(player);
			if(list == null)
				unitsByAgent.put(player, list = new ArrayList<Unit>());
			allUnits.add(u);
			list.add(u);
		}
	}
	public List<Resource> getResources() {
		return Collections.unmodifiableList(resources);
	}
	public Unit unitAt(int x, int y) {
		//This could probably be replaced by a 2D boolean array, but then you would need to ensure that things can't move without changing that array 
		for(Unit u : allUnits) {
			if(u.getxPosition() == x && u.getyPosition() == y)
				return u;
		}
		return null;
	}
	public boolean inBounds(int x, int y)
	{
		return x>=0 && y>=0 && x<xextent && y<yextent; 
	}
	public Resource resourceAt(int x, int y) {
		for(Resource r : resources)
		{
			if(r.getxPosition() == x && r.getyPosition() == y)
				return r;
		}
		return null;
	}
	/**
	 * Provide a referenceless state for 
	 * @return
	 */
	public ReferencelessState getStateData()
	{
		return new ReferencelessState();
	}
	
	/**
	 * Builder class that allows one-time access to a new state for construction purposes.
	 * @author Tim
	 *
	 */
	public static class StateBuilder {
		private State state;
		private boolean built;
		public StateBuilder() {
			state = new State();
			built = false;
		}
		public void addUnit(Unit u) {
			state.addUnit(u);
		}
		public void setSize(int x, int y) {
			state.setSize(x, y);
		}
		public void addResource(Resource r) {
			if(!state.resources.contains(r))
				state.resources.add(r);
		}
		public void setTurn(int turn) {
			state.turnNumber = turn;
		}
		public void setResourceAmount(int player, Resource.Type resource, int amount) {
			state.currentResources.put(new Pair<Integer,Resource.Type>(player,resource), amount);
		}
		/**
		 * Completes construction of the state and returns a reference to the state.
		 * Subsequent calls to this method will result in returning null.
		 * @return - the state being built if this is the first call for this object, null otherwise
		 */
		public State build() {
			if(!built)
			{
				built = true;
				return state;
			}
			else
				return null;
		}
		public boolean closed() {
			return built;
		}
	}
}
