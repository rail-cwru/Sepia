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

}
