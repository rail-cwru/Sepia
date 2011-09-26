package edu.cwru.SimpleRTS.model.upgrade;

import java.util.List;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

/**
 * A class that doesn't really do anything on its own 
 * It has a template though, so that's something
 */
public class Upgrade 
{
	private UpgradeTemplate template;
	public Upgrade(int attackchange, int defensechange, List<UnitTemplate> affectedunits, UpgradeTemplate template)
	{
		this.template = template;
	}
	public UpgradeTemplate getTemplate() {
		return template;
	}
}
