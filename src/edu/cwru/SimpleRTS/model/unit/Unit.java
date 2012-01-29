package edu.cwru.SimpleRTS.model.unit;

import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate.UnitTemplateView;

public class Unit extends Target implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	private UnitView view;
	protected int currentHealth;
	protected int xPosition;
	protected int yPosition;
	protected UnitTemplate template;
	protected ResourceType cargoType;
	protected UnitTask task;
	protected int cargoAmount;
	@SuppressWarnings("rawtypes")
	protected Template currentProduction;
	protected int currentProductionAmount;
	public Unit(UnitTemplate template, int ID) {
		super(ID);
		this.template = template;
		this.currentHealth = template.getBaseHealth();
		currentProductionAmount = 0;
		task = UnitTask.Idle;
	}
	
	@Override
	protected Object clone() {
		Unit unit = new Unit(template, ID);
		unit.currentHealth = currentHealth;
		unit.xPosition = xPosition;
		unit.yPosition = yPosition;
		unit.cargoType = cargoType;
		unit.task = task;
		unit.cargoAmount = cargoAmount;
		unit.currentProduction = currentProduction;
		unit.currentProductionAmount = currentProductionAmount;
		return unit;
	}
	
	public Unit copyOf() {
		Unit copy = (Unit)clone();		
		return copy;
	}

	public int getPlayer() {
		return template.getPlayer();
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
	/**
	 * Set the x position of the unit.
	 * DO NOT USE THIS TO MOVE UNITS
	 * @param x
	 */
	public void setxPosition(int x) {
		xPosition = x;
		
	}
	
	public int getyPosition() {
		return yPosition;
	}
	/**
	 * Set the y position of the unit.
	 * DO NOT USE THIS TO MOVE UNITS
	 * @param y
	 */
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
	public void setHP(int amount)
	{
		currentHealth=amount;
	}
	public int getAmountProduced()
	{
		return currentProductionAmount;
	}
	public int getCurrentProductionID()
	{
		if (currentProduction==null)
			return Integer.MIN_VALUE;
		return currentProduction.ID;
	}
	public void resetProduction() {
		currentProduction = null;
		currentProductionAmount = 0;
	}
	/**
	 * Increment production amount
	 * @param templateID
	 */
	public void setProduction(Template toproduce, int amount) {
		
		currentProduction = toproduce;
		currentProductionAmount = amount;
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
				+ currentHealth + ", player=" + template.getPlayer() + ", xPosition="
				+ xPosition + ", yPosition=" + yPosition + ", template="
				+ template + ", cargoType=" + cargoType + ", cargoAmount="
				+ cargoAmount + "]";
	}
	public boolean setCargo(ResourceType type, int amount) {
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
	public void deprecateOldView()
	{
		//Stop linking to the old one
		view=null;
		
	}
	public static class UnitView implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private final int currentHealth;
		private final int cargoAmount;
		private final ResourceType cargoType;
		private final int xPosition;
		private final int yPosition;
		private final UnitTemplateView templateView;
		private final UnitTask task;
		private final int ID;
		private final Integer currentProduction;
		private final int currentProductionAmount;
		public UnitView(Unit unit) {
			currentHealth = unit.currentHealth;
			templateView = unit.template.getView();
			cargoAmount = unit.cargoAmount;
			cargoType = unit.cargoType;
			xPosition = unit.xPosition;
			yPosition = unit.yPosition;
			task = unit.task;
			ID = unit.ID;
			currentProduction = unit.currentProduction==null?null:unit.currentProduction.ID;
			currentProductionAmount = unit.currentProductionAmount;
		}
		/**
		 * Get the current health of the unit
		 * @return
		 */
		public int getHP()   {
			return currentHealth;
		}
		/**
		 * Get the amount of gold or wood being carried by this unit.
		 * @return
		 */
		public int getCargoAmount()   {
			return cargoAmount;
		}
		/**
		 * Get the type of resource being carried by the unit.
		 * This is only relevant if getCargoAmount() indicates an amount being carried.
		 * @return
		 */
		public ResourceType getCargoType()   {
			return cargoType;
		}
		/**
		 * Get the x coordinate of the unit
		 * @return
		 */
		public int getXPosition()   {
			return xPosition;
		}
		/**
		 * Get the y coordinate of the unit
		 * @return
		 */
		public int getYPosition()   {
			return yPosition;
		}
		
		/**
		 * Get the progress of this unit toward making whatever it is making (as indicated by getCurrentProductionType())
		 * @return
		 */
		public int getCurrentProductionAmount() {
			return currentProductionAmount;
		}
		/**
		 * Get the template id of the unit or upgrade currently being produced.
		 * This is only relevant if getCurrentProductionAmount indicates something has progress
		 * @return The id of the template of what is being produced, or null
		 */
		public Integer getCurrentProductionType() {
			return currentProduction;
		}
		
		/**
		 * Get a view of the template this unit was made from.
		 * This carries information shared by all units of that type,
		 * such as the name, attack, armor, basic health, cost, 
		 * prerequisites for building it, and what units/upgrades it can produce 
		 * @return
		 */
		public UnitTemplateView getTemplateView()   {
			return templateView;
		}
		/**
		 * Get the most recent task assigned to the unit.  Most uses deprecated through the event logging functionality
		 * @return
		 */
		public UnitTask getTask()   {
			return task;
		}
		/**
		 * Get the unique identification number of this unit
		 * @return
		 */
		public int getID() {
			return ID;
		}
		
		
	}
}
