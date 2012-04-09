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

	
	
}
