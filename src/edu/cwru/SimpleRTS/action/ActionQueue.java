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
	public boolean hasNext()
	{
		return !primitives.isEmpty();
	}
	public void resetPrimitives(LinkedList<Action> primitives)
	{
		this.primitives = primitives;
	}
}
