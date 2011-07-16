package edu.cwru.SimpleRTS.model.prerequisite;

import edu.cwru.SimpleRTS.environment.State;

public interface Prerequisite {
	boolean isFulfilled(State state);
}
