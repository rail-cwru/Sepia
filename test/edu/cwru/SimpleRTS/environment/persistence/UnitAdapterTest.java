package edu.cwru.SimpleRTS.environment.persistence;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.environment.state.persistence.UnitAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTask;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class UnitAdapterTest {

	@SuppressWarnings("rawtypes")
	private static Map<Integer,Template> templates;
	
	@Test
	public void testToXml() {
		UnitAdapter adapter = new UnitAdapter(templates);
		Unit unit = new Unit((UnitTemplate)templates.get(0));
		unit.setTask(UnitTask.Idle);
		unit.setxPosition(1);
		unit.setyPosition(1);
		
		XmlUnit xml = adapter.toXml(unit);
		
		assertEquals("template did not match!", unit.getTemplate().ID, xml.getTemplateID());
		assertEquals("x position did not match!", unit.getxPosition(), xml.getXPosition());
		assertEquals("y position did not match!", unit.getyPosition(), xml.getYPosition());
		assertEquals("cargo type did not match!", unit.getCurrentCargoType(), xml.getCargoType());
		assertEquals("cargo aount did not match!", unit.getCurrentCargoAmount(), xml.getCargoAmount());
		assertEquals("current health did not match!", unit.getCurrentHealth(), xml.getCurrentHealth());
		assertEquals("current production ID did not match!", unit.getCurrentProductionID(), xml.getProductionTemplateID());
		//assertEquals("cargo type did not match!", unit.get, xml.getProductionAmount());
		assertEquals("task did not match!", unit.getTask(), xml.getUnitTask());
	}
	
	@Test
	public void testFromXml() throws JAXBException {
		UnitAdapter adapter = new UnitAdapter(templates);
		XmlUnit xml = AdapterTestUtil.createExampleUnit();
		/*
		JAXBContext context = JAXBContext.newInstance(XmlUnit.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.marshal(xml, System.out);
		*/
		Unit unit = adapter.fromXml(xml);

		assertEquals("template did not match!", xml.getTemplateID(), unit.getTemplate().ID);
		assertEquals("x position did not match!", xml.getXPosition(), unit.getxPosition());
		assertEquals("y position did not match!", xml.getYPosition(), unit.getyPosition());
		assertEquals("cargo type did not match!", xml.getCargoType(), unit.getCurrentCargoType());
		assertEquals("cargo aount did not match!", xml.getCargoAmount(), unit.getCurrentCargoAmount());
		assertEquals("current health did not match!", xml.getCurrentHealth(), unit.getCurrentHealth());
		//assertEquals("current production ID did not match!", xml.getProductionTemplateID(), unit.getCurrentProductionID());
		//assertEquals("cargo type did not match!", xml.getProductionAmount(), unit.get);
		assertEquals("task did not match!", xml.getUnitTask(), unit.getTask());
		
	}
}
