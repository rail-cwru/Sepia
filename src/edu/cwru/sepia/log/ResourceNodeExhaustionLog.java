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
package edu.cwru.sepia.log;

import java.io.Serializable;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.util.DeepEquatable;

/**
 * A read only class that represents the exhaustion of a resource node
 * @author The Condor
 *
 */
public class ResourceNodeExhaustionLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int nodeid;
	private ResourceNode.Type nodetype;
	public ResourceNodeExhaustionLog(int exhaustednodeid, ResourceNode.Type resoucenodetype) {
		nodeid=exhaustednodeid;
		this.nodetype = resoucenodetype;
	}
	public int getExhaustedNodeID() {
		return nodeid;
	}
	public ResourceNode.Type getResourceNodeType() {
		return nodetype;
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		ResourceNodeExhaustionLog o = (ResourceNodeExhaustionLog)other;
		if (nodeid != o.nodeid)
			return false;
		if (nodetype != o.nodetype)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*nodeid;
		sum += (product = product*prime)*nodetype.ordinal();
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}
