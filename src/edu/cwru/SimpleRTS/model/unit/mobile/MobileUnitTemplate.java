package edu.cwru.SimpleRTS.model.unit.mobile;

import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public abstract class MobileUnitTemplate extends UnitTemplate {
	protected boolean canGather;
	@Override
	public abstract Unit produceInstance();

}
