package edu.cwru.SimpleRTS.model.prerequisite;

import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.State.StateView;

public interface Prerequisite extends Serializable {
	boolean isFulfilled(StateView state);
}
