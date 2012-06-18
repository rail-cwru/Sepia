package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.util.DeepEquatable;
import edu.cwru.SimpleRTS.util.DeepEquatableUtil;

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
