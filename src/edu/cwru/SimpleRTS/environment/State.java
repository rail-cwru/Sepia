package edu.cwru.SimpleRTS.environment;

import java.util.*;

import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.Pair;

public class State {
	private List<Unit> allUnits;
	private Map<Integer,List<Unit>> unitsByAgent;
	private List<Resource> resources;
	private int turnNumber;
	private Map<Pair<Integer,Resource.Type>,Integer> currentResources;
	
	public State() {
		allUnits = new ArrayList<Unit>();
		unitsByAgent = new HashMap<Integer,List<Unit>>();
		currentResources = new HashMap<Pair<Integer,Resource.Type>,Integer>();
	}
	public int getTurnNumber() { return turnNumber; }
	public List<Unit> getUnits() {
		return Collections.unmodifiableList(allUnits);
	}
	public List<Unit> getUnits(int agent) {
		if(unitsByAgent.get(agent) == null)
			return null;
		return Collections.unmodifiableList(unitsByAgent.get(agent));
	}
	public List<Resource> getResources() {
		return Collections.unmodifiableList(resources);
	}
	public Unit unitAt(int x, int y) {
		for(Unit u : allUnits) {
			if(u.getxPosition() == x && u.getyPosition() == y)
				return u;
		}
		return null;
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
}
