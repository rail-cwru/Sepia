package edu.cwru.SimpleRTS.agent;

import java.util.List;

import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.ScriptedGoalAgent.RelevantStateView;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.ResourceView;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

public class BasicGatheringCoordinator
{
	int playerID;
	List<Integer> miners;
	List<Integer> others;
	List<Integer> lumberjacks;
	List<Integer> slackers;
	
	public void assignGold(int unitID) {
		
	}
	public void assignWood(int unitID) {
		
	}
	public void assignOther(int unitID) {
		
	}
	public void assignIdle(int unitID) {
		
	}
	
	public void assignActions(StateView state, RelevantStateView relstate, Builder<Integer,Action> actions) {
		//for each miner/lumberjack
		for (Integer minerID : miners)
		{
			UnitView miner = state.getUnit(minerID);
			//detect if it is carrying a resource
			if (miner.getCargoAmount()>0)
			{
				//if it is, find the nearest base and tell it to go there
				assignWorkerToReturn(state, miner, actions);
			}
			else
			{
				//if it is not, find the nearest appropriate resource and tell it to go there
				int closestDist = Integer.MIN_VALUE;
				int closestID = Integer.MIN_VALUE;
				for (Integer mineID :state.getResourceNodeIds(ResourceNode.Type.GOLD_MINE)) {
					ResourceView mine = state.getResourceNode(mineID);
					int dist = Math.abs(miner.getXPosition() - mine.getXPosition()) + Math.abs(miner.getYPosition() - mine.getYPosition());
					if (dist < closestDist) {
						closestID = mineID;
						closestDist = dist;
					}
				}
				if (closestID != Integer.MIN_VALUE) {
					actions.put(minerID, Action.createCompoundGather(minerID, closestID));
				}
				
			}
		}
		for (Integer lumberjackID : lumberjacks)
		{
			UnitView lumberjack = state.getUnit(lumberjackID);
			//detect if it is carrying a resource
			if (lumberjack.getCargoAmount()>0)
			{
				//if it is, find the nearest base and tell it to go there
				assignWorkerToReturn(state, lumberjack, actions);
			}
			else
			{
				//if it is not, find the nearest appropriate resource and tell it to go there
				int closestDist = Integer.MIN_VALUE;
				int closestID = Integer.MIN_VALUE;
				for (Integer treeID :state.getResourceNodeIds(ResourceNode.Type.TREE)) {
					ResourceView tree = state.getResourceNode(treeID);
					int dist = Math.abs(lumberjack.getXPosition() - tree.getXPosition()) + Math.abs(lumberjack.getYPosition() - tree.getYPosition());
					if (dist < closestDist) {
						closestID = treeID;
						closestDist = dist;
					}
				}
				if (closestID != Integer.MIN_VALUE) {
					actions.put(lumberjackID, Action.createCompoundGather(lumberjackID, closestID));
				}
				
			}
		}
	}
	private void assignWorkerToReturn(StateView state, UnitView worker, Builder<Integer,Action> actions) {
		//find the nearest base and tell it to go there
		int closestID = Integer.MIN_VALUE;
		int closestDist = Integer.MIN_VALUE;
		for (Integer potentialStoragePitID : state.getUnitIds(playerID))
		{
			UnitView potentialStoragePit = state.getUnit(potentialStoragePitID);
			//it can still be carrying the other resource, it is better to have it choose to return with what it has than just go straight for the other resource
			if (worker.getCargoType() == ResourceType.GOLD && potentialStoragePit.getTemplateView().canAcceptGold() || worker.getCargoType() == ResourceType.WOOD && potentialStoragePit.getTemplateView().canAcceptWood())
			{
				int dist = Math.abs(worker.getXPosition() - potentialStoragePit.getXPosition()) + Math.abs(worker.getYPosition() - potentialStoragePit.getYPosition());
				if (dist < closestDist)
				{
					closestID = potentialStoragePitID;
					closestDist = dist;
				}
			}
			if (closestID != Integer.MIN_VALUE) {
				actions.put(worker.getID(), Action.createCompoundDeposit(worker.getID(), closestID));
			}
		}
	}
}
