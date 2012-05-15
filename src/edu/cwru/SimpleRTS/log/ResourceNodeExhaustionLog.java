package edu.cwru.SimpleRTS.log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.util.DeepEquatable;

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
