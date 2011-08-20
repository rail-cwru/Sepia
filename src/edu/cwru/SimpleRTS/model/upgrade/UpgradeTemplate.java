package edu.cwru.SimpleRTS.model.upgrade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.prerequisite.BuildingPrerequisite;
import edu.cwru.SimpleRTS.model.prerequisite.UpgradePrerequisite;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
public class UpgradeTemplate extends Template<Upgrade>
{
	private int attackchange;
	private int defensechange;
	private List<UnitTemplate> unittemplatesaffected;
	private String[] stringunitsaffected;
	private int timetoproduce;
	private int upgradecount; //A count of the number of times this upgrade has been completed

	public UpgradeTemplate(int attackchange, int defensechange, String[] affectedunits)
	{
		this.attackchange = attackchange;
		this.defensechange = defensechange;
		this.stringunitsaffected = affectedunits;
	}
	public Upgrade produceInstance()
	{
		return new Upgrade(attackchange, defensechange, unittemplatesaffected, upgradecount,this);
	}
	public void incrementUpgradeCount()
	{
		upgradecount++;
	}
	public int getUpgradeCount()
	{
		return upgradecount;
	}
	@Override
	public void namesToIds(List<UnitTemplate> unittemplates, List<UpgradeTemplate> upgradetemplates) {
		super.namesToIds(unittemplates, upgradetemplates);
		unittemplatesaffected = new LinkedList<UnitTemplate>();
		if (stringunitsaffected != null) {
			for (int i = 0; i<stringunitsaffected.length;i++) {
				for (UnitTemplate t : unittemplates) {
					if (stringunitsaffected[i].equals(t.getName()) && t instanceof UnitTemplate) {
						unittemplatesaffected.add((UnitTemplate)t);
					}
				}
			}
		}
		
		
	}
	
}
