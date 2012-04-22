package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import edu.cwru.SimpleRTS.environment.RawStateCreator;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.environment.StateCreator;

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
		
	}
}
