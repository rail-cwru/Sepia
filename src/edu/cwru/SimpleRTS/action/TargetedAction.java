package edu.cwru.SimpleRTS.action;

public class TargetedAction extends Action
{
	private int targetid;
	public TargetedAction(int unitid, ActionType type, int targetid)
	{
		super(unitid, type);
		this.targetid = targetid;
	}
	public int getTargetId()
	{
		return targetid;
	}
	@Override
	public String toString() {
		return "TargetedAction [targetid=" + targetid + ", type=" + type
				+ ", unitId=" + unitId + "]";
	}
}
