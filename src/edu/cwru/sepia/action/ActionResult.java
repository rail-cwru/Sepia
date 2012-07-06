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
package edu.cwru.sepia.action;

import edu.cwru.sepia.util.DeepEquatable;
import edu.cwru.sepia.util.DeepEquatableUtil;

/**
 * An immutable (so long as Action is) result giving feedback on a specific action.
 * @author The Condor
 *
 */
public class ActionResult implements DeepEquatable {
	private final Action action;
	private final ActionFeedback result;
	
	public ActionResult(Action action, ActionFeedback result)
	{
		this.action = action;
		this.result = result;
	}
	
	public Action getAction()
	{
		return action;
	}
	
	public ActionFeedback getFeedback()
	{
		return result;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ActionResult))
			return false;
		ActionResult arother= (ActionResult)other;
		return this.action.equals(arother.action) && this.result == arother.result;
	}
	
	@Override
	public int hashCode()
	{
		int prime = 61;
		return prime*action.hashCode() + result.hashCode();
	}
	
	@Override 
	public String toString()
	{
		return "ActionResult: "+action + " result:"+result;
	}
	
	@Override
	public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		ActionResult o = (ActionResult)other;
		if (!DeepEquatableUtil.deepEquals(this.action,o.action))
			return false;
		if (this.result != o.result)
			return false;
		return true;
	}
}
