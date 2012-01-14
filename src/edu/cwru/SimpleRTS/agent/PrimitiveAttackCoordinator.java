package edu.cwru.SimpleRTS.agent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

/**
 * A class that offers primative micromanagement of soldiers
 * Just sends them to the nearest building, shooting anything in range
 * @author The Condor
 *
 */
public class PrimitiveAttackCoordinator implements Serializable {
	int playernum;
	List<Integer> attackers;
	private final static int NOTARGET = Integer.MIN_VALUE;
	Integer primarytargetID;
	public PrimitiveAttackCoordinator(int playernum) {
		this.playernum=playernum;
		attackers = new LinkedList<Integer>();
		primarytargetID = NOTARGET;
	}
	public void addAttacker(Integer attacker) {
		attackers.add(attacker);
	}
	/**
	 * Append the relevant actions to the action set.
	 * This will replace previous ones, as Builder does not have any means to check.
	 * TODO: Make this more efficient by having it do it's own path calculations
	 * @param state
	 * @param actions
	 */
	public void coordinate(StateView state, Map<Integer, Action> actions) {
		
		if (primarytargetID == NOTARGET || state.getUnit(primarytargetID) == null) {
			getNewTarget(state);
		}
		List<Integer> allunitsID = state.getAllUnitIds();
		List<UnitView> allenemies = new LinkedList<UnitView>();
		for (Integer i : allunitsID)
		{
			UnitView u = state.getUnit(i);
			if (u.getTemplateView().getPlayer()!=playernum) {
				allenemies.add(u);
			}
		}
		
		if (primarytargetID != NOTARGET) {
			UnitView primarytarget = state.getUnit(primarytargetID);
			for (Integer unitID : attackers) {
				UnitView unit = state.getUnit(unitID);
				boolean foundanenemy=false;
				for (UnitView enemy : allenemies) {
					if (enemy.getTemplateView().getRange() <= DistanceMetrics.chebyshevDistance(unit.getXPosition(), unit.getYPosition(), enemy.getXPosition(), enemy.getYPosition()))
					{
						actions.put(unitID, Action.createCompoundAttack(unitID, enemy.hashCode()));
						foundanenemy=true;
						break;
					}
				}
				//if you didn't run across anything, keep moving to the primary target
				if (!foundanenemy) {
					actions.put(unitID, Action.createCompoundMove(unitID,primarytarget.getXPosition(), primarytarget.getYPosition()));
				}
			}
		}
	}
	
	private void getNewTarget(StateView state) {
		primarytargetID = NOTARGET;
		boolean foundanenemy=false;
		boolean foundanenemybuilding = false;
		for (Integer i : state.getAllUnitIds()) {
			UnitView unit = state.getUnit(i);
			if (unit.getTemplateView().getPlayer() != playernum) {
				if (!foundanenemy) {
					foundanenemy = true;
					primarytargetID = i;
				}
				else if (!foundanenemybuilding && !unit.getTemplateView().canMove()) {
					 
					foundanenemybuilding = true;
					primarytargetID = i;
					break;
				}
			}
		}
	}
	
}
