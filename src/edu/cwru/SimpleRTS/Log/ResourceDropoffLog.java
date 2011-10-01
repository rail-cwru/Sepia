package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.resource.ResourceType;

/**
 * A read only class documenting an historic event wherein  
 * @author The Condor
 *
 */
public class ResourceDropoffLog implements Serializable {
	private int pickuperid;
	private ResourceType resource;
	private int amount;
	private int depotid;
	public ResourceDropoffLog(int gathererid, int controller, int amountpickedup, ResourceType resource, int depotid) {
		pickuperid = gathererid;
		this.resource = resource;
		amount = amountpickedup;
		this.depotid = depotid;
	}
	public ResourceType getResource() {
		return resource;
	}
	public int getGathererID() {
		return pickuperid;
	}
	public int getAmountPickedUpID() {
		return amount;
	}
	public int getDepotId() {
		return depotid;
	}
}