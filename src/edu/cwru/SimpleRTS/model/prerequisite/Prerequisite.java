package edu.cwru.SimpleRTS.model.prerequisite;

import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.util.DeepEquatable;

public interface Prerequisite extends Serializable, DeepEquatable {
	boolean isFulfilled(StateView state);
}
