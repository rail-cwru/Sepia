package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;
import java.util.*;

import edu.cwru.SimpleRTS.model.Template;
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
	private Map<Integer, List<Template>> templatesByAgent;
	private List<Template> allTemplates;
	private StateView view;
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
	public Resource getResource(int resourceId) {
		for(Resource r : resources)
		{
			if(resourceId == r.hashCode())
				return r;
		}
		return null;
	}
	/**
	 * Find the closest unoccupied position using a spiraling out search pattern
	 * @param x
	 * @param y
	 * @return
	 */
	public int[] getClosestPosition(int x, int y)
	{
		//if the space in question is already open
		if (positionAvailable(x,y))
			return new int[]{x,y};
		int maxradius = Math.max(Math.max(x, xextent-x), Math.max(y,yextent-y));
		for (int r = 1; r<=maxradius;r++)
		{
			//go up/left diagonal
			x = x-1;
			y = y-1;
			
			//go down
			for (int i = 0; i<2*r;i++) {
				y = y + 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
			//go right
			for (int i = 0; i<2*r;i++) {
				x = x + 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
			//go up
			for (int i = 0; i<2*r;i++) {
				y = y - 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
			//go left
			for (int i = 0; i<2*r;i++) {
				x = x - 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
		}
		return new int[]{-1,-1};
	}
	public Template getTemplate(int templateId) {
		for(Template t : allTemplates)
		{
			if(templateId == t.hashCode())
				return t;
		}
		return null;
	}
	public List<Template> getTemplates(int player) {
		if(templatesByAgent.get(player) == null)
			return null;
		return Collections.unmodifiableList(templatesByAgent.get(player));
	}
	public void setSize(int x, int y) {
		xextent = x;
		yextent = y;
	}
	public List<Unit> getUnits(int player) {
		if(unitsByAgent.get(player) == null)
			return null;
		return Collections.unmodifiableList(unitsByAgent.get(player));
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
	public boolean positionAvailable(int x, int y)
	{
		return inBounds(x,y) && unitAt(x,y)==null && resourceAt(x,y)==null;
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
	public int getXExtent() {
		return xextent;
	}
	public int getYExtent() {
		return yextent;
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
	 * Adds an amount of a resource to a player's global amount.
	 * @param player
	 * @param type
	 * @param amount
	 */
	public void addResourceAmount(int player, Resource.Type type, int amount) {
		Pair<Integer,Resource.Type> pair = new Pair<Integer,Resource.Type>(player,type);
		Integer i = currentResources.get(pair);
		if(i == null)
			i = 0;
		currentResources.put(pair, i+amount);
	}
	/**
	 * Attempts to reduce the player's amount of the given resource by an amount.
	 * If the player does not have enough of that resource, the transaction fails.
	 * @param player
	 * @param type
	 * @param amount
	 * @return - whether or not the player had enough of the resource
	 */
	public boolean consumeResourceAmount(int player, Resource.Type type, int amount) {
		Pair<Integer,Resource.Type> pair = new Pair<Integer,Resource.Type>(player,type);
		Integer i = currentResources.get(pair);
		if(i == null || i < amount)
			return false;
		currentResources.put(pair, i-amount);
		return true;
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
	public StateView getView() {
		if(view == null)
			view = new StateView(this);
		return view;
	}
	/**
	 * Provides a read-only view of class values
	 * @author Tim
	 *
	 */
	public static class StateView {
		private State state;
		private StateView(State state) {
			this.state = state;
		}
		public List<Integer> getAllUnitIds() {
			List<Integer> i = new ArrayList<Integer>();
			for(Unit u : state.allUnits)
				i.add(u.hashCode());
			return i;
		}
		public List<Integer> getUnitIds(int agent) {
			List<Integer> i = new ArrayList<Integer>();
			List<Unit> units = state.getUnits(agent);
			if(units != null)
				for(Unit u : units)
					i.add(u.hashCode());
			return i;
		}
		public Unit.UnitView getUnit(int unitID) {
			return state.getUnit(unitID).getView();
		}
		public List<Integer> getAllTemplateIds() {
			List<Integer> i = new ArrayList<Integer>();
			for(Template t : state.allTemplates)
				i.add(t.hashCode());
			return i;
		}
		public List<Integer> getTemplateIds(int agent) {
			List<Integer> i = new ArrayList<Integer>();
			List<Template> templates = state.getTemplates(agent);
			if(templates != null)
				for(Template t : templates)
					i.add(t.hashCode());
			return i;
		}
		public Template.TemplateView getTemplate(int templateID) {
			return state.getTemplate(templateID).getView();
		}
		public int getResourceAmount(int player, Resource.Type type) {
			Integer amount = state.currentResources.get(new Pair<Integer,Resource.Type>(player,type));
			return amount != null ? amount : 0;			
		}
	}
}
