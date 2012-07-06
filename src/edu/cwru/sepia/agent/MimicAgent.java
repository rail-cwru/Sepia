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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.History.HistoryView;
import edu.cwru.sepia.environment.State.StateView;

/**
 * An agent that uses a map of turn numbers to action assignments to replay a series of actions.
 * @author The Condor
 *
 */
public class MimicAgent extends Agent {
	private static final long	serialVersionUID	= 1L;
	
	private Map<Integer,? extends Map<Integer, Action>> actions;
	/**
	 * Creates a mimic agent that will blindly attempt to do a series of actions.
	 * <br>Warning: copies the actual map and uses it.  Edits to the map after passing it will alter the agent's behaviour
	 * @param player
	 * @param actions A map of step numbers to the map to return for that step.
	 */
	public MimicAgent(int player, Map<Integer,? extends Map<Integer,Action>> actions) {
		super(player);
		this.actions = actions;
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate,
			HistoryView statehistory) {
		return pullActionFromMemory(newstate);
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate,
			HistoryView statehistory) {
		return pullActionFromMemory(newstate);
	}

	private Map<Integer, Action> pullActionFromMemory(StateView state) {
		int step = state.getTurnNumber();
		if (!actions.containsKey(step)) {
			//if it was not given a response, return a blank
			return new HashMap<Integer,Action>();
		}
		else {
			return actions.get(step);
			
		}
	}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory) {
		//do nothing
		
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
