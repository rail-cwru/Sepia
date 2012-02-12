package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.ActionResult;
/**
 * Logs the results for a single player.
 * @author The Condor
 *
 */
public class ActionResultLogger implements Serializable {
	List<List<ActionResult>> actionresults;
	public ActionResultLogger () {
		actionresults = new ArrayList<List<ActionResult>>();
	}
	public void addActionResult(int stepnumber, ActionResult action) {
		while (actionresults.size()<=stepnumber)
		{
			actionresults.add(new ArrayList<ActionResult>());
		}
		actionresults.get(stepnumber).add(action);
	}
	/**
	 * Get the results of actions for a specific round.
	 * @param roundnumber
	 * @return an unmodifiable list of ActionResults
	 */
	public List<ActionResult> getActionResults(int roundnumber) {
		if ( roundnumber<0 || roundnumber >= actionresults.size()) {
			return new ArrayList<ActionResult>();
		}
		else {
			return Collections.unmodifiableList(actionresults.get(roundnumber));
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionresults == null) ? 0 : actionresults.hashCode());
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
		ActionResultLogger other = (ActionResultLogger) obj;
		if (actionresults == null) {
			if (other.actionresults != null)
				return false;
		} else if (!actionresults.equals(other.actionresults))
			return false;
		return true;
	}
	public ActionResultLoggerView getView()
	{
		return new ActionResultLoggerView();
	}
	public class ActionResultLoggerView
	{
		private ActionResultLoggerView()
		{
			
		}
		
		/**
		 * Get the actions for a specific round.
		 * @param roundnumber
		 * @return an unmodifiable list of Actions
		 */
		public List<ActionResult> getActionResults(int roundnumber) {
			//Grab the version in the containing class, then make it unmodifiable
			return Collections.unmodifiableList(ActionResultLogger.this.getActionResults(roundnumber));
		}
	}
	
}
