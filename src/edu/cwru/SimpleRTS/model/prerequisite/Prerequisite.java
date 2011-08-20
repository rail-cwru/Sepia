package edu.cwru.SimpleRTS.model.prerequisite;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;

public interface Prerequisite {
	boolean isFulfilled(StateView state);
}
