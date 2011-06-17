package edu.cwru.SimpleRTS.model.unit;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.SimpleRTS.model.Template;
/**
 * Contains information about default and invariant attributes of units. The class
 * itself contains some invariant attributes, but defaults, invariants and factory
 * methods for specific types of units should be handles in concrete subclasses.
 * @author Tim
 *
 */

public class UnitTemplate extends Template<Unit>
{
	protected String unitName;
	protected int baseHealth;
	protected int attack;
	protected int piercingAttack;
	protected int range;
	protected int armor;
	protected int sightRange;
	protected int timeCost;
	protected int goldCost;
	protected int woodCost;
	protected int foodCost;
	protected boolean canGather;
	protected boolean canBuild;
	protected boolean canMove;
	private List<String> produces;
	public UnitTemplate()
	{
		produces = new ArrayList<String>();
	}
	@Override
	public Unit produceInstance() {
		Unit unit = new Unit(this);
		return unit;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public int getBaseHealth() {
		return baseHealth;
	}
	public void setBaseHealth(int baseHealth) {
		this.baseHealth = baseHealth;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public int getPiercingAttack() {
		return piercingAttack;
	}
	public void setPiercingAttack(int piercingAttack) {
		this.piercingAttack = piercingAttack;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getArmor() {
		return armor;
	}
	public void setArmor(int armor) {
		this.armor = armor;
	}
	public int getSightRange() {
		return sightRange;
	}
	public void setSightRange(int sightRange) {
		this.sightRange = sightRange;
	}
	public int getTimeCost() {
		return timeCost;
	}
	public void setTimeCost(int timeCost) {
		this.timeCost = timeCost;
	}
	public int getGoldCost() {
		return goldCost;
	}
	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}
	public int getWoodCost() {
		return woodCost;
	}
	public void setWoodCost(int woodCost) {
		this.woodCost = woodCost;
	}
	public int getFoodCost() {
		return foodCost;
	}
	public void setFoodCost(int foodCost) {
		this.foodCost = foodCost;
	}
	public boolean canAttack() {
		return attack > 0 || piercingAttack > 0;
	}
	public boolean canGather() { return canGather; }
	public void setCanGather(boolean canGather) { this.canGather = canGather; } 
	public boolean canBuild() { return canBuild; }
	public void setCanBuild(boolean canBuild) { this.canBuild = canBuild; }
	public boolean canMove() { return canMove; }
	public void setCanMove(boolean canMove) { this.canMove = canMove; }
	public void addProductionItem(String item) {
		this.produces.add(item);
	}
	public List<String> getProduces() {
		return produces;
	}
}
