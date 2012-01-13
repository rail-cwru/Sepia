package edu.cwru.SimpleRTS.model.resource;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.Target;

public class ResourceNode extends Target implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	private Type type;
	private int xPosition;
	private int yPosition;
	private int initialAmount;
	private int amountRemaining;
	private ResourceView view;
	public ResourceNode(Type type, int xPosition, int yPosition, int initialAmount,int ID) {
		super(ID);
		this.type = type;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.initialAmount = initialAmount;
		this.amountRemaining = initialAmount;
		view=null;
	}
	
	@Override
	protected Object clone() {
		ResourceNode copy = new ResourceNode(type, xPosition, yPosition, initialAmount,ID);
		copy.amountRemaining = amountRemaining;
		return copy;
	}
	
	public ResourceNode copyOf() {
		return (ResourceNode)clone();
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
	
	/**
	 * Try to pick some resources out of this node
	 * @param amount
	 * @return The amount of resources successfully removed from the node
	 */
	public int reduceAmountRemaining(int amount) {
		int prevAmount = amountRemaining;
		amountRemaining = Math.max(0, amountRemaining - amount);
		return prevAmount - amountRemaining;
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
	
	public static enum Type { TREE, GOLD_MINE ;
		public static ResourceType getResourceType(Type t){
			if (t == TREE)
				return ResourceType.WOOD;
			return ResourceType.GOLD;
		}
	};
	public static class ResourceView implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
