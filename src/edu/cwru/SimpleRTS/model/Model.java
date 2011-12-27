package edu.cwru.SimpleRTS.model;
import java.io.Serializable;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.environment.State.StateView;
public interface Model extends Serializable{
	void createNewWorld();
	boolean isTerminated();
	/**
	 * Set the actions to be executed
	 * @param action
	 */
	void setActions(Action[] action);
	/**
	 * Execute actions and do anything else that needs to be done
	 */
	void executeStep();
	StateView getState(int player);
}
