package edu.cwru.SimpleRTS.model.unit;

import edu.cwru.SimpleRTS.model.Target;

public abstract class Unit {
	protected Target target;
	protected int currentHealth;
	protected int player;
	protected int xPosition;
	protected int yPosition;
	protected UnitTemplate template;
}
