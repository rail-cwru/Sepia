package edu.cwru.SimpleRTS.model.resource;

import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class Resource extends Target {
	private Type type;
	private int xPosition;
	private int yPosition;
	private int amountRemaining;
	
	public Resource(Type type, int xPosition, int yPosition, int initialAmount) {
		this.type = type;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.amountRemaining = initialAmount;
	}
	
	public Type getType() {
		return type;
	}

	public int getxPosition() {
		return xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public int getID() {
		return ID;
	}
	public int getAmountRemaining() {
		return amountRemaining;
	}
	public void setAmountRemaining(int amount) {
		amountRemaining = amount;
	}
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Resource))
			return false;
		return ((Resource)o).ID == ID;
	}
	
	public enum Type { TREE, GOLD_MINE };
}
