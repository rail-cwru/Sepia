package edu.cwru.SimpleRTS.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.environment.State.StateView;

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
}
