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

import java.io.FileNotFoundException;
import java.util.Random;

import org.json.JSONException;
import org.junit.Test;

import edu.cwru.sepia.environment.PlayerState;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.state.persistence.PlayerAdapter;
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
public class RawStateCreatorTest {
	
	/**
	 * Uses the xml methods for compares because they are already there. <br/>
	 * TODO: make a state generating method in test that doesn't use xml
	 */
	@Test
	public void testStateUSINGXMLMETHODSTOO()
	{
		Random r = new Random();
		State.StateBuilder s = new State.StateBuilder();
		int nplayers=r.nextInt(6);
		for (int i = 0; i<nplayers; i++)
		{
			AdapterTestUtil.createExamplePlayer(r);
			
		}
	}
}
