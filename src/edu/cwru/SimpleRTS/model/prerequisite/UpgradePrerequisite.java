package edu.cwru.SimpleRTS.model.prerequisite;

import edu.cwru.SimpleRTS.environment.State.StateView;

public class UpgradePrerequisite implements Prerequisite{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	int upgradeid;
	int playerid;
	public UpgradePrerequisite(int player, int upgradeid) {
		this.upgradeid = upgradeid;
		this.playerid = player;
	}
	@Override
	public boolean isFulfilled(StateView state) {
		return state.hasUpgrade(upgradeid, playerid);
	}
	public String toString() {
		return "UpgradePrereq, requires player " + playerid + " to have upgraded template " + upgradeid;
	}
	@Override
	public boolean deepEquals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		UpgradePrerequisite o = (UpgradePrerequisite)other;
		return this.upgradeid == o.upgradeid && this.playerid == o.playerid;
		
	}

}
