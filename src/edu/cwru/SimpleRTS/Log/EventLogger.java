package edu.cwru.SimpleRTS.Log;
import java.util.*;

public class EventLogger { 
	private List<List<DamageLog>> damagelog;
	private List<List<DeathLog>> deathlog;
	private List<List<BirthLog>> birthlog;
	private List<List<UpgradeLog>> upgradelog;
	private int currentroundnumber = -1;
	private EventLoggerView view = null;
	public EventLogger() {
		damagelog = new ArrayList<List<DamageLog>>();
		deathlog = new ArrayList<List<DeathLog>>();
		birthlog = new ArrayList<List<BirthLog>>();
		upgradelog = new ArrayList<List<UpgradeLog>>();
	}
	public EventLoggerView getView() {
		if (view == null)
			view = new EventLoggerView(this);
		return view;
	}
	public void nextRound() {
		currentroundnumber++;
		List<DamageLog> nextrounddamage = new ArrayList<DamageLog>();
		damagelog.add(nextrounddamage);
		List<DeathLog> nextrounddeath = new ArrayList<DeathLog>();
		deathlog.add(nextrounddeath);
		List<BirthLog> nextroundbirth= new ArrayList<BirthLog>();
		birthlog.add(nextroundbirth);
		List<UpgradeLog> nextroundupgrade= new ArrayList<UpgradeLog>();
		upgradelog.add(nextroundupgrade);
	}
	public void recordDamage(int attackerid, int defenderid, int damage) {
		if (damagelog.size() == 0)
		{
			nextRound();
		}
		damagelog.get(damagelog.size()-1).add(new DamageLog(attackerid,defenderid, damage));
	}
	public void recordDeath(int deadunitid, int controller) {
		if (deathlog.size() == 0)
		{
			nextRound();
		}
		deathlog.get(deathlog.size()-1).add(new DeathLog(deadunitid,controller));
	}
	public void recordBirth(int newunitid, int parentunitid, int controller) {
		if (birthlog.size() == 0)
		{
			nextRound();
		}
		birthlog.get(birthlog.size()-1).add(new BirthLog(newunitid,parentunitid,controller));
	}
	public void recordUpgrade(int deadunitid, int controller) {
		if (upgradelog.size() == 0)
		{
			nextRound();
		}
		upgradelog.get(upgradelog.size()-1).add(new UpgradeLog(deadunitid,controller));
	}
	public int getCurrentRound() {
		return currentroundnumber;
	}
	public List<DeathLog> getDeaths(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<DeathLog>();
		else 
			return Collections.unmodifiableList(deathlog.get(roundnumber));
	}
	public List<DamageLog> getDamage(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<DamageLog>();
		else 
			return Collections.unmodifiableList(damagelog.get(roundnumber));
	}
	public List<BirthLog> getBirths(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<BirthLog>();
		else 
			return Collections.unmodifiableList(birthlog.get(roundnumber));
	}
	public List<UpgradeLog> getUpgrades(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<UpgradeLog>();
		else 
			return Collections.unmodifiableList(upgradelog.get(roundnumber));
	}
	
	public class EventLoggerView {
		EventLogger master;
		public EventLoggerView(EventLogger master) {
			this.master = master;
		}
		public int getCurrentRound() {
			return master.getCurrentRound();
		}
		public List<DeathLog> getDeaths(int roundnumber) {
			return master.getDeaths(roundnumber);
		}
		public List<DamageLog> getDamage(int roundnumber) {
			return master.getDamage(roundnumber);
		}
		public List<BirthLog> getBirths(int roundnumber) {
			return master.getBirths(roundnumber);
		}
		public List<UpgradeLog> getUpgrades(int roundnumber) {
			return master.getUpgrades(roundnumber);
		}
	}
}
