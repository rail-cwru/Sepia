package edu.cwru.SimpleRTS.Log;

/**
 * A read only class  
 * @author The Condor
 *
 */
public class DamageLog {
	private int damager;
	private int damagee;
	private int amount;
	public DamageLog(int attackerid, int defenderid, int damageamount) {
		damager = attackerid;
		damagee = defenderid;
		amount = damageamount;
	}
	public int getDamage() {
		return amount;
	}
	public int getAttackerID() {
		return damager;
	}
	public int getDefenderID() {
		return damagee;
	}
}
