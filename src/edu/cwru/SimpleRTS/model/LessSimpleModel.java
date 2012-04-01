package edu.cwru.SimpleRTS.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionFeedback;
import edu.cwru.SimpleRTS.action.ActionQueue;
import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.LocatedAction;
import edu.cwru.SimpleRTS.action.LocatedProductionAction;
import edu.cwru.SimpleRTS.action.ProductionAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.StateCreator;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTask;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.Configuration;
import edu.cwru.SimpleRTS.util.ConfigurationValues;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
import edu.cwru.SimpleRTS.util.GameMap;
import edu.cwru.SimpleRTS.util.PreferencesConfigurationLoader;
/**
 * A less simple model that allows consistency, turn-taking and durative actions.
 * No longer supports unit tasks
 * Recalculates compound actions automatically every step.
 * Supports consistent actions* according to the following principles:
 * The order of actions does not affect their results,
 * Sets of actions may not cause invalid states (so some must fail),
 * The effects of one action succeeding may not change based on the actions of others**,
 * An set of actions that fails cannot be made to succeed by adding actions for other units.
 * 
 * From this, compound actions may only calculate primitives once in a turn, and only based on the previous state.
 * This makes compound actions much much less effective than in SimpleModel.
 * 
 * 
 * The particular solution in this makes sure that successes based on ranges (including directional actions with proximity) are calculated based on the previous state.
 * It also ensures that an action may not fail due to a unit involved being attacked for enough damage to kill it.
 * * Production/build actions that make new units fail consistency.  The units are placed sequentially in places that were empty last state in such a way as to not disrupt move actions.  In the situation where more productions exist than empty spaces that are not being moved into, then only the minimum number of production actions will fail
 * ** More generally, where s is the event "action has succeeded" and E is a set of possible results, and O is actions sent to other units, E given s must be independant of O, IE: for all O, P(E|s) = P(E|s,O)***
 * *** as an example that works, attacks, being based on sequential Random variables from a seed, will change exact result, but the distribution of successful attacks does not change based on other actions.  As a failing example, random movements that only fail on an actual collision wouldn't work, as the action of the other unit affects the distribution of effects. 
 * 
 */
