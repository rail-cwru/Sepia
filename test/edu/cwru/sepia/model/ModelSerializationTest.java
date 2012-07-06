package edu.cwru.sepia.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import edu.cwru.sepia.environment.RawStateCreator;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.StateCreator;
import edu.cwru.sepia.environment.State.StateBuilder;

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
