package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;

/**
 * A read only class that represents the exhaustion of a resource node
 * @author The Condor
 *
 */
public class ResourceExhaustionLog implements Serializable {
	private int nodeid;
	private ResourceNode.Type nodetype;
	public ResourceExhaustionLog(int exhaustednodeid, ResourceNode.Type resoucenodetype) {
		nodeid=exhaustednodeid;
		this.nodetype = resoucenodetype;
	}
	public int getExhaustedNodeID() {
		return nodeid;
	}
	public ResourceNode.Type getResourceNodeType() {
		return nodetype;
	}
}
