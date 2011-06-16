package edu.cwru.SimpleRTS.model.upgrade;
import java.util.ArrayList;

import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
public class UpgradeTemplate extends Template<Upgrade>
{
	private boolean isattackupgrade;
	private UnitTemplate[] unittemplatesaffected;
	private int timetoproduce;
	private int upgradecount; //A count of the number of times this upgrade has been completed
	public UpgradeTemplate(boolean isattackupgrade, UnitTemplate[] affectedunits)
	{
		this.isattackupgrade = isattackupgrade;
		this.unittemplatesaffected = affectedunits;
	}
	public Upgrade produceInstance()
	{
		return new Upgrade(isattackupgrade, unittemplatesaffected, upgradecount);
	}
	public void incrementUpgradeCount()
	{
		upgradecount++;
	}
	public int getUpgradeCount()
	{
		return upgradecount;
	}
	
}
