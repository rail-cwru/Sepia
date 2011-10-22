package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

/**
 * A read only class that represents the death of something
 * @author The Condor
 *
 */
public class DeathLog implements Serializable {
	private int unitid;
	private int player;
	public DeathLog(int deadunitid, int player) {
		unitid=deadunitid;
		this.player = player;
	}
	public int getDeadUnitID() {
		return unitid;
	}
	public int getPlayer() {
		return player;
	}
	public String toString() {
		return unitid + " (owned by " + player+") has been tragically slain";
	}
}
