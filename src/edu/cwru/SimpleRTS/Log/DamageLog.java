package edu.cwru.SimpleRTS.log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.util.DeepEquatable;

/**
 * A read only class logging damage.
 * @author The Condor
 *
 */
public class DamageLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
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
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		DamageLog o = (DamageLog)other;
		if (amount != o.amount)
			return false;
		if (damager != o.damager)
			return false;
		if (damagercontroller != o.damagercontroller)
			return false;
		if (damagee != o.damagee)
			return false;
		if (damageecontroller != o.damageecontroller)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*amount;
		sum += (product = product*prime)*damager;
		sum += (product = product*prime)*damagercontroller;
		sum += (product = product*prime)*damagee;
		sum += (product = product*prime)*damageecontroller;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}

