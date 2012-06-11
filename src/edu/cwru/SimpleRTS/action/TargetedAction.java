package edu.cwru.SimpleRTS.action;

/**
 * A subtype of Action, include CompoundAttack, PrimitiveAttack, CompoundGather, CompoundDeposit
 *
 */
public class TargetedAction extends Action {
	
	private static final long serialVersionUID = 5319275698704767319L;
	
	private final int targetid;
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
			
			TargetedAction aother = (TargetedAction)other;
			return aother.type == type && aother.unitId == unitId && aother.targetid == targetid;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime*prime * targetid + prime * type.hashCode() + unitId;
	}
}
