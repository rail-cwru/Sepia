package edu.cwru.SimpleRTS.model.upgrade;

import java.util.List;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public class Upgrade 
{
	private List<UnitTemplate> affectedunits;
	private int numpriorupgrades;
	private int attackchange;
	private int defensechange;
	private UpgradeTemplate template;
	public Upgrade(int attackchange, int defensechange, List<UnitTemplate> affectedunits, int numpriorupgrades, UpgradeTemplate template)
	{
		this.affectedunits = affectedunits;
		this.numpriorupgrades = numpriorupgrades;
		this.attackchange = attackchange;
		this.defensechange = defensechange;
		this.template = template;
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
	public List<UnitTemplate> getAffectedUnits()
	{
		return affectedunits;
	}
	public void execute(State state) {
		//make sure no other building completed it first
		if (template.getUpgradeCount() == numpriorupgrades) {
			//upgrade all of the affected units
			for (UnitTemplate toupgrade : affectedunits) {
				toupgrade.setBasicAttackLow(toupgrade.getBasicAttackLow() + attackchange);
				toupgrade.setArmor(toupgrade.getArmor() + defensechange);
				
			}
			//and make the number right
			template.incrementUpgradeCount();
			state.addUpgrade(template.ID, template.getPlayer());
		}
		
		
	}
	
}
