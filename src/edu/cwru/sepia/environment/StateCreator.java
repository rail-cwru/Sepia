package edu.cwru.sepia.environment;

import java.io.Serializable;

public interface StateCreator extends Serializable {
	State createState();
}
