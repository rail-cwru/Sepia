package edu.cwru.SimpleRTS.log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.util.DeepEquatable;

/**
 * A read only class that represents the revealing of units at the start of the game
 * @author The Condor
 *
 */
public class RevealedResourceNodeLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int nodex;
	private int nodey;
	private ResourceNode.Type nodetype;
	public RevealedResourceNodeLog(int resourcenodex, int resourcenodey, ResourceNode.Type resoucenodetype) {
		this.nodex=resourcenodex;
		this.nodey=resourcenodey;
		this.nodetype = resoucenodetype;
	}
	public int getResourceNodeXPosition() {
		return nodex;
	}
	public int getResourceNodeYPosition() {
		return nodey;
	}
	public ResourceNode.Type getResourceNodeType() {
		return nodetype;
	}
	@Override
	public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		RevealedResourceNodeLog o = (RevealedResourceNodeLog)other;
		return nodex==o.nodex && nodey==o.nodey && nodetype==o.nodetype;
	}
}
