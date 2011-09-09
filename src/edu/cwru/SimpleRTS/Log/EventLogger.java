package edu.cwru.SimpleRTS.Log;
import java.util.*;

public class EventLogger { 
	List<List<DamageLog>> damagelog;
	List<List<DeathLog>> deathlog;
	int currentroundnumber = -1;
	public EventLogger() {
		damagelog = new ArrayList<List<DamageLog>>();
		deathlog = new ArrayList<List<DeathLog>>();
	}
	public void nextRound() {
		currentroundnumber++;
		List<DamageLog> nextrounddamage = new ArrayList<DamageLog>();
		damagelog.add(nextrounddamage);
		List<DeathLog> nextrounddeath = new ArrayList<DeathLog>();
		deathlog.add(nextrounddeath);
	}
	public void recordDamage(int attackerid, int defenderid, int damage) {
		if (damagelog.size() == 0)
		{
			nextRound();
		}
		damagelog.get(damagelog.size()-1).add(new DamageLog(attackerid,defenderid, damage));
	}
	public void recordDeath(int deadunitid) {
		if (deathlog.size() == 0)
		{
			nextRound();
		}
		deathlog.get(deathlog.size()-1).add(new DeathLog(deadunitid));
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
}
