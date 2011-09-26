package edu.cwru.SimpleRTS.Log;

public class UpgradeLog {
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
