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
public class GameMap implements Serializable{
	
	// information in state
	//private List<Resource> resources;
	//private List<Unit> units;
	//private Map<Integer, List<Unit>> unitsByAgent;
	//private List<Template> templates;
	//private Map<Integer, List<Template>> templateByAgent;
	
	private State state;
	
	public GameMap() {
		// TODO: create a new map from scratch here
		//State.StateBuilder stateBuilder = new StateBuilder();
		
	}
	
	public GameMap(State state) {
		this.setState(state);
	}
	
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public static void storeMap(String filename, GameMap gameMap) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
			outputStream.writeObject(gameMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static GameMap loadMap(String filename) {
		GameMap gameMap = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
			gameMap = (GameMap) inputStream.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return gameMap;
	}
}








