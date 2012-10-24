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
import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import org.json.JSONException;
import org.junit.Test;

import edu.cwru.sepia.environment.model.persistence.PlayerAdapter;
import edu.cwru.sepia.environment.model.state.PlayerState;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.StateCreator;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.model.state.UpgradeTemplate;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
public class RawStateCreatorTest {
	
	/**
	 * Uses the xml methods for compares because they are already there. <br/>
	 * TODO: make a state generating method in test that doesn't use xml
	 * @throws IOException 
	 */
	@Test
	public void testState() throws IOException
	{
		Random r = new Random();
		State.StateBuilder s = new State.StateBuilder();
		int nplayers=r.nextInt(6);
		int nresource=r.nextInt(12)+2;
		for (int i = 0; i<nplayers; i++)
		{
			AdapterTestUtil.createExamplePlayer(r);
		}
		for (int i = 0; i<nresource; i++) {
//			AdapterTestUtil.createExampleResource(, );
		}
		State state = s.build();
		StateCreator stateCreator = state.getStateCreator();
		
		//Sanity Check
		assertTrue("state doesn't deep equal itself",state.deepEquals(state));
		
		State stateCopy = stateCreator.createState();
		State stateCopy2 = stateCreator.createState();
		
		assertTrue("Bad copy made",state.deepEquals(stateCopy));
		assertTrue("Bad copy made",state.deepEquals(stateCopy2));
		
		
		//Make sure that the new are not linked to the old one 
		state.addResource(new ResourceNode(ResourceNode.Type.GOLD_MINE, 4, 4, 343, state.nextTargetID()));
		assertFalse("Copy linked to original",state.deepEquals(stateCopy));
		assertFalse("Copy linked to original",state.deepEquals(stateCopy2));
		
		//Do the same thing to a copy 
		stateCopy.addResource(new ResourceNode(ResourceNode.Type.GOLD_MINE, 4, 4, 343, stateCopy.nextTargetID()));
		
		assertTrue("Deep Equals not doing equivalent",stateCopy.deepEquals(state));
		assertFalse("Copies linked to each other",stateCopy.deepEquals(stateCopy2));
	}
}
