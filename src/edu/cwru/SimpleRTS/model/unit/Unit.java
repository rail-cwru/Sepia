package edu.cwru.SimpleRTS.model.unit;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Target;
import edu.cwru.SimpleRTS.model.resource.Resource;

public class Unit extends Target {
	
	private UnitView view;
	protected Target target;
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

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
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
		return "Unit [target=" + target + ", ID=" + ID + ", currentHealth="
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
	public UnitView getView() {
		if(view == null)
			view = new UnitView(this);
		return view;
	}
	public class UnitView {
		Unit unit;
		public UnitView(Unit unit) {
			this.unit = unit;
		}
		public int getHP() {
			return unit.currentHealth;
		}
		public int getBasicAttack() {
			return unit.template.basicAttackLow;
		}
		public int getPierceAttack() {
			return unit.template.piercingAttack;
		}
		
		
	}
}
