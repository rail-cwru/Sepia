package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;

public interface StateCreator extends Serializable {
	State createState();
}
