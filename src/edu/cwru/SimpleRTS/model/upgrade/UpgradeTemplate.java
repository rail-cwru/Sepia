package edu.cwru.SimpleRTS.model.upgrade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cwru.SimpleRTS.environment.IDDistributer;
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

	public UpgradeTemplate(int ID, int attackchange, int defensechange, String[] affectedunits)
	{
		super(ID);
		this.attackchange = attackchange;
		this.defensechange = defensechange;
		this.stringunitsaffected = affectedunits;
	}
	public Upgrade produceInstance(IDDistributer idsource)
	{
		return new Upgrade(attackchange, defensechange, unittemplatesaffected, this);
	}
	public List<UnitTemplate> getAffectedUnits()
	{
		return unittemplatesaffected;
	}
	public int getAttackChange() {
		return attackchange;
	}
	public int getDefenseChange() {
		return defensechange;
	}
	@Override
	public void namesToIds(List<UnitTemplate> unittemplates, List<UpgradeTemplate> upgradetemplates) {
		super.namesToIds(unittemplates, upgradetemplates);
		unittemplatesaffected = new LinkedList<UnitTemplate>();
		if (stringunitsaffected != null) {
			for (int i = 0; i<stringunitsaffected.length;i++) {
				for (UnitTemplate t : unittemplates) {
					if (stringunitsaffected[i].equals(t.getName())) {
						unittemplatesaffected.add((UnitTemplate)t);
					}
				}
			}
		}
		
		
	}
	
	public class UpgradeTemplateView extends TemplateView
	{
		private final int attackChange;
		private final int defenseChange;
		public UpgradeTemplateView(UpgradeTemplate template)
		{
			super(template);
			attackChange = template.attackchange;
			defenseChange = template.defensechange;
		}
		public int getAttackChange() {
			return attackChange;
		}
		public int getDefenseChange() {
			return defenseChange;
		}
	}

	@Override
	public edu.cwru.SimpleRTS.model.Template.TemplateView getView() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void deprecateOldView() {
		// TODO Auto-generated method stub
		
	}
	
}
