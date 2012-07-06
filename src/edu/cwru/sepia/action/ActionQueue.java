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

import java.util.LinkedList;

/**
 * A data class that stores an action and the proposed subactions to acheive that
 * Required because the full
 */
public class ActionQueue
{
	Action fullAction;
	LinkedList<Action> primitives;
	public ActionQueue(Action fullAction, LinkedList<Action> primitives)
	{
		this.fullAction = fullAction;
		this.primitives = primitives;
	}
	public Action getFullAction()
	{
		return fullAction;
	}
	public Action popPrimitive()
	{
		return primitives.pop();
	}
	public Action peekPrimitive()
	{
		return primitives.peek();
	}
	public boolean hasNext()
	{
		return !primitives.isEmpty();
	}
	public void resetPrimitives(LinkedList<Action> primitives)
	{
		this.primitives = primitives;
	}
	/**
	 * Returns the hash code of the full action.
	 * The primitives are of no consequence.
	 */
	@Override
	public int hashCode()
	{
		return fullAction.hashCode();
	}
	/**
	 * Returns the equality of this queue's full action with the other queue's full action.
	 * Primitives are of no consequence.
	 * @return
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (!this.getClass().equals(other.getClass()))
			return true;
		else
			return fullAction.equals(((ActionQueue)other).fullAction);
	}
	
	@Override
	public String toString() 
	{
		return "ActionQueue: " + fullAction + " => " + primitives;
	}
}
