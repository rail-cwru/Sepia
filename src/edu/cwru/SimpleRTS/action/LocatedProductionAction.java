package edu.cwru.SimpleRTS.action;

/**
 * A subtype of Action, include CompoundBuild
 *
 */
public class LocatedProductionAction extends Action{
	
	private static final long serialVersionUID = 6137732739465321081L;
	
	private final int templateid;
	private final int x;
	private final int y;
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
		return "LocatedProductionAction [x="+x+", y="+y+", templateid=" + templateid + ", type=" + type
				+ ", unitIdOfBuilder=" + unitId + "]";
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
			
			LocatedProductionAction aother = (LocatedProductionAction)other;
			return aother.type == type && aother.unitId == unitId && aother.x == x && aother.y == y && aother.templateid == templateid;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime*prime*prime*prime*x + prime*prime*prime*y+prime *prime* templateid + prime * type.hashCode() + unitId;
	}
}
