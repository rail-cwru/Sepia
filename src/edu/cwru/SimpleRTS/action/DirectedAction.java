
package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;

public class DirectedAction extends Action {
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
		else if (!(other instanceof DirectedAction))
		{
			return false;
		}
		else
		{
			
			DirectedAction aother = (DirectedAction)other;
			return super.equals(aother) && aother.direction == direction;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime * direction.hashCode() + super.hashCode();
	}
}
