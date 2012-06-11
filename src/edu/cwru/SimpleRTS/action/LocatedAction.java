package edu.cwru.SimpleRTS.action;

/**
 * A subtype of Action, include CompoundMove
 *
 */
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
		else if (other == null || !this.getClass().equals(other.getClass()))
		{
			return false;
		}
		else
		{
			
			LocatedAction aother = (LocatedAction)other;
			return aother.type == type && aother.unitId == unitId && aother.x == x && aother.y == y;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime * prime * prime * x + prime * prime * y + prime * type.hashCode() + unitId;
	}
}
