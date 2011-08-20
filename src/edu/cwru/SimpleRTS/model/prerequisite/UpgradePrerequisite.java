package edu.cwru.SimpleRTS.model.prerequisite;

import java.util.List;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public class UpgradePrerequisite implements Prerequisite{

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
