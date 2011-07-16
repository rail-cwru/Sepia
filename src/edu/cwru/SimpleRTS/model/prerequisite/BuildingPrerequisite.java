package edu.cwru.SimpleRTS.model.prerequisite;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

/**
 * A prerequisite requiring that the player control a unit with that template id
 * @author The Condor
 *
 */
public class BuildingPrerequisite implements Prerequisite{
	int buildingtemplateid;
	int player;
	public BuildingPrerequisite(int player, int buildingtemplateid) {
		this.buildingtemplateid = buildingtemplateid;
		this.player = player;
	}
	@Override
	public boolean isFulfilled(State state) {
		return state.doesPlayerHaveUnit(player, buildingtemplateid);
	}
	
}
