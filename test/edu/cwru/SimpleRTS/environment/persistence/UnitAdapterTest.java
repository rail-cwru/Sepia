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

import edu.cwru.SimpleRTS.environment.state.persistence.ActionAdapter;
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
		Unit unit = new Unit((UnitTemplate)templates.get(0),34);
		unit.setTask(UnitTask.Idle);
		unit.setxPosition(1);
		unit.setyPosition(2);
		
		XmlUnit xml = adapter.toXml(unit);
		
		checkEquality(xml, unit);
	}
	@Test
	public void testFromXml() throws JAXBException {
		//Functionality should be covered in the player one
//		UnitAdapter adapter = new UnitAdapter(templates);
//		XmlUnit xml = AdapterTestUtil.createExampleUnit();
//		/*
//		JAXBContext context = JAXBContext.newInstance(XmlUnit.class);
//		Marshaller marshaller = context.createMarshaller();
//		marshaller.setProperty("jaxb.formatted.output", true);
//		marshaller.marshal(xml, System.out);
//		*/
//		Unit unit = adapter.fromXml(xml);
//
//		checkEquality(xml,unit);
		
		
	}
	public static void checkEquality(XmlUnit xml, Unit unit)
	{
		assertEquals("template did not match!", unit.getTemplate().ID, xml.getTemplateID());
		assertEquals("x position did not match!", unit.getxPosition(), xml.getXPosition());
		assertEquals("y position did not match!", unit.getyPosition(), xml.getYPosition());
		assertEquals("cargo type did not match!", unit.getCurrentCargoType(), xml.getCargoType());
		assertEquals("cargo amount did not match!", unit.getCurrentCargoAmount(), xml.getCargoAmount());
		assertEquals("current health did not match!", unit.getCurrentHealth(), xml.getCurrentHealth());
		assertEquals("current durative action did not match!", unit.getActionProgressPrimitive(), ActionAdapter.fromXml(xml.getProgressPrimitive()));
//		assertEquals("current durative action did not match!", ActionAdapter.toXml(unit.getActionProgressPrimitive()), xml.getProgressPrimitive());
		assertEquals("current durative progress amount did not match!", unit.getActionProgressAmount(), xml.getProgressAmount());
		assertEquals("task did not match!", unit.getTask(), xml.getUnitTask());
		assertEquals("id did not match!", unit.ID, xml.getID());
	}
}
