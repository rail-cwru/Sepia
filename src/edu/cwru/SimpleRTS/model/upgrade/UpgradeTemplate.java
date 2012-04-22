package edu.cwru.SimpleRTS.model.upgrade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import edu.cwru.SimpleRTS.environment.IDDistributer;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
public class UpgradeTemplate extends Template<Upgrade>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID =-114292005500418550l;
	private int piercingAttackChange;
	private int basicAttackChange;
	private int armorChange;
	private int healthChange;
	private int rangeChange;
	private int sightRangeChange;
	private List<UnitTemplate> unitTemplatesAffected;
	private String[] stringUnitsAffected;
	private UpgradeTemplateView view;
	public UpgradeTemplate(int ID)
	{
		super(ID);
		stringUnitsAffected=new String[0];
		
	}
	public Upgrade produceInstance(IDDistributer idsource)
	{
		return new Upgrade(this);
	}
	public List<UnitTemplate> getAffectedUnits()
	{
		return unitTemplatesAffected;
	}
	public void setPiercingAttackChange(int piercingAttackchange) {
		this.piercingAttackChange = piercingAttackchange;
	}
	public int getPiercingAttackChange() {
		return piercingAttackChange;
	}
	public void setBasicAttackChange(int basicAttackChange) {
		this.basicAttackChange = basicAttackChange;
	}
	public int getBasicAttackChange() {
		return basicAttackChange;
	}
	public void setArmorChange(int armorChange) {
		this.armorChange = armorChange;
	}
	public int getArmorChange() {
		return armorChange;
	}
	public void setHealthChange(int healthChange) {
		this.healthChange = healthChange;
	}
	public int getHealthChange() {
		return healthChange;
	}
	public void setRangeChange(int rangeChange) {
		this.rangeChange = rangeChange;
	}
	public int getRangeChange() {
		return rangeChange;
	}
	public void setSightRangeChange(int sightRangeChange) {
		this.sightRangeChange = sightRangeChange;
	}
	public int getSightRangeChange() {
		return sightRangeChange;
	}
	public void addAffectedUnit(String templatename)
	{//needs to be list, kept as array to avoid breaking serialized stuff,hopefully
		String[] newstringunitsaffected = new String[stringUnitsAffected.length+1];
		System.arraycopy(stringUnitsAffected, 0, newstringunitsaffected, 0, stringUnitsAffected.length);
		newstringunitsaffected[newstringunitsaffected.length-1]=templatename;
		stringUnitsAffected=newstringunitsaffected;
	}
	@Override
	public void namesToIds(List<UnitTemplate> unittemplates, List<UpgradeTemplate> upgradetemplates) {
		super.namesToIds(unittemplates, upgradetemplates);
		unitTemplatesAffected = new LinkedList<UnitTemplate>();
		if (stringUnitsAffected != null) {
			for (int i = 0; i<stringUnitsAffected.length;i++) {
				for (UnitTemplate t : unittemplates) {
					if (stringUnitsAffected[i].equals(t.getName())) {
						unitTemplatesAffected.add((UnitTemplate)t);
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
	public static class UpgradeTemplateView extends TemplateView
	{
		private static final long	serialVersionUID	= 2L;
		private final int piercingAttackChange;
		private final int basicAttackChange;
		private final int armorChange;
		private final int healthChange;
		private final int rangeChange;
		private final int sightRangeChange;
		private final List<Integer> affectedUnitTypes;
		public UpgradeTemplateView(UpgradeTemplate template)
		{
			super(template);
			piercingAttackChange = template.piercingAttackChange;
			basicAttackChange = template.basicAttackChange;
			armorChange = template.armorChange;
			rangeChange = template.rangeChange;
			sightRangeChange = template.sightRangeChange;
			healthChange = template.healthChange;
			List<Integer> taffectedUnitTypes = new ArrayList<Integer>(template.unitTemplatesAffected.size());
			for (UnitTemplate u : template.unitTemplatesAffected)
				taffectedUnitTypes.add(u.ID);
			affectedUnitTypes = Collections.unmodifiableList(taffectedUnitTypes);
		}
		/**
		 * Get the increase in basic (armor-reduced) attack caused by this upgrade
		 * @return
		 */
		public int getBasicAttackChange() {
			return basicAttackChange;
		}
		/**
		 * Get the increase in piercing (armor-bypassing) attack caused by this upgrade
		 * @return
		 */
		public int getPiercingAttackChange() {
			return piercingAttackChange;
		}
		/**
		 * Get the increase in armor caused by this upgrade
		 * @return
		 */
		public int getArmorChange() {
			return armorChange;
		}
		/**
		 * Get the increase in range caused by this upgrade
		 * @return
		 */
		public int getRangeChange() {
			return rangeChange;
		}
		/**
		 * Get the increase in sight range caused by this upgrade
		 * @return
		 */
		public int getSightRangeChange() {
			return rangeChange;
		}
		/**
		 * Get the increase in health caused by this upgrade
		 * @return
		 */
		public int getHealthChange() {
			return healthChange;
		}
		
		/**
		 * Get an unmodifyable list of IDs representing the unit templates that are affected by this upgrade
		 * @return
		 */
		public List<Integer> getAffectedUnitTypes()
		{
			return affectedUnitTypes;
		}
	}
	@Override
	public boolean deepEquals(Object other) {
		//note, this method ignores the view.  Hopefully that is not an issue
		
				if (other == null || !this.getClass().equals(other.getClass()))
					return false;
				UpgradeTemplate o = (UpgradeTemplate)other;
				
				
				//Stuff common to all templates
				if (this.ID != o.ID)
					return false;
				
				if (this.timeCost != o.timeCost)
					return false;
				if (this.goldCost != o.timeCost)
					return false;
				if (this.woodCost != o.woodCost)
					return false;
				if (this.foodCost != o.foodCost)
					return false;
				if (this.player != o.player)
					return false;
				{
					boolean thisnull = this.prereqs == null;
					boolean othernull = o.prereqs == null;
					if ((thisnull == othernull)==false)
					{
						return false;
					}
					//if both aren't null, need to check deeper
					if (!thisnull && !othernull)
					{
						if (!prereqs.deepEquals(o))
							return false;
					}
				}
				{
					boolean thisnull = this.buildPrereq == null;
					boolean othernull = o.buildPrereq == null;
					if ((thisnull == othernull)==false)
					{
						return false;
					}
					//if both aren't null, need to check deeper
					if (!thisnull && !othernull)
					{
						if (this.buildPrereq.size() != o.buildPrereq.size())
							return false;
						for (String s : this.buildPrereq)
						{
							if (!o.buildPrereq.contains(s))
								return false;
						}
					}
				}
				{
					boolean thisnull = this.upgradePrereq == null;
					boolean othernull = o.upgradePrereq == null;
					if ((thisnull == othernull)==false)
					{
						return false;
					}
					//if both aren't null, need to check deeper
					if (!thisnull && !othernull)
					{
						if (this.upgradePrereq.size() != o.upgradePrereq.size())
							return false;
						for (String s : this.upgradePrereq)
						{
							if (!o.upgradePrereq.contains(s))
								return false;
						}
					}
				}
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
				
				
				//UpgradeTemplate specific methods
				if (this.piercingAttackChange != o.piercingAttackChange)
					return false;
				if (this.basicAttackChange != o.basicAttackChange)
					return false;
				if (this.armorChange != o.armorChange)
					return false;
				if (this.healthChange != o.healthChange)
					return false;
				if (this.rangeChange != o.rangeChange)
					return false;
				if (this.sightRangeChange != o.sightRangeChange)
					return false;
				{
					boolean thisnull = this.unitTemplatesAffected == null;
					boolean othernull = o.unitTemplatesAffected == null;
					if ((thisnull == othernull)==false)
					{
						return false;
					}
					//if both aren't null, need to check deeper
					if (!thisnull && !othernull)
					{
						if (this.unitTemplatesAffected.size() != o.unitTemplatesAffected.size())
							return false;
						for (int i = 0; i<this.unitTemplatesAffected.size(); i++)
						{
							if (!this.unitTemplatesAffected.get(i).deepEquals(o.unitTemplatesAffected.get(i)))
								return false;
						}
					}
				}
				{
					boolean thisnull = this.stringUnitsAffected == null;
					boolean othernull = o.stringUnitsAffected == null;
					if ((thisnull == othernull)==false)
					{
						return false;
					}
					//if both aren't null, need to check deeper
					if (!thisnull && !othernull)
					{
						if (this.stringUnitsAffected.length != o.stringUnitsAffected.length)
							return false;
						for (int i = 0; i<this.stringUnitsAffected.length; i++)
						{
							if (!this.stringUnitsAffected[i].equals(o.stringUnitsAffected[i]))
								return false;
						}
					}
				}
				return true;
	}
	
	
	
	
}
