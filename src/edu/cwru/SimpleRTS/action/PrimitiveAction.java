package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class PrimitiveAction extends Action {
	
	protected PrimitiveActionType type;
	protected Direction direction;
	
	public PrimitiveAction(PrimitiveActionType type, Direction direction) {
		this.type = type;
		this.direction = direction;
	}
	public PrimitiveAction(PrimitiveActionType type) {
		this.type = type;
	}
	public PrimitiveActionType getType() {
		return type;
	}
	public Direction getDirection() {
		return direction;
	}
}
