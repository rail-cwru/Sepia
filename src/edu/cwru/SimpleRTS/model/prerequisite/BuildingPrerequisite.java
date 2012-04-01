package edu.cwru.SimpleRTS.model.prerequisite;

import edu.cwru.SimpleRTS.environment.State.StateView;

/**
 * A prerequisite requiring that the player control a unit with that template id
 * @author The Condor
 *
 */
public class BuildingPrerequisite implements Prerequisite{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	int buildingtemplateid;
	int player;
	public BuildingPrerequisite(int player, int buildingtemplateid) {
		this.buildingtemplateid = buildingtemplateid;
		this.player = player;
	}
	@Override
	public boolean isFulfilled(StateView state) {
		return state.doesPlayerHaveUnit(player, buildingtemplateid);
	}
	public String toString() {
		return "BuildingPrereq, requires player " + player + " to have built template " + buildingtemplateid;
	}
	
}
