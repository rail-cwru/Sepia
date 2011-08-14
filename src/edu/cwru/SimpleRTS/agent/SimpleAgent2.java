package edu.cwru.SimpleRTS.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

public class SimpleAgent2 extends Agent {
	
	private StateView currentState;
	private Map<Integer,Integer> targetsOfUnits;
	private Map<Integer,Integer> targetCounts;
	
	public SimpleAgent2(int playernum) {
		super(playernum);
		targetsOfUnits = new HashMap<Integer,Integer>();
		targetCounts = new HashMap<Integer,Integer>();
	}
	
	@Override
	public ImmutableMap.Builder<Integer,Action> initialStep(StateView newstate) {		
		return middleStep(newstate);
	}

	@Override
	public ImmutableMap.Builder<Integer,Action> middleStep(StateView newState) {
		ImmutableMap.Builder<Integer,Action> builder = new ImmutableMap.Builder<Integer,Action>();
		currentState = newState;
		targetsOfUnits.clear();
		List<Integer> unitIds = currentState.getUnitIds(ID);
		for(int unitId : unitIds)
		{
			UnitView u = currentState.getUnit(unitId);
			int sightRange = u.getTemplateView().getSightRange();
			List<Integer> targetsInRange = new ArrayList<Integer>();
			for(int i = 0; i <= Agent.maxId(); i++)
			{
				if(i == ID)
					continue;
				for(int enemy : currentState.getUnitIds(i))
				{
					UnitView v = currentState.getUnit(enemy);
					double distance = DistanceMetrics.euclideanDistance(u.getXPosition(), u.getYPosition(), v.getXPosition(), v.getYPosition());
					if(distance <= sightRange)
					{
						targetsInRange.add(enemy);
					}						
				}
			}
			if(targetsInRange.size() >= 0)
			{
				int target = -1;
				int max = -1;
				for(int enemy : targetsInRange)
				{
					Integer count = targetCounts.get(enemy);
					if(count != null && count > max)
						target = enemy;
				}
				Action a = new TargetedAction(unitId, ActionType.COMPOUNDATTACK, target);
				builder.put(unitId, a);
				targetsOfUnits.put(unitId, target);
				Integer count = targetCounts.get(target);
				targetCounts.put(target, count != null ? count+1 : 1 );
			}
			else if(!targetCounts.isEmpty())
			{
				double minDist = Double.MAX_VALUE;
				int target = 0;
				for(int enemy : targetCounts.keySet())
				{
					UnitView v = currentState.getUnit(enemy);
					double distance = DistanceMetrics.euclideanDistance(u.getXPosition(), u.getYPosition(), v.getXPosition(), v.getYPosition());
					if(distance < minDist)
					{
						target = enemy;
						minDist = distance;
					}
				}
				Action a = new TargetedAction(unitId, ActionType.COMPOUNDATTACK, target);
				builder.put(unitId, a);
			}
			else
			{
				int dir = (int)(Math.random()*8);
				Action a = new DirectedAction(unitId, ActionType.PRIMITIVEMOVE, Direction.values()[dir]);
				builder.put(unitId, a);
			}
		}
		return builder;
	}

	@Override
	public void terminalStep(StateView newstate) {
	}

}