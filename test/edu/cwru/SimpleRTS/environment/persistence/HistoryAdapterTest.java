package edu.cwru.SimpleRTS.environment.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.json.JSONException;
import org.junit.Test;

import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.PlayerState;
import edu.cwru.SimpleRTS.environment.state.persistence.HistoryAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.PlayerAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlHistory;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public class HistoryAdapterTest {
	
	
	@Test
	public void test() throws JSONException, JAXBException, IOException {
		History h = AdapterTestUtil.createExampleHistory(new Random());
		History copy = HistoryAdapter.fromXml(HistoryAdapter.toXml(h));
//		JAXBContext.newInstance(XmlHistory.class).createMarshaller().marshal(HistoryAdapter.toXml(h), new FileWriter(new File("temp.temp")));
		assertTrue("Problem in either copying to or copying from xml",h.deepEquals(copy));
		assertTrue("Problem in either copying to or copying from xml, and also deepequals isn't symmetric",copy.deepEquals(h));
	}
}
