package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.resource.ResourceNode;

/**
 * A read only class that represents the revealing of units at the start of the game
 * @author The Condor
 *
 */
public class RevealedResourceLog implements Serializable {
	private int nodex;
	private int nodey;
	private ResourceNode.Type nodetype;
	public RevealedResourceLog(int resourcenodex, int resourcenodey, ResourceNode.Type resoucenodetype) {
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
}
