package edu.cwru.SimpleRTS.log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.util.DeepEquatable;

/**
 * A read only class that represents the birth of something
 * 
 */
public class BirthLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int unitid;
	private int player;
	private int parent;
	public BirthLog(int newunitid, int producerid, int player) {
		unitid=newunitid;
		this.player = player;
		this.parent = producerid;
	}
	public int getNewUnitID() {
		return unitid;
	}
	public int getController() {
		return player;
	}
	public int getParentID() {
		return parent;
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		BirthLog o = (BirthLog)other;
		if (unitid != o.unitid)
			return false;
		if (player != o.player)
			return false;
		if (parent != o.parent)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*unitid;
		sum += (product = product*prime)*player;
		sum += (product = product*prime)*parent;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}
