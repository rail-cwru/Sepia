package edu.cwru.SimpleRTS.action;
/**
 * An immutable (so long as Action is) result giving feedback on a specific action.
 * @author The Condor
 *
 */
public class ActionResult {
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
	public ActionFeedback getResult()
	{
		return result;
	}
	public boolean equals(Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ActionResult))
			return false;
		ActionResult arother= (ActionResult)other;
		return this.action.equals(arother.action) && this.result == arother.result;
	}
	public int hashCode()
	{
		int prime = 61;
		return prime*action.hashCode() + result.hashCode();
	}
	@Override public String toString()
	{
		return "ActionResult: "+action + " result:"+result;
	}
}
