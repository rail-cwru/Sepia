package edu.cwru.SimpleRTS.environment.state.persistence;

import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;

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
