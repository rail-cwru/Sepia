package edu.cwru.SimpleRTS.model.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.cwru.SimpleRTS.environment.IDDistributer;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.prerequisite.PrerequisiteHolder;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.DeepEquatableUtil;
/**
 * Contains information shared between units of the same type.
 * @author Tim
 *
 */

public class UnitTemplate extends Template<Unit> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int baseHealth;
	protected int basicAttack;
	protected int piercingAttack;
	protected int range;
	protected int armor;
	protected int sightRange;
	protected boolean canGather;
	protected boolean canBuild;
	protected boolean canMove;
	protected boolean canAcceptGold;
	protected boolean canAcceptWood;
	protected int foodProvided;
	protected char character;
	protected int goldCapacity;
	protected int woodCapacity;
	protected int goldGatherRate;
	protected int woodGatherRate;
	protected int durationGoldGather;
	protected int durationWoodGather;
	protected int durationMove;
	protected int durationAttack;
	protected int durationDeposit;
	private UnitTemplateView view;
	private List<Integer> producesID;
	public UnitTemplate(int ID)
	{
		super(ID);
		producesID = new ArrayList<Integer>();
	}
	@Override
	public Unit produceInstance(IDDistributer idsource) {
		Unit unit = new Unit(this, idsource.nextTargetID());
		return unit;
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
	public int getBasicAttack() {
		return basicAttack;
	}
	public void setBasicAttack(int basicAttack) {
		this.basicAttack = basicAttack;
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
	public boolean canAttack() {
		return basicAttack > 0 || piercingAttack > 0;
	}
	public boolean canAcceptGold() {
		return canAcceptGold;
	}
	public boolean canAcceptWood() {
		return canAcceptWood;
	}
	public void setCanAcceptGold(boolean canAcceptGold) {
		this.canAcceptGold=canAcceptGold;
	}
	public void setCanAcceptWood(boolean canAcceptWood) {
		this.canAcceptWood=canAcceptWood;
	}
	public int getFoodProvided() {
		return foodProvided;
	}
	public void setFoodProvided(int numFoodProvided) {
		this.foodProvided = numFoodProvided;
	}
	public void setGoldGatherRate(int goldpertrip) {
		this.goldGatherRate = goldpertrip;
		this.goldCapacity = goldpertrip;
	}
	public void setWoodGatherRate(int woodpertrip) {
		this.woodGatherRate = woodpertrip;
		this.woodCapacity = woodpertrip;
	}
	public int getGatherRate(ResourceNode.Type type) {
		if (type == ResourceNode.Type.GOLD_MINE) {
			return goldGatherRate;
		}
		else if (type == ResourceNode.Type.TREE) {
			return woodGatherRate;
		}
			
		return 0;
	}
	/**
	 * Return the amount of resource that you can gather from each node type.
	 * @param type The type of resource node to gather from.
	 * @return
	 */
	public int getCarryingCapacity(ResourceNode.Type type) {
		if (type == ResourceNode.Type.GOLD_MINE) {
			return goldCapacity;
		}
		else if (type == ResourceNode.Type.TREE) {
			return woodCapacity;
		}
			
		return 0;
	}
	public boolean canGather() { return canGather; }
	public void setCanGather(boolean canGather) { this.canGather = canGather; } 
	public boolean canBuild() { return canBuild; }
	public void setCanBuild(boolean canBuild) { this.canBuild = canBuild; }
	public boolean canMove() { return canMove; }
	public void setCanMove(boolean canMove) { this.canMove = canMove; }
	/**
	 * Get the number of steps needed to make a single gather on a mine.
	 * @return
	 */
	public int getDurationGatherGold() {
		return durationGoldGather;
	}
	/**
	 * Set the number of steps needed to make a single gather on a mine.
	 * @param durationGoldGather
	 */
	public void setDurationGatherGold(int durationGoldGather) {
		this.durationGoldGather = durationGoldGather;
	}
	/**
	 * Get the number of steps needed to make a single gather on a tree.
	 * @return
	 */
	public int getDurationGatherWood() {
		return durationWoodGather;
	}
	/**
	 * Set the number of steps needed to make a single gather on a tree.
	 * @param durationWoodGather
	 */
	public void setDurationGatherWood(int durationWoodGather) {
		this.durationWoodGather = durationWoodGather;
	}
	/**
	 * Get the number of steps needed to make a single move.
	 * @return
	 */
	public int getDurationMove() {
		return durationMove;
	}
	/**
	 * Set the number of steps needed to make a single move.
	 * @param durationMove
	 */
	public void setDurationMove(int durationMove) {
		this.durationMove = durationMove;
	}
	/**
	 * Get the number of steps needed to make a single attack
	 * @return
	 */
	public int getDurationAttack() {
		return durationAttack;
	}
	/**
	 * Set the number of steps needed to make a single attack
	 * @param durationAttack
	 */
	public void setDurationAttack(int durationAttack) {
		this.durationAttack = durationAttack;
	}
	/**
	 * Get the number of steps needed to make a single deposit.
	 * @return
	 */
	public int getDurationDeposit() {
		return durationDeposit;
	}
	/**
	 * Set the number of steps needed to make a single deposit.
	 * @param durationDeposit
	 */
	public void setDurationDeposit(int durationDeposit) {
		this.durationDeposit = durationDeposit;
	}
	public void addProductionItem(Integer item) {
		this.producesID.add(item);
	}
	/**
	 * Get a list of IDs of templates that this unit can make.
	 * <br>Changing this list will alter the data of this template.
	 * @return A list of IDs for templates that can be produced/built by this unit. 
	 */
	public List<Integer> getProduces() {
		return this.producesID;
	}
	/**
	 * Return whether the unit is capable of producing a specific template
	 * @param t
	 * @return
	 */
	public boolean canProduce(@SuppressWarnings("rawtypes") Template t) {
		if (t==null)
			return false;
		for (Integer i : producesID)
			if (t.ID == i)
				return true;
		return false;
	}
	public String toString() {
		return name;
	}
	@Override
	public UnitTemplateView getView() {
		if (view == null)
			view = new UnitTemplateView(this);
		return view;
	}
	@Override
	public void deprecateOldView() {
		view = null;		
	}

	/**
	 * An immutable representation of a UnitTemplate.
	 */
	public static class UnitTemplateView extends TemplateView implements Serializable{
		private final boolean canGather;
		private final boolean canBuild;
		private final boolean canMove;
		private final boolean canAttack;
		private final int baseHealth;
		private final int basicAttack;
		private final int piercingAttack;
		private final int range;
		private final int armor;
		private final int sightRange;
		private final List<Integer> producesID;
		private final char character;
		private final boolean acceptsGold;
		private final boolean acceptsWood;
		private final int foodProvided;
		private final int durationMove;
		private final int durationAttack;
		private final int durationGatherGold;
		private final int durationGatherWood;
		private final int durationDeposit;
		private static final long serialVersionUID = 1L;
		
		/**
		 * Copy all information from a template and save it.
		 * @param template
		 */
		public UnitTemplateView(UnitTemplate template) {
			super(template);
			canGather = template.canGather();
			canBuild = template.canBuild();
			canMove = template.canMove();
			canAttack = template.canAttack();
			baseHealth = template.getBaseHealth();
			basicAttack = template.getBasicAttack();
			piercingAttack = template.getPiercingAttack();
			range = template.getRange();
			armor = template.getArmor();
			sightRange = template.getSightRange();
			List<Integer> tproducesID = new ArrayList<Integer>(template.producesID.size());
			for (Integer i : template.producesID)
				tproducesID.add(i);
				//note: get method currently relies on producesID being unmodifiable (as it returns it directly), so change that if you change this
			producesID = Collections.unmodifiableList(tproducesID);
			character = template.getCharacter();
			acceptsGold = template.canAcceptGold;
			acceptsWood = template.canAcceptWood;
			foodProvided = template.getFoodProvided();
			durationMove = template.getDurationMove();
			durationAttack = template.getDurationAttack();
			durationDeposit = template.getDurationDeposit();
			durationGatherGold = template.getDurationGatherGold();
			durationGatherWood = template.getDurationGatherWood();
			
		}
		public boolean canGather() { return canGather; }
		/**
		 * Get whether units with this template uses the build action to make things.  This is independant of whether the template can actually make anything.
		 * @return true if this template makes units with build actions, false if produce actions are used instead.
		 */
		public boolean canBuild() { return canBuild; }
		/**
		 * Get whether units with this template can move
		 * @return true if this template makes units that can move, false if it makes units that can't move (like buildings)
		 */
		public boolean canMove() { return canMove; }
		/**
		 * Get whether units with this template can make attacks.
		 * @return true if this template makes units that can attack, false if it makes units that cannot attack.
		 */
		public boolean canAttack() { return canAttack; }
		/**
		 * Get the starting health of units with this template.
		 * @return The amount of health/hit points that units made with this template start with.
		 */
		public int getBaseHealth() { return baseHealth;	}
		/**
		 * Get the Basic Attack of units with this template.  This is one of the fields used in damage calculations.  It represents the portion of the attack that can be mitigated with armor.
		 * @return The Basic Attack of units with this template.
		 */
		public int getBasicAttack() { return basicAttack; }
		/**
		 * Get the Piercing Attack of units with this template.  This is one of the fields used in damage calculations.  It represents the portion of the attack that is unaffected by armor.
		 * @return The Piercing Attack of units with this template.
		 */
		public int getPiercingAttack() { return piercingAttack;	}
		/**
		 * Get the maximum distance at which units with this template are able to make successful attacks.
		 * @return The range of attack for units with this template.
		 */
		public int getRange() {	return range; }
		/**
		 * Get the armor of units with this template.  Higher armor causes greater reduction of the Basic Attack component of damage.
		 * @return The amount of armor of units with this template.
		 */
		public int getArmor() {	return armor; }
		/**
		 * Get the sight range of units with this template.  A unit "sees" only units and events that occur at distances not exceeding it's sight range.  In partially observable maps, an agent is only able to observe events and units that can be seen by units it controls.
		 * @return
		 */
		public int getSightRange() { return sightRange;	}
		/**
		 * Get whether units with this template are able to make a specific other template.  This includes making by either building or producing.
		 * <br>Currently just a List.contains()
		 * @param templateID The ID of the template that may be able to BE produced.
		 * @return Whether this template can make the template in the parameter.
		 */
		public boolean canProduce(Integer templateID) {return producesID.contains(templateID);};
		/**
		 * Get a list of template ids that can be produced by this unit. 
		 * <br>The list is unmodifiable.
		 * @return A list of ids of templates this unit can make.
		 */
		public List<Integer> getProduces() {
			return producesID;
			}
		/**
		 * Get the character to be used in visualization.
		 * @return The character to be used in visualization.
		 */
		public char getCharacter() { return character; }
		/**
		 * Get whether units with this template can successfully be the target of a deposit action by a unit carrying gold.
		 * @return Whether this template makes units that can be the target of a gold deposit.
		 */
		public boolean canAcceptGold() {return acceptsGold; }
		/**
		 * Get whether units with this template can successfully be the target of a deposit action by a unit carrying wood.
		 * @return Whether this template makes units that can be the target of a wood deposit.
		 */
		public boolean canAcceptWood() {return acceptsWood; }
		/**
		 * Get the amount of food provided by a unit with this template.
		 * @return The amount of food/supply that units made with this template provide.
		 */
		public int getFoodProvided() {return foodProvided; }
		/**
		 * Get the duration of a primitive move action.  This is the base amount for how many consecutive steps the primitive action needs to be repeated before it has an effect.
		 * Actual number of steps may depend on other factors, determined by the Planner and Model being used. 
		 * @return The base duration of a primitive move action.
		 */
		public int getDurationMove() {return durationMove;};
		/**
		 * Get the duration of a primitive gather action on a gold mine.  This is the base amount for how many consecutive steps the primitive action needs to be repeated before it has an effect.
		 * Actual number of steps may depend on other factors, determined by the Planner and Model being used. 
		 * @return The base duration of a primitive gather action on a gold mine.
		 */
		public int getDurationGatherGold() {return durationGatherGold;};
		/**
		 * Get the duration of a primitive gather action on a tree.  This is the base amount for how many consecutive steps the primitive action needs to be repeated before it has an effect.
		 * Actual number of steps may depend on other factors, determined by the Planner and Model being used. 
		 * @return The base duration of a primitive gather action on a tree.
		 */
		public int getDurationGatherWood() {return durationGatherWood;};
		/**
		 * Get the duration of a primitive attack action.  This is the base amount for how many consecutive steps the primitive action needs to be repeated before it has an effect.
		 * Actual number of steps may depend on other factors, determined by the Planner and Model being used. 
		 * @return The base duration of a primitive attack action.
		 */
		public int getDurationAttack() {return durationAttack;};
		/**
		 * Get the duration of a primitive deposit action.  This is the base amount for how many consecutive steps the primitive action needs to be repeated before it has an effect.
		 * Actual number of steps may depend on other factors, determined by the Planner and Model being used. 
		 * @return The base duration of a primitive deposit action.
		 */
		public int getDurationDeposit() {return durationDeposit;};
	}
	@Override
	public boolean deepEquals(Object other) {
		//note, this method ignores the view.  Hopefully that is not an issue
		
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		UnitTemplate o = (UnitTemplate)other;
		
		
		//Stuff common to all templates
		if (this.ID != o.ID)
			return false;
		
		if (this.timeCost != o.timeCost)
			return false;
		if (this.goldCost != o.goldCost)
			return false;
		if (this.woodCost != o.woodCost)
			return false;
		if (this.foodCost != o.foodCost)
			return false;
		if (this.player != o.player)
			return false;
		if (!DeepEquatableUtil.deepEqualsIntSet(this.buildPrerequisites, o.buildPrerequisites))
			return false;
		if (!DeepEquatableUtil.deepEqualsIntSet(this.upgradePrerequisites, o.upgradePrerequisites))
			return false;
		{
			boolean thisnull = this.name== null;
			boolean othernull = o.name == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!this.name.equals(o.name))
					return false;
			}
		}
		
		
		//UnitTemplate specific stuff
		if (this.baseHealth != o.baseHealth)
			return false;
		if (this.basicAttack != o.basicAttack)
			return false;
		if (this.piercingAttack != o.piercingAttack)
			return false;
		if (this.range != o.range)
			return false;
		if (this.armor != o.armor)
			return false;
		if (this.sightRange != o.sightRange)
			return false;
		if (this.canGather != o.canGather)
			return false;
		if (this.canBuild != o.canBuild)
			return false;
		if (this.canMove != o.canMove)
			return false;
		if (this.canAcceptGold != o.canAcceptGold)
			return false;
		if (this.canAcceptWood != o.canAcceptWood)
			return false;
		if (this.foodProvided != o.foodProvided)
			return false;
		if (this.character != o.character)
			return false;
		if (this.goldCapacity != o.goldCapacity)
			return false;
		if (this.woodCapacity != o.woodCapacity)
			return false;
		if (this.goldGatherRate != o.goldGatherRate)
			return false;
		if (this.woodGatherRate != o.woodGatherRate)
			return false;
		if (this.durationGoldGather != o.durationGoldGather)
			return false;
		if (this.durationWoodGather != o.durationWoodGather)
			return false;
		if (this.durationMove != o.durationMove)
			return false;
		if (this.durationAttack != o.durationAttack)
			return false;
		if (this.durationDeposit != o.durationDeposit)
			return false;
		
		
		{
			boolean thisnull = this.producesID == null;
			boolean othernull = o.producesID == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.producesID.size() != o.producesID.size())
					return false;
				for (Integer i : this.producesID)
				{
					if (!o.producesID.contains(i))
						return false;
				}
			}
		}
		return true;
	}
}
