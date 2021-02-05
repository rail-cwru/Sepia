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
package edu.cwru.sepia.agent;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.environment.model.history.BirthLog;
import edu.cwru.sepia.environment.model.history.DeathLog;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.environment.model.state.UnitTemplate.UnitTemplateView;
import edu.cwru.sepia.util.DistanceMetrics;

/**
 * A naive gathering demonstration agent, it makes peasants and farms, then gathers wood and gold up to a goal amount
 * <br>Does no planning, not even anticipating the results of issued orders.  As a result, it will overissue some orders
 * <br>Assumes a fairly rigid set of events: it starts with only a town hall-style building and some peasants, everything is idle at the start, and it will only make peasants
 */
public class ResourceCollectionAgent extends Agent {
	/**
	 * The interim goals that can be pursued
	 *
	 */
	private static enum Goal {
		GOLD, WOOD, PEASANTS, FARMS, DONE;
	}
	
	private static final long serialVersionUID = -4047208702628325380L;
	private final Logger logger;
	private int goldRequired;
	private int woodRequired;
	private int peasantsRequired;
	private int lastTurnNumber;
	
	
	private String farmName="Farm";
	private String peasantName="Peasant";
	private String townHallName="TownHall";
	
	//Don't use hardcoded values, but at the same time, we will be assuming that it doesn't change
	private UnitTemplateView townHallTemplate;
	private UnitTemplateView peasantTemplate;
	private UnitTemplateView farmTemplate;
	
	//For more complex classes, a simple 
	private List<Integer> idleTownHalls;
	private Map<Integer, Action> workingTownHalls;
	private List<Integer> idleWorkers;
	private Map<Integer, Action> workingWorkers;
	
