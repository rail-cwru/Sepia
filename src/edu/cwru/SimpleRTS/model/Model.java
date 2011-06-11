package edu.cwru.SimpleRTS.model;
import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.*;
public interface Model {
	void createNewWorld();
	boolean isTerminated();
	void executeActions(Action[] action);
	State getState();
}
