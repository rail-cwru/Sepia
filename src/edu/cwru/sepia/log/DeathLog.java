package edu.cwru.sepia.log;

import java.io.Serializable;

import edu.cwru.sepia.util.DeepEquatable;

/**
 * A read only class that represents the death of something
 * @author The Condor
 *
 */
public class DeathLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int unitid;
	private int player;
	public DeathLog(int deadunitid, int player) {
		unitid=deadunitid;
		this.player = player;
	}
	public int getDeadUnitID() {
		return unitid;
	}
	public int getController() {
		return player;
	}
	public String toString() {
		return unitid + " (owned by " + player+") has been tragically slain";
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		DeathLog o = (DeathLog)other;
		if (unitid != o.unitid)
			return false;
		if (player != o.player)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*unitid;
		sum += (product = product*prime)*player;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}
