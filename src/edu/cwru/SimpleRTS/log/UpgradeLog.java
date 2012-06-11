package edu.cwru.SimpleRTS.log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.util.DeepEquatable;

/**
 * Logs the upgrade of something
 *
 */
public class UpgradeLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int upgradetemplateid;
	private int producingunitid;
	private int player;
	public UpgradeLog(int upgradetemplateid, int producingunitid, int player) {
		this.upgradetemplateid = upgradetemplateid;
		this.producingunitid = producingunitid;
		this.player = player;
	}
	public int getUpgradeTemplateID() {
		return upgradetemplateid;
	}
	public int getController() {
		return player;
	}
	public int getProducingUnitID() {
		return producingunitid;
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		UpgradeLog o = (UpgradeLog)other;
		if (upgradetemplateid != o.upgradetemplateid)
			return false;
		if (producingunitid != o.producingunitid)
			return false;
		if (player != o.player)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*upgradetemplateid;
		sum += (product = product*prime)*producingunitid;
		sum += (product = product*prime)*player;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}
