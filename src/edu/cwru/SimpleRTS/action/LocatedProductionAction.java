package edu.cwru.SimpleRTS.action;

public class LocatedProductionAction extends Action{
	private int templateid;
	private int x;
	private int y;
	public LocatedProductionAction(int unitid, ActionType type, int templateid, int x, int y)
	{
		super(unitid, type);
		this.templateid = templateid;
		this.x=x;
		this.y=y;
	}
	public int getTemplateId()
	{
		return templateid;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	@Override
	public String toString() {
		return "ProductionAction [x="+x+", y="+y+", templateid=" + templateid + ", type=" + type
				+ ", unitIdOfBuilder=" + unitId + "]";
	}
}
