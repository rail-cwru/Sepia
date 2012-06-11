package edu.cwru.SimpleRTS.environment;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.util.Configuration;

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
}
