package edu.cwru.SimpleRTS.action;

public class LocatedAction extends Action
{
	private int x;
	private int y;
	public LocatedAction(int unitid, ActionType type, int x, int y)
	{
		super(unitid, type);
		this.x = x;
		this.y = y;
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
		return "LocatedAction [x=" + x + ", y=" + y + ", type=" + type
				+ ", unitId=" + unitId + "]";
	}
}
