package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

public class UpgradeLog implements Serializable {
	private int upgradetemplateid;
	private int player;
	public UpgradeLog(int upgradetemplateid, int player) {
		this.upgradetemplateid = upgradetemplateid;
		this.player = player;
	}
	public int getUpgradeTemplateID() {
		return upgradetemplateid;
	}
	public int getPlayer() {
		return player;
	}
}