public class LessSimpleModel implements Model {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -8289868580233478749L;
	private Random rand;
	private History history;
	private State state;
	private HashMap<Integer,HashMap<Integer, ActionQueue>> queuedActions; //ActionQueue uses equals and 
	private DurativePlanner planner;
	private StateCreator restartTactic;
	@SuppressWarnings("unused")
	private boolean verbose;
	private Configuration configuration;
	public LessSimpleModel(State init, int seed, StateCreator restartTactic) {
		state = init;
		history = new History();
		queuedActions = new HashMap<Integer,HashMap<Integer, ActionQueue>>();
		for (Integer i : state.getPlayers())
		{
			history.addPlayer(i);
			queuedActions.put(i, new HashMap<Integer,ActionQueue>());
		}
		rand = new Random(seed);
		planner = new DurativePlanner(init);
		
		this.restartTactic = restartTactic;
		verbose = false;
		configuration = PreferencesConfigurationLoader.loadConfiguration();
	}
	public void setVerbosity(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	

	
	@Override
	public void createNewWorld() {
		state = restartTactic.createState();
		history = new History();
		queuedActions = new HashMap<Integer,HashMap<Integer, ActionQueue>>();
		for (Integer i : state.getPlayers())
		{
			history.addPlayer(i);
			queuedActions.put(i, new HashMap<Integer,ActionQueue>());
		}
		
		planner = new DurativePlanner(state);
	}

	@Override
	public boolean isTerminated() {
		boolean terminated = true;
		if(ConfigurationValues.MODEL_CONQUEST.getBooleanValue(configuration))
			terminated = conquestTerminated();
		if(ConfigurationValues.MODEL_MIDAS.getBooleanValue(configuration))
			terminated = resourceGatheringTerminated();
		if(ConfigurationValues.MODEL_MANIFEST_DESTINY.getBooleanValue(configuration))
			terminated = buildingTerminated();
		
		terminated = terminated || 
					 state.getTurnNumber() > ConfigurationValues.MODEL_TIME_LIMIT.getIntValue(configuration);
		return terminated;
	}
	private boolean conquestTerminated() {
		int numLivePlayers = 0;
		for(Integer player : state.getPlayers())
		{
			if(state.getUnits(player).size() == 0)
			{
				continue;
			}
			for(Unit u : state.getUnits(player).values())
			{
				if(u.getCurrentHealth() > 0)
				{
					numLivePlayers++;
					break;
				}
			}
			if (numLivePlayers > 1)
				break;
			
		}
		return numLivePlayers <= 1;
	}
	private boolean resourceGatheringTerminated() {
		boolean resourcesGathered = true;
		int gold = ConfigurationValues.MODEL_REQUIRED_GOLD.getIntValue(configuration);
		int wood = ConfigurationValues.MODEL_REQUIRED_WOOD.getIntValue(configuration);
//		System.out.println("Agent.maxId() " + Agent.maxId());
		for(Integer player : state.getPlayers())
		{
			resourcesGathered = state.getResourceAmount(player, ResourceType.GOLD) >= gold &&
								state.getResourceAmount(player, ResourceType.WOOD) >= wood;
		}
		return resourcesGathered;
	}
	private boolean buildingTerminated() {
		boolean built = true;
		for(Integer i : state.getPlayers())
		{
			built = true;
			for(@SuppressWarnings("rawtypes") Template template : state.getTemplates(i).values())
			{
				int required = 0;
				if(configuration.containsKey("Required"+template.getName()+"Player"+i))
					required = configuration.getInt("Required"+template.getName()+"Player"+i);
				int actual = 0;
				if (required>0) //Only check if you need to find at least one
				{
					for(Unit u : state.getUnits(i).values())
					{
						if(u.getTemplate().equals(template))
							actual++;
						if(actual >= required) //if you found enough of a type of unit, you can stop looking for more
							break;
					}
				}
//				System.out.println("Player "+i+" has at least "+actual + "{"+template.getName()+"}s"+" (needed "+required+")");
				built = built && (actual >= required);
				if (!built) //if you haven't built one of the requirements, you can't have built all of them
					break;
			}
			if (built)
				break;
		}
		return built;
	}
	@Override
	public void addActions(Map<Integer, Action> actions, int sendingPlayerNumber) {
		for (Entry<Integer, Action> aent : actions.entrySet()) {
			
			int unitId = aent.getKey();
			Action a = aent.getValue();
			history.recordCommandRecieved(sendingPlayerNumber, state.getTurnNumber(), a);
			//If the unit is not the same as in the action, ignore the action
			if (a.getUnitId() != unitId)
			{
				history.recordActionFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDUNIT));
				continue;
			}
			//If the unit does not exist, ignore the action
			else if (state.getUnit(unitId) == null)
			{
				history.recordActionFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDUNIT));
				continue;
			}
			//If the unit is not the player's, ignore the action
			else if(state.getUnit(unitId).getPlayer() != sendingPlayerNumber)
			{
				history.recordActionFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDCONTROLLER));
				continue;
			}
			else
			{//Valid
				ActionQueue queue = new ActionQueue(a, null);
				queuedActions.get(sendingPlayerNumber).put(unitId, queue);
			}
			
			
		}
	}
	private LinkedList<Action> calculatePrimitives(Action action) {
		LinkedList<Action> primitives = null;
		Unit actor = state.getUnit(action.getUnitId());
		if (actor != null)
		{
		switch (action.getType()) {
			case PRIMITIVEMOVE:
			case PRIMITIVEATTACK:
			case PRIMITIVEGATHER:
			case PRIMITIVEDEPOSIT:
			case PRIMITIVEBUILD:
			case PRIMITIVEPRODUCE:
			case FAILED:
				//The only primitive action needed to execute a primitive action is itself
				primitives = new LinkedList<Action>();
				primitives.add(action);
				break;
			case COMPOUNDMOVE:
				LocatedAction aMove = (LocatedAction)action;
				primitives = planner.planMove(actor, aMove.getX(), aMove.getY());
				break;
			case COMPOUNDGATHER:
				TargetedAction aGather = (TargetedAction)action;
				int resourceId = aGather.getTargetId();
				primitives = planner.planGather(actor, state.getResource(resourceId));
				break;
			case COMPOUNDATTACK:
				TargetedAction aAttack = (TargetedAction)action;
				int targetId = aAttack.getTargetId();
				primitives = planner.planAttack(actor, state.getUnit(targetId));
				break;
			case COMPOUNDPRODUCE:
				ProductionAction aProduce = (ProductionAction)action;
				int unitTemplateId = aProduce.getTemplateId();
				primitives = planner.planProduce(actor, (UnitTemplate)state.getTemplate(unitTemplateId));
				break;
			case COMPOUNDBUILD:
				LocatedProductionAction aBuild = (LocatedProductionAction)action;
				int buildTemplateId = aBuild.getTemplateId();
				primitives = planner.planBuild(actor, aBuild.getX(), aBuild.getY(), (UnitTemplate)state.getTemplate(buildTemplateId));
				break;
			case COMPOUNDDEPOSIT:
				TargetedAction aDeposit = (TargetedAction)action;
				int depotId = aDeposit.getTargetId();
				primitives = planner.planDeposit(actor, state.getUnit(depotId));
				break;
			default:
				primitives = null;
		}
		}
		return primitives;
	}
	
	/**
	 * The main loop of the engine.
	 * Removes the old views
	 */
	@Override
	public void executeStep() {
		
		//if for some reason you start getting views before you are done changing things, then you will need to deprecate them again
		//Set each agent to have no task
		for (Unit u : state.getUnits().values()) {
			u.setTask(UnitTask.Idle);
		}
		
		//Run the Actions
		//This is based on 3 factors: the last state, spaces/resources/nodes with pending actions that are not known to be failures, and spaces/resources/nodes that are known to be failures
		
		//The basic procedure is (list and set are used colloquially, and may be implemented as other things):
			//if compound, check if you can do the action in the last state, recalculate if needed
			//if it is the last one in a compound,
			//check if you can do the action in general/based on last state
				//if you can't, put yourself on a fail list and stop processing this action
			//if you are a durative action that won't complete this step, put yourself on the successful list then stop processing this action
			//check if what you would effect is in the list of those with known problematic claims
				//if it is problematic, then put yourself on the fail list and stop processing this action
			//add your effect to the claims list
			//check if the claims including yourself will now cause a problem
				//if it will, mark yourself and all others claiming it on the failed list (removing them from the successful list too), and remove it from the claims list and put it on the known problematic list
				//if it will not cause a problem, mark yourself on the successful list and stop processing this action
		//after all actions have been checked and claimed
			//execute and log all actions remaining in the successful list
			//log all failed actions, and remove them from the queue
			
		Integer[] playersWhoseTurnItIs= state.getPlayers();
		//interpret coordinates as integers
		Map<Integer,ActionQueue> claimedspaces=new HashMap<Integer,ActionQueue>(); //merge the boolean for whether it has claimed with the set of actions that claimed it, and since one is known to be enough, don't need a set
		Set<Integer> problemspaces=new HashSet<Integer>();//places that you know are problems
		Map<Integer,Integer> claimedgathering=new HashMap<Integer,Integer>();//<nodeid,amountclaimed>
		Map<Integer,Set<ActionQueue>> claimedgatheringactions=new HashMap<Integer,Set<ActionQueue>>();//<nodeid,claimingactions>
		Set<Integer> problemgatherings=new HashSet<Integer>();//<problemnodeids>
		Map<Integer,Map<ResourceType,Integer>> claimedcosts=new HashMap<Integer,Map<ResourceType,Integer>>();//<player,<resourcetype,amountclaimed>>
		Map<Integer,Map<ResourceType,Set<ActionQueue>>> claimedcostactions=new HashMap<Integer,Map<ResourceType,Set<ActionQueue>>>();//<player,<resourcetypes,claimingactions>>
		Map<Integer,Set<ResourceType>> problemcosts=new HashMap<Integer,Set<ResourceType>>();//<player, problemresourcetypes>
		Map<Integer,Integer> claimedfoodcosts=new HashMap<Integer,Integer>();//<player,amountclaimed>
		Map<Integer,Set<ActionQueue>> claimedfoodcostactions=new HashMap<Integer,Set<ActionQueue>>();//<player,claimingactions>
		Set<Integer> problemfoodcosts=new HashSet<Integer>();//<problemplayer>
		Set<ActionQueue> failed=new HashSet<ActionQueue>();
		Set<ActionQueue> successfulsofar=new HashSet<ActionQueue>();
		/** Track all units of active players, to reset their progress if they failed or weren't moved*/Set<Integer> unsuccessfulUnits = new HashSet<Integer>(); 
		Set<ActionQueue> productionsuccessfulsofar=new HashSet<ActionQueue>();
		for (Integer player : playersWhoseTurnItIs)
		{
			//Gather all units of that player, so that we can remove the ones that were successful later
			for (Integer id : state.getUnits(player).keySet())
			{
				unsuccessfulUnits.add(id);
			}
			Iterator<Entry<Integer,ActionQueue>> playerActions=queuedActions.get(player).entrySet().iterator();
			while(playerActions.hasNext())
			{
				Entry<Integer,ActionQueue> entry = playerActions.next();;
				ActionQueue aq =  entry.getValue();
//				if (a==null) //Then it failed to calculate primitives, so it fails
				int uid = entry.getKey();
				Unit u = state.getUnit(uid);
				if (u == null || uid!=aq.getFullAction().getUnitId())
				{
					//unit is dead or never existed
					playerActions.remove();
					history.recordActionFeedback(player, state.getTurnNumber(), new ActionResult(aq.getFullAction(),ActionFeedback.INVALIDUNIT));
					
				}
				else
				{
					aq.resetPrimitives(calculatePrimitives(aq.getFullAction()));
					Action a = aq.peekPrimitive();
				
				if (!ActionType.isPrimitive(a.getType()))
				{
					throw new RuntimeException("This should never happen, all subactions should be primitives");
				}
				if (a.getType() == ActionType.PRIMITIVEATTACK && !(a instanceof TargetedAction)
						|| a.getType() == ActionType.PRIMITIVEGATHER && !(a instanceof DirectedAction)
						|| a.getType() == ActionType.PRIMITIVEDEPOSIT && !(a instanceof DirectedAction)
						|| a.getType() == ActionType.PRIMITIVEPRODUCE&& !(a instanceof ProductionAction)
						|| a.getType() == ActionType.PRIMITIVEBUILD && !(a instanceof ProductionAction)
						|| a.getType() == ActionType.PRIMITIVEMOVE && !(a instanceof DirectedAction))
				{//shouldn't have to do this, should make actions so it is never possible to have the types not match
					//log a wrong type thing
					history.recordActionFeedback(player, state.getTurnNumber(), new ActionResult(aq.getFullAction(),ActionFeedback.INVALIDTYPE));
					//remove it from the queues
					playerActions.remove();
				}
				else
				{
					
					if (a.getType() == ActionType.FAILED || a.getType() == ActionType.FAILEDPERMANENTLY)
					{
						failed.add(aq);
						//recalcAndStuff();//This marks a place where recalculation would be called for
					}
					//check if it is a move
					if (a.getType() == ActionType.PRIMITIVEMOVE)
					{
						//if it can't move, that is a problem
						if (!u.canMove())
						{
							failed.add(aq);
							//recalcAndStuff();//This marks a place where recalculation would be called for
						}
						else //hasn't failed yet
						{
							//find out where it will be next
							
							DirectedAction da =(DirectedAction)a;
							Direction d = da.getDirection();
							int xdest = u.getxPosition() + d.xComponent();
							int ydest = u.getyPosition() + d.yComponent();
							
							//if it is not empty there is a problem
							if (!empty(xdest, ydest))
							{ 
								failed.add(aq);
								//recalcAndStuff();//This marks a place where recalculation would be called for
							}
							else //hasn't failed yet
							{
								int newdurativeamount;
								if (da.equals(u.getActionProgressPrimitive()))
								{
									newdurativeamount = u.getActionProgressAmount()+1;
								}
								else
								{
									newdurativeamount = 1;
								}
								boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateMoveDuration(u,u.getxPosition(),u.getyPosition(), d);
								//if it will finish, then verify claim stuff
								if (willcompletethisturn)
								{
									Integer dest = getCoordInt(xdest,ydest);
									//check if the space is a problem
									if (problemspaces.contains(dest))
									{
										failed.add(aq);
									}
									else //not a problem space
									{
										//check if it is claimed
										ActionQueue priorclaimant = claimedspaces.get(dest);
										if (priorclaimant != null)
										{//it is claimed
											successfulsofar.remove(priorclaimant);
											failed.add(priorclaimant);
											failed.add(aq);
											problemspaces.add(dest);
											claimedspaces.remove(dest); //remove all claims, as it is now a problem, not a claim, may be pointless
										}
										else
										{//it is not claimed
											//so claim it
											claimedspaces.put(dest,aq);
											successfulsofar.add(aq);
										}
									}
								}
								else //won't complete, so passes all claims
								{
									successfulsofar.add(aq);
								}
							}
						}
					}
				
				else if (a.getType() == ActionType.PRIMITIVEDEPOSIT)
				{
					if (!u.canGather() || u.getCurrentCargoAmount() <= 0)
					{//if can't gather or isn't carrying anything, then this isn't an acceptible action
						failed.add(aq);
						//recalcAndStuff();//This marks a place where recalculation would be called for
					}
					else
					{
						DirectedAction da =(DirectedAction)a;
						Direction d = da.getDirection();
						int xdest = u.getxPosition() + d.xComponent();
						int ydest = u.getyPosition() + d.yComponent();
						Unit townHall = state.unitAt(xdest, ydest);
						if (townHall == null || townHall.getPlayer() != u.getPlayer())
						{//no unit there on your team
							failed.add(aq);
							//recalcAndStuff();//This marks a place where recalculation would be called for
						}
						else //there is a unit on your team
						{
							//check if the unit can accept the kind of resources that you have
							boolean canAccept=false;
							if (u.getCurrentCargoType() == ResourceType.GOLD && townHall.getTemplate().canAcceptGold())
								canAccept=true;
							else if (u.getCurrentCargoType() == ResourceType.WOOD && townHall.getTemplate().canAcceptWood())
								canAccept=true;
							if (!canAccept)
							{//then it isn't a town hall of the right type
								failed.add(aq);
								//recalcAndStuff();//This marks a place where recalculation would be called for
							}
							else //there is an appropriate town hall there
							{
								//deposit has no chance of conflicts, so this works
								successfulsofar.add(aq);
							}
						}
					}
				}
				else if (a.getType() == ActionType.PRIMITIVEATTACK)
				{
					//make sure you can attack and the target exists and is in range in the last state
					if (!u.canAttack())
					{
						failed.add(aq);
						//recalcAndStuff();//This marks a place where recalculation would be called for
					}
					else
					{
						TargetedAction ta =(TargetedAction)a;
						Unit target = state.getUnit(ta.getTargetId());
						if (target == null || target.getCurrentHealth() <= 0 || !inRange(u, target))
						{
							failed.add(aq);
							//recalcAndStuff();//This marks a place where recalculation would be called for
						}
						else //target exists and is in range
						{
							//no possibility for conflict, so this succeeds
							successfulsofar.add(aq);
						}
					}
				}
				else if (a.getType() == ActionType.PRIMITIVEPRODUCE || a.getType() == ActionType.PRIMITIVEBUILD)
				{//currently, this adds to productionsuccessfulsofar because they are not processed consistantly
				//consistancy could be restored by making unit production and building actions require a place or direction for the new unit to go, and then processing it as a move
					
					//last state check:
					ProductionAction pa =(ProductionAction)a;
					@SuppressWarnings("rawtypes")
					Template t = state.getTemplate(pa.getTemplateId());
					if (a.getType() == ActionType.PRIMITIVEPRODUCE && u.canBuild()|| a.getType() == ActionType.PRIMITIVEBUILD && !u.canBuild())
					{//if it should build and is trying to produce or should produce and is trying to build
						failed.add(aq);
						//recalcAndStuff();//This marks a place where recalculation would be called for
					}
					else if (t==null || !u.getTemplate().canProduce(t) || !t.canProduce(state.getView(Agent.OBSERVER_ID)))
					{//if the template does not exist or the unit cannot make the template or the template's prerequisites are not met
						failed.add(aq);
						//recalcAndStuff();//This marks a place where recalculation would be called for
					}
					else //template exists, is producable by the unit, and has it's tech-tree prerequisites met
					{
						int newdurativeamount;
						if (pa.equals(u.getActionProgressPrimitive()))
						{
							newdurativeamount = u.getActionProgressAmount()+1;
						}
						else
						{
							newdurativeamount = 1;
						}
						boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateProductionDuration(u,t);
						if (willcompletethisturn)
						{
							if (!problemcosts.containsKey(player))
								problemcosts.put(player, new HashSet<ResourceType>());
							if (!claimedcosts.containsKey(player))
								claimedcosts.put(player, new HashMap<ResourceType, Integer>());
							if (!claimedcostactions.containsKey(player))
								claimedcostactions.put(player, new HashMap<ResourceType, Set<ActionQueue>>());
							boolean failedaclaim=false;
							
							//check all the resources, including supply for problems and claims
							//note that if you don't need any, it doesn't matter if it is overdrawn
							//do all even if one fails, because if you stop checking when you fail one resource and don't claim the others, then another production that should conflict will not be detected as such
							{
								int goldneeded = t.getGoldCost();
								//if you have a cost, then check the claims
								if (goldneeded > 0)
								{
									//check if it is a problem
									if (problemcosts.get(player).contains(ResourceType.GOLD))
									{
										failedaclaim=true;
									}
									else
									{//not a problem already, check claims
										//get the amount of the resource that you had before
										int previousamount = state.getResourceAmount(player, ResourceType.GOLD);
										// get the previous claim (if there is none, that is zero)
										Integer previousclaim = claimedcosts.get(player).get(ResourceType.GOLD); if (previousclaim==null) previousclaim=0;
										int updatedclaim=previousclaim+goldneeded;
										
										//check if the total claim is more than the amount the player has
										if (updatedclaim > previousamount)
										{
											//if the claim is more, then this and all others with claims on this resource fail
											Set<ActionQueue> otherclaimants = claimedcostactions.get(player).get(ResourceType.GOLD);
											if (otherclaimants != null)
											{
												for (ActionQueue otherclaimant : otherclaimants)
												{
													productionsuccessfulsofar.remove(otherclaimant);
													failed.add(otherclaimant);
												}
												//since we are marking this as a problem, don't need the claim anymore
												claimedcostactions.get(player).remove(ResourceType.GOLD);
											}
											failedaclaim=true;
											problemcosts.get(player).add(ResourceType.GOLD);
										}
										else
										{//not too much, so claim it
											claimedcosts.get(player).put(ResourceType.GOLD, updatedclaim);
											if (!claimedcostactions.get(player).containsKey(ResourceType.GOLD))
											{
												claimedcostactions.get(player).put(ResourceType.GOLD, new HashSet<ActionQueue>());
											}
											claimedcostactions.get(player).get(ResourceType.GOLD).add(aq);
										}
									}
								}
							}
								{
									int woodneeded = t.getWoodCost();
									//if you have a cost, then check the claims
									if (woodneeded > 0)
									{
										//check if it is a problem
										if (problemcosts.get(player).contains(ResourceType.WOOD))
										{
											failedaclaim=true;
										}
										else
										{//not a problem already, check claims
											//get the amount of the resource that you had before
											int previousamount = state.getResourceAmount(player, ResourceType.WOOD);
											// get the previous claim (if there is none, that is zero)
											Integer previousclaim = claimedcosts.get(player).get(ResourceType.WOOD); if (previousclaim==null) previousclaim=0;
											int updatedclaim=previousclaim+woodneeded;
											
											//check if the total claim is more than the amount the player has
											if (updatedclaim > previousamount)
											{
												//if the claim is more, then this and all others with claims on this resource fail
												Set<ActionQueue> otherclaimants = claimedcostactions.get(player).get(ResourceType.WOOD);
												if (otherclaimants != null)
												{
													for (ActionQueue otherclaimant : otherclaimants)
													{
														productionsuccessfulsofar.remove(otherclaimant);
														failed.add(otherclaimant);
													}
													//since we are marking this as a problem, don't need the claim anymore
													claimedcostactions.get(player).remove(ResourceType.WOOD);
												}
												failedaclaim=true;
												problemcosts.get(player).add(ResourceType.WOOD);
											}
											else
											{//not too much, so claim it
												claimedcosts.get(player).put(ResourceType.WOOD, updatedclaim);
												if (!claimedcostactions.get(player).containsKey(ResourceType.WOOD))
												{
													claimedcostactions.get(player).put(ResourceType.WOOD, new HashSet<ActionQueue>());
												}
												claimedcostactions.get(player).get(ResourceType.WOOD).add(aq);
											}
										}
									}
							}
							{
								int foodneeded = t.getFoodCost();
								//if you have a cost, then check the claims
								if (foodneeded > 0)
								{
									//check if it is a problem
									if (problemfoodcosts.contains(player))
									{
										failedaclaim=true;
									}
									else
									{//not a problem already, check claims
										//get the amount of the resource that you had before
										int previousamount = state.getSupplyCap(player)-state.getSupplyAmount(player);
										// get the previous claim (if there is none, that is zero)
										Integer previousclaim = claimedfoodcosts.get(player); if (previousclaim==null) previousclaim=0;
										int updatedclaim=previousclaim+foodneeded;
										
										//check if the total claim is more than the amount the player has
										if (updatedclaim > previousamount)
										{
											//if the claim is more, then this and all others with claims on this resource fail
											Set<ActionQueue> otherclaimants = claimedfoodcostactions.get(player);
											if (otherclaimants != null)
											{
												for (ActionQueue otherclaimant : otherclaimants)
												{
													productionsuccessfulsofar.remove(otherclaimant);
													failed.add(otherclaimant);
												}
												//since we are marking this as a problem, don't need the claim anymore
												claimedfoodcostactions.remove(player);
											}
											failedaclaim=true;
											problemfoodcosts.add(player);
										}
										else
										{//not too much, so claim it
											claimedfoodcosts.put(player, updatedclaim);
											if (!claimedfoodcostactions.containsKey(player))
											{
												claimedfoodcostactions.put(player, new HashSet<ActionQueue>());
											}
											claimedfoodcostactions.get(player).add(aq);
										}
									}
								}
							}
							
							if (failedaclaim)
							{
								failed.add(aq);
							}
							else
							{
								productionsuccessfulsofar.add(aq);
							}
							
						}
						else //won't complete, so passes all claims
						{
							successfulsofar.add(aq);
						}
					}
					
				}
				else if (a.getType() == ActionType.PRIMITIVEGATHER)
				{
					//check if it can gather at all
					if (!u.canGather())
					{
						failed.add(aq);
						//recalcAndStuff();//This marks a place where recalculation would be called for
					}
					else //it can gather
					{
						//find the node you want to gather from, and make sure it exists
						DirectedAction da =(DirectedAction)a;
						Direction d = da.getDirection();
						int xdest = u.getxPosition() + d.xComponent();
						int ydest = u.getyPosition() + d.yComponent();
						ResourceNode rn  = state.resourceAt(xdest,ydest);
						//check if the node exists and was not exhausted last turn
						if (rn==null || rn.getAmountRemaining() <= 0)
						{
							failed.add(aq);
							//recalcAndStuff();//This marks a place where recalculation would be called for
						}
						else //there is a node and it has resources
						{
							int newdurativeamount;
							if (da.equals(u.getActionProgressPrimitive()))
							{
								newdurativeamount = u.getActionProgressAmount()+1;
							}
							else
							{
								newdurativeamount = 1;
							}
							boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateGatherDuration(u,rn);
							if (willcompletethisturn)
							{
								
								
								//then check if the node will be a problem
								if (problemgatherings.contains(rn.ID))
								{
									failed.add(aq);
								}
								else //no problem yet
								{
									
									
									//so test out the new claim
									int previousamount = rn.getAmountRemaining();
									boolean isotherclaimant=true;
									Integer otherclaims = claimedgathering.get(rn.ID);
									//if there is no other claim, then the other claim is 0, and it should be noted that noone else is claiming it
									if (otherclaims == null)
									{
										isotherclaimant=false;
										otherclaims=0;
									}
									int updatedclaim = otherclaims + u.getTemplate().getGatherRate(rn.getType());
									//if the claim is too much, then the node has a problem
										//but don't fail if this is the only claimant
											//in that case, the result should be that this mines out the resource
									if (updatedclaim > previousamount && isotherclaimant)
									{
										//the node is a problem, so make all claimants fail and mark it as such
										problemgatherings.add(rn.ID);
										for (ActionQueue otherclaimant :claimedgatheringactions.get(rn.ID))
										{
											successfulsofar.remove(otherclaimant);
											failed.add(otherclaimant);
										}
										failed.add(aq);
										claimedgatheringactions.remove(rn.ID);
									}
									else //the state isn't a problem
									{
										//so make the claim and succeed
										claimedgathering.put(rn.ID, updatedclaim);
										//make sure the set is initialized
										if (!claimedgatheringactions.containsKey(rn.ID))
											claimedgatheringactions.put(rn.ID, new HashSet<ActionQueue>());
										claimedgatheringactions.get(rn.ID).add(aq);
										successfulsofar.add(aq);
									}
								}
								
							}
							else //won't complete, so passes all claims
							{
								successfulsofar.add(aq);
							}
						}
					}
				}
				}
				}
			}
		}
		
		//to make production spawning as consistant yet sequential as possible, find positions that weren't occupied before and which weren't claimed by moves or other production actions
		//need to avoid claimed spaces so that the inconsistancy doesn't break move's consistancy
		//so calculate now and use later
		Map<ActionQueue,int[]> productionplaces = new HashMap<ActionQueue,int[]>();
		
		{
			Set<Integer> productionclaimedspaces=new HashSet<Integer>();
			boolean nomorespaces=false;//once you run out of spaces, no further productions that make units will succeed
			Set<Integer> moveclaimedspaces = claimedspaces.keySet(); //grab the spaces claimed by move, it shouldn't change during this
			for (ActionQueue aq : productionsuccessfulsofar)
			{
				//only production actions that will complete should be in productionsuccessfulsofar
				ProductionAction a = (ProductionAction)aq.peekPrimitive(); 
				Unit u = state.getUnit(a.getUnitId());
				//check if it is an upgrade and thus doesn't risk failure and can just succeed
				if (state.getTemplate(a.getTemplateId()) instanceof UpgradeTemplate)
				{
					successfulsofar.add(aq);
				}
				else //will be a unit/building, needs to reserve a space
				{
					if (nomorespaces)
					{//if you ran out of spaces before, then there is no point trying again, as it will be no better
						failed.add(aq);
					}
					else
					{
						//find the nearest open position, which will be null if there is none
						int[] newposition = getClosestEmptyUnclaimedPosition(u.getxPosition(), u.getyPosition(), moveclaimedspaces, productionclaimedspaces);
						if (newposition == null)
						{//if no place for new unit
							//then this fails
							failed.add(aq);
							nomorespaces = true;
						}
						else //there is a place for the new unit
						{
							//so reserve the new position and mark as successful
							productionplaces.put(aq, newposition);
							productionclaimedspaces.add(getCoordInt(newposition[0],newposition[1]));
							successfulsofar.add(aq);
						}
					}
					
				}
			}
		}
		
		//Take all of the actions that haven't failed yet and execute them
		for (ActionQueue aq : successfulsofar)
		{
			//Mark it's unit as having moved successfully
			unsuccessfulUnits.remove(aq.getFullAction().getUnitId());
			
			//execute it without further checking, logging it
			Action a = aq.popPrimitive();
			int uid = a.getUnitId();
			Unit u = state.getUnit(uid);
			boolean willcompletethisturn = true;
			{
				//check if it is a move
				if (a.getType() == ActionType.PRIMITIVEMOVE)
				{
					//if it can't move, that is a problem
					{
						//find out where it will be next
						
						DirectedAction da =(DirectedAction)a;
						Direction d = da.getDirection();
						//calculate the amount of duration
						int newdurativeamount;
						if (da.equals(u.getActionProgressPrimitive()))
						{
							newdurativeamount = u.getActionProgressAmount()+1;
						}
						else
						{
							newdurativeamount = 1;
						}
						willcompletethisturn = newdurativeamount== DurativePlanner.calculateMoveDuration(u,u.getxPosition(),u.getyPosition(),d);
						//if it will finish, then execute the atomic action
						if (willcompletethisturn)
						{
							//do the atomic action
							state.moveUnit(u, d);
							//you did the action, so reset the progress
							u.resetDurative();
						}
						else
						{
							//increment the duration
							u.setDurativeStatus(da, newdurativeamount);
						}
					}
				}
			
			if (a.getType() == ActionType.PRIMITIVEDEPOSIT)
			{
				DirectedAction da =(DirectedAction)a;
				Direction d = da.getDirection();
				int xdest = u.getxPosition() + d.xComponent();
				int ydest = u.getyPosition() + d.yComponent();
				Unit townHall = state.unitAt(xdest, ydest);

				//calculate the amount of duration
					int newdurativeamount;
					if (da.equals(u.getActionProgressPrimitive()))
					{
						newdurativeamount = u.getActionProgressAmount()+1;
					}
					else
					{
						newdurativeamount = 1;
					}
					willcompletethisturn = newdurativeamount== DurativePlanner.calculateDepositDuration(u,townHall);
				//if it will finish, then execute the atomic action
				if (willcompletethisturn)
				{
					//do the atomic action
					int player = townHall.getPlayer();
					history.recordDropoffResource(u, townHall, state);
					state.depositResources(player, u.getCurrentCargoType(), u.getCurrentCargoAmount());
					u.clearCargo();
					//you completed the action, so reset the durative progress
					u.resetDurative();
				}
				else
				{
					//increment the duration
					u.setDurativeStatus(da, newdurativeamount);
				}
			}
			if (a.getType() == ActionType.PRIMITIVEATTACK)
			{
				//make sure you can attack and the target exists and is in range in the last state
				TargetedAction ta =(TargetedAction)a;
				Unit target = state.getUnit(ta.getTargetId());
				int newdurativeamount;
				if (ta.equals(u.getActionProgressPrimitive()))
				{
					newdurativeamount = u.getActionProgressAmount()+1;
				}
				else
				{
					newdurativeamount = 1;
				}
				willcompletethisturn = newdurativeamount== DurativePlanner.calculateAttackDuration(u,target);
				//if it will finish, then execute the atomic action
				if (willcompletethisturn)
				{
					//do the atomic action
					int damage = calculateDamage(u,target);
					history.recordDamage(u, target, damage, state);
					target.setHP(Math.max(target.getCurrentHealth()-damage,0));
					//you have finished the primitive, so progress resets
					u.resetDurative();
				}
				else
				{
					//increment the duration
					u.setDurativeStatus(ta, newdurativeamount);
				}
			}
			if (a.getType() == ActionType.PRIMITIVEPRODUCE || a.getType() == ActionType.PRIMITIVEBUILD)
			{
				//last state check:
				ProductionAction pa =(ProductionAction)a;
				@SuppressWarnings("rawtypes")
				Template t = state.getTemplate(pa.getTemplateId());
				//the willcomplete is somewhat related to the production amount
				int newdurativeamount;
				if (pa.equals(u.getActionProgressPrimitive()))
				{
					newdurativeamount = u.getActionProgressAmount()+1;
				}
				else
				{
					newdurativeamount = 1;
				}
				willcompletethisturn = newdurativeamount== DurativePlanner.calculateProductionDuration(u,t);
				//if it will finish, then execute the atomic action
				if (willcompletethisturn)
				{
					//do the atomic action
					if (t instanceof UnitTemplate)
					{
						Unit produced = ((UnitTemplate)t).produceInstance(state);
						int[] newxy = productionplaces.get(aq);
						if (u.canBuild())
						{
							int oldx = u.getxPosition();
							int oldy = u.getyPosition();
							state.transportUnit(u, newxy[0], newxy[1]);
							if (state.tryProduceUnit(produced,oldx,oldy))
							{
								history.recordBirth(produced, u, state);
							}
						}
						else
						{
							if (state.tryProduceUnit(produced,newxy[0],newxy[1]))
							{
								history.recordBirth(produced, u, state);
							}
						}
					}
					else if (t instanceof UpgradeTemplate) {
						UpgradeTemplate upgradetemplate = ((UpgradeTemplate)t);
						if (state.tryProduceUpgrade(upgradetemplate.produceInstance(state)))
						{
							history.recordUpgrade(upgradetemplate,u, state);
						}
					}
					//you have finished the primitive, so progress resets
					u.resetDurative();
				}
				else
				{
					//increment the duration
					u.setDurativeStatus(pa, newdurativeamount);
				}
			}
			if (a.getType() == ActionType.PRIMITIVEGATHER)
			{
				//check if it can gather at all
				//find the right node
				DirectedAction da =(DirectedAction)a;
				Direction d = da.getDirection();
				int xdest = u.getxPosition() + d.xComponent();
				int ydest = u.getyPosition() + d.yComponent();
				ResourceNode rn  = state.resourceAt(xdest,ydest);
				int newdurativeamount;
				if (da.equals(u.getActionProgressPrimitive()))
				{
					newdurativeamount = u.getActionProgressAmount()+1;
				}
				else
				{
					newdurativeamount = 1;
				}
				willcompletethisturn = newdurativeamount== DurativePlanner.calculateGatherDuration(u,rn);
				//if it will finish, then execute the atomic action
				if (willcompletethisturn)
				{
					//do the atomic action
					int amountPickedUp = rn.reduceAmountRemaining(u.getTemplate().getGatherRate(rn.getType()));
					u.setCargo(rn.getResourceType(), amountPickedUp);
					history.recordPickupResource(u, rn, amountPickedUp, state);
					//you have finished the primitive, so progress resets
					u.resetDurative();
				}
				else
				{
					//increment the duration
					u.setDurativeStatus(da, newdurativeamount);
				}

			}
			}
			history.recordPrimitiveExecuted(u.getPlayer(), state.getTurnNumber(), a);
			
			ActionFeedback feedback;
			if (!aq.hasNext())
			{
				queuedActions.get(u.getPlayer()).remove(aq);
				feedback = willcompletethisturn?ActionFeedback.COMPLETED:ActionFeedback.INCOMPLETE;
					
				
			}
			else
			{
				feedback = ActionFeedback.INCOMPLETE;
			}
			history.recordActionFeedback(state.getUnit(aq.getFullAction().getUnitId()).getPlayer(), state.getTurnNumber(), new ActionResult(aq.getFullAction(),feedback));
			history.recordActionFeedback(state.getUnit(aq.getFullAction().getUnitId()).getPlayer(), state.getTurnNumber(), new ActionResult(a,feedback));
		}
		for (ActionQueue aq : failed)
		{
			//should be safe to get the unitid, as it should have not been put into failed if the player was bad
			history.recordActionFeedback(state.getUnit(aq.getFullAction().getUnitId()).getPlayer(), state.getTurnNumber(), new ActionResult(aq.getFullAction(),ActionFeedback.FAILED));
			history.recordActionFeedback(state.getUnit(aq.getFullAction().getUnitId()).getPlayer(), state.getTurnNumber(), new ActionResult(aq.peekPrimitive(),ActionFeedback.FAILED));
			queuedActions.get(state.getUnit(aq.getFullAction().getUnitId()).getPlayer()).remove(aq.getFullAction().getUnitId());
		}
		
		
		//Take all the dead units and clear them
		//Find the dead units
		Map<Integer, Unit> allunits = state.getUnits();
		List<Integer> dead= new ArrayList<Integer>(allunits.size());
		for (Unit u : allunits.values()) {
			if (u.getCurrentHealth() <= 0)
			{
				history.recordDeath(u, state);
				dead.add(u.ID);
			}
		}
		//Remove them
		for (int uid : dead)
		{
			state.removeUnit(uid);
		}
		//Take all of the used up resources and get rid of them
		List<ResourceNode> allnodes = state.getResources();
		List<Integer> usedup= new ArrayList<Integer>(allnodes.size());
		for (ResourceNode r : allnodes) {
			if (r.getAmountRemaining() <= 0)
			{
				history.recordExhaustedResourceNode(r, state);
				usedup.add(r.ID);
			}
		}
		//Remove the used up resource nodes
		for (int rid : usedup)
		{
			
			state.removeResourceNode(rid);
		}
		
		//Reset the progress of any unit of an active player that was unable to successfully move
		for (Integer id : unsuccessfulUnits)
		{
			//Grab the unsuccessful unit
			Unit slacker = state.getUnit(id);
			//Reset the progress of the unsuccessful unit if it didn't die or something
			if (slacker!=null)
			{
				slacker.resetDurative();
			}
		}
		
		state.incrementTurn();
		for (Unit u : state.getUnits().values()) {
			u.deprecateOldView();
		}
		//Set each template to not keep the old view
		for (Integer player : state.getPlayers())
			for (@SuppressWarnings("rawtypes") Template t : state.getTemplates(player).values())
				t.deprecateOldView();
	}

	
	
	/**
	 * More or less duplicates the functionality of getClosestPosition in state, with claims.
	 * Also returns null instead of -1,-1 if nothing is available.
	 * @param x
	 * @param y
	 * @param claims
	 * @param otherclaims
	 * @return The closest in bounds position, null if there is none.
	 */
	private int[] getClosestEmptyUnclaimedPosition(int x,
			int y, Set<Integer> claims, Set<Integer> otherclaims) {
		//This is fairly inefficient so that getCoordInt can be altered without fear
		//It could be somewhat more efficient if it didn't check as many out-of-bounds positions
		
		//if the space in question is already open
		Integer xy = getCoordInt(x, y);
		if (empty(x,y)&&!claims.contains(xy) && !otherclaims.contains(xy))
			return new int[]{x,y};
		int xextent = state.getXExtent();
		int yextent = state.getYExtent();
		int maxradius = Math.max(Math.max(x, xextent-x), Math.max(y,yextent-y));
		for (int r = 1; r<=maxradius;r++)
		{
			//go up/left diagonal
			x = x-1;
			y = y-1;
			
			//go down
			for (int i = 0; i<2*r;i++) {
				y = y + 1;
				xy = getCoordInt(x, y);
				if (empty(x,y)&&!claims.contains(xy) && !otherclaims.contains(xy))
					return new int[]{x,y};
			}
			//go right
			for (int i = 0; i<2*r;i++) {
				x = x + 1;
				xy = getCoordInt(x, y);
				if (empty(x,y)&&!claims.contains(xy) && !otherclaims.contains(xy))
					return new int[]{x,y};
			}
			//go up
			for (int i = 0; i<2*r;i++) {
				y = y - 1;
				xy = getCoordInt(x, y);
				if (empty(x,y)&&!claims.contains(xy) && !otherclaims.contains(xy))
					return new int[]{x,y};
			}
			//go left
			for (int i = 0; i<2*r;i++) {
				x = x - 1;
				xy = getCoordInt(x, y);
				if (empty(x,y)&&!claims.contains(xy) && !otherclaims.contains(xy))
					return new int[]{x,y};
			}
		}
		return null;
	}
	private boolean inRange(Unit u, Unit target) {
		return DistanceMetrics.chebyshevDistance(u.getxPosition(), u.getyPosition(), target.getxPosition(), target.getyPosition()) <= u.getTemplate().getRange();
	}
	private Integer getCoordInt(int xdest, int ydest) {
		return xdest*state.getYExtent()+ydest;
	}
	private int calculateDamage(Unit attacker, Unit defender)
	{
		int armor = defender.getTemplate().getArmor();
		int damage;
		int basic_damage;
		int piercing_damage;

		basic_damage = attacker.getTemplate().getBasicAttack();
		piercing_damage = attacker.getTemplate().getPiercingAttack();
//		if (bloodlust) {
//			basic_damage *= 2;
//			piercing_damage *= 2;
//		}

		damage = (basic_damage - armor) > 1 ?
			(basic_damage - armor) : 1;
		damage += piercing_damage;
		damage -= rand.nextInt() % ((damage + 2) / 2);

		return damage;
	}
	private boolean empty(int x, int y) {
		return state.inBounds(x, y) && state.unitAt(x, y) == null && state.resourceAt(x, y) == null;
	}
	@Override
	public State.StateView getState(int player) {
		return state.getView(player);
	}
	@Override
	public History.HistoryView getHistory(int player) {
		return history.getView(player);
	}
	public void save(String filename) {
		GameMap.storeState(filename, state);
	}
}
