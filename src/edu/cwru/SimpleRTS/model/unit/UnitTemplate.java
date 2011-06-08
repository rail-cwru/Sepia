package edu.cwru.SimpleRTS.model.unit;

import edu.cwru.SimpleRTS.model.Template;
/**
 * Contains information about default and invariant attributes of units. The class
 * itself contains some invariant attributes, but defaults, invariants and factory
 * methods for specific types of units should be handles in concrete subclasses.
 * @author Tim
 *
 */
public abstract class UnitTemplate implements Template<Unit>{
	protected String unitName;
	protected int baseHealth;
	protected int attack;
	protected int piercingAttack;
	protected int range;
	protected int armor;
	protected int sightRange;
	protected int goldCost;
	protected int woodCost;
	protected int foodCost;
	protected boolean canAttack;
	@Override
	public abstract Unit produceInstance();

}
