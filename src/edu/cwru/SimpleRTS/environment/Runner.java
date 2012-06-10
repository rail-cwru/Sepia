package edu.cwru.SimpleRTS.environment;

import java.util.Map;

/**
 * An abstract base for classes that manage running one or more episodes
 * @author tim
 *
 */
public abstract class Runner {

	protected Environment environment;
	protected Map<String,String> parameters;
	
	public Runner(Environment environment, Map<String,String> parameters) {
		this.environment = environment;
		this.parameters = parameters;
	}
	
	public abstract void run();
}
