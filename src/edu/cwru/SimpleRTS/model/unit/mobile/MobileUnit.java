package edu.cwru.SimpleRTS.model.unit.mobile;

import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class MobileUnit extends Unit {
	protected Resource.Type cargoType;
	protected int cargoAmount;
	public MobileUnit(MobileUnitTemplate template) {
		super(template);
	}
	public void move(Direction direction) {
		xPosition += direction.xComponent();
		yPosition += direction.yComponent();
	}
	public void pickUpResource(Resource.Type type, int amount) {
		if(!((MobileUnitTemplate)template).canGather())
			return;
		cargoType = type;
		cargoAmount = amount;
	}
}
