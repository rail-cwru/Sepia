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

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.History.HistoryView;
import edu.cwru.sepia.environment.State.StateView;


/**
 * An intermediary allowing for the trading of states and actions between
 * the agent and the environment.
 * @author The Condor
 *
 */
public class ThreadIntermediary implements Runnable {
	private Agent associatedAgent;
	private Map<Integer, Action> submittedActions;
	private StateView submittedState;
	private HistoryView submittedHistory;
	private CountDownLatch stateLatch;
	private CountDownLatch actionLatch;
	private StateType submittedStateType;
	public ThreadIntermediary(Agent associatedAgent)
	{
		this.associatedAgent = associatedAgent;
		submittedState = null;
		submittedActions = null;
		submittedStateType = null;
		stateLatch = new CountDownLatch(1);
	}
	/**
	 * Submit a state and its type, receiving a latch for the agent's action.
	 * @param stateView
	 * @param stateType
	 * @return A latch whose countdown indicates that the agent has responded with an action.
	 */
	public synchronized CountDownLatch submitState(StateView stateView, HistoryView historyView, StateType stateType)
	{
		stateLatch.countDown();
		submittedState = stateView;
		submittedStateType = stateType;
		submittedActions = null;
		submittedHistory = historyView;
		actionLatch = new CountDownLatch(1);
		return actionLatch;
	}
	/**
	 * Get the current state and a latch to await for the next state.
	 * @return A structure with a StateView and a CountDownLatch.  The former is null when no state was set before.
	 */
	public synchronized ViewAndNextLatch retrieveState()
	{
		
		//only make a new latch if the old one is used up
		if (stateLatch.getCount()==0)
		{
			stateLatch = new CountDownLatch(1);
		}
		return new ViewAndNextLatch(submittedState, submittedHistory, submittedStateType, stateLatch);
	}
	public synchronized void submitActions(Map<Integer, Action> actions)
	{
		submittedActions = actions;
		actionLatch.countDown();
	}
	public synchronized Map<Integer, Action> retrieveActions()
	{
		return submittedActions;
	}
	
	/**
	 * A simple structure allowing the return of the 
	 * current state/history and a latch that indicates when
	 * the next state will be ready. 
	 * @author The Condor
	 *
	 */
	public static class ViewAndNextLatch
	{
		public final HistoryView historyView;
		public final StateView stateView;
		public final CountDownLatch nextStateLatch;
		public final StateType stateType;
		/**
		 * Build a structure with a view and a latch.
		 * @param stateView The current state.
		 * @param historyView The current history.
		 * @param stateType INITIAL if this is the first state of an episode, TERMINAL if it is the last, MIDDLE otherwise
		 * @param nextStateLatch A latch for the next state.
		 */
		public ViewAndNextLatch(StateView stateView, HistoryView historyView, StateType stateType, CountDownLatch nextStateLatch)
		{
			this.stateView = stateView;
			this.historyView = historyView;
			this.stateType = stateType;
			this.nextStateLatch = nextStateLatch;
		}
	}
	public static enum StateType
	{
		INITIAL, MIDDLE, TERMINAL;
	}
	@Override
	public void run() {
		while (true) {
			ViewAndNextLatch viewAndLatch = retrieveState();
			StateView state = viewAndLatch.stateView;
			HistoryView history = viewAndLatch.historyView;
			CountDownLatch latch = viewAndLatch.nextStateLatch;
			ThreadIntermediary.StateType type = viewAndLatch.stateType;
			//If there is a current state
			if (state != null && history!=null) {
				Map<Integer,Action> actions;
				switch (type) {
				case INITIAL:
					actions = associatedAgent.initialStep(state, history);
					break;
				case MIDDLE:
					actions = associatedAgent.middleStep(state, history);
					break;
				case TERMINAL:
					associatedAgent.terminalStep(state, history);
					actions = null;
					break;
				default:
					throw new IllegalArgumentException("State type \""+type+"\" is not supported by agent.");	
				}
				submitActions(actions);
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
