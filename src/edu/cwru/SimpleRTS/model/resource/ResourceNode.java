package edu.cwru.SimpleRTS.model.resource;

import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class ResourceNode extends Target {
	private Type type;
	private int xPosition;
	private int yPosition;
	private int amountRemaining;
	private ResourceView view;
	public ResourceNode(Type type, int xPosition, int yPosition, int initialAmount) {
		this.type = type;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.amountRemaining = initialAmount;
		view=null;
	}
	
	public Type getType() {
		return type;
	}
	public ResourceView getView() {
		if (view == null)
			view = new ResourceView(this);
		return view;
	}
	public ResourceType getResourceType() {
		return Type.getResourceType(type);
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
		if(!(o instanceof ResourceNode))
			return false;
		return ((ResourceNode)o).ID == ID;
	}
	
	public enum Type { TREE, GOLD_MINE ;
		public static ResourceType getResourceType(Type t){
			if (t == TREE)
				return ResourceType.Wood;
			return ResourceType.Gold;
		}
	};
	public class ResourceView {
		ResourceNode node;
		public ResourceView(ResourceNode node) {
			this.node = node;
		}
		public int getID() {
			return node.ID;
		}
		public int getAmountRemaining() {
			return node.amountRemaining;
		}
		public ResourceNode.Type getType() {
			return node.type;
		}
		public int getXPosition() {
			return node.xPosition;
		}
		public int getYPosition() {
			return node.yPosition;
		}
	}
}
