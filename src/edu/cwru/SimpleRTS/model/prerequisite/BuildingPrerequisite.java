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
		return state.hasUnit(player, buildingtemplateid);
	}
	public String toString() {
		return "BuildingPrereq, requires player " + player + " to have built template " + buildingtemplateid;
	}
	
	@Override
	public boolean deepEquals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		BuildingPrerequisite o = (BuildingPrerequisite)other;
		return this.buildingtemplateid == o.buildingtemplateid && this.player == o.player;
		
	}
	
}
