/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.environment.model.history;

import java.io.Serializable;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
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