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

import edu.cwru.sepia.environment.History;
import edu.cwru.sepia.environment.PlayerState;
import edu.cwru.sepia.environment.state.persistence.HistoryAdapter;
import edu.cwru.sepia.environment.state.persistence.PlayerAdapter;
import edu.cwru.sepia.environment.state.persistence.generated.XmlHistory;
import edu.cwru.sepia.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.sepia.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.sepia.environment.state.persistence.generated.XmlTemplate;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUnit;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.resource.ResourceNode.Type;
import edu.cwru.sepia.model.unit.Unit;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.model.upgrade.UpgradeTemplate;

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
