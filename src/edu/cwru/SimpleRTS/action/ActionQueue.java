package edu.cwru.SimpleRTS.action;

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
