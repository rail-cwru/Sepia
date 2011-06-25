package edu.cwru.SimpleRTS.model.unit;

import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate.UnitTemplateView;

public class Unit extends Target {
	
	private UnitView view;
	protected int currentHealth;
	protected int player;
	protected int xPosition;
	protected int yPosition;
	protected UnitTemplate template;
	protected Resource.Type cargoType;
	protected int cargoAmount;
	
	protected Unit(UnitTemplate template) {
		
		this.template = template;
		this.currentHealth = template.getBaseHealth();
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public int getxPosition() {
		return xPosition;
	}
	public void setxPosition(int x) {
		xPosition = x;
	}
	public int getyPosition() {
		return yPosition;
	}
	public void setyPosition(int y) {
		yPosition = y;
	}
	public UnitTemplate getTemplate() {
		return template;
	}
	public void takeDamage(int amount)
	{
		currentHealth -= amount;
		if (currentHealth < 0)
			currentHealth = 0;
		if (currentHealth > template.baseHealth)
			currentHealth = template.baseHealth;
	}
	public boolean canGather()
	{
		return template.canGather;
	}
	public boolean canBuild()
	{
		return template.canBuild;
	}
	public boolean canMove()
	{
		return template.canMove;
	}
	public boolean canAttack()
	{
		return template.canAttack();
	}
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Unit))
			return false;
		return ((Unit)o).ID == ID;
	}
	@Override
	public String toString() {
		return "Unit [ID=" + ID + ", currentHealth="
				+ currentHealth + ", player=" + player + ", xPosition="
				+ xPosition + ", yPosition=" + yPosition + ", template="
				+ template + ", cargoType=" + cargoType + ", cargoAmount="
				+ cargoAmount + "]";
	}
	public boolean pickUpResource(Resource.Type type, int amount) {
		if(!(template).canGather())
			return false;
		cargoType = type;
		cargoAmount = amount;
		return true;
	}
	public void clearCargo() {
		cargoType = null;
		cargoAmount = 0;
	}
	public Resource.Type getCurrentCargoType() {
		return cargoType;
	}
	public int getCurrentCargoAmount() {
		return cargoAmount;
	}
	public UnitView getView() {
		if(view == null)
			view = new UnitView(this);
		return view;
	}
	public static class UnitView {
		private Unit unit;
		public UnitView(Unit unit) {
			this.unit = unit;
		}
		public int getHP() {
			return unit.currentHealth;
		}
		public int getPlayer() {
			return unit.player;
		}
		public int getCargoAmount() {
			return unit.cargoAmount;
		}
		public Resource.Type getCargoType() {
			return unit.cargoType;
		}
		public int getXPosition() {
			return unit.xPosition;
		}
		public int getYPosition() {
			return unit.yPosition;
		}
		public UnitTemplateView getTemplateView() {
			return new UnitTemplateView(unit.template);
		}
	}
}
