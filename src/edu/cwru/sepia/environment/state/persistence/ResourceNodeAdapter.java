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
package edu.cwru.sepia.environment.state.persistence;

import edu.cwru.sepia.environment.model.persistence.generated.XmlResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceNode;

public class ResourceNodeAdapter {

	public ResourceNode fromXml(XmlResourceNode xml) {
		return new ResourceNode(xml.getType(), xml.getXPosition(), xml.getYPosition(), xml.getAmountRemaining(), xml.getID());
	}
	
	public XmlResourceNode toXml(ResourceNode node) {
		XmlResourceNode xml = new XmlResourceNode();
		xml.setType(node.getType());
		xml.setAmountRemaining(node.getAmountRemaining());
		xml.setXPosition(node.getxPosition());
		xml.setYPosition(node.getyPosition());
		xml.setID(node.ID);
		return xml;
	}
}
