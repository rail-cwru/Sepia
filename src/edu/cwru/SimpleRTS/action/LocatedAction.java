package edu.cwru.SimpleRTS.action;

public class LocatedAction extends Action
{
	private final int x;
	private final int y;
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
	@Override public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		else if (!(other instanceof LocatedAction))
		{
			return false;
		}
		else
		{
			
			LocatedAction aother = (LocatedAction)other;
			return super.equals(aother) && aother.x == x && aother.y == y;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime * prime * x + prime * y + super.hashCode();
	}
}