	/**
	 * @param playernum
	 * @param arguments first index is gold required, second is wood required, third is peasants required (1 will be used if there is no third argument)
	 */
	public ResourceCollectionAgent(int playernum, String[] arguments) {
		this(playernum, Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]), arguments.length >= 3 ? Integer.parseInt( arguments[2]) : 1);
	}
	
	/**
	 * Basic constructor
	 * @param playerNumber
	 * @param goldRequired
	 * @param woodRequired
	 * @param peasantsRequired
	 */
	public ResourceCollectionAgent(int playerNumber, int goldRequired, int woodRequired, int peasantsRequired) {
		super(playerNumber);
		this.goldRequired = goldRequired;
		this.woodRequired = woodRequired;
		this.peasantsRequired = peasantsRequired;
		try {
		this.logger = Logger.getLogger(ResourceCollectionAgent.class.getCanonicalName()+"@Player#"+playerNumber);
		initializeVisualLog();
		visualLog.attachLogger(logger);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newState, History.HistoryView stateHistory) {
		//TODO: determine if the game is impossible and fail early, logging the problem
		//Possible conditions:
			//Need more peasants than the maximum possible
			//Need more wood or gold than the maximum possible (not perfect, present design is wasteful)
			//Don't have a town hall
			//Farm doesn't give food, town hall doesn't accept a resource, peasant can't gather or can't carry a resource, peasant can't make farm, town hall can't make peasant
		lastTurnNumber = -1;
		findCosts(newState);
		initializeLocalState(newState);
		
		Map<Integer, Action> actions = takeTurn(newState, stateHistory);
		lastTurnNumber = newState.getTurnNumber();
		return actions;
	}
	
	/**
	 * Find the costs of the various units
	 * @param newState
	 */
	private void findCosts(StateView state) {
		townHallTemplate = (UnitTemplateView)state.getTemplate(getPlayerNumber(), townHallName);
		peasantTemplate = (UnitTemplateView)state.getTemplate(getPlayerNumber(), peasantName);
		farmTemplate = (UnitTemplateView)state.getTemplate(getPlayerNumber(), farmName);
	}

	@Override
	public Map<Integer,Action> middleStep(StateView newState, History.HistoryView stateHistory) {
		updateLocalState(newState, stateHistory);
		Map<Integer, Action> actions = takeTurn(newState, stateHistory);
		lastTurnNumber = newState.getTurnNumber();
		return actions;
	}
	
	private void updateLocalState(StateView newState,
			HistoryView stateHistory) {
		//Get the events for each turn you haven't gotten events for
		//Starts with the results of the last turn that you moved on, ends with the results from the previous turn (this turn's events have yet to be run)
		for (int turnNumber = lastTurnNumber; turnNumber < newState.getTurnNumber(); turnNumber ++) {
			//Get the new units created on the turn
			List<BirthLog> birthLogs = stateHistory.getBirthLogs(turnNumber);
			for (BirthLog birthLog : birthLogs) {
				//Only get births for your player, since the history records all births that this player is aware of, even if they are for other player's units
				if (birthLog.getController() == getPlayerNumber()) {
					int newUnitTemplateId = newState.getUnit(birthLog.getNewUnitID()).getTemplateView().getID();
					if (newUnitTemplateId == peasantTemplate.getID()) {
						idleWorkers.add(birthLog.getNewUnitID());
					} else if (newUnitTemplateId == townHallTemplate.getID()) {
							idleTownHalls.add(birthLog.getNewUnitID());
					}
				}
			}
			
			//Get your units that died this turn (this agent is ill-equipped for combat scenarios, any death is a bad sign)
			List<DeathLog> deathLogs = stateHistory.getDeathLogs(turnNumber);
			for (DeathLog deathLog : deathLogs) {
				//Only get births for your player, since the history records all births that this player is aware of, even if they are for other player's units
				if (deathLog.getController() == getPlayerNumber()) {
					//Remove it from the lists of units that you will be dealing with
					Integer unitIdToRemove = deathLog.getDeadUnitID(); //Note: it is cast to integer because there is another List.remove that uses an int index, and that is not what we want
					idleWorkers.remove(unitIdToRemove); 
					workingWorkers.remove(unitIdToRemove);
					idleTownHalls.remove(unitIdToRemove); 
					workingTownHalls.remove(unitIdToRemove);
				}
			}
			
			//If this was a smarter agent, it would also check for resource nodes running out (ResourceNodeExhaustionLog), and use that information to preempt any gathers on that node
			
			//Check if any of the commands you issued are done (different from getPrimitiveFeedback, which reports on the steps of the command)
			Map<Integer, ActionResult> commandFeedback = stateHistory.getCommandFeedback(getPlayerNumber(), turnNumber);
			
			//Check for newly idle workers
			List<Integer> newlyIdleWorkers = new ArrayList<Integer>();
			for (Integer workerId : workingWorkers.keySet()) {
				ActionFeedback workerFeedback = commandFeedback.get(workerId).getFeedback();
				if (workerFeedback != ActionFeedback.INCOMPLETE && workerFeedback != ActionFeedback.INCOMPLETEMAYBESTUCK) {
					//All others but incompletes represent either some kind of failure or a success.  This agent isn't complex enough to care which, so long as the unit can take a new order
					newlyIdleWorkers.add(workerId);
				}
			}
			//Remove the newly idle workers you found from the working workers and add them to the idle workers
			for (Integer workerId : newlyIdleWorkers) {
				idleWorkers.add(workerId);
				workingWorkers.remove(workerId);
			}
			
			//Do the same thing for town halls
			List<Integer> newlyIdleTownHalls= new ArrayList<Integer>();
			for (Integer townHallId : workingTownHalls.keySet()) {
				ActionFeedback townHallFeedback = commandFeedback.get(townHallId).getFeedback();
				if (townHallFeedback != ActionFeedback.INCOMPLETE && townHallFeedback != ActionFeedback.INCOMPLETEMAYBESTUCK) {
					//All others but incompletes represent either some kind of failure or a success.  This agent isn't complex enough to care which, so long as the unit can take a new order
					newlyIdleTownHalls.add(townHallId);
				}
			}
			for (Integer townHallId : newlyIdleTownHalls) {
				idleTownHalls.add(townHallId);
				workingTownHalls.remove(townHallId);
			}
		}
		
	}

	private void initializeLocalState(StateView initialState) {
		//Initialize the local state, which will be used to track what things are idle (can be given new orders) and which are awaiting completion of existing orders
		idleWorkers = new ArrayList<Integer>();
		workingWorkers = new HashMap<Integer, Action>();
		idleTownHalls = new ArrayList<Integer>();
		workingTownHalls = new HashMap<Integer, Action>();
		//Assume all units are either peasants (can get gold and wood) or town halls
		List<UnitView> units = initialState.getUnits(getPlayerNumber());
		for (UnitView unit : units) {
			if (unit.getTemplateView().getID() == peasantTemplate.getID()) {
				//Assume it is an idle peasant
				idleWorkers.add(unit.getID());
			}
			else if (unit.getTemplateView().getID() == townHallTemplate.getID()){
				//Assume that it is an idle townhall
				idleTownHalls.add(unit.getID());
			}
		}
		
	}
	public Map<Integer, Action> takeTurn(StateView state, HistoryView history) {
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		
		//Find out the current status of some useful values
		int currentPeasantCount = idleWorkers.size() + workingWorkers.size();
		int currentGoldCount = state.getResourceAmount(getPlayerNumber(), ResourceType.GOLD);
		int currentWoodCount = state.getResourceAmount(getPlayerNumber(), ResourceType.WOOD);
		int currentSupplyCap = state.getSupplyAmount(getPlayerNumber());
		int currentSupplyUsage = state.getSupplyCap(getPlayerNumber());
		
		//Derive some other useful values
		int currentSupplyOpen = currentSupplyCap - currentSupplyUsage;
		
		//Find out needs for long term goals
		boolean atPopulationLimit = currentSupplyOpen == 0;
		
		//Make interim goal based on needs
		Goal interimGoal;
		
		if (peasantsRequired > currentPeasantCount) {
			if (atPopulationLimit) {
				//At population limit, so need to make a farm first
				int goldNeededForFarm = farmTemplate.getGoldCost() - currentGoldCount;
				int woodNeededForFarm = farmTemplate.getWoodCost() - currentWoodCount;
				
				if (goldNeededForFarm <= 0 && woodNeededForFarm <= 0) {
					//Have all needed resources for farm
					interimGoal = Goal.FARMS;
				} else if (goldNeededForFarm > 0) {
					//If you need gold, gather it (gold gathering is prioritized over wood gathering)
					interimGoal = Goal.GOLD;
				} else {
					//Need a resource and it isn't gold
					interimGoal = Goal.WOOD;
				}
			}
			else {
				//Not at population limit, but do need peasants
				//At population limit, so need to make a farm first
				int goldNeededForPeasant = peasantTemplate.getGoldCost() - currentGoldCount;
				int woodNeededForPeasant = peasantTemplate.getWoodCost() - currentWoodCount;
				if (goldNeededForPeasant <= 0 && woodNeededForPeasant <= 0) {
					//Have all needed resources for farm
					interimGoal = Goal.PEASANTS;
				} else if (goldNeededForPeasant > 0) {
					//If you need gold, gather it (gold gathering is prioritized over wood gathering)
					interimGoal = Goal.GOLD;
				} else {
					//Need a resource and it isn't gold
					interimGoal = Goal.WOOD;
				}
			}
		} else {
			//Done making peasants, gather all needed gold, then gather all needed wood
			int goldNeeded = goldRequired - currentGoldCount;
			int woodNeeded = woodRequired - currentWoodCount;
			
			if (goldNeeded <= 0 && woodNeeded <= 0) {
				//Have all needed resources for farm
				interimGoal = Goal.DONE;
			} else if (goldNeeded > 0) {
				//If you need gold, gather it (gold gathering is prioritized over wood gathering)
				interimGoal = Goal.GOLD;
			} else {
				//Need a resource and it isn't gold
				interimGoal = Goal.WOOD;
			}
		}
		
		switch (interimGoal) {
		case DONE:
			//Hooray, we're done, pass out the pizzas and the high fives
			
			break;
		case FARMS:
			produceOneOf(builder, state, new ArrayList<Integer>(idleWorkers), true, farmTemplate);
			continueWorking(builder, state, workingWorkers, workingTownHalls);
			break;
		case PEASANTS:
			produceOneOf(builder, state, new ArrayList<Integer>(idleTownHalls), false, peasantTemplate);
			continueWorking(builder, state, workingWorkers, workingTownHalls);
			break;
		case GOLD:
			assignPeasantsToResource(builder, state, new ArrayList<Integer>(idleWorkers), ResourceNode.Type.GOLD_MINE);
			continueWorking(builder, state, workingWorkers, workingTownHalls);
			break;
		case WOOD:
			assignPeasantsToResource(builder, state, new ArrayList<Integer>(idleWorkers), ResourceNode.Type.TREE);
			continueWorking(builder, state, workingWorkers, workingTownHalls);
			break;
		}
		logger.fine("Goal: "+interimGoal);
		return builder;
	}

	/**
	 * @param builder
	 * @param state
	 * @param workingWorkers
	 * @param workingTownHalls
	 */
	private void continueWorking(Map<Integer, Action> builder, StateView state,
			Map<Integer, Action> workingWorkers,
			Map<Integer, Action> workingTownHalls) {
		for (Entry<Integer, Action> entry : workingWorkers.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		for (Entry<Integer, Action> entry : workingTownHalls.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param builder
	 * @param state
	 * @param workerIds
	 * @param resourceNodeType
	 */
	private void assignPeasantsToResource(Map<Integer, Action> builder, StateView state,
			List<Integer> workerIds, ResourceNode.Type resourceNodeType) {
		for (Integer workerId : workerIds) {
			assignPeasantToResource(builder, state, workerId, resourceNodeType);
		}
	}

	/**
	 * Assign worker to gather from the first closest resource node of a specific type, or to return the resources to the first closest town hall that they have if they already are carrying the right resource
	 * @param builder
	 * @param state
	 * @param workerId
	 * @param resourceNodeType
	 */
	private void assignPeasantToResource(Map<Integer, Action> builder,
			StateView state, Integer workerId, ResourceNode.Type resourceNodeType) {
		UnitView worker = state.getUnit(workerId);
		logger.finest("Trying to give unit "+workerId + " gather/deposit task for " + resourceNodeType);
		
		if (worker.getCargoType() == ResourceNode.Type.getResourceType(resourceNodeType) && worker.getCargoAmount() > 0) {
			//If you are carrying any of the right resource, go home
			Integer closestTownHallId = null;
			int closestTownHallDistance = -1;
			for (UnitView potentialTownHall : state.getUnits(getPlayerNumber())) {
				//TODO: keep a full list of town halls cached based on births and deaths
				if (potentialTownHall.getTemplateView().getID() == (townHallTemplate.getID())) {
					//It is a town hall, so take its distance into account
					int distance = DistanceMetrics.chebyshevDistance(worker.getXPosition(), worker.getYPosition(), potentialTownHall.getXPosition(), potentialTownHall.getYPosition());
					if (closestTownHallId == null || closestTownHallDistance > distance) {
						closestTownHallId = potentialTownHall.getID();
						closestTownHallDistance = distance;
					}
				}
			}
			//If there actually was a resource node of the right type, gather from it
			if (closestTownHallId != null) {
				Action action = Action.createCompoundDeposit(workerId, closestTownHallId);
				builder.put(workerId, action);
				idleWorkers.remove(workerId);
				workingWorkers.put(workerId, action);
			}
			
			logger.finest("Unit "+workerId + (closestTownHallId==null?" can't find a town hall":" ordered to deposit in town hall " + closestTownHallId));
			
		} else {
			//Carrying nothing or carrying the wrong type
			
			//Find the nearest node
			Integer closestResourceNodeId = null;
			int closestResourceNodeDistance = -1;
			
			for (ResourceView resourceNode : state.getResourceNodes(resourceNodeType)) {
				int distance = DistanceMetrics.chebyshevDistance(worker.getXPosition(), worker.getYPosition(), resourceNode.getXPosition(), resourceNode.getYPosition());
				if (closestResourceNodeId == null || closestResourceNodeDistance > distance) {
					closestResourceNodeId = resourceNode.getID();
					closestResourceNodeDistance = distance;
				}
			}
			//If there actually was a resource node of the right type, gather from it
			if (closestResourceNodeId != null) {
				Action action = Action.createCompoundGather(workerId, closestResourceNodeId);
				builder.put(workerId, action);
				idleWorkers.remove(workerId);
				workingWorkers.put(workerId, action);
			}
			logger.finest("Unit "+workerId + (closestResourceNodeId==null?" can't find a "+resourceNodeType:" ordered to gather from " + closestResourceNodeId));
		}
		
		
	}

	/**
	 * @param actionMap The action map that will be added to
	 * @param state
	 * @param idleBuilderIds
	 * @param build true=the production is a build action, false = the production is a produce action
	 * @param builtTemplate
	 */
	private void produceOneOf(Map<Integer, Action> actionMap, StateView state,
			List<Integer> idleBuilderIds, boolean build, UnitTemplateView builtTemplate) {
		//Make the first builder make something, everyone else gets a break
		if (idleBuilderIds.size() > 0) {
			
			Integer idleBuilderId = idleBuilderIds.get(0);
			Action action;
			if (build) {
				UnitView unit = state.getUnit(idleBuilderId);
				if (unit == null) {
					throw new IllegalStateException("Something is wrong, the agent stored " + idleBuilderId + " as a builder, but there was no such unit in the state");
				}
				int[] closestXY = state.getClosestOpenPosition(unit.getXPosition(), unit.getYPosition());
				action = Action.createCompoundBuild(idleBuilderId, builtTemplate.getID(), closestXY[0], closestXY[1]);
				logger.finest("Unit ordered to build a " + builtTemplate.getName() + " at "+closestXY[0] + "," + closestXY[1]);
			} else {
				action = Action.createCompoundProduction(idleBuilderId, builtTemplate.getID());
			}
			actionMap.put(idleBuilderId, action);
			if (idleWorkers.remove(idleBuilderId)) {
			workingWorkers.put(idleBuilderId, action);
			}
			if (idleTownHalls.remove(idleBuilderId)) {
				workingTownHalls.put(idleBuilderId, action);
			}
		}
		
	}

	/**
	 * Log basic turn information
	 * @param state
	 */
	private void logTurn(StateView state) {
		logger.fine("=> Step: " + (lastTurnNumber+1));
		
		int currentGold = state.getResourceAmount(getPlayerNumber(), ResourceType.GOLD);
		int currentWood = state.getResourceAmount(getPlayerNumber(), ResourceType.WOOD);
		
			logger.fine("Current Gold: " + currentGold);
			logger.fine("Current Wood: " + currentWood);
	}
	
	@Override
	public void terminalStep(StateView newState, History.HistoryView stateHistory) {
		logTurn(newState);
		logger.fine("Congratulations, you have finished the task");
	}
	
	

	public static String getUsage() {
		return "Three arguments, amount of gold to gather and amount of wood to gather and number of peasants to make to do that";
	}
	@Override
	public void savePlayerData(OutputStream os) {
		//this agent lacks learning and so has nothing to persist.
		
	}
	@Override
	public void loadPlayerData(InputStream is) {
		//this agent lacks learning and so has nothing to persist.
	}
}
