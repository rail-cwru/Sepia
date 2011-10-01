package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

/**
 * A read only class logging damage.
 * @author The Condor
 *
 */
public class DamageLog implements Serializable {
	private int damager;
	private int damagercontroller;
	private int damagee;
	private int damageecontroller;
	private int amount;
	public DamageLog(int attackerid, int attackercontroller, int defenderid, int defendercontroller, int damageamount) {
		damager = attackerid;
		damagee = defenderid;
		amount = damageamount;
		this.damagercontroller = attackercontroller;
		this.damageecontroller = defendercontroller;
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
	public int getAttackerController() {
		return damagercontroller;
	}
	public int getDefenderController() {
		return damageecontroller;
	}
}

