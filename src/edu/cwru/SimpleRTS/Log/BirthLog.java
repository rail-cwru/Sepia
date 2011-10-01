package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

public class BirthLog implements Serializable {
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
	public int getPlayer() {
		return player;
	}
	public int getParentID() {
		return parent;
	}
}
