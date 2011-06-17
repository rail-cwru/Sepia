
package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;

public class DirectedAction extends Action {
	Direction direction;
	public DirectedAction(int unitid, ActionType type, Direction direction)
	{
		super(unitid, type);
		this.direction = direction;
	}
	public Direction getDirection()
	{
		return direction;
	}
}
