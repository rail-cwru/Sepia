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

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.History.HistoryView;
import edu.cwru.sepia.environment.State.StateView;
import edu.cwru.sepia.model.Direction;
import edu.cwru.sepia.model.unit.Unit.UnitView;
import edu.cwru.sepia.util.DistanceMetrics;

public class SimpleAgent2 extends Agent {
	private static final long serialVersionUID = 1L;
	
	private StateView currentState;
	//Maps unit to it's target
	private Map<Integer,Integer> targetsOfUnits;
	private Map<Integer,Integer> targetCounts;
	
	public SimpleAgent2(int playernum, String[] notused) {
		super(playernum);
		targetsOfUnits = new HashMap<Integer,Integer>();
		targetCounts = new HashMap<Integer,Integer>();
	}
	
	@Override
	public Map<Integer,Action> initialStep(StateView newstate, HistoryView statehistory) {		
		return middleStep(newstate, statehistory);
	}

	@Override
	public Map<Integer,Action> middleStep(StateView newState, HistoryView statehistory) {
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		currentState = newState;
		targetsOfUnits.clear();
		List<Integer> unitIds = currentState.getUnitIds(playernum);
		for(int unitId : unitIds)
		{
			UnitView u = currentState.getUnit(unitId);
			int sightRange = u.getTemplateView().getSightRange();
			List<Integer> targetsInRange = new ArrayList<Integer>();
			for(int enemy : currentState.getAllUnitIds())
			{
				UnitView v = currentState.getUnit(enemy);
				if (v.getTemplateView().getPlayer() == playernum)
					continue;
				int distance = DistanceMetrics.chebyshevDistance(u.getXPosition(), u.getYPosition(), v.getXPosition(), v.getYPosition());
				if(distance <= sightRange)
				{
					targetsInRange.add(enemy);
				}						
			}
			if(targetsInRange.size() > 0)
			{
				int target = -1;
				int max = -1;
				for(int enemy : targetsInRange)
				{
					Integer count = targetCounts.get(enemy);
					if((count == null?0:count) > max)
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
					double distance = DistanceMetrics.chebyshevDistance(u.getXPosition(), u.getYPosition(), v.getXPosition(), v.getYPosition());
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
	public void terminalStep(StateView newstate, HistoryView statehistory) {
	}
	public static String getUsage() {
		return "None";
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
