package edu.cwru.SimpleRTS.log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.util.DeepEquatable;
import edu.cwru.SimpleRTS.util.DeepEquatableUtil;

public class EventLogger implements Serializable, DeepEquatable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<List<DamageLog>> damagelog;
	private List<List<DeathLog>> deathlog;
	private List<List<BirthLog>> birthlog;
	private List<List<UpgradeLog>> upgradelog;
	private List<List<ResourceNodeExhaustionLog>> exhaustlog;
	private List<List<ResourcePickupLog>> gatherlog;
	private List<List<ResourceDropoffLog>> depositlog;
	private List<RevealedResourceNodeLog> reveallog;
	private EventLoggerView view = null;
	public EventLogger() {
		damagelog = new ArrayList<List<DamageLog>>();
		deathlog = new ArrayList<List<DeathLog>>();
		birthlog = new ArrayList<List<BirthLog>>();
		upgradelog = new ArrayList<List<UpgradeLog>>();
		gatherlog = new ArrayList<List<ResourcePickupLog>>();
		depositlog = new ArrayList<List<ResourceDropoffLog>>();
		exhaustlog = new ArrayList<List<ResourceNodeExhaustionLog>>();
		reveallog = new ArrayList<RevealedResourceNodeLog>();
	}
	public EventLoggerView getView() {
		if (view == null)
			view = new EventLoggerView(this);
		return view;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundDamage() {
		return damagelog.size() - 1;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundDeath() {
		return deathlog.size() - 1;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundBirth() {
		return birthlog.size() - 1;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundUpgrade() {
		return upgradelog.size() - 1;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundResourcePickup() {
		return gatherlog.size() - 1;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundResourceDropoff() {
		return depositlog.size() - 1;
	}
	/**
	 * Get the number of the highest round for which this logger has recorded data.
	 * @return The highest recorded round
	 */
	public int getHighestRoundResourceNodeExhaustion() {
		return exhaustlog.size() - 1;
	}
	public void recordDamage(int turnnumber, int attackerid, int attackercontroller, int defenderid, int defendercontroller, int damage) {
		while (turnnumber+1>damagelog.size())
		{
			damagelog.add(new ArrayList<DamageLog>());
		}
		damagelog.get(turnnumber).add(new DamageLog(attackerid,attackercontroller, defenderid, defendercontroller, damage));
	}
	public void recordDeath(int turnnumber, int deadunitid, int controller) {
		while (turnnumber+1>deathlog.size())
		{
			deathlog.add(new ArrayList<DeathLog>());
		}
		deathlog.get(turnnumber).add(new DeathLog(deadunitid,controller));
	}
	public void recordBirth(int turnnumber, int newunitid, int parentunitid, int controller) {
		while (turnnumber+1>birthlog.size())
		{
			birthlog.add(new ArrayList<BirthLog>());
		}
		birthlog.get(turnnumber).add(new BirthLog(newunitid,parentunitid,controller));
	}
	public void recordUpgrade(int turnnumber, int upgradetemplateid, int producingunitid, int controller) {
		while (turnnumber+1>upgradelog.size())
		{
			upgradelog.add(new ArrayList<UpgradeLog>());
		}
		upgradelog.get(turnnumber).add(new UpgradeLog(upgradetemplateid,producingunitid, controller));
	}
	public void recordResourceNodeExhaustion(int turnnumber, int exhaustednodeid, ResourceNode.Type type) {
		while (turnnumber+1>exhaustlog.size())
		{
			exhaustlog.add(new ArrayList<ResourceNodeExhaustionLog>());
		}
		exhaustlog.get(turnnumber).add(new ResourceNodeExhaustionLog(exhaustednodeid,type));
	}
	public void recordResourcePickup(int turnnumber, int gathererid, int controller, ResourceType type, int amount, int nodeid, ResourceNode.Type nodetype) {
		while (turnnumber+1>gatherlog.size())
		{
			gatherlog.add(new ArrayList<ResourcePickupLog>());
		}
		gatherlog.get(turnnumber).add(new ResourcePickupLog(gathererid,controller, type, amount, nodeid, nodetype));
	}
	public void recordResourceDropoff(int turnnumber, int depositerid, int depositplaceid, int controller, ResourceType type, int amount) {
		while (turnnumber+1>depositlog.size())
		{
			depositlog.add(new ArrayList<ResourceDropoffLog>());
		}
		depositlog.get(turnnumber).add(new ResourceDropoffLog(depositerid, controller, amount, type, depositplaceid));
	}
	public void recordRevealedResourceNode(int resourcenodex, int resourcenodey, ResourceNode.Type resourcenodetype) {
		reveallog.add(new RevealedResourceNodeLog(resourcenodex, resourcenodey, resourcenodetype));
	}
	public void eraseResourceNodeReveals() {
		reveallog = new ArrayList<RevealedResourceNodeLog>();
	}
	
	public List<DeathLog> getDeaths(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= deathlog.size())
			return new ArrayList<DeathLog>();
		else 
			return Collections.unmodifiableList(deathlog.get(roundnumber));
	}
	public List<DamageLog> getDamage(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= damagelog.size())
			return new ArrayList<DamageLog>();
		else 
			return Collections.unmodifiableList(damagelog.get(roundnumber));
	}
	public List<BirthLog> getBirths(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= birthlog.size())
			return new ArrayList<BirthLog>();
		else 
			return Collections.unmodifiableList(birthlog.get(roundnumber));
	}
	public List<UpgradeLog> getUpgrades(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= upgradelog.size())
			return new ArrayList<UpgradeLog>();
		else 
			return Collections.unmodifiableList(upgradelog.get(roundnumber));
	}
	public List<ResourceNodeExhaustionLog> getResourceNodeExhaustions(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= exhaustlog.size())
			return new ArrayList<ResourceNodeExhaustionLog>();
		else 
			return Collections.unmodifiableList(exhaustlog.get(roundnumber));
	}
	public List<ResourceDropoffLog> getResourceDropoffs(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= depositlog.size())
			return new ArrayList<ResourceDropoffLog>();
		else 
			return Collections.unmodifiableList(depositlog.get(roundnumber));
	}
	public List<ResourcePickupLog> getResourcePickups(int roundnumber) {
		if (roundnumber < 0 || roundnumber >= gatherlog.size())
			return new ArrayList<ResourcePickupLog>();
		else 
			return Collections.unmodifiableList(gatherlog.get(roundnumber));
	}
	public List<RevealedResourceNodeLog> getRevealedResourceNodes() {
		return Collections.unmodifiableList(reveallog);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((birthlog == null) ? 0 : birthlog.hashCode());
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
		result = prime * result
				+ ((reveallog == null) ? 0 : reveallog.hashCode());
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
		if (reveallog == null) {
			if (other.reveallog != null)
				return false;
		} else if (!reveallog.equals(other.reveallog))
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
		public List<ResourceNodeExhaustionLog> getResourceExhaustions(int roundnumber) {
			return master.getResourceNodeExhaustions(roundnumber);
		}
		public List<ResourceDropoffLog> getResourceDropoffs(int roundnumber) {
			return master.getResourceDropoffs(roundnumber);
		}
		public List<ResourcePickupLog> getResourcePickups(int roundnumber) {
			return master.getResourcePickups(roundnumber);
		}
		public List<RevealedResourceNodeLog> getRevealedResources() {
			return master.getRevealedResourceNodes();
		}
	}

	@Override
	public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		EventLogger o = (EventLogger)other;
		
		if (!DeepEquatableUtil.deepEqualsListList(damagelog, o.damagelog))
			return false;
		if (!DeepEquatableUtil.deepEqualsListList(deathlog, o.deathlog))
			return false;
		if (!DeepEquatableUtil.deepEqualsListList(birthlog, o.birthlog))
			return false;
		if (!DeepEquatableUtil.deepEqualsListList(upgradelog, o.upgradelog))
			return false;
		if (!DeepEquatableUtil.deepEqualsListList(exhaustlog, o.exhaustlog))
			return false;
		if (!DeepEquatableUtil.deepEqualsListList(gatherlog, o.gatherlog))
			return false;
		if (!DeepEquatableUtil.deepEqualsListList(depositlog, o.depositlog))
			return false;
		if (!DeepEquatableUtil.deepEqualsList(reveallog, o.reveallog))
			return false;
		return true;
	}
}
