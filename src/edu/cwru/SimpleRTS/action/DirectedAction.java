
package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;

/**
 * A subtype of Action, include PrimitiveMove, PrimitiveGather, PrimitiveDeposit
 *
 */
public class DirectedAction extends Action {
	
	private static final long serialVersionUID = -8274872242806705391L;
	
	private final Direction direction;
	public DirectedAction(int unitid, ActionType type, Direction direction)
	{
		super(unitid, type);
		this.direction = direction;
	}
	public Direction getDirection()
	{
		return direction;
	}
	@Override
	public String toString() {
		return "DirectedAction [direction=" + direction + ", type=" + type
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
			
			DirectedAction aother = (DirectedAction)other;
			return aother.type == type && aother.unitId == unitId && aother.direction == direction;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime * prime * direction.hashCode() + prime * type.hashCode() + unitId;
	}
}
