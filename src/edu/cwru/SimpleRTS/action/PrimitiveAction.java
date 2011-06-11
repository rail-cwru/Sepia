package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class PrimitiveAction {
	
	protected PrimitiveActionType type;
	protected Direction direction;
	protected Unit unit;
	
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
	public Unit getUnit() {
		return unit;
	}
}
