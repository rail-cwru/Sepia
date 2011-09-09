package edu.cwru.SimpleRTS.model.unit;

import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate.UnitTemplateView;

public class Unit extends Target {
	
	private UnitView view;
	protected int currentHealth;
	protected int player;
	protected int xPosition;
	protected int yPosition;
	protected UnitTemplate template;
	protected ResourceType cargoType;
	protected UnitTask task;
	protected int cargoAmount;
	protected Template currentProduction;
	protected int currentProductionAmount;
	public Unit(UnitTemplate template) {
		
		this.template = template;
		this.currentHealth = template.getBaseHealth();
		currentProductionAmount = 0;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}
	public char getCharacter() {
		return template.getCharacter();
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
	/**
	 * 
	 * @param amount
	 * @return
	 */
	public void takeDamage(int amount)
	{
		currentHealth -= amount;
		if (currentHealth > template.baseHealth)
			currentHealth = template.baseHealth;
		if (currentHealth < 0)
		{
			currentHealth = 0;
		}
	}
	public int getAmountProduced()
	{
		return currentProductionAmount;
	}
	public int getCurrentProductionID()
	{
		if (currentProduction==null)
			return Integer.MIN_VALUE;
		return currentProduction.hashCode();
	}
	public void resetProduction() {
		currentProduction = null;
		currentProductionAmount = 0;
	}
	/**
	 * Increment production amount
	 * @param templateID
	 */
	public void incrementProduction(Template toproduce, StateView state) {
		//check if it is even capable of producing the
		if (template.canProduce(toproduce)&&toproduce.canProduce(state))
		{
			if (getCurrentProductionID() == toproduce.hashCode())
			{
				currentProductionAmount++;
			}
			else
			{
				resetProduction();
				currentProduction = toproduce;
				currentProductionAmount++;
			}
		}
		else
		{
			resetProduction();
		}
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
	public boolean pickUpResource(ResourceType type, int amount) {
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
	public ResourceType getCurrentCargoType() {
		return cargoType;
	}
	public int getCurrentCargoAmount() {
		return cargoAmount;
	}
	public void setTask(UnitTask task) {
		this.task = task;
	}
	public UnitTask getTask() {
		return task;
	}
	public UnitView getView() {
		if(view == null)
			view = new UnitView(this);
		return view;
	}
	public static class UnitView implements Serializable{
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
		public ResourceType getCargoType() {
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
		public UnitTask getTask() {
			return unit.task;
		}
		public int getID() {
			return unit.ID;
		}
		
	}
}
