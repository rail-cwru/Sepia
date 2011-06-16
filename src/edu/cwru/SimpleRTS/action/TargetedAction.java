package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.unit.Unit;

public class TargetedAction extends Action
{
	int targetid;
	public TargetedAction(int unitid, ActionType type, int targetid)
	{
		super(unitid, type);
		this.targetid = targetid;
	}
	public int getTargetId()
	{
		return targetid;
	}
}
