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

import edu.cwru.sepia.environment.model.state.PlayerState;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.model.state.UpgradeTemplate;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.state.persistence.PlayerAdapter;
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
