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
	private UpgradeTemplateView view;
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
	@Override
	public UpgradeTemplateView getView() {
		if (view == null)
			view = new UpgradeTemplateView(this);
		return view;
	}
	@Override
	public void deprecateOldView() {
		view = null;
	}
	public class UpgradeTemplateView extends TemplateView
	{
		private final int attackChange;
		private final int defenseChange;
		private final List<Integer> affectedUnitTypes;
		public UpgradeTemplateView(UpgradeTemplate template)
		{
			super(template);
			attackChange = template.attackchange;
			defenseChange = template.defensechange;
			affectedUnitTypes = new ArrayList<Integer>(template.unittemplatesaffected.size());
			for (UnitTemplate u : template.unittemplatesaffected)
				affectedUnitTypes.add(u.ID);
		}
		/**
		 * Get the increase in attack caused by this upgrade
		 * @return
		 */
		public int getAttackChange() {
			return attackChange;
		}
		/**
		 * Get the increase in armor caused by this upgrade
		 * @return
		 */
		public int getDefenseChange() {
			return defenseChange;
		}
		/**
		 * Get a list of IDs representing the unit templates that are affected by this upgrade
		 */
		public List<Integer> getAffectedUnitTypes()
		{
			return affectedUnitTypes;
		}
	}

	
	
}
