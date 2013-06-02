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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.ScriptedGoalAgent.RelevantStateView;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.DistanceMetrics;


/**
 * A basic AI that does simple commanding of gatherer units.
 * <br>It tracks idle units, does simple gathering and returning of resources, and assists others with repeating their actions
 * <br>Can be overridden with 
 */
public class BasicGatheringCoordinator implements Serializable {
	private static final long serialVersionUID = 3922892996550559953L;
	private static final Logger logger = Logger.getLogger(BasicGatheringCoordinator.class.getCanonicalName());
	
	private int playerID;
	private List<Integer> miners;//unit IDs for gold gatherers
	private Map<Integer, Action> others;//IDs and actions for units otherwise disposed
	private List<Integer> idles;//unit IDs for unassigned units
	private List<Integer> lumberjacks;//unit IDs for wood gatherers
	private Random r;//random seed passed given in constructor
	private boolean verbose;
	
	public BasicGatheringCoordinator(int playerID, Random r) {
		miners = new ArrayList<Integer>();
		lumberjacks = new ArrayList<Integer>();
		others = new HashMap<Integer,Action>();
		idles = new ArrayList<Integer>();
		this.r=r;
	}

	public void setVerbose(boolean verbosity) {
		this.verbose=verbosity;
	}
	public boolean getVerbose() {
		return this.verbose;
	}
	public Integer getGoldWorker() {
		return miners.get(r.nextInt(miners.size()));
	}
	public Integer getWoodWorker() {
		return lumberjacks.get(r.nextInt(lumberjacks.size()));
	}
	public Integer getIdleWorker() {
		return idles.get(r.nextInt(idles.size()));
	}
	public int numGoldWorkers() {
		return miners.size();
	}
	public int numWoodWorkers() {
		return lumberjacks.size();
	}
	public int numIdleWorkers() {
		return idles.size();
	}
	public boolean hasGoldWorker(Integer id) {
		return miners.contains(id);
	}
	public boolean hasWoodWorker(Integer id) {
		return lumberjacks.contains(id);
	}
	public boolean hasIdleWorker(Integer id) {
		return idles.contains(id);
	}
	public boolean hasOtherWorker(Integer id) {
		return others.containsKey(id);
	}
	public void removeUnit(Integer unitID) {
		lumberjacks.remove(unitID);
		others.remove(unitID);
		idles.remove(unitID);
		miners.remove(unitID);
	}
	public void assignGold(Integer unitID) {
		lumberjacks.remove(unitID);
		others.remove(unitID);
		idles.remove(unitID);
		miners.add(unitID);
	}
	public void assignWood(Integer unitID) {
		miners.remove(unitID);
		others.remove(unitID);
		idles.remove(unitID);
		lumberjacks.add(unitID);
	}
	public void assignOther(Integer unitID, Action assignment) {
		miners.remove(unitID);
		lumberjacks.remove(unitID);
		idles.remove(unitID);
		others.put(unitID,assignment);
	}
	public void assignIdle(Integer unitID) {
		miners.remove(unitID);
		lumberjacks.remove(unitID);
		idles.add(unitID);
		others.remove(unitID);
	}
	/**
	 * Adds gather/deposit actions to the given action map for units previously specified
	 * as miners or lumberjacks.
	 * @param state
	 * @param relstate
	 * @param actions
	 */
	public void assignActions(StateView state, RelevantStateView relstate, Map<Integer,Action> actions) {
		if (verbose)
		{
			logger.info("Assigning actions for " + miners.size() + " miners, " +
						lumberjacks.size() + " lumberjacks, and " + idles.size() + 
						" idle units. Ignoring "+ others.size() + " others");
		}
		//for each builder (or other partially monitored worker) keep it doing what it was doing
		for (Entry<Integer,Action> indact : others.entrySet()) {
			actions.put(indact.getKey(), indact.getValue());
		}
		//for each miner/lumberjack
		continueWork(state, actions, miners, ResourceNode.Type.GOLD_MINE);
		continueWork(state, actions, lumberjacks, ResourceNode.Type.TREE);
	}
	
	/**
	 * Ensures that resource-gathering workers continue doing so for both gather and deposit phases
	 */
	private void continueWork(StateView state, Map<Integer, Action> actionMap, List<Integer> workerIds, ResourceNode.Type assignedResourceNodeType) {
		for (Integer workerID : workerIds)
		{
			UnitView worker = state.getUnit(workerID);
			//detect if it is carrying a resource
			logger.fine("A "+assignedResourceNodeType+"-worker (id:"+workerID+") is carrying: " + worker.getCargoAmount() + " of type " + worker.getCargoType());
			if (worker.getCargoAmount()>0)
			{
				//if it is carrying a resource (even the wrong one) return it
				actionMap.put(workerID, assignWorkerToReturn(state, worker));
			}
			else
			{
				//if it is not carrying a resource, gather
				actionMap.put(workerID, assignWorkerToGather(state, worker, assignedResourceNodeType));
			}
		}
	}
	
	private Action assignWorkerToGather(StateView state, UnitView worker, ResourceNode.Type resourceNodeType) {
		Action gatherAction = Action.createPermanentFail(worker.getID());
		//find the nearest appropriate resource and tell it to gather from that node
		int closestDist = Integer.MAX_VALUE;
		int closestID = Integer.MIN_VALUE;
		for (Integer resourceNodeID :state.getResourceNodeIds(resourceNodeType)) {
			ResourceView resourceNode = state.getResourceNode(resourceNodeID);
			int dist = DistanceMetrics.chebyshevDistance(worker.getXPosition(),worker.getYPosition(), resourceNode.getXPosition(), resourceNode.getYPosition());
			if (dist < closestDist) {
				closestID = resourceNodeID;
				closestDist = dist;
			}
		}
		//If it found an action, use it
		if (closestID != Integer.MIN_VALUE) {
			gatherAction = Action.createCompoundGather(worker.getID(), closestID);
		}
		return gatherAction;
	}
	/**
	 * Returns an action for returning resources to a depot that accepts the resource, null if it cannot find an appropriate depot (including if it is not carrying anything)
	 * @param state
	 * @param worker
	 * @return
	 */
	private Action assignWorkerToReturn(StateView state, UnitView worker) {
		Action returnAction = Action.createPermanentFail(worker.getID());
		if (worker.getCargoAmount() > 0) {
			//find the nearest base and tell it to go there
			int closestID = Integer.MIN_VALUE;
			int closestDist = Integer.MAX_VALUE;
			for (Integer potentialStoragePitID : state.getUnitIds(playerID))
			{
				logger.fine("Evaluating Unit with id "+potentialStoragePitID);
				UnitView potentialStoragePit = state.getUnit(potentialStoragePitID);
				if (worker.getCargoType() == ResourceType.GOLD && potentialStoragePit.getTemplateView().canAcceptGold() || worker.getCargoType() == ResourceType.WOOD && potentialStoragePit.getTemplateView().canAcceptWood())
				{
					int dist = DistanceMetrics.chebyshevDistance(worker.getXPosition(),worker.getYPosition(), potentialStoragePit.getXPosition(), potentialStoragePit.getYPosition());
					if (dist < closestDist)
					{
						closestID = potentialStoragePitID;
						closestDist = dist;
					}
				}
			}
			if (closestID != Integer.MIN_VALUE) {
				returnAction = Action.createCompoundDeposit(worker.getID(), closestID);
			} else {
				
			}
		}
		return returnAction;
	}
	

}
