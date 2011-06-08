package edu.cwru.SimpleRTS.model.unit.building;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public class BuildingTemplate extends UnitTemplate {
	private List<String> produces;
	@Override
	public Unit produceInstance() {
		Building building = new Building(this);
		produces = new ArrayList<String>();
		return building;
	}
	public void addProductionItem(String item) {
		this.produces.add(item);
	}
	public List<String> getProduces() {
		return produces;
	}

}
