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
package edu.cwru.sepia.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.state.History;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.StateCreator;
import edu.cwru.sepia.util.GameMap;

/**
 * An abstract base for classes that manage running one or more episodes
 * @author tim
 *
 */
public abstract class Runner {

	protected Configuration configuration;
	protected StateCreator stateCreator;
	protected Agent[] agents;
	
	public Runner(Configuration configuration, StateCreator stateCreator, Agent[] agents) {
		this.configuration = configuration;
		this.stateCreator = stateCreator;
		this.agents = agents;
	}
	
	public abstract void run();
	
	/**
	 * Save an agent.
	 * <br> This stores a file (agent\<agentIndex\>.agt) in the directory specified, overwriting the file and/or creating the directory as needed.
	 * <br> The contents of the file is completely determined by the agent. 
	 * 
	 * @param file The directory to store the agent in.
	 * @param agentIndex The agent to store.
	 * @throws IllegalArgumentException when the file exists but is not a directory.
	 */
	protected void saveAgentData(File directory, int agentIndex) throws FileNotFoundException {
		if (directory.exists() && !directory.isDirectory()) {
			throw new IllegalArgumentException("File must be either a directory or not yet created");
		}
		if (!directory.exists()) {
			directory.mkdirs();
		}
		saveAgentData(new File(directory, "agent"+agentIndex+".agt"), agents[agentIndex]);
	}
	/**
	 * Save the agent's data (using {@link edu.cwru.sepia.agent.Agent#savePlayerData(java.io.OutputStream)})
	 * @param file
	 * @param agent
	 * @throws FileNotFoundException
	 */
	protected void saveAgentData(File file, Agent agent) throws FileNotFoundException {
		agent.savePlayerData(new FileOutputStream(file));
	}
	/**
	 * Force the agent with a specific index to store in the specified directory 
	 * @param directory The directory to store in.
	 * @param agentIndex
	 * @throws FileNotFoundException
	 */
	protected void loadAgentData(File directory, int agentIndex) throws FileNotFoundException {
		loadAgentData(new File(directory, "agent"+agentIndex+".agt"), agents[agentIndex]);
	}
	protected void loadAgentData(File file, Agent agent) throws FileNotFoundException {
		agent.loadPlayerData(new FileInputStream(file));
	}
	/**
	 * Save the initial state and history, as is needed to construct a replay.
	 * <br> This stores two files (history.xml and initstate.xml) in the directory specified, overwriting those files and/or creating the directory as needed.
	 * @param file The directory to store the replay in.
	 * @throws IllegalArgumentException when the file exists but is not a directory.
	 */
	protected void saveReplay(File file, State initState, History finalHistory) {
		if (file.exists() && !file.isDirectory()) {
			throw new IllegalArgumentException("File must be either a directory or not yet created");
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		saveHistory(new File(file, "history.xml"),finalHistory);
		saveState(new File(file, "initstate.xml"), initState);
	}
	protected void saveHistory(File file, History history) {
		GameMap.storeHistory(file.getAbsolutePath(), history);
	}
	/**
	 * Save the state as a specific file.
	 * @param file The file to save the state as.
	 * @param state The state to save.
	 */
	protected void saveState(File file, State state) {
		GameMap.storeState(file.getAbsolutePath(), state);
	}
}
