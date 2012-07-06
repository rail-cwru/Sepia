package edu.cwru.sepia.log;

import java.io.Serializable;

import edu.cwru.sepia.model.resource.ResourceType;
import edu.cwru.sepia.util.DeepEquatable;

/**
 * A read only class documenting an historic event wherein resources are dropped off (deposited)
 * @author The Condor
 *
 */
public class ResourceDropoffLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int pickuperid;
	private ResourceType resource;
	private int amount;
	private int depotid;
	private int controller;
	public ResourceDropoffLog(int gathererid, int controller, int amountpickedup, ResourceType resource, int depotid) {
		pickuperid = gathererid;
		this.resource = resource;
		amount = amountpickedup;
		this.depotid = depotid;
		this.controller = controller;
	}
	public ResourceType getResourceType() {
		return resource;
	}
	public int getGathererID() {
		return pickuperid;
	}
	public int getAmountDroppedOff() {
		return amount;
	}
	public int getDepotID() {
		return depotid;
	}
	public int getController() {
		return controller;
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		ResourceDropoffLog o = (ResourceDropoffLog)other;
		if (resource != o.resource)
			return false;
		if (pickuperid != o.pickuperid)
			return false;
		if (amount != o.amount)
			return false;
		if (depotid != o.depotid)
			return false;
		if (controller != o.controller)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*resource.ordinal();
		sum += (product = product*prime)*pickuperid;
		sum += (product = product*prime)*amount;
		sum += (product = product*prime)*depotid;
		sum += (product = product*prime)*controller;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}