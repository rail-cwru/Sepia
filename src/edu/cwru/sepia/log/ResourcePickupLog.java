package edu.cwru.sepia.log;

import java.io.Serializable;

import edu.cwru.sepia.model.resource.ResourceNode;
import edu.cwru.sepia.model.resource.ResourceType;
import edu.cwru.sepia.util.DeepEquatable;

/**
 * A read only class documenting an event 
 * @author The Condor
 *
 */
public class ResourcePickupLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int pickuper;
	private ResourceType resource;
	private int amount;
	private ResourceNode.Type nodetype;
	private int nodeid;
	private int controller;
	public ResourcePickupLog(int gathererid, int controller, ResourceType resource, int amountpickedup, int nodeid, ResourceNode.Type nodetype) {
		pickuper = gathererid;
		this.resource = resource;
		amount = amountpickedup;
		this.nodetype = nodetype;
		this.nodeid = nodeid;
		this.controller = controller;
	}
	public ResourceType getResourceType() {
		return resource;
	}
	public int getGathererID() {
		return pickuper;
	}
	public int getAmountPickedUp() {
		return amount;
	}
	public ResourceNode.Type getNodeType() {
		return nodetype;
	}
	public int getNodeID() {
		return nodeid;
	}
	public int getController() {
		return controller;
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		ResourcePickupLog o = (ResourcePickupLog)other;
		if (resource != o.resource)
			return false;
		if (pickuper != o.pickuper)
			return false;
		if (amount != o.amount)
			return false;
		if (nodetype != o.nodetype)
			return false;
		if (nodeid != o.nodeid)
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
		sum += (product = product*prime)*pickuper;
		sum += (product = product*prime)*amount;
		sum += (product = product*prime)*nodetype.ordinal();
		sum += (product = product*prime)*nodeid;
		sum += (product = product*prime)*controller;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}