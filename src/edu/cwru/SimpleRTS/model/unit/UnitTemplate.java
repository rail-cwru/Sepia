package edu.cwru.SimpleRTS.model.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.prerequisite.Prerequisite;
/**
 * Contains information about default and invariant attributes of units. The class
 * itself contains some invariant attributes, but defaults, invariants and factory
 * methods for specific types of units should be handles in concrete subclasses.
 * @author Tim
 *
 */

public class UnitTemplate extends Template<Unit> implements Serializable
{
	protected String unitName;
	protected int baseHealth;
	protected int basicAttackLow;
	protected int basicAttackDiff;
	protected int piercingAttack;
	protected int range;
	protected int armor;
	protected int sightRange;
	protected boolean canGather;
	protected boolean canBuild;
	protected boolean canMove;
	protected char character;
	protected Prerequisite prerequisite;
	private List<String> produces;
	private List<Integer> producesID;
	public UnitTemplate()
	{
		producesID = new ArrayList<Integer>();
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
	public char getCharacter() {
		return character;
	}
	public void setCharacter(char character) {
		this.character = character;
	}
	public int getBasicAttackLow() {
		return basicAttackLow;
	}
	public int getBasicAttackDiff() {
		return basicAttackDiff;
	}
	public void setBasicAttackLow(int basicAttackLow) {
		this.basicAttackLow = basicAttackLow;
	}
	public void setBasicAttackDiff(int basicAttackDiff) {
		this.basicAttackDiff = basicAttackDiff;
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
	public void setPrerequisite(Prerequisite prerequisite) {
		this.prerequisite = prerequisite;
	}
	public boolean canBeBuilt(State state) {
		return prerequisite.isFulfilled(state);
	}
	public boolean canAttack() {
		return basicAttackLow+basicAttackDiff > 0 || piercingAttack > 0;
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
	public boolean canProduce(Template t) {
		for (Integer i : producesID)
			if (t.hashCode() == i)
				return true;
		return false;
	}
	@Override
	public void turnTemplatesToStrings(List<Template> controllerstemplates) {
		producesID.clear();
		for (String s : produces)
			for (Template t : controllerstemplates) {
				if (s.equals(t.getName())) {
					producesID.add(t.hashCode());
					break;
				}
				
			}
	}
	public static class UnitTemplateView extends TemplateView<Unit> implements Serializable{

		public UnitTemplateView(UnitTemplate template) {
			super(template);
		}
		public boolean canGather() { return ((UnitTemplate)template).canGather(); }
		public boolean canBuild() { return ((UnitTemplate)template).canBuild(); }
		public boolean canMove() { return ((UnitTemplate)template).canMove(); }
		public boolean canAttack() { return ((UnitTemplate)template).canAttack(); }
		public String getUnitName() { return ((UnitTemplate)template).getUnitName(); }
		public int getBaseHealth() { return ((UnitTemplate)template).getBaseHealth();	}
		public int getBasicAttackLow() { return ((UnitTemplate)template).getBasicAttackLow(); }
		public int getBasicAttackDiff() { return ((UnitTemplate)template).getBasicAttackDiff(); }
		public int getPiercingAttack() { return ((UnitTemplate)template).getPiercingAttack();	}
		public int getRange() {	return ((UnitTemplate)template).getRange(); }
		public int getArmor() {	return ((UnitTemplate)template).getArmor(); }
		public int getSightRange() { return ((UnitTemplate)template).getSightRange();	}
		public boolean canProduce(Integer templateID) { return ((UnitTemplate)template).producesID.contains(templateID);};
	}

	
}
