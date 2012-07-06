package edu.cwru.sepia.model.upgrade;

/**
 * An instance of an upgrade template.
 * As it has no existence in the game world,
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
