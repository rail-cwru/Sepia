package edu.cwru.SimpleRTS.agent;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
/**
 * A simple agent that makes all its units move in random directions if they are not attacking.
 * Will attack any enemy within sight range.
 * @author Tim
 *
 */
public class SimpleAgent1 extends Agent {

	StateView currentState;
	
	@Override
	public ImmutableMap.Builder<Integer,Action> initialStep(StateView newstate) {		
		return middleStep(newstate);
	}

	@Override
	public ImmutableMap.Builder<Integer,Action> middleStep(StateView newState) {
		ImmutableMap.Builder<Integer,Action> builder = new ImmutableMap.Builder<Integer,Action>();
		currentState = newState;
		List<Integer> unitIds = currentState.getUnitIds(ID);
		for(int unitId : unitIds)
		{
			UnitView u = currentState.getUnit(unitId);
			int sightRange = u.getTemplateView().getSightRange();
			int target = -1;
			for(int i = 0; i <= Agent.maxId() && target < 0; i++)
			{
				if(i == ID)
					continue;
				for(int enemy : currentState.getUnitIds(i))
				{
					UnitView v = currentState.getUnit(enemy);
					double distance = Math.sqrt((u.getXPosition()-v.getXPosition())*(u.getXPosition()-v.getXPosition()) + 
							(u.getYPosition()-v.getYPosition())*(u.getYPosition()-v.getYPosition()));
					if(distance <= sightRange)
					{
						target = enemy;
						break;
					}						
				}
			}
			if(target >= 0)
			{
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
