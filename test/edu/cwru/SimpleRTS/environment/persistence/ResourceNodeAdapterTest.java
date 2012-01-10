package edu.cwru.SimpleRTS.environment.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.cwru.SimpleRTS.environment.state.persistence.ResourceNodeAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;

public class ResourceNodeAdapterTest {

	@Test
	public void testToXml() {
		ResourceNodeAdapter adapter = new ResourceNodeAdapter();
		ResourceNode node = new ResourceNode(ResourceNode.Type.GOLD_MINE, 1, 1, 1000);
		
		XmlResourceNode xml = adapter.toXml(node);
		
		assertEquals("resource type did not match!", node.getType(), xml.getType());
		assertEquals("x position did not match!", node.getxPosition(), xml.getXPosition());
		assertEquals("y position did not match!", node.getyPosition(), xml.getYPosition());
		assertEquals("amount remaining did not match!", node.getAmountRemaining(), xml.getAmountRemaining());
	}
	
	@Test
	public void testFromXml() {
		ResourceNodeAdapter adapter = new ResourceNodeAdapter();
		XmlResourceNode xml = new XmlResourceNode();
		xml.setType(ResourceNode.Type.TREE);
		xml.setXPosition(1);
		xml.setYPosition(2);
		xml.setAmountRemaining(100);
		
		ResourceNode node = adapter.fromXml(xml);

		assertEquals("resource type did not match!", xml.getType(), node.getType());
		assertEquals("x position did not match!", xml.getXPosition(), node.getxPosition());
		assertEquals("y position did not match!", xml.getYPosition(), node.getyPosition());
		assertEquals("amount remaining did not match!", xml.getAmountRemaining(), node.getAmountRemaining());
		
	}
}