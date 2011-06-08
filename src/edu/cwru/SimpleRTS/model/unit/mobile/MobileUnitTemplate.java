package edu.cwru.SimpleRTS.model.unit.mobile;

import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public abstract class MobileUnitTemplate extends UnitTemplate<MobileUnit> {
	protected boolean canGather;
	@Override
	public abstract MobileUnit produceInstance();

}
