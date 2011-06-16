package edu.cwru.SimpleRTS.model.upgrade;

import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public class Upgrade 
{
	private UnitTemplate[] affectedunits;
	private int numpriorupgrades;
	private boolean isattackupgrade;
	public Upgrade(boolean isattackupgrade, UnitTemplate[] affectedunits, int numpriorupgrades)
	{
		this.affectedunits = affectedunits;
		this.numpriorupgrades = numpriorupgrades;
		this.isattackupgrade = isattackupgrade;
	}
	
	/**
	 * Return the number of times this upgrade type was run before this one was started, 
	 * to prevent cheating faster upgrades by building multiple upgrade buildings
	 * @return
	 */
	public int getNumPriorUpgrades()
	{
		return numpriorupgrades;
	}
	public UnitTemplate[] getAffectedUnits()
	{
		return affectedunits;
	}
	public boolean isAttackUpgrade()
	{
		return isattackupgrade;
	}
	
}
