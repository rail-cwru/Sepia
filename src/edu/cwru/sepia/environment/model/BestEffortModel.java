/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.environment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionQueue;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.LocatedAction;
import edu.cwru.sepia.action.LocatedProductionAction;
import edu.cwru.sepia.action.ProductionAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.TurnTracker;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.StateCreator;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.model.state.UpgradeTemplate;
import edu.cwru.sepia.experiment.Configuration;
import edu.cwru.sepia.experiment.ConfigurationValues;
import edu.cwru.sepia.util.Direction;
import edu.cwru.sepia.util.DistanceMetrics;
import edu.cwru.sepia.util.GameMap;
/**
 * A Model that attempts the best effort.
 * <br>A branch of SimpleModel with some added features: durative actions and turn taking. 
 * <br>This model is sequential, processing most actions one at a time.  Because of this, it resolves conflicts by allowing them to proceed on a first-come-first-served basis.
 * <br> 
 */
public class BestEffortModel implements Model {
	private static final long serialVersionUID = -8289868580233478749L;
	
	
	private final String NUM_ATTEMPTS = "environment.model.numattempts";
	
	private Random rand;
	private History history;
	private State state;
	private HashMap<Integer, HashMap<Integer, ActionQueue>> queuedActions;
	private DurativePlanner planner;
	private StateCreator restartTactic;
	private boolean verbose;
	private Configuration configuration;
	private int numAttempts;
	private TurnTracker turnTracker;
	public BestEffortModel(State init, int seed, StateCreator restartTactic, Configuration configuration) {
		this.configuration = configuration;
		this.numAttempts = configuration.getInt(NUM_ATTEMPTS, 2);
		state = init;
		history = new History();
		queuedActions = new HashMap<Integer, HashMap<Integer, ActionQueue>>();
		for (Integer i : state.getPlayers()) {
			history.addPlayer(i);
			queuedActions.put(i, new HashMap<Integer,ActionQueue>());
		}
		rand = new Random(seed);
		planner = new DurativePlanner(init);
		
		this.restartTactic = restartTactic;
		verbose = false;
	}
	
	@Override
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	@Override
	public boolean getVerbose() {
		return this.verbose;
	}
	
	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
	
