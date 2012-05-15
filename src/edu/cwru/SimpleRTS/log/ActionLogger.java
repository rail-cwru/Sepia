package edu.cwru.SimpleRTS.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.util.DeepEquatable;
import edu.cwru.SimpleRTS.util.DeepEquatableUtil;
/**
 * Logs the results for a single player.
 * @author The Condor
 *
 */
public class ActionLogger implements Serializable, DeepEquatable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<List<Action>> actions;
	public ActionLogger () {
		actions = new ArrayList<List<Action>>();
	}
	public void addAction(int stepnumber, Action action) {
		while (actions.size()<=stepnumber)
		{
			actions.add(new ArrayList<Action>());
		}
		actions.get(stepnumber).add(action);
	}
	/**
	 * Get the actions for a specific round.
	 * @param roundnumber
	 * @return an unmodifiable list of Actions
	 */
	public List<Action> getActions(int roundnumber) {
		if ( roundnumber<0 || roundnumber >= actions.size()) {
			return new ArrayList<Action>();
		}
		else {
			return Collections.unmodifiableList(actions.get(roundnumber));
		}
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRound() {
		return actions.size()-1;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionLogger other = (ActionLogger) obj;
		if (actions == null) {
			if (other.actions != null)
				return false;
		} else if (!actions.equals(other.actions))
			return false;
		return true;
	}
	@Override public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return true;
		
		ActionLogger o = (ActionLogger) other;
		
		
		if (!DeepEquatableUtil.deepEqualsListList(this.actions, o.actions))
			return false;
		
		return true;
	}
	public ActionLoggerView getView()
	{
		return new ActionLoggerView();
	}
	public class ActionLoggerView
	{
		private ActionLoggerView()
		{
			
		}
		
		/**
		 * Get the actions for a specific round.
		 * @param roundnumber
		 * @return an unmodifiable list of Actions
		 */
		public List<Action> getActions(int roundnumber) {
			//Grab the version in the containing class, then make it unmodifiable
			return Collections.unmodifiableList(ActionLogger.this.getActions(roundnumber));
		}
	}
	
}
