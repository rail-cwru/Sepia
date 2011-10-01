package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;
import java.util.*;

import edu.cwru.SimpleRTS.Log.ActionLogger;
import edu.cwru.SimpleRTS.Log.EventLogger;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.Upgrade;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.Pair;

public class State implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//TODO: move this constant somewhere
	private final int MAXSUPPLY = 50;
	
	private Map<Integer,Unit> allUnits;
	private Map<Integer,Map<Integer, Unit>> unitsByAgent;
	private List<ResourceNode> resourceNodes;
	private int turnNumber;
	private Map<Pair<Integer,ResourceType>,Integer> currentResources;
	private Map<Integer,Integer> currentSupply;
	private Map<Integer,Integer> currentSupplyCap;
	private Map<Integer, Set<Integer>> upgradesByAgent;
	private int xextent;
	private int yextent;
	private Map<Integer, Map<Integer,Template>> templatesByAgent;
	private Map<Integer,Template> allTemplates;
	private StateView view;
	private EventLogger eventlog;
	private ActionLogger actionlog;
	public State() {
		allUnits = new HashMap<Integer,Unit>();
		unitsByAgent = new HashMap<Integer,Map<Integer,Unit>>();
		allTemplates = new HashMap<Integer,Template>();
		templatesByAgent = new HashMap<Integer,Map<Integer,Template>>();
		upgradesByAgent = new HashMap<Integer, Set<Integer>>();
		resourceNodes = new ArrayList<ResourceNode>();
		currentResources = new HashMap<Pair<Integer,ResourceType>,Integer>();
		currentSupply = new HashMap<Integer,Integer>();
		currentSupplyCap = new HashMap<Integer,Integer>();
		eventlog = new EventLogger();
		actionlog = new ActionLogger();
	}
	public ActionLogger getActionLog() {
		return actionlog;
	}
	public EventLogger getEventLog() {
		return eventlog;
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
	public void addResource(ResourceNode resource) {
		resourceNodes.add(resource);
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
	public Template getTemplate(int player, String name) {
		Map<Integer,Template> playerstemplates = templatesByAgent.get(player);
		if (playerstemplates == null)
		{
			return null;
		}
		for (Template t : playerstemplates.values()) {
			if (t.getName().equals(name))
				return t;
		}
		return null;
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
	private static final Map<Integer,Unit> EMPTY_MAP = new HashMap<Integer,Unit>();
	public Map<Integer,Unit> getUnits(int player) {
		if(unitsByAgent.get(player) == null)
			return EMPTY_MAP;
		return Collections.unmodifiableMap(unitsByAgent.get(player));
	}
	public boolean tryProduceUnit(Unit u) {
			UnitTemplate ut = u.getTemplate();
			Pair<Integer,ResourceType> goldpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.GOLD);
			Pair<Integer,ResourceType> woodpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.WOOD);
			Integer currentgold = currentResources.get(goldpair);
			Integer currentwood = currentResources.get(woodpair);
			if (currentgold == null)
				currentgold = 0;
			if (currentwood == null)
				currentwood = 0;
			if (currentgold >= ut.getGoldCost() && currentwood >= ut.getWoodCost() && checkValidSupplyAddition(ut.getPlayer(), ut.getFoodCost(),ut.getFoodProvided()))
			{
				reduceResourceAmount(ut.getPlayer(), ResourceType.GOLD, u.getTemplate().getGoldCost());
				reduceResourceAmount(ut.getPlayer(), ResourceType.WOOD, u.getTemplate().getWoodCost());
				addUnit(u);
				return true;
			}
			else
			{
				return false;
			}
	}
	public void addUnit(Unit u) {
		int player = u.getPlayer();
		if(!allUnits.containsKey(u)) {
			Map<Integer, Unit> map = unitsByAgent.get(player);
			if(map == null)
			{
				unitsByAgent.put(player, map = new HashMap<Integer, Unit>());
			}
			allUnits.put(u.ID,u);
			map.put(u.ID, u);
			alterSupplyCapAmount(player,u.getTemplate().getFoodProvided());
			alterSupplyAmount(player, u.getTemplate().getFoodCost());
		}
	}
	public void removeUnit(int unitID) {
		if (allUnits.containsKey(unitID))
		{
			Unit u = allUnits.remove(unitID);
			unitsByAgent.get(u.getPlayer()).remove(unitID);
			alterSupplyCapAmount(u.getPlayer(),-u.getTemplate().getFoodProvided());
			alterSupplyAmount(u.getPlayer(), -u.getTemplate().getFoodCost());
		}
	}
	@SuppressWarnings("rawtypes")
	public void addTemplate(Template t, int player) {
		if(!allTemplates.containsKey(t.ID)) {
			Map<Integer, Template> map = templatesByAgent.get(player);
			if(map == null)
			{
				templatesByAgent.put(player, map = new HashMap<Integer, Template>());
			}
			allTemplates.put(t.ID,t);
			map.put(t.ID, t);
		}
	}
	public boolean tryProduceUpgrade(Upgrade upgrade) {
		UpgradeTemplate ut = upgrade.getTemplate();
		Pair<Integer,ResourceType> goldpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.GOLD);
		Pair<Integer,ResourceType> woodpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.WOOD);
		Integer currentgold = currentResources.get(goldpair);
		Integer currentwood = currentResources.get(woodpair);
		if (currentgold == null)
			currentgold = 0;
		if (currentwood == null)
			currentwood = 0;
		if (currentgold >= ut.getGoldCost() && currentwood >= ut.getWoodCost())
		{
			reduceResourceAmount(ut.getPlayer(), ResourceType.GOLD, ut.getGoldCost());
			reduceResourceAmount(ut.getPlayer(), ResourceType.WOOD, ut.getWoodCost());
			addUpgrade(upgrade);
			return true;
		}
		else
		{
			return false;
		}
	}
	private void addUpgrade(Upgrade upgrade) {
			UpgradeTemplate upgradetemplate = upgrade.getTemplate();
			int player = upgradetemplate.getPlayer();
			Set<Integer> list = upgradesByAgent.get(player);
			if(list == null)
			{
				upgradesByAgent.put(player, list = new HashSet<Integer>());
			}
			if (!list.contains(upgradetemplate.ID))
			{
				//upgrade all of the affected units
				for (UnitTemplate toupgrade : upgradetemplate.getAffectedUnits()) {
					toupgrade.setBasicAttackLow(toupgrade.getBasicAttackLow() + upgradetemplate.getAttackChange());
					toupgrade.setArmor(toupgrade.getArmor() + upgradetemplate.getDefenseChange());
					
				}
			}
			list.add(upgradetemplate.ID);
	}
	public boolean hasUpgrade(Integer upgradetemplateid, int player) {
		Set<Integer> set = upgradesByAgent.get(player);
		if(set == null)
		{
			return false;
		}
		return set.contains(upgradetemplateid);
	}
	public List<ResourceNode> getResources() {
		return Collections.unmodifiableList(resourceNodes);
	}
	public void removeResourceNode(int resourceID) {
		for (int i = 0; i<resourceNodes.size();i++) {
			if (resourceNodes.get(i).ID == resourceID) {
				resourceNodes.remove(i);
				break;
			}
		}
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
	public int getResourceAmount(int player, ResourceType type) {
		Integer amount = currentResources.get(new Pair<Integer,ResourceType>(player,type));
		return amount != null ? amount : 0;			
	}
	public void depositResources(int player, ResourceType type, int amount)
	{
		if (amount > 0)
		{
			addResourceAmount(player, type, amount);
		}
	}
	/**
	 * Adds an amount of a resource to a player's global amount.
	 * @param player
	 * @param type
	 * @param amount
	 */
	private void addResourceAmount(int player, ResourceType type, int amount) {
		Pair<Integer,ResourceType> pair = new Pair<Integer,ResourceType>(player,type);
		Integer previous = currentResources.get(pair);
		if(previous == null)
			previous = 0;
		currentResources.put(pair, previous+amount);
	}
	/**
	 * Attempts to reduce the player's amount of the given resource by an amount.
	 * If the player does not have enough of that resource, the transaction fails.
	 * @param player
	 * @param type
	 * @param amount
	 */
	private void reduceResourceAmount(int player, ResourceType type, int amount) {
		Pair<Integer,ResourceType> pair = new Pair<Integer,ResourceType>(player,type);
		Integer i = currentResources.get(pair);
		if (i == null) {
			i = 0;
		}
		currentResources.put(pair, i-amount);
	}
	public int getSupplyAmount(int player) {
		Integer amount = currentSupply.get(player);
		return amount != null ? amount : 0;
	}
	public int getSupplyCap(int player) {
		Integer amount = currentSupplyCap.get(player);
		return Math.min(amount != null ? amount : 0,MAXSUPPLY);
	}
	
	/**
	 * Adds some supply to the current amount.  It tracks the full value, but won't return any more than the maximum cap
	 * @param player
	 * @param amount
	 */
	private void addSupplyCapAmount(int player, int amount) {
		
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
	private void alterSupplyCapAmount(int player, int amount) {
		Integer i = currentSupplyCap.get(player);
		if(i == null) //this should never happen
			i=0;
		currentSupplyCap.put(player, i+amount);
	}
	/**
	 * Consume some of the supply
	 * @param player
	 * @param amount
	 */
	private void alterSupplyAmount(int player, int amount) {
		
		Integer currentsupply = currentSupply.get(player);
		if (currentsupply == null)
			currentsupply = 0;
		
		currentSupply.put(player, currentsupply+amount);
	}
	public boolean checkValidSupplyAddition(int player, int amounttoadd, int offsettingcapgain) {
		if (amounttoadd<=0)
		{
			//it is always valid to make something that takes no or negative supply
			return true;
		}
		else
		{
			Integer currentcap = currentSupplyCap.get(player);
			if (currentcap == null)
			{
				currentcap = 0; //just set it to zero if it isn't set, this way it functions right if for some reason we make it possible to be using a negative amount of supply
				//set it, because why not
				currentSupplyCap.put(player, 0);
			}
			Integer currentsupply = currentSupply.get(player);
			if (currentsupply == null)
			{
				currentsupply = 0;
				//set it, because why not
				currentSupply.put(player, 0);
			}
			return Math.min(currentcap+ offsettingcapgain, MAXSUPPLY) >= currentsupply + amounttoadd;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + MAXSUPPLY;
		result = prime * result
				+ ((actionlog == null) ? 0 : actionlog.hashCode());
		result = prime * result
				+ ((allTemplates == null) ? 0 : allTemplates.hashCode());
		result = prime * result
				+ ((allUnits == null) ? 0 : allUnits.hashCode());
		result = prime
				* result
				+ ((currentResources == null) ? 0 : currentResources.hashCode());
		result = prime * result
				+ ((currentSupply == null) ? 0 : currentSupply.hashCode());
		result = prime
				* result
				+ ((currentSupplyCap == null) ? 0 : currentSupplyCap.hashCode());
		result = prime * result
				+ ((eventlog == null) ? 0 : eventlog.hashCode());
		result = prime * result
				+ ((resourceNodes == null) ? 0 : resourceNodes.hashCode());
		result = prime
				* result
				+ ((templatesByAgent == null) ? 0 : templatesByAgent.hashCode());
		result = prime * result + turnNumber;
		result = prime * result
				+ ((unitsByAgent == null) ? 0 : unitsByAgent.hashCode());
		result = prime * result
				+ ((upgradesByAgent == null) ? 0 : upgradesByAgent.hashCode());
		result = prime * result + xextent;
		result = prime * result + yextent;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (MAXSUPPLY != other.MAXSUPPLY)
			return false;
		if (actionlog == null) {
			if (other.actionlog != null)
				return false;
		} else if (!actionlog.equals(other.actionlog))
			return false;
		if (allTemplates == null) {
			if (other.allTemplates != null)
				return false;
		} else if (!allTemplates.equals(other.allTemplates))
			return false;
		if (allUnits == null) {
			if (other.allUnits != null)
				return false;
		} else if (!allUnits.equals(other.allUnits))
			return false;
		if (currentResources == null) {
			if (other.currentResources != null)
				return false;
		} else if (!currentResources.equals(other.currentResources))
			return false;
		if (currentSupply == null) {
			if (other.currentSupply != null)
				return false;
		} else if (!currentSupply.equals(other.currentSupply))
			return false;
		if (currentSupplyCap == null) {
			if (other.currentSupplyCap != null)
				return false;
		} else if (!currentSupplyCap.equals(other.currentSupplyCap))
			return false;
		if (eventlog == null) {
			if (other.eventlog != null)
				return false;
		} else if (!eventlog.equals(other.eventlog))
			return false;
		if (resourceNodes == null) {
			if (other.resourceNodes != null)
				return false;
		} else if (!resourceNodes.equals(other.resourceNodes))
			return false;
		if (templatesByAgent == null) {
			if (other.templatesByAgent != null)
				return false;
		} else if (!templatesByAgent.equals(other.templatesByAgent))
			return false;
		if (turnNumber != other.turnNumber)
			return false;
		if (unitsByAgent == null) {
			if (other.unitsByAgent != null)
				return false;
		} else if (!unitsByAgent.equals(other.unitsByAgent))
			return false;
		if (upgradesByAgent == null) {
			if (other.upgradesByAgent != null)
				return false;
		} else if (!upgradesByAgent.equals(other.upgradesByAgent))
			return false;
		if (xextent != other.xextent)
			return false;
		if (yextent != other.yextent)
			return false;
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
		public void addTemplate(Template t, int player) {
			state.addTemplate(t, player);
		}
		public Template getTemplate(int player, String name)
		{
			return state.getTemplate(player, name);
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
		public void setSupplyCap(int player, int supply) {
			state.currentSupplyCap.put(player, supply);
		}
		public String getTextString() {
			return state.getTextString();
		}
		public boolean hasTemplates(int player) {
			Map<Integer,Template> templates = state.templatesByAgent.get(player);
			if (templates == null) {
				return false;
			}
			return templates.size() != 0;
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private State state;
		private StateView(State state) {
			this.state = state;
		}
		public EventLogger.EventLoggerView getEventLog() {
			return state.eventlog.getView();
		}
		public List<Integer> getAllUnitIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Integer i : state.allUnits.keySet())
				ids.add(i);
			return ids;
		}
		public List<Integer> getUnitIds(int player) {
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Unit> units = state.getUnits(player);
			if(units != null)
				for(Integer i : units.keySet())
					ids.add(i);
			return ids;
		}
		public Unit.UnitView getUnit(int unitID) {
			Unit u = state.getUnit(unitID);
			if (u==null)
				return null;
			return u.getView();
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
			Template t = state.getTemplate(player,name);
			if (t!=null)
				return t.getView();
			else //if it is null
				return null;
		}
		public int getResourceAmount(int player, ResourceType type) {
			return state.getResourceAmount(player, type);	
		}
		public int getSupplyAmount(int player) {
			return state.getSupplyAmount(player);
		}
		public int getSupplyCap(int player) {
			return state.getSupplyCap(player);
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
		public boolean doesPlayerHaveUnit(int player, int buildingtemplateid) {
			return state.doesPlayerHaveUnit(player, buildingtemplateid);
		}
		public boolean hasUpgrade(int upgradeid, int playerid) {
			return state.hasUpgrade(upgradeid, playerid);
		}
		public boolean inBounds(int x, int y) {
			return state.inBounds(x, y);
		}
		public boolean isUnitAt(int x, int y) {
			return state.unitAt(x, y) != null;
		}
		public boolean isResourceAt(int x, int y) {
			return state.resourceAt(x, y) != null;
		}
		
	}
}
