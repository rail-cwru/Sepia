package edu.cwru.SimpleRTS.action;

public class Action {

	protected ActionType type;
	protected int unitId;
	public Action(int untId, ActionType type)
	{
		this.type = type;
		this.unitId = untId;
	}
	public int getUnitId() {
		return unitId;
	}
	public ActionType getType()
	{
		return type;
	}
}
