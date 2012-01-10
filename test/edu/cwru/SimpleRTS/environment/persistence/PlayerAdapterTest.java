package edu.cwru.SimpleRTS.environment.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.Test;

import edu.cwru.SimpleRTS.environment.PlayerState;
import edu.cwru.SimpleRTS.environment.state.persistence.PlayerAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;

public class PlayerAdapterTest {
	
	
	@Test
	public void textFromXml() throws FileNotFoundException, JSONException {
		XmlPlayer xml = AdapterTestUtil.createExamplePlayer();
		PlayerAdapter adapter = new PlayerAdapter();
		
		PlayerState player = adapter.fromXml(xml, AdapterTestUtil.loadTemplates());		
		
		assertEquals("playernum did not match!", xml.getID(), player.playerNum);
		for(XmlResourceQuantity amount : xml.getResourceAmount())
		{
			assertEquals(amount.getType() + " amount did not match!", 
						 amount.getQuantity(), 
						 player.getCurrentResourceAmount(amount.getType()));
		}
		assertEquals("supply did not match!", xml.getSupply(), player.getCurrentSupply());
		assertEquals("supply cap did not match!", xml.getSupplyCap(), player.getCurrentSupplyCap());
		for(Integer i : xml.getTemplate())
		{
			assertNotNull("template " + i + " was not instantiated!", player.getTemplate(i));
		}
		for(Integer i : xml.getUpgrade())
		{
			assertNotNull("upgrade " + i + " was not instantiated!", player.getUpgrades().contains(i));
		}
	}

}
