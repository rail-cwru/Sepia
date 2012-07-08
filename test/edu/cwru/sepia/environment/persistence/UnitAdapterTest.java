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
package edu.cwru.sepia.environment.persistence;

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

import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.state.persistence.ActionAdapter;
import edu.cwru.sepia.environment.state.persistence.UnitAdapter;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUnit;
import edu.cwru.sepia.util.TypeLoader;

public class UnitAdapterTest {

	@SuppressWarnings("rawtypes")
	private static Map<Integer,Template> templates;
	
	@Test
	public void testToXml() throws FileNotFoundException, JSONException {
		int player = 0;
		templates = new HashMap<Integer, Template>();
		List<? extends Template> templateList = TypeLoader.loadFromFile("data/unit_templates", player, new State());
		for(Template t : templateList)
		{
			if (t instanceof UnitTemplate)
				templates.put(t.ID,t);
		}
		UnitAdapter adapter = new UnitAdapter(templates);
		Unit unit = new Unit((UnitTemplate)templates.get(player),34);
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
		assertEquals("current durative progress amount did not match!", unit.getActionProgressAmount(), xml.getProgressAmount());
		assertEquals("id did not match!", unit.ID, xml.getID());
	}
}
