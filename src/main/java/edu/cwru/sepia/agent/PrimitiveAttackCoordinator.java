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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.DistanceMetrics;

/**
 * A class that offers primitive micromanagement of soldiers
 * Just sends them to the nearest building, shooting anything in range
 * @author The Condor
 *
 */
public class PrimitiveAttackCoordinator implements Serializable {
	
	private static final long serialVersionUID = -8583438202310015079L;
	
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
	 * @param state
	 * @param actions
	 */
	public void coordinate(StateView state, Map<Integer, Action> actions) {
		//DON'T DO ANYTHING IF NO ATTACKERS
		if (attackers.size()==0)
			return;
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
						actions.put(unitID, Action.createCompoundAttack(unitID, enemy.getID()));
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