	@Override
	public void createNewWorld() {
		state = restartTactic.createState();
		history = new History();
		queuedActions = new HashMap<Integer, HashMap<Integer, ActionQueue>>();
		for (Integer i : state.getPlayers()) {
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
			history.recordCommandRecieved(sendingPlayerNumber, state.getTurnNumber(), unitId, a);
			//If the unit is not the same as in the action, ignore the action
			if (a.getUnitId() != unitId)
			{
				history.recordCommandFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDUNIT));
				continue;
			}
			//If the unit does not exist, ignore the action
			else if (state.getUnit(unitId) == null)
			{
				history.recordCommandFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDUNIT));
				continue;
			}
			//If the unit is not the player's, ignore the action
			else if(state.getUnit(unitId).getPlayer() != sendingPlayerNumber)
			{
				history.recordCommandFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDCONTROLLER));
				continue;
			}
			else
			{//Valid
				ActionQueue queue = new ActionQueue(a, calculatePrimitives(a));
				queuedActions.get(sendingPlayerNumber).put(unitId, queue);
			}
			
			
		}
		
	
	}
	private LinkedList<Action> calculatePrimitives(Action action) {
		LinkedList<Action> primitives = null;
		Unit actor = state.getUnit(action.getUnitId());
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
		return primitives;
	}
	@Override
	public void executeStep() {
		
		//Set each agent to have no task
		for (Unit u : state.getUnits().values()) {
			u.deprecateOldView();
		}
		//Set each template to not keep the old view
		for (Integer player : state.getPlayers())
			for (@SuppressWarnings("rawtypes") Template t : state.getTemplates(player).values())
				t.deprecateOldView();
		
		//Run the Action
		for (Integer player : state.getPlayers())
		{
			//hedge against players entering the state for some reason
			if (!queuedActions.containsKey(player)) {
				queuedActions.put(player, new HashMap<Integer, ActionQueue>());
			}
			if (turnTracker == null || turnTracker.isPlayersTurn(player)) {
				Iterator<ActionQueue> queuedActItr = queuedActions.get(player).values().iterator();
				while(queuedActItr.hasNext()) 
				{
					ActionQueue queuedAct = queuedActItr.next();
					if (verbose)
						System.out.println("Doing full action: "+queuedAct.getFullAction());
					//Pull out the primitive
					if (!queuedAct.hasNext()) 
						continue;
					Action a = queuedAct.popPrimitive();
					if (verbose)
						System.out.println("Doing primitive action: "+a);
					//Execute it
					Unit u = state.getUnit(a.getUnitId());			
					if (u == null)
						continue;
					//Set the tasks and grab the common features
					int x = u.getxPosition();
					int y = u.getyPosition();
					int xPrime = 0;
					int yPrime = 0;
					Action fullact = queuedAct.getFullAction();
					if(a instanceof DirectedAction)
					{
						Direction d = ((DirectedAction)a).getDirection();
						xPrime = x + d.xComponent();
						yPrime = y + d.yComponent();
					}
					else if(a instanceof LocatedAction)
					{
						xPrime = x + ((LocatedAction)a).getX();
						yPrime = y + ((LocatedAction)a).getY();
					}
					
					
					//Gather the last of the information and actually execute the actions
					int timesTried=0;
					boolean failedTry=true;
					boolean wrongType=false;
					boolean fullIsPrimitive=ActionType.isPrimitive(a.getType());
					boolean incompletePrimitive;
					/*recalculate and try again once if it has failed, so long as the full action 
					  is not primitive (since primitives will recalculate to the same as before) 
					  and not the wrong type (since something is wrong if it is the wrong type)*/
					do
					{
						timesTried++;
						failedTry = false;
						incompletePrimitive = false;
						switch(a.getType())
						{
							case PRIMITIVEMOVE:
								if (!(a instanceof DirectedAction))
								{
									wrongType=true;
									break;
								}
								if(state.inBounds(xPrime, yPrime) && u.canMove() && empty(xPrime,yPrime)) {
									int newdurativeamount;
									if (a.equals(u.getActionProgressPrimitive()))
									{
										newdurativeamount = u.getActionProgressAmount()+1;
									}
									else
									{
										newdurativeamount = 1;
									}
									Direction d = ((DirectedAction)a).getDirection();
									boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateMoveDuration(u,u.getxPosition(),u.getyPosition(),d);
									//if it will finish, then execute the atomic action
									if (willcompletethisturn)
									{
										state.moveUnit(u, d);
										u.resetDurative();
									}
									else {
										incompletePrimitive=true;
										u.setDurativeStatus(a, newdurativeamount);
									}
								}
								else {
									failedTry=true;
									queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
								}
								break;
							case PRIMITIVEGATHER:
								if (!(a instanceof DirectedAction))
								{
									wrongType=true;
									break;
								}
								boolean failed=false;
								ResourceNode resource = state.resourceAt(xPrime, yPrime);
								if(resource == null) {
									failed=true;
								}
								else if(!u.canGather()) {
									failed=true;
								}
								else {
									int newdurativeamount;
									if (a.equals(u.getActionProgressPrimitive()))
									{
										newdurativeamount = u.getActionProgressAmount()+1;
									}
									else
									{
										newdurativeamount = 1;
									}
									boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateGatherDuration(u, resource);
									//if it will finish, then execute the atomic action
									if (willcompletethisturn)
									{
										int amountPickedUp = resource.reduceAmountRemaining(u.getTemplate().getGatherRate(resource.getType()));
										u.setCargo(resource.getResourceType(), amountPickedUp);
										history.recordResourcePickup(u, resource, amountPickedUp, state);
										u.resetDurative();
									}
									else {
										incompletePrimitive=true;
										u.setDurativeStatus(a, newdurativeamount);
									}
								}
								if (failed) {
									failedTry=true;
									queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
								}
								break;
							case PRIMITIVEDEPOSIT:
								if (!(a instanceof DirectedAction))
								{
									wrongType=true;
									break;
								}
								//only can do a primitive if you are in the right position
								Unit townHall = state.unitAt(xPrime, yPrime);
								boolean canAccept=false;
								if (townHall!=null && townHall.getPlayer() == u.getPlayer())
								{
									if (u.getCurrentCargoType() == ResourceType.GOLD && townHall.getTemplate().canAcceptGold())
										canAccept=true;
									else if (u.getCurrentCargoType() == ResourceType.WOOD && townHall.getTemplate().canAcceptWood())
										canAccept=true;
								}
								if(!canAccept)
								{
									failedTry=true;
									queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
									break;
								}
								else {
									int newdurativeamount;
									if (a.equals(u.getActionProgressPrimitive()))
									{
										newdurativeamount = u.getActionProgressAmount()+1;
									}
									else
									{
										newdurativeamount = 1;
									}
									boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateDepositDuration(u, townHall);
									//if it will finish, then execute the atomic action
									if (willcompletethisturn)
									{
										int agent = u.getPlayer();
										history.recordResourceDropoff(u, townHall, state);
										state.addResourceAmount(agent, u.getCurrentCargoType(), u.getCurrentCargoAmount());
										u.clearCargo();
										u.resetDurative();
									}
									else {
										incompletePrimitive=true;
										u.setDurativeStatus(a, newdurativeamount);
									}
									break;
								}
							case PRIMITIVEATTACK:
								if (!(a instanceof TargetedAction))
								{
									wrongType=true;
									break;
								}
								Unit target = state.getUnit(((TargetedAction)a).getTargetId());
								if (target!=null)
								{
									if (u.getTemplate().getRange() >= DistanceMetrics.chebyshevDistance(u.getxPosition(),u.getyPosition(), target.getxPosition(), target.getyPosition()))
									{
										int newdurativeamount;
										if (a.equals(u.getActionProgressPrimitive()))
										{
											newdurativeamount = u.getActionProgressAmount()+1;
										}
										else
										{
											newdurativeamount = 1;
										}
										boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateAttackDuration(u, target);
										//if it will finish, then execute the atomic action
										if (willcompletethisturn)
										{
											int damage = calculateDamage(u,target);
											history.recordDamage(u, target, damage, state);
											target.setHP(Math.max(target.getCurrentHealth()-damage,0));
											u.resetDurative();
										}
										else {
											incompletePrimitive=true;
											u.setDurativeStatus(a, newdurativeamount);
										}
									}
									else //out of range
									{
										failedTry=true;
										queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
									}
								}
								else
								{
									failedTry=true;
									queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
								}
								break;
							case PRIMITIVEBUILD:
							{
								if (!(a instanceof ProductionAction))
								{
									wrongType=true;
									break;
								}
								if (queuedAct.getFullAction().getType() == ActionType.COMPOUNDBUILD && queuedAct.getFullAction() instanceof LocatedProductionAction)
								{
									LocatedProductionAction fullbuild = (LocatedProductionAction) queuedAct.getFullAction();
									if (fullbuild.getX() != u.getxPosition() || fullbuild.getY() != u.getyPosition())
									{
										failedTry=true;
										queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
										break;
									}
								}
								UnitTemplate template = (UnitTemplate)state.getTemplate(((ProductionAction)a).getTemplateId());
								if (u.getTemplate().canProduce(template))
								{
									boolean prerequisitesMet = true;
									//check if the prerequisites for the template's production are met
									for (Integer buildingtemplateid : template.getBuildPrerequisites()) {
										if (!state.hasUnit(u.getPlayer(), buildingtemplateid)) {
											prerequisitesMet = false;
											break;
										}
									}
									if (prerequisitesMet) {
										for (Integer upgradetemplateid : template.getUpgradePrerequisites()) {
											if (!state.hasUpgrade(u.getPlayer(),upgradetemplateid)) {
												prerequisitesMet = false;
												break;
											}
										}
									}
									if (prerequisitesMet) {
										int newdurativeamount = a.equals(u.getActionProgressPrimitive())? u.getActionProgressAmount()+1 : 1;
										boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateProductionDuration(u, template);
										//if it will finish, then execute the atomic action
										if (willcompletethisturn)
										{
											Unit building = template.produceInstance(state);
											int[] newxy = state.getClosestPosition(x,y);
											if (state.tryProduceUnit(building,newxy[0],newxy[1]))
											{
												history.recordBirth(building, u, state);
											}
											u.resetDurative();
										}
										else {
											incompletePrimitive=true;
											u.setDurativeStatus(a, newdurativeamount);
										}
									}
									else //didn't meet prerequisites
									{
										failedTry=true;
									}
								}
								else //it can't produce the appropriate thing
								{
									failedTry=true;
								}
								
								break;
							}
							case PRIMITIVEPRODUCE:
							{
								if (!(a instanceof ProductionAction))
								{
									wrongType=true;
									break;
								}
								Template<?> template = state.getTemplate(((ProductionAction)a).getTemplateId());
								//check if it is even capable of producing the template
								if (u.getTemplate().canProduce(template))
								{
									boolean prerequisitesMet = true;
									//check if the prerequisites for the template's production are met
									for (Integer buildingtemplateid : template.getBuildPrerequisites()) {
										if (!state.hasUnit(u.getPlayer(), buildingtemplateid)) {
											prerequisitesMet = false;
											break;
										}
									}
									if (prerequisitesMet) {
										for (Integer upgradetemplateid : template.getUpgradePrerequisites()) {
											if (!state.hasUpgrade(u.getPlayer(), upgradetemplateid)) {
												prerequisitesMet = false;
												break;
											}
										}
									}
									if (prerequisitesMet) {
										int newdurativeamount = a.equals(u.getActionProgressPrimitive())? u.getActionProgressAmount()+1 : 1;
										boolean willcompletethisturn = newdurativeamount== DurativePlanner.calculateProductionDuration(u, template);
										//if it will finish, then execute the atomic action
										if (willcompletethisturn)
										{
											if (template instanceof UnitTemplate)
											{
												Unit produced = ((UnitTemplate)template).produceInstance(state);
												int[] newxy = state.getClosestPosition(x,y);
												if (state.tryProduceUnit(produced,newxy[0],newxy[1]))
												{
													history.recordBirth(produced, u, state);
												}
											}
											else if (template instanceof UpgradeTemplate) {
												UpgradeTemplate upgradetemplate = ((UpgradeTemplate)template);
												if (state.tryProduceUpgrade(upgradetemplate.produceInstance(state)))
												{
													history.recordUpgrade(upgradetemplate,u, state);
												}
											}
										}
										else {
											incompletePrimitive=true;
											u.setDurativeStatus(a, newdurativeamount);
										}
									}
									else { //prerequisites not met
										failedTry=true;
									}
								}
								else//can't produce it
								{
									failedTry=true;
								}
								break;
							}
							case FAILED:
							{
								failedTry=true;
								queuedAct.resetPrimitives(calculatePrimitives(queuedAct.getFullAction()));
								break;
							}
							case FAILEDPERMANENTLY:
							{
								break;
							}
						}
						
						//Record results
						
						ActionFeedback primitiveFeedback=null;
						ActionFeedback compoundFeedback=null;
						boolean removeAction=false;
						if (wrongType)
						{
							//if it had the wrong type, then either the planner is bugged (unlikely) or the user provided a bad primitive action
							//either way, record it as failed and toss it
							compoundFeedback = ActionFeedback.INVALIDTYPE;
							removeAction = true;
						}
						else if (!failedTry && a.getType() != ActionType.FAILEDPERMANENTLY)
						{
							primitiveFeedback = incompletePrimitive? ActionFeedback.INCOMPLETE : ActionFeedback.COMPLETED;
							if (!incompletePrimitive && !queuedAct.hasNext())
							{
								compoundFeedback = ActionFeedback.COMPLETED;
								removeAction=true;
							}
							else
							{
								compoundFeedback = ActionFeedback.INCOMPLETE;
							}
						}
						else if (a.getType()==ActionType.FAILEDPERMANENTLY || failedTry && fullIsPrimitive)
						{
							compoundFeedback = ActionFeedback.FAILED;
							primitiveFeedback = ActionFeedback.FAILED;
							removeAction = true;
							
						}
						else
						{
							compoundFeedback = ActionFeedback.INCOMPLETEMAYBESTUCK;
							primitiveFeedback = ActionFeedback.FAILED;
						}
						if (compoundFeedback != null) {
							history.recordCommandFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(queuedAct.getFullAction(),compoundFeedback));
						}
						if (primitiveFeedback != null) {
							history.recordPrimitiveFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(a,primitiveFeedback));
						}
						if (removeAction) {
							queuedActItr.remove();
						}
					}
					while (timesTried < numAttempts && failedTry && !fullIsPrimitive && !wrongType);
				}
			}//end if it is the player's turn
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
				history.recordResourceNodeExhaustion(r, state);
				usedup.add(r.ID);
			}
		}
		//Remove the used up resource nodes
		for (int rid : usedup)
		{
			
			state.removeResourceNode(rid);
		}
		
		state.incrementTurn();
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
		return state.unitAt(x, y) == null && state.resourceAt(x, y) == null;
	}
	@Override
	public State getState() {
		return state;
	}
	@Override
	public History getHistory() {
		return history;
	}
	public void save(String filename) {
		GameMap.storeState(filename, state);
	}

	@Override
	public void setTurnTracker(TurnTracker turnTracker) {
		this.turnTracker = turnTracker;
	}
}
