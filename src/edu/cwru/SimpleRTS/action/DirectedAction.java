
package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;

public class DirectedAction extends Action {
	private Direction direction;
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
}
