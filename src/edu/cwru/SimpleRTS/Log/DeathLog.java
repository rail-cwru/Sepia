package edu.cwru.SimpleRTS.Log;

/**
 * A read only class that represents the death of something
 * @author The Condor
 *
 */
public class DeathLog {
	private int unitid;
	public DeathLog(int deadunitid) {
		unitid=deadunitid;
	}
	public int getDeadUnitID() {
		return unitid;
	}
}
