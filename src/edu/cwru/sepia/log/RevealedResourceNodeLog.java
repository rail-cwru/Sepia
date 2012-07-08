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
