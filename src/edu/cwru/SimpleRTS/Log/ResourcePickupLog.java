package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;

/**
 * A read only class documenting an event 
 * @author The Condor
 *
 */
public class ResourcePickupLog implements Serializable {
	private int pickuper;
	private ResourceType resource;
	private int amount;
	private ResourceNode.Type nodetype;
	private int nodeid;
	public ResourcePickupLog(int gathererid, int controller, ResourceType resource, int amountpickedup, int nodeid, ResourceNode.Type nodetype) {
		pickuper = gathererid;
		this.resource = resource;
		amount = amountpickedup;
		this.nodetype = nodetype;
		this.nodeid = nodeid;
	}
	public ResourceType getResource() {
		return resource;
	}
	public int getGathererID() {
		return pickuper;
	}
	public int getAmountPickedUpID() {
		return amount;
	}
	public ResourceNode.Type getNodeType() {
		return nodetype;
	}
	public int getNodeId() {
		return nodeid;
	}
}