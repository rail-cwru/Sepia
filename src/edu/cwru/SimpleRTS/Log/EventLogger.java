package edu.cwru.SimpleRTS.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;

public class EventLogger implements Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<List<DamageLog>> damagelog;
	private List<List<DeathLog>> deathlog;
	private List<List<BirthLog>> birthlog;
	private List<List<UpgradeLog>> upgradelog;
	private List<List<ResourceExhaustionLog>> exhaustlog;
	private List<List<ResourcePickupLog>> gatherlog;
	private List<List<ResourceDropoffLog>> depositlog;
	private List<RevealedResourceLog> reveallog;
	
	//starts at -1, must be incremented to be used
	private int currentroundnumber = -1;

	private EventLoggerView view = null;
	public EventLogger() {
		damagelog = new ArrayList<List<DamageLog>>();
		deathlog = new ArrayList<List<DeathLog>>();
		birthlog = new ArrayList<List<BirthLog>>();
		upgradelog = new ArrayList<List<UpgradeLog>>();
		gatherlog = new ArrayList<List<ResourcePickupLog>>();
		depositlog = new ArrayList<List<ResourceDropoffLog>>();
		exhaustlog = new ArrayList<List<ResourceExhaustionLog>>();
		reveallog = new ArrayList<RevealedResourceLog>();
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
		List<ResourceExhaustionLog> nextroundexhaust = new ArrayList<ResourceExhaustionLog>();
		exhaustlog.add(nextroundexhaust);
		List<ResourceDropoffLog> nextrounddropoff = new ArrayList<ResourceDropoffLog>();
		depositlog.add(nextrounddropoff);
		List<ResourcePickupLog> nextroundpickup= new ArrayList<ResourcePickupLog>();
		gatherlog.add(nextroundpickup);
	}
	public void recordDamage(int attackerid, int attackercontroller, int defenderid, int defendercontroller, int damage) {
		if (damagelog.size() == 0)
		{
			nextRound();
		}
		damagelog.get(damagelog.size()-1).add(new DamageLog(attackerid,attackercontroller, defenderid, defendercontroller, damage));
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
	public void recordExhaustedResourceNode(int exhaustednodeid, ResourceNode.Type type) {
		if (exhaustlog.size() == 0)
		{
			nextRound();
		}
		exhaustlog.get(exhaustlog.size()-1).add(new ResourceExhaustionLog(exhaustednodeid,type));
	}
	public void recordPickupResource(int gathererid, int controller, ResourceType type, int amount, int nodeid, ResourceNode.Type nodetype) {
		if (gatherlog.size() == 0)
		{
			nextRound();
		}
		gatherlog.get(gatherlog.size()-1).add(new ResourcePickupLog(gathererid,controller, type, amount, nodeid, nodetype));
	}
	public void recordDropoffResource(int depositerid, int depositplaceid, int controller, ResourceType type, int amount) {
		if (depositlog.size() == 0)
		{
			nextRound();
		}
		depositlog.get(depositlog.size()-1).add(new ResourceDropoffLog(depositerid, controller, amount, type, depositplaceid));
	}
	public void recordResourceNodeReveal(int resourcenodex, int resourcenodey, ResourceNode.Type resourcenodetype) {
		reveallog.add(new RevealedResourceLog(resourcenodex, resourcenodey, resourcenodetype));
	}
	public void eraseResourceNodeReveals() {
		reveallog = new ArrayList<RevealedResourceLog>();
	}
	
	public int getLastRound()
	{
		return currentroundnumber-1;
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
	public List<ResourceExhaustionLog> getResourceExhaustions(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<ResourceExhaustionLog>();
		else 
			return Collections.unmodifiableList(exhaustlog.get(roundnumber));
	}
	public List<ResourceDropoffLog> getResourceDropoffs(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<ResourceDropoffLog>();
		else 
			return Collections.unmodifiableList(depositlog.get(roundnumber));
	}
	public List<ResourcePickupLog> getResourcePickups(int roundnumber) {
		if (roundnumber < 0 || roundnumber > currentroundnumber)
			return new ArrayList<ResourcePickupLog>();
		else 
			return Collections.unmodifiableList(gatherlog.get(roundnumber));
	}
	public List<RevealedResourceLog> getRevealedResources() {
		return Collections.unmodifiableList(reveallog);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((birthlog == null) ? 0 : birthlog.hashCode());
		result = prime * result + currentroundnumber;
		result = prime * result
				+ ((damagelog == null) ? 0 : damagelog.hashCode());
		result = prime * result
				+ ((deathlog == null) ? 0 : deathlog.hashCode());
		result = prime * result
				+ ((depositlog == null) ? 0 : depositlog.hashCode());
		result = prime * result
				+ ((exhaustlog == null) ? 0 : exhaustlog.hashCode());
		result = prime * result
				+ ((gatherlog == null) ? 0 : gatherlog.hashCode());
		result = prime * result
				+ ((upgradelog == null) ? 0 : upgradelog.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventLogger other = (EventLogger) obj;
		if (birthlog == null) {
			if (other.birthlog != null)
				return false;
		} else if (!birthlog.equals(other.birthlog))
			return false;
		if (currentroundnumber != other.currentroundnumber)
			return false;
		if (damagelog == null) {
			if (other.damagelog != null)
				return false;
		} else if (!damagelog.equals(other.damagelog))
			return false;
		if (deathlog == null) {
			if (other.deathlog != null)
				return false;
		} else if (!deathlog.equals(other.deathlog))
			return false;
		if (depositlog == null) {
			if (other.depositlog != null)
				return false;
		} else if (!depositlog.equals(other.depositlog))
			return false;
		if (exhaustlog == null) {
			if (other.exhaustlog != null)
				return false;
		} else if (!exhaustlog.equals(other.exhaustlog))
			return false;
		if (gatherlog == null) {
			if (other.gatherlog != null)
				return false;
		} else if (!gatherlog.equals(other.gatherlog))
			return false;
		if (upgradelog == null) {
			if (other.upgradelog != null)
				return false;
		} else if (!upgradelog.equals(other.upgradelog))
			return false;
		return true;
	}
	
	/**
	 * A read-only version of the Event Log, which is used to keep track of all events that the player has seen.
	 * @author The Condor
	 *
	 */
	public static class EventLoggerView implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		EventLogger master;
		public EventLoggerView(EventLogger master) {
			this.master = master;
		}
		public int getLastRound() {
			return master.getLastRound();
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
		public List<ResourceExhaustionLog> getResourceExhaustions(int roundnumber) {
			return master.getResourceExhaustions(roundnumber);
		}
		public List<ResourceDropoffLog> getResourceDropoffs(int roundnumber) {
			return master.getResourceDropoffs(roundnumber);
		}
		public List<ResourcePickupLog> getResourcePickups(int roundnumber) {
			return master.getResourcePickups(roundnumber);
		}
		public List<RevealedResourceLog> getRevealedResources() {
			return master.getRevealedResources();
		}
	}
}
