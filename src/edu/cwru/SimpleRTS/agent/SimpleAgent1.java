package edu.cwru.SimpleRTS.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
/**
 * A simple agent that makes all its units move in random directions if they are not attacking.
 * Will attack any enemy within sight range.
 * @author Tim
 *
 */
public class SimpleAgent1 extends Agent {
	private static final long serialVersionUID = 1L;

	public SimpleAgent1(int playernum) {
		super(playernum);
		
	}

	StateView currentState;
	
	@Override
	public Map<Integer,Action> initialStep(StateView newstate) {		
		return middleStep(newstate);
	}

	@Override
	public Map<Integer,Action> middleStep(StateView newState) {
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		currentState = newState;
		List<Integer> unitIds = currentState.getUnitIds(playernum);
		for(int unitId : unitIds)
		{
			UnitView u = currentState.getUnit(unitId);
			int sightRange = u.getTemplateView().getSightRange();
			int target = -1;
			for(int enemy : currentState.getAllUnitIds())
			{
				UnitView v = currentState.getUnit(enemy);
				if (v.getTemplateView().getPlayer() == playernum)
					continue;
				double distance = DistanceMetrics.chebyshevDistance(u.getXPosition(), u.getYPosition(), v.getXPosition(), v.getYPosition());
				if(distance <= sightRange)
				{
					target = enemy;
					break;
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

	public static String getUsage() {
		return "None";
	}

}
