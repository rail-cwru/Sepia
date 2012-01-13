package edu.cwru.SimpleRTS.environment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.cwru.SimpleRTS.Log.ActionLogger;
import edu.cwru.SimpleRTS.Log.EventLogger;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.Upgrade;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public class State implements Serializable, Cloneable, IDDistributer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	 
	
	private int nextIDTarget;
	private int nextIDTemplate;
		@Override
	public int nextTargetID() {
		// TODO Auto-generated method stub
		return nextIDTarget++;
	}
	@Override
	public int nextTemplateID() {
		// TODO Auto-generated method stub
		return nextIDTemplate++;
	}
	/**
	 * DON'T USE THIS IF YOU AREN'T THE XML SAVING MECHANISM
	 * This must be different from the other so saves in the middle of an episode don't mess up the ids
	 * @return
	 */
	public int getNextTargetIDForXMLSave()
	{
		return nextIDTarget;
	}
	/**
	 * DON'T USE THIS IF YOU AREN'T THE XML SAVING MECHANISM
	 * This must be different from the other so saves in the middle of an episode don't mess up the ids
	 * @return
	 */
	public int getNextTemplateIDForXMLSave()
	{
		return nextIDTemplate;
	}
	
	//TODO: move this constant somewhere
	private final int MAXSUPPLY = 50;
	
	private Map<Integer,Unit> allUnits;
	//private Map<Integer, StateView> views;
	/**
	 * Maps player to an array of how many units can see each cell in the map
	 * Note that observer sight may not be used like the others
	 * Observer sight is tracked as the sum of all other sight
	 * But when it comes time to tell if the observer can see something, it always can
	 */
	//private Map<Integer,int[][]> playerCanSee;
	private boolean hasFogOfWar;
	private boolean revealedResources;
	//private Map<Integer,Map<Integer, Unit>> unitsByAgent;
	private List<ResourceNode> resourceNodes;
	private int turnNumber;
	//private Map<Pair<Integer,ResourceType>,Integer> currentResources;
	//private Map<Integer,Integer> currentSupply;
	//private Map<Integer,Integer> currentSupplyCap;
	//private Map<Integer, Set<Integer>> upgradesByAgent;
	private int xextent;
	private int yextent;
	//@SuppressWarnings("rawtypes")
	//private Map<Integer, Map<Integer,Template>> templatesByAgent;
	@SuppressWarnings("rawtypes")
	private Map<Integer,Template> allTemplates;
	//private HashMap<Integer, EventLogger> eventlogs;
	private ActionLogger actionlog;
	private Map<Integer,PlayerState> playerStates;
	private PlayerState observerState;
	@SuppressWarnings("rawtypes")
	public State() {
		nextIDTarget=0;
		nextIDTemplate=0;
		//playerCanSee = new HashMap<Integer,int[][]>();
		allUnits = new HashMap<Integer,Unit>();
		//unitsByAgent = new HashMap<Integer,Map<Integer,Unit>>();
		allTemplates = new HashMap<Integer,Template>();
		//templatesByAgent = new HashMap<Integer,Map<Integer,Template>>();
		//upgradesByAgent = new HashMap<Integer, Set<Integer>>();
		resourceNodes = new ArrayList<ResourceNode>();
		//currentResources = new HashMap<Pair<Integer,ResourceType>,Integer>();
		//currentSupply = new HashMap<Integer,Integer>();
		//currentSupplyCap = new HashMap<Integer,Integer>();
		//views = new HashMap<Integer,StateView>();
		//eventlogs = new HashMap<Integer, EventLogger>();
		//eventlogs.put(Agent.OBSERVER_ID, new EventLogger());
		actionlog = new ActionLogger();
		playerStates = new HashMap<Integer,PlayerState>();
		//observerState = new PlayerState(Agent.OBSERVER_ID);
		addPlayer(Agent.OBSERVER_ID);
		setFogOfWar(false);
		setRevealedResources(false);
	}
	
	public void updateGlobalListsFromPlayers() {
		for(PlayerState player : playerStates.values())
		{
			for(Unit u : player.getUnits().values())
			{
				allUnits.put(u.ID, u);
			}
			for(Template t : player.getTemplates().values())
			{
				allTemplates.put(t.ID, t);
			}
		}
	}
	
	@Override
	protected Object clone() {
		State newstate = new State();
		/*for(Integer i : playerCanSee.keySet())
		{
			state.playerCanSee.put(i, playerCanSee.get(i).clone());
		}	*/
		for(Unit u : allUnits.values())
		{//takes care of allUnits and unitsByAgent
			Unit copy = u.copyOf();
			newstate.addUnit(copy, copy.getxPosition(), copy.getyPosition());
		}
		newstate.allTemplates.putAll(allTemplates);
		/*for(Integer i : templatesByAgent.keySet())
		{
			@SuppressWarnings("rawtypes")
			Map<Integer,Template> templates = new HashMap<Integer,Template>();
			templates.putAll(templatesByAgent.get(i));
			state.templatesByAgent.put(i, templates);
		}*//*
		for(Integer i : upgradesByAgent.keySet())
		{
			Set<Integer> upgrades = new HashSet<Integer>();
			upgrades.addAll(upgradesByAgent.get(i));
			state.upgradesByAgent.put(i, upgradesByAgent.get(i));
		}*/
		for(ResourceNode node : resourceNodes)
		{
			newstate.resourceNodes.add(node.copyOf());
		}
		for(Integer i : playerStates.keySet())
		{
			newstate.playerStates.put(i, playerStates.get(i).copyOf());
		}
		//state.currentResources.putAll(currentResources);
		//state.currentSupply.putAll(currentSupply);
		//state.currentSupplyCap.putAll(currentSupplyCap);
		
		return newstate;
	}
	
	public StateView getStaticCopy(int player) {
		State state = (State)clone();
		return state.getView(player);
	}
	/**
	 * Return an array of the players currently in the game
	 * @return
	 */
	public Integer[] getPlayers()
	{
		return playerStates.keySet().toArray(new Integer[]{});
	}
	//@SuppressWarnings("rawtypes")
	/**
	 * Add another player
	 * @param playernumber The player number of the player to add
	 */
	public void addPlayer(int playernumber)
	{
		if (!playerStates.containsKey(playernumber) || playernumber == Agent.OBSERVER_ID)
		{
			PlayerState playerState = new PlayerState(playernumber);
			if(playernumber != Agent.OBSERVER_ID)
			{
				playerStates.put(playernumber, playerState);
			}
			else
			{
				observerState = playerState;
			}
			EventLogger neweventlogger = new EventLogger();
			for (int i = 0; i<=turnNumber;i++)
				neweventlogger.nextRound();
			//eventlogs.put(playernumber, neweventlogger);
			playerState.setEventLogger(neweventlogger);
			/*templatesByAgent.put(playernumber, new HashMap<Integer,Template>());
			unitsByAgent.put(playernumber, new HashMap<Integer,Unit>());
			upgradesByAgent.put(playernumber, new HashSet<Integer>());
			playerCanSee.put(playernumber, new int[getXExtent()][getYExtent()]);*/
			playerState.setVisibilityMatrix(new int[getXExtent()][getYExtent()]);
			if (revealedResources) {
				for (ResourceNode r : resourceNodes) {
					neweventlogger.recordResourceNodeReveal(r.getxPosition(), r.getyPosition(), r.getType());
				}
			}
			
		}
	}
	
	public PlayerState getPlayerState(int player) {
		return playerStates.get(player);
	}
	
	public Collection<PlayerState> getPlayerStates() {
		return playerStates.values();
	}
	
	public ActionLogger getActionLog() {
		return actionlog;
	}
	public EventLogger getEventLog(int playerid) {
		//return eventlogs.get(playerid);
		if(playerid == Agent.OBSERVER_ID)
			return observerState.getEventLogger();
		return playerStates.get(playerid).getEventLogger();
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
		if (this.revealedResources) {
			revealResource(resource);
		}
	}
	
	public void setFogOfWar(boolean fogofwar) {
		if (fogofwar)
		{
			hasFogOfWar = true;
//			recalculateVisionFromScratch();
		}
		else
		{
			hasFogOfWar = false;
		}
	}
	public boolean getFogOfWar()
	{
		return hasFogOfWar;
	}
	public void setRevealedResources(boolean revealedResources) {
		if (revealedResources) {
			//only need to do something if it is a change, or you risk duplicates
			if (!this.revealedResources)
			{
				this.revealedResources = true;
				for (ResourceNode resource : resourceNodes) {
					revealResource(resource);
				}
			}
		}
		else {
			this.revealedResources = false;
			//Don't change this to foreach player, as that will skip the observer's log
			/*for (EventLogger e : eventlogs.values()) {
				e.eraseResourceNodeReveals();
			}*/
			for(PlayerState s : playerStates.values())
			{
				s.getEventLogger().eraseResourceNodeReveals();
			}
			observerState.getEventLogger().eraseResourceNodeReveals();
		}
	}
	public boolean getRevealedResources()
	{
		return revealedResources;
	}
	private void revealResource(ResourceNode resource) {
		//Don't change this to foreach player, as that will skip the observer's log
		/*for (EventLogger e : eventlogs.values()) {
			e.recordResourceNodeReveal(resource.getxPosition(), resource.getyPosition(), resource.getType());
		}*/
		for(PlayerState s : playerStates.values())
		{
			s.getEventLogger().recordResourceNodeReveal(resource.getxPosition(), 
														resource.getyPosition(), 
														resource.getType());
		}
		observerState.getEventLogger().recordResourceNodeReveal(resource.getxPosition(), 
																resource.getyPosition(), 
																resource.getType());
	}
	/**
	 * Returns whether the selected coordinates are visible to the player through the fog of war.
	 * @param x
	 * @param y
	 * @param player
	 * @return
	 */
	public boolean canSee(int x, int y, int player) {
		if (!hasFogOfWar || player == Agent.OBSERVER_ID)
		{
			return true;
		}
		if (!inBounds(x, y))
		{
			return false;
		}
		else
		{
			//int[][] cansee = playerCanSee.get(player);
			int[][] cansee = playerStates.get(player).getVisibilityMatrix();
			if (cansee==null)
			{
				return false;
			}
			else
			{
				return cansee[x][y]>0;
			}
		}
	}
	/**
	 * Recalculates the vision of each agent from scratch.
	 */
	private void recalculateVisionFromScratch() {
		observerState.setVisibilityMatrix(new int[getXExtent()][getYExtent()]);
		for (PlayerState player : playerStates.values())
		{
			player.setVisibilityMatrix(new int[getXExtent()][getYExtent()]);
		}
		for (Unit u : allUnits.values())
		{
			int player = u.getPlayer();
			PlayerState state = playerStates.get(player);
			int x = u.getxPosition();
			int y = u.getyPosition();
			int s = u.getTemplate().getSightRange();
			for (int i = x-s; i<=x+s;i++)
				for (int j = y-s; j<=y+s;j++)
					if (inBounds(i,j))
					{
						state.getVisibilityMatrix()[i][j]++;
						//playerCanSee.get(player)[i][j]++;
						//observersight[i][j]++;
						observerState.getVisibilityMatrix()[i][j]++;
					}
		}
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
	@SuppressWarnings("rawtypes")
	public Template getTemplate(int templateId) {
		return allTemplates.get(templateId);
	}
	@SuppressWarnings("rawtypes")
	public Template getTemplate(int player, String name) {
		/*
		Map<Integer,Template> playerstemplates = templatesByAgent.get(player);
		if (playerstemplates == null)
		{
//			System.out.println("Player not found");
			return null;
		}
		for (Template t : playerstemplates.values()) {
			if (t.getName().equals(name))
			{
				return t;
			}
		}
		return null;*/
		PlayerState playerState = playerStates.get(player);
		if (playerState == null)
		{
			System.out.println("Player not found");
			return null;
		}		
		return playerState.getTemplate(name);
	}
	@SuppressWarnings("rawtypes")
	public Map<Integer,Template> getTemplates(int player) {
		/*if(templatesByAgent.get(player) == null)
			return null;
		return Collections.unmodifiableMap(templatesByAgent.get(player));*/
		if(playerStates.get(player) == null)
			return null;
		return Collections.unmodifiableMap(playerStates.get(player).getTemplates());
	}
	public boolean doesPlayerHaveUnit(int player, int templateid) {
		/*Map<Integer, Template> templates = templatesByAgent.get(player);
		if (templates != null) {
			return templates.containsKey(templateid);
		}
		
		return false;*/
		if(playerStates.get(player) != null)
		{
			return playerStates.get(player).getTemplate(templateid) != null;
		}
		return false;
	}
	public void setSize(int x, int y) {
		xextent = x;
		yextent = y;
		recalculateVisionFromScratch();
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
		//if(unitsByAgent.get(player) == null)
		if(playerStates.get(player) == null)
			return EMPTY_MAP;
		//return Collections.unmodifiableMap(unitsByAgent.get(player));
		return Collections.unmodifiableMap(playerStates.get(player).getUnits());
	}
	public boolean tryProduceUnit(Unit u,int x, int y) {
			if (!positionAvailable(x,y))
				return false;
			UnitTemplate ut = u.getTemplate();
			//Pair<Integer,ResourceType> goldpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.GOLD);
			//Pair<Integer,ResourceType> woodpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.WOOD);
			//Integer currentgold = currentResources.get(goldpair);
			Integer currentgold = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.GOLD);
			//Integer currentwood = currentResources.get(woodpair);
			Integer currentwood = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.WOOD);
			if (currentgold == null)
				currentgold = 0;
			if (currentwood == null)
				currentwood = 0;
			if (currentgold >= ut.getGoldCost() && currentwood >= ut.getWoodCost() && checkValidSupplyAddition(ut.getPlayer(), ut.getFoodCost(),ut.getFoodProvided()))
			{
				reduceResourceAmount(ut.getPlayer(), ResourceType.GOLD, u.getTemplate().getGoldCost());
				reduceResourceAmount(ut.getPlayer(), ResourceType.WOOD, u.getTemplate().getWoodCost());
				addUnit(u, x, y);
				return true;
			}
			else
			{
				return false;
			}
	}
	public void addUnit(Unit u,int x, int y) {
		
		int player = u.getPlayer();
		if (!playerStates.containsKey(player))
			addPlayer(player);
		if(!allUnits.containsKey(u.ID)) {
			//Map<Integer, Unit> map = unitsByAgent.get(player);
			PlayerState playerState = playerStates.get(player);
			if(playerState == null)
			{
//				TODO: see if we can do without this
				addPlayer(player);
			}
			Map<Integer, Unit> map = playerState.getUnits();
			/*if(map == null)
			{
				unitsByAgent.put(player, map = new HashMap<Integer, Unit>());
			}*/
			allUnits.put(u.ID,u);
			map.put(u.ID, u);
			alterSupplyCapAmount(player,u.getTemplate().getFoodProvided());
			alterSupplyAmount(player, u.getTemplate().getFoodCost());
			u.setxPosition(x);
			u.setyPosition(y);
			int sightrange = u.getTemplate().getSightRange();
			for (int i = x-sightrange; i<= x+sightrange;i++)
				for (int j = y-sightrange; j<= y+sightrange;j++)
				{
					if (inBounds(i,j))
					{
						//playerCanSee.get(Agent.OBSERVER_ID)[i][j]++;
						observerState.getVisibilityMatrix()[i][j]++;
						//System.out.println(u);
						//System.out.println(u.getPlayer());
						//System.out.println(playerCanSee.entrySet());
						//playerCanSee.get(u.getPlayer())[i][j]++;
						playerState.getVisibilityMatrix()[i][j]++;
					}
				}
		}
		
	}
	/**
	 * Move a unit in a direction.
	 * Does not perform collision checks of any kind
	 * @param u
	 * @param direction
	 */
	public void moveUnit(Unit u, Direction direction) {
		int sightrange = u.getTemplate().getSightRange();
		int x = u.getxPosition();
		int y = u.getyPosition();
		//int[][] playersight=playerCanSee.get(u.getTemplate().getPlayer());
		int[][] playersight = playerStates.get(u.getPlayer()).getVisibilityMatrix();
		//int[][] observersight=playerCanSee.get(Agent.OBSERVER_ID);
		int[][] observersight = observerState.getVisibilityMatrix();
		
//		String s="";
//		for (int i = 0; i<getXExtent();i++)
//		{
//			for (int j = 0; j<getYExtent();j++)
//			{
//				s+="|"+playersight[i][j];
//				
//			}
//			s+="|\n";
//		}
//		System.out.println(s);
		if (direction.xComponent()!=0)
		{
			int xoffset;
			int xdirection;
			if (direction.xComponent() > 0 )
			{
				xdirection = 1;
				xoffset = direction.xComponent();
			}
			else
			{
				xdirection = -1;
				xoffset = -direction.xComponent();
			}
			for (int dist = 0; dist < xoffset; dist++)
			{
				
				int xtoadd = x + (sightrange - dist+1) * xdirection;
				int xtoremove = x - (sightrange + dist) * xdirection;
				for (int j = y - sightrange; j <= y + sightrange; j++)
				{
					if (inBounds(xtoadd,j))
					{
						
						playersight[xtoadd][j]++;
						observersight[xtoadd][j]++;
					}
					if (inBounds(xtoremove,j))
					{
						playersight[xtoremove][j]--;
						observersight[xtoremove][j]--;
					}
				}
				
			}
			//move along x
			u.setxPosition(x+direction.xComponent());
			//Get the new x
			x = u.getxPosition();
		}
		
		if (direction.yComponent()!=0)
		{
			int yoffset;
			int ydirection;
			if (direction.yComponent() > 0 )
			{
				ydirection = 1;
				yoffset = direction.yComponent();
			}
			else
			{
				ydirection = -1;
				yoffset = -direction.yComponent();
			}
			for (int dist = 0; dist < yoffset; dist++)
			{
				
				int ytoadd = y + (sightrange - dist+1) * ydirection;
				int ytoremove = y - (sightrange + dist) * ydirection;
				for (int i = x - sightrange; i <= x + sightrange; i++)
				{
					if (inBounds(i,ytoadd))
					{
						
						playersight[i][ytoadd]++;
						observersight[i][ytoadd]++;
					}
					if (inBounds(i,ytoremove))
					{
						playersight[i][ytoremove]--;
						observersight[i][ytoremove]--;
					}
				}
				
			}
			//move along y
			u.setyPosition(y+direction.yComponent());
			//Get the new y
			y = u.getyPosition();
		}
	}
	public void removeUnit(int unitID) {
		if (allUnits.containsKey(unitID))
		{
			Unit u = allUnits.remove(unitID);
			//unitsByAgent.get(u.getPlayer()).remove(unitID);
			playerStates.get(u.getPlayer()).getUnits().remove(unitID);
			alterSupplyCapAmount(u.getPlayer(),-u.getTemplate().getFoodProvided());
			alterSupplyAmount(u.getPlayer(), -u.getTemplate().getFoodCost());
			int x = u.getxPosition();
			int y = u.getyPosition();
			int sightrange = u.getTemplate().getSightRange();
			for (int i = x-sightrange; i<= x+sightrange;i++)
				for (int j = y-sightrange; j<= y+sightrange;j++)
				{
					if (inBounds(i,j))
					{
						//playerCanSee.get(Agent.OBSERVER_ID)[i][j]--;
						observerState.getVisibilityMatrix()[i][j]--;
						//playerCanSee.get(u.getPlayer())[i][j]--;
						playerStates.get(u.getPlayer()).getVisibilityMatrix()[i][j]--;
					}
				}
		}
		
	}
	@SuppressWarnings("rawtypes")
	public void addTemplate(Template t) {
		int player = t.getPlayer(); 
		if (!playerStates.containsKey(player))
			addPlayer(player);
		if(!allTemplates.containsKey(t.ID)) {
			//Map<Integer, Template> map = templatesByAgent.get(player);
			PlayerState playerState = playerStates.get(player);
			if(playerState == null)
			{
//				TODO: see if we can do without this
				addPlayer(player);
			}
			Map<Integer, Template> map = playerState.getTemplates();
			/*if(map == null)
			{
				templatesByAgent.put(player, map = new HashMap<Integer, Template>());
			}*/
			allTemplates.put(t.ID,t);
			map.put(t.ID, t);
		}
	}
	public boolean tryProduceUpgrade(Upgrade upgrade) {
		UpgradeTemplate ut = upgrade.getTemplate();
		//Pair<Integer,ResourceType> goldpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.GOLD);
		//Pair<Integer,ResourceType> woodpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.WOOD);
		//Integer currentgold = currentResources.get(goldpair);
		Integer currentgold = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.GOLD);
		//Integer currentwood = currentResources.get(woodpair);
		Integer currentwood = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.WOOD);
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
			//Set<Integer> list = upgradesByAgent.get(player);
			PlayerState playerState = playerStates.get(player);
			if(playerState == null)
			{
//				TODO: see if we can do without this
				addPlayer(player);
			}
			Set<Integer> list = playerState.getUpgrades();
			/*if(list == null)
			{
				upgradesByAgent.put(player, list = new HashSet<Integer>());
			}*/
			if (!list.contains(upgradetemplate.ID))
			{
				//upgrade all of the affected units
				for (UnitTemplate toupgrade : upgradetemplate.getAffectedUnits()) {
					toupgrade.setBasicAttack(toupgrade.getBasicAttack() + upgradetemplate.getAttackChange());
					toupgrade.setArmor(toupgrade.getArmor() + upgradetemplate.getDefenseChange());
					
				}
			}
			list.add(upgradetemplate.ID);
	}
	public boolean hasUpgrade(Integer upgradetemplateid, int player) {
		/*Set<Integer> set = upgradesByAgent.get(player);
		if(set == null)
		{
			return false;
		}
		return set.contains(upgradetemplateid);*/
		return playerStates.get(player).getUpgrades().contains(upgradetemplateid);
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
		//Integer amount = currentResources.get(new Pair<Integer,ResourceType>(player,type));
		Integer amount = playerStates.get(player).getCurrentResourceAmount(type);
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
		/*Pair<Integer,ResourceType> pair = new Pair<Integer,ResourceType>(player,type);
		Integer previous = currentResources.get(pair);
		if(previous == null)
			previous = 0;
		currentResources.put(pair, previous+amount);*/
		playerStates.get(player).addToCurrentResourceAmount(type, amount);
	}
	/**
	 * Attempts to reduce the player's amount of the given resource by an amount.
	 * If the player does not have enough of that resource, the transaction fails.
	 * @param player
	 * @param type
	 * @param amount
	 */
	private void reduceResourceAmount(int player, ResourceType type, int amount) {
		addResourceAmount(player, type, -amount);
	}
	public int getSupplyAmount(int player) {
		/*Integer amount = currentSupply.get(player);
		return amount != null ? amount : 0;*/
		return playerStates.get(player).getCurrentSupply();
	}
	public int getSupplyCap(int player) {
		/*Integer amount = currentSupplyCap.get(player);
		return Math.min(amount != null ? amount : 0,MAXSUPPLY);*/
		return Math.min(playerStates.get(player).getCurrentSupplyCap(), MAXSUPPLY);
	}
	
	/**
	 * Reduce the supply cap of a player (EG: when a farm dies)
	 * @param player
	 * @param amount
	 */
	private void alterSupplyCapAmount(int player, int amount) {
		/*Integer i = currentSupplyCap.get(player);
		if(i == null) //this should never happen
			i=0;
		currentSupplyCap.put(player, i+amount);*/
		playerStates.get(player).addToCurrentSupplyCap(amount);
	}
	/**
	 * Consume some of the supply
	 * @param player
	 * @param amount
	 */
	private void alterSupplyAmount(int player, int amount) {
		/*
		Integer currentsupply = currentSupply.get(player);
		if (currentsupply == null)
			currentsupply = 0;
		
		currentSupply.put(player, currentsupply+amount);*/
		playerStates.get(player).addToCurrentSupply(amount);
	}
	public boolean checkValidSupplyAddition(int player, int amounttoadd, int offsettingcapgain) {
		if (amounttoadd<=0)
		{
			//it is always valid to make something that takes no or negative supply
			return true;
		}
		else
		{
			/*Integer currentcap = currentSupplyCap.get(player);
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
			return Math.min(currentcap+ offsettingcapgain, MAXSUPPLY) >= currentsupply + amounttoadd;*/
			
			PlayerState playerState = playerStates.get(player);
			return Math.min(playerState.getCurrentSupplyCap() + offsettingcapgain, MAXSUPPLY) >=
						    playerState.getCurrentSupply() + amounttoadd;
			 
		}
	}
	/**
	 * Go to the next turn.
	 * Increases the turn number and tells the logs to go to the next turn
	 */
	public void incrementTurn() {
		turnNumber++;
		//Don't change this unless you change how OBSERVERs work
		/*for (EventLogger eventlog: eventlogs.values())
		{
			eventlog.nextRound();
		}*/
		for (PlayerState s : playerStates.values())
		{
			s.getEventLogger().nextRound();
		}
		observerState.getEventLogger().nextRound();
		actionlog.nextRound();
	}
	
	@SuppressWarnings("rawtypes")
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
		public void setIDDistributerTemplateMax(int newmax)
		{
			state.nextIDTemplate=newmax;
		}
		public void setIDDistributerTargetMax(int newmax)
		{
			state.nextIDTarget=newmax;
		}
		public void addPlayer(PlayerState player) {
			state.playerStates.put(player.playerNum, player);
		}
		public void addUnit(Unit u, int x, int y) {
			state.addUnit(u,x,y);
		}
		public void addTemplate(Template t) {
			state.addTemplate(t);
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
			state.addResourceAmount(player, resource, amount);
		}
		public void setSupplyCap(int player, int supply) {
			//state.currentSupplyCap.put(player, supply);
			state.alterSupplyCapAmount(player, supply);
		}
		public String getTextString() {
			return state.getTextString();
		}
		public boolean hasTemplates(int player) {
			//Map<Integer,Template> templates = state.templatesByAgent.get(player);
			if(state.getPlayerState(player) == null)
			{
				return false;
			}
			Map<Integer,Template> templates = state.getPlayerState(player).getTemplates();
			/*if (templates == null) {
				return false;
			}*/
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
	public StateView getView(int player) {
		//StateView toreturn = views.get(player);
		if(player == Agent.OBSERVER_ID)
		{
			if(observerState.getView() == null)
				observerState.setStateView(new StateView(this,player));

			return observerState.getView();
		}
		
		StateView toreturn = playerStates.get(player).getView();
		if(toreturn == null)
		{
			toreturn = new StateView(this,player);
			//views.put(player, toreturn);
			playerStates.get(player).setStateView(toreturn);
		}
		return toreturn;
	}
	@SuppressWarnings("rawtypes")
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
		private int player;
		private StateView(State state, int player) {
			this.state = state;
			this.player = player;
		}
		
		public StateView getStaticCopy() {
			return state.getStaticCopy(player);
		}
		/**
		 * Get a read-only log of the game's events _that you have seen_
		 * @return
		 */
		public EventLogger.EventLoggerView getEventLog() {
			EventLogger logger = state.getEventLog(player);
			return state.getEventLog(player).getView();
		}
		/**
		 * Get all of the unit ids that you can see
		 * @return
		 */
		public List<Integer> getAllUnitIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Entry<Integer, Unit> e: state.allUnits.entrySet())
				if (canSee(e.getValue().getxPosition(), e.getValue().getyPosition()))
					ids.add(e.getKey());
			return ids;
		}
		/**
		 * Returns whether the selected coordinates are visible to the player through the fog of war.
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean canSee(int x, int y)
		{
			return state.canSee(x,y,player);
		}
		public int getTurnNumber()
		{
			return state.getTurnNumber();
		}
		/**
		 * Get the unit ids of those units owned by the selected players.
		 * Will not work 
		 * @param player
		 * @return
		 */
		public List<Integer> getUnitIds(int player) {
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Unit> units = state.getUnits(player);
			if(units != null)
				for(Entry<Integer, Unit> e: units.entrySet())
				{
					if (canSee(e.getValue().getxPosition(), e.getValue().getyPosition()))
						ids.add(e.getKey());
				}
			return ids;
		}
		/**
		 * Get the unit with the selected id if you can see it.
		 * @param unitID
		 * @return The unit with that ID, null if you can't see it or if it doesn't exist
		 */
		public Unit.UnitView getUnit(int unitID) {
			Unit u = state.getUnit(unitID);
			if (u==null)
				return null;
			if (!canSee(u.getxPosition(),u.getyPosition()))
				return null;
			return u.getView();
		}
		/**
		 * Get the IDs of all of the resource nodes that you can see
		 * @return
		 */
		public List<Integer> getAllResourceIds() {
			List<Integer> i = new ArrayList<Integer>();
			for(ResourceNode r : state.resourceNodes)
				if (canSee(r.getxPosition(),r.getyPosition()))
					i.add(r.getID());
			return i;
		}
		/**
		 * Get the IDs of all of the resource nodes of a type that you can see
		 * @param type Gold mine or Tree
		 * @return
		 */
		public List<Integer> getResourceNodeIds(ResourceNode.Type type) {
			List<Integer> i = new ArrayList<Integer>();
			for(ResourceNode r : state.resourceNodes)
				if (r.getType() == type && canSee(r.getxPosition(), r.getyPosition()))
					i.add(r.getID());
			return i;
		}
		
		/**
		 * Get the resource node with the selected ID (if you can see it)
		 * @param resourceID
		 * @return The resource node with id resourceID, or null if you can't see it or if there isn't one.
		 */
		public ResourceNode.ResourceView getResourceNode(int resourceID) {
			ResourceNode r =state.getResource(resourceID);
			if (r==null)
				return null;
			if (!canSee(r.getxPosition(),r.getyPosition()))
				return null;
			return state.getResource(resourceID).getView();
		}
		/**
		 * Get the ids of all the templates.
		 * If you are not an observer, it will only give you yours
		 * @return
		 */
		public List<Integer> getAllTemplateIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Entry<Integer, Template> e : state.allTemplates.entrySet())
			{
				if (!state.hasFogOfWar || e.getValue().getPlayer() == player || player == Agent.OBSERVER_ID)
					ids.add(e.getKey());
			}
			return ids;
		}
		
		/**
		 * Get a player's template IDs
		 * If you are not an observer, you can't get other people's
		 * @param playerid
		 * @return
		 */
		public List<Integer> getTemplateIds(int playerid) {
			if (state.hasFogOfWar && playerid != player && player != Agent.OBSERVER_ID)
				return null;
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Template> templates = state.getTemplates(playerid);
			if(templates != null)
				for(Integer key : templates.keySet())
				{
					ids.add(key);
				}
			return ids;
		}
		
		/**
		 * Get a template with a specific ID
		 * If you are not an observer, it won't work with somebody else's template.
		 * @param templateID
		 * @return
		 */
		public Template.TemplateView getTemplate(int templateID) {
			Template template = state.getTemplate(templateID);
			if (template == null)
				return null;
			if (state.hasFogOfWar && player != template.getPlayer() && player != Agent.OBSERVER_ID)
				return null;
			return template.getView();
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
		/**
		 * Get the amount of wood or gold (specified by the type) available to a specific player.
		 * If you are not an observer, it will not work on other people.
		 * @param player
		 * @param type The type of resource
		 * @return
		 */
		public Integer getResourceAmount(int playerid, ResourceType type) {
			if (state.hasFogOfWar && player != playerid && player != Agent.OBSERVER_ID)
				return null;
			return state.getResourceAmount(playerid, type);	
		}
		
		/**
		 * Get the amount of supply (food) used by a specific player.
		 * If you are not an observer, it will not work on other people.
		 * @param player
		 * @return
		 */
		public Integer getSupplyAmount(int playerid) {
			if (state.hasFogOfWar && player != playerid && player != Agent.OBSERVER_ID)
				return null;
			return state.getSupplyAmount(playerid);
		}
		/**
		 * Get the maximum amount of supply (food) available to a specific player.
		 * If you are not an observer, it will not work on other people.
		 * @param player
		 * @return
		 */
		public Integer getSupplyCap(int playerid) {
			if (state.hasFogOfWar && player != playerid && player != Agent.OBSERVER_ID)
				return null;
			return state.getSupplyCap(playerid);
		}
		/**
		 * Gets the closest position that you can see.
		 * @param x
		 * @param y
		 * @return
		 */
		public int[] getClosestOpenPosition(int x, int y) {
			
			return state.getClosestPosition(x, y);
		}
		/**
		 * Get how big the map is in the x direction
		 * @return
		 */
		public int getXExtent() {
			return state.getXExtent();
		}
		/**
		 * Get how big the map is in the y direction
		 * @return
		 */
		public int getYExtent() {
			return state.getYExtent();
		}
		/**
		 * Get whether a player has a unit of a certain type.  (Say, a tech building).
		 * If you are not an observer, then this will not work on other players
		 * @param player
		 * @param buildingtemplateid
		 * @return Whether the player with id playerid has a unit with a template with the template id templateid, or false if the player is not you
		 */
		public boolean doesPlayerHaveUnit(int playerid, int templateid) {
			if (state.hasFogOfWar && playerid!=this.player && this.player != Agent.OBSERVER_ID)
				return false;
			return state.doesPlayerHaveUnit(playerid, templateid);
		}
		/**
		 * Get whether a player has researched a specific upgrade.
		 * If you are not an observer, then this will not work on other players
		 * @param upgradeid
		 * @param playerid
		 * @return Whether the player has researched an upgrade with id upgradeid.  Always false if you try it on someone else.
		 */
		public boolean hasUpgrade(int upgradeid, int playerid) {
			if (state.hasFogOfWar && playerid!=this.player && this.player != Agent.OBSERVER_ID)
				return false;
			return state.hasUpgrade(upgradeid, playerid);
		}
		
		/**
		 * Find whether a position is in bounds.
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean inBounds(int x, int y) {
			return state.inBounds(x, y);
		}
		
		/**
		 * Find if there is a unit at a position
		 * @param x
		 * @param y
		 * @return Whether there is a unit at the position (always false if you can't see there)
		 */
		public boolean isUnitAt(int x, int y) {
			if (!canSee(x, y))
				return false;
			return state.unitAt(x, y) != null;
		}
		/**
		 * Get the unit at a position
		 * @param x
		 * @param y
		 * @return The unit's ID, or null if there is no unit (or if you can't see there)
		 */
		public Integer unitAt(int x, int y) {
			if (!canSee(x, y))
				return null;
			Unit unit = state.unitAt(x,y);
			return unit==null?null:unit.ID;
		}
		/**
		 * Find whether there is a resource at the position
		 * @param x
		 * @param y
		 * @return Whether there is a resource there (always false if you can't see there)
		 */
		public boolean isResourceAt(int x, int y) {
			if (!canSee(x, y))
				return false;
			return state.resourceAt(x, y) != null;
		}
		/**
		 * Get the resource at a position
		 * @param x
		 * @param y
		 * @return The resource's ID, or null if there is no resource or if you can't see the position
		 */
		public Integer resourceAt(int x, int y) {
			if (!canSee(x, y))
				return null;
			ResourceNode resource = state.resourceAt(x,y);
			return resource==null?null:resource.ID;
		}
		
	}
	public void recordBirth(Unit newunit, Unit builder) {
		int x = newunit.getxPosition();
		int y = newunit.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player))
			{
				getEventLog(player).recordBirth(newunit.ID, builder.ID, newunit.getPlayer());
			}
		}*/
		for(PlayerState playerState : playerStates.values())
		{
			if (canSee(x, y, playerState.playerNum))
			{
				playerState.getEventLogger().recordBirth(newunit.ID, builder.ID, newunit.getPlayer());
			}
		}
		observerState.getEventLogger().recordBirth(newunit.ID, builder.ID, newunit.getPlayer());
	}
	public void recordUpgrade(UpgradeTemplate upgradetemplate, Unit creator) {
		
		int x = creator.getxPosition();
		int y = creator.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player))
			{
				getEventLog(player).recordUpgrade(upgradetemplate.ID, upgradetemplate.getPlayer());
			}
		}*/
		for(PlayerState playerState : playerStates.values())
		{
			if (canSee(x, y, playerState.playerNum))
			{
				playerState.getEventLogger().recordUpgrade(upgradetemplate.ID, upgradetemplate.getPlayer());
			}
		}
		observerState.getEventLogger().recordUpgrade(upgradetemplate.ID, upgradetemplate.getPlayer());		
	}
	public void recordDamage(Unit u, Unit target, int damage) {
		
		int x = target.getxPosition();
		int y = target.getyPosition();
		int x2 = u.getxPosition();
		int y2 = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player) || canSee(x2, y2, player))
			{
				getEventLog(player).recordDamage(u.ID, u.getPlayer(), target.ID, target.getPlayer(), damage);
			}
		}*/
		for(PlayerState playerState : playerStates.values())
		{
			int player = playerState.playerNum;
			if (canSee(x, y, player) || canSee(x2, y2, player))
			{
				playerState.getEventLogger().recordDamage(u.ID, u.getPlayer(), target.ID, target.getPlayer(), damage);
			}
		}
		observerState.getEventLogger().recordDamage(u.ID, u.getPlayer(), target.ID, target.getPlayer(), damage);		
	}
	public void recordPickupResource(Unit u, ResourceNode resource, int amountPickedUp) {
		int x = resource.getxPosition();
		int y = resource.getyPosition();
		int x2 = u.getxPosition();
		int y2 = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player) || canSee(x2, y2, player))
			{
				getEventLog(player).recordPickupResource(u.ID, u.getPlayer(), resource.getResourceType(), amountPickedUp, resource.ID, resource.getType());;
			}
		}*/
		for(PlayerState playerState : playerStates.values())
		{
			int player = playerState.playerNum;
			if (canSee(x, y, player) || canSee(x2, y2, player))
			{
				playerState.getEventLogger().recordPickupResource(u.ID, 
																  u.getPlayer(), 
																  resource.getResourceType(), 
																  amountPickedUp, 
																  resource.ID, 
																  resource.getType());
			}
		}
		observerState.getEventLogger().recordPickupResource(u.ID, 
														    u.getPlayer(), 
														    resource.getResourceType(), 
														    amountPickedUp, 
														    resource.ID, 
														    resource.getType());
		
	}
	public void recordDeath(Unit u) {
		int x = u.getxPosition();
		int y = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player))
			{
				getEventLog(player).recordDeath(u.ID,u.getPlayer());
			}
		}*/

		for(PlayerState playerState : playerStates.values())
		{
			if (canSee(x, y, playerState.playerNum))
			{
				playerState.getEventLogger().recordDeath(u.ID,u.getPlayer());
			}
		}
		observerState.getEventLogger().recordDeath(u.ID,u.getPlayer());
	}
	public void recordExhaustedResourceNode(ResourceNode r) {
		
		int x = r.getxPosition();
		int y = r.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player))
			{
				getEventLog(player).recordExhaustedResourceNode(r.ID, r.getType());
			}
		}*/
		for(PlayerState playerState : playerStates.values())
		{
			if (canSee(x, y, playerState.playerNum))
			{
				playerState.getEventLogger().recordExhaustedResourceNode(r.ID, r.getType());
			}
		}
		observerState.getEventLogger().recordExhaustedResourceNode(r.ID, r.getType());
	}
	public void recordDropoffResource(Unit u, Unit townHall) {
		int x = townHall.getxPosition();
		int y = townHall.getyPosition();
		int x2 = u.getxPosition();
		int y2 = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (canSee(x, y, player) || canSee(x2, y2, player))
			{
				getEventLog(player).recordDropoffResource(u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
			}
		}*/
		for(PlayerState playerState : playerStates.values())
		{
			int player = playerState.playerNum;
			if (canSee(x, y, player) || canSee(x2, y2, player))
			{
				playerState.getEventLogger().recordDropoffResource(u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
			}
		}
		observerState.getEventLogger().recordDropoffResource(u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
		
	}
	public void forceRecalculateVision() {
		recalculateVisionFromScratch();
		
	}


	
	
}
