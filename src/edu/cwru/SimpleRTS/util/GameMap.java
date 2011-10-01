package edu.cwru.SimpleRTS.util;

import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.Environment;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;


/**
 * store maps and status into files; 
 * load maps and status from files.
 * @author Feng
 *
 */
public final class GameMap implements Serializable{
		
	private GameMap() {}
	
	public static void storeState(String filename, State state) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
			outputStream.writeObject(state);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static State loadState(String filename) {
		State state = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
			state = (State) inputStream.readObject();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return state;
	}
}








