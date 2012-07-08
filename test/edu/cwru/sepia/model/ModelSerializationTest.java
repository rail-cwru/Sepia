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
package edu.cwru.sepia.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import edu.cwru.sepia.environment.model.state.RawStateCreator;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.StateCreator;
import edu.cwru.sepia.environment.model.state.State.StateBuilder;

public class ModelSerializationTest {

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		StateBuilder builder = new StateBuilder();
		State state = builder.build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(state);
		oos.close();
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		State state2 = (State) ois.readObject();
		assertTrue("State did not come out the same in direct serialization!",state.deepEquals(state2));
		
		StateCreator sc = new RawStateCreator(baos.toByteArray());
		State newstate = sc.createState();
		State newstate2 = sc.createState();
		State newstate3 = sc.createState();
		State newstate4 = sc.createState();
		assertTrue("State did not come out the same in serialization with rawstatecreator!",state.deepEquals(newstate));
		assertTrue("State did not come out the same in serialization with rawstatecreator!",state.deepEquals(newstate2));
		assertTrue("State did not come out the same in serialization with rawstatecreator!",state.deepEquals(newstate3));
		assertTrue("State did not come out the same in serialization with rawstatecreator!",state.deepEquals(newstate4));
		
	}
}
