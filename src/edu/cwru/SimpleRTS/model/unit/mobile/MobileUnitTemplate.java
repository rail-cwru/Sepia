package edu.cwru.SimpleRTS.model.unit.mobile;

import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public class MobileUnitTemplate extends UnitTemplate {
	protected boolean canGather;
	protected boolean canBuild;
	@Override
	public Unit produceInstance() {
		MobileUnit unit = new MobileUnit(this);
		return unit;
	}
	public boolean canGather() { return canGather; }
	public void setCanGather(boolean canGather) { this.canGather = canGather; } 
	public boolean canBuild() { return canBuild; }
	public void setCanBuild(boolean canBuild) { this.canBuild = canBuild; }
}
