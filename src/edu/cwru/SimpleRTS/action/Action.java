package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.unit.Unit;

public class Action {

	protected ActionType type;
	protected int acterid;
	public Action(int acterid, ActionType type)
	{
		this.type = type;
		this.acterid = acterid;
	}
	public int getActer() {
		return acterid;
	}
	public ActionType getType()
	{
		return type;
	}
}
