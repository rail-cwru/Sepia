package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;

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
		assertEquals("State did not come out the same!",state,state2);
	}
}
