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
package edu.cwru.sepia.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.util.DeepEquatable;
import edu.cwru.sepia.util.DeepEquatableUtil;
/**
 * Logs the results for a single player.
 * @author The Condor
 *
 */
public class ActionResultLogger implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	List<Map<Integer, ActionResult>> actionresults;
	public ActionResultLogger () {
		actionresults = new ArrayList<Map<Integer, ActionResult>>();
	}
	/**
	 * Insert an action result, overwriting any ActionResult for the same unit in the same round.
	 * <br>
	 * @param stepNumber
	 * @param actionResult
	 */
	public void addActionResult(int stepNumber, ActionResult actionResult) {
		addActionResult(stepNumber,actionResult.getAction().getUnitId(),actionResult);
	}
	/**
	 * Insert an action result, overwriting any ActionResult for the same unit id in the same round.
	 * @param stepNumber
	 * @param unitID
	 * @param actionResult
	 */
	public void addActionResult(int stepNumber, int unitID, ActionResult actionResult) {
		while (actionresults.size()<=stepNumber)
		{
			actionresults.add(new HashMap<Integer, ActionResult>());
		}
		actionresults.get(stepNumber).put(unitID,actionResult);
	}
	/**
	 * Get the results of actions for a specific round.
	 * @param roundnumber
	 * @return an unmodifiable list of ActionResults
	 */
	public Map<Integer, ActionResult> getActionResults(int roundnumber) {
		if ( roundnumber<0 || roundnumber >= actionresults.size()) {
			return new HashMap<Integer, ActionResult>();
		}
		else {
			return Collections.unmodifiableMap(actionresults.get(roundnumber));
		}
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRound() {
		return actionresults.size()-1;
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
	public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return true;
		
		ActionResultLogger o = (ActionResultLogger) other;
		if (!DeepEquatableUtil.deepEqualsListMap(actionresults, o.actionresults))
			return false;
		
		return true;
	}
	
}
