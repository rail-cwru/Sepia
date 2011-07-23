package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;
import java.util.*;

import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.Pair;

public class State implements Serializable{
	//TODO: move this constant somewhere
	private final int MAXSUPPLY = 50;
	
	private Map<Integer,Unit> allUnits;//TODO - find a more efficient way of storing these (maybe HashMap of IDs to units?)s
	private Map<Integer,Map<Integer, Unit>> unitsByAgent;
	private List<ResourceNode> resourceNodes;
	private int turnNumber;
	private Map<Pair<Integer,ResourceType>,Integer> currentResources;
	private Map<Integer,Integer> currentSupply;
	private Map<Integer,Integer> currentSupplyCap;
	private int xextent;
	private int yextent;
	private Map<Integer, Map<Integer,Template>> templatesByAgent;
	private Map<Integer,Template> allTemplates;
	private StateView view;
	public State() {
		allUnits = new HashMap<Integer,Unit>();
		unitsByAgent = new HashMap<Integer,Map<Integer,Unit>>();
		resourceNodes = new ArrayList<ResourceNode>();
		currentResources = new HashMap<Pair<Integer,ResourceType>,Integer>();
		currentSupply = new HashMap<Integer,Integer>();
		currentSupplyCap = new HashMap<Integer,Integer>();
	}
	public int getTurnNumber() { return turnNumber; }
	public Map<Integer, Unit> getUnits() {
		return Collections.unmodifiableMap(allUnits);
	}
	public Unit getUnit(int unitId) {
		return allUnits.get(unitId);
	}
	public ResourceNode getResource(int resourceId) {
		for(ResourceNode r : resourceNodes)
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
		return allTemplates.get(templateId);
	}
	public Map<Integer,Template> getTemplates(int player) {
		if(templatesByAgent.get(player) == null)
			return null;
		return Collections.unmodifiableMap(templatesByAgent.get(player));
	}
	public boolean doesPlayerHaveUnit(int player, int templateid) {
		Map<Integer, Unit> units = unitsByAgent.get(player);
		if (units != null) {
			if (units.containsKey(templateid));
		}
		
		return false;
	}
	public void setSize(int x, int y) {
		xextent = x;
		yextent = y;
	}
	public String getTextString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i<xextent;i++)
		{
			str.append('|');
			for (int j = 0; j < yextent; j++)
			{
				Unit u = unitAt(i,j);
				if (u!=null)
				{//if there is a unit there
					str.append(u.getCharacter());
				}
				else
				{
					ResourceNode r = resourceAt(i, j);
					if (r != null)
					{
						str.append('0');
					}
					else
					{
						str.append(' ');
					}
					
				}
				str.append('|');
			}
			str.append('\n');
		}
		return str.toString();
	}
	public Map<Integer,Unit> getUnits(int player) {
		if(unitsByAgent.get(player) == null)
			return null;
		return Collections.unmodifiableMap(unitsByAgent.get(player));
	}
	public void addUnit(Unit u) {
		int player = u.getPlayer();
		if(!allUnits.containsKey(u))
		{
			Map<Integer, Unit> map = unitsByAgent.get(player);
			if(map == null)
				unitsByAgent.put(player, map = new HashMap<Integer, Unit>());
			allUnits.put(u.hashCode(),u);
			map.put(u.getPlayer(), u);
		}
	}
	public List<ResourceNode> getResources() {
		return Collections.unmodifiableList(resourceNodes);
	}
	public boolean positionAvailable(int x, int y)
	{
		return inBounds(x,y) && unitAt(x,y)==null && resourceAt(x,y)==null;
		
	}
	public Unit unitAt(int x, int y) {
		//This could probably be replaced by a 2D boolean array, but then you would need to ensure that things can't move without changing that array 
		for(Unit u : allUnits.values()) {
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
	public ResourceNode resourceAt(int x, int y) {
		for(ResourceNode r : resourceNodes)
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
	public void addResourceAmount(int player, ResourceType type, int amount) {
		Pair<Integer,ResourceType> pair = new Pair<Integer,ResourceType>(player,type);
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
	public boolean consumeResourceAmount(int player, ResourceType type, int amount) {
		Pair<Integer,ResourceType> pair = new Pair<Integer,ResourceType>(player,type);
		Integer i = currentResources.get(pair);
		if(i == null || i < amount)
			return false;
		currentResources.put(pair, i-amount);
		return true;
	}
	/**
	 * Adds some supply to the current amount.  It tracks the full value, but won't return any more than the maximum cap
	 * @param player
	 * @param amount
	 */
	public void addSupplyCapAmount(int player, int amount) {
		
		Integer i = currentSupplyCap.get(player);
		if(i == null)
			i = 0;
		currentSupplyCap.put(player, i+amount);
	}
	/**
	 * Reduce the supply cap of a player (EG: when a farm dies)
	 * @param player
	 * @param amount
	 */
	public void reduceSupplyCapAmount(int player, int amount) {
		Integer i = currentSupplyCap.get(player);
		if(i == null) //this should never happen
			i=0;
		currentSupplyCap.put(player, i-amount);
	}
	/**
	 * Consume some of the supply
	 * @param player
	 * @param amount
	 * @return Whether there is enough of the resource to consume
	 */
	public boolean consumeSupplyAmount(int player, int amount) {
		
		Integer currentsupply = currentSupply.get(player);
		if (currentsupply == null)
			currentsupply = 0;
		Integer currentcap = currentSupplyCap.get(player);
		if (currentcap == null)
			currentcap = 0; //just set it to zero if it isn't set, this way it functions right if for some reason we make it possible to be using a negative amount of supply 
		if (Math.min(currentcap, MAXSUPPLY) < currentsupply + amount) {
			return false;
		}
		else {
			currentSupplyCap.put(player, currentsupply+amount);
			return true;
		}
	}
	/**
	 * Return the supply that was being used by a unit
	 * @param player
	 * @param amount
	 */
	public void returnSupplyAmount(int player, int amount) {
		Integer i = currentSupplyCap.get(player);
		if(i == null) //this should never happen
			i=0;
		currentSupplyCap.put(player, i-amount);
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
		public boolean positionAvailable(int x, int y) {
			return state.positionAvailable(x, y);
		}
		public void addResource(ResourceNode r) {
			if(!state.resourceNodes.contains(r))
				state.resourceNodes.add(r);
		}
		public void setTurn(int turn) {
			state.turnNumber = turn;
		}
		public void setResourceAmount(int player, ResourceType resource, int amount) {
			state.currentResources.put(new Pair<Integer,ResourceType>(player,resource), amount);
		}
		public String getTextString() {
			return state.getTextString();
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
	public static class StateView implements Serializable{
		private State state;
		private StateView(State state) {
			this.state = state;
		}
		public List<Integer> getAllUnitIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Integer i : state.allUnits.keySet())
				ids.add(i);
			return ids;
		}
		public List<Integer> getUnitIds(int agent) {
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Unit> units = state.getUnits(agent);
			if(units != null)
				for(Integer i : units.keySet())
					ids.add(i);
			return ids;
		}
		public Unit.UnitView getUnit(int unitID) {
			return state.getUnit(unitID).getView();
		}
		public List<Integer> getAllResourceIds() {
			List<Integer> i = new ArrayList<Integer>();
			for(ResourceNode r : state.resourceNodes)
				i.add(r.hashCode());
			return i;
		}
		public List<Integer> getResourceNodeIds(ResourceNode.Type type) {
			List<Integer> i = new ArrayList<Integer>();
			for(ResourceNode r : state.resourceNodes)
				if (r.getType() == type)
					i.add(r.hashCode());
			return i;
		}
		public ResourceNode.ResourceView getResourceNode(int resourceID) {
			return state.getResource(resourceID).getView();
		}
		public List<Integer> getAllTemplateIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Integer i : state.allTemplates.keySet())
				ids.add(i);
			return ids;
		}
		public List<Integer> getTemplateIds(int agent) {
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Template> templates = state.getTemplates(agent);
			if(templates != null)
				for(Integer i : templates.keySet())
					ids.add(i);
			return ids;
		}
		public Template.TemplateView getTemplate(int templateID) {
			return state.getTemplate(templateID).getView();
		}
		/**
		 * Get a template with that name owned by that player 
		 * @param player
		 * @param name
		 * @return The view of the first (and what should be the only) template that has the specified name, or null if that player does not have a template by that name
		 */
		public Template.TemplateView getTemplate(int player, String name) {
			Map<Integer,Template> playerstemplates = state.templatesByAgent.get(player);
			if (playerstemplates == null)
				return null;
			for (Template t : playerstemplates.values()) {
				if (t.getName().equals(name))
					return t.getView();
			}
			return null;
		}
		public int getResourceAmount(int player, ResourceType type) {
			Integer amount = state.currentResources.get(new Pair<Integer,ResourceType>(player,type));
			return amount != null ? amount : 0;			
		}
		public int getSupplyAmount(int player) {
			Integer amount = state.currentSupply.get(player);
			return amount != null ? amount : 0;
		}
		public int getSupplyCap(int player) {
			Integer amount = state.currentSupplyCap.get(player);
			return Math.min(amount != null ? amount : 0,state.MAXSUPPLY);
		}
		public int[] getClosestOpenPosition(int x, int y) {
			return state.getClosestPosition(x, y);
		}
		public int getXExtent() {
			return state.getXExtent();
		}
		public int getYExtent() {
			return state.getYExtent();
		}
	}
}
