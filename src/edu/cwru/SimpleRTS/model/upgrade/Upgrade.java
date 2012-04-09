package edu.cwru.SimpleRTS.model.upgrade;

import java.util.List;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

/**
 * An instance of an upgrade template.
 * As it has no existance in the game world,
 * all relevant data is stored in its template.
 */
public class Upgrade 
{
	private UpgradeTemplate template;
	public Upgrade(UpgradeTemplate template)
	{
		this.template = template;
	}
	public UpgradeTemplate getTemplate() {
		return template;
	}
}
