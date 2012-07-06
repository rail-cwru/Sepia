package edu.cwru.sepia.model;
import java.io.Serializable;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.History;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.TurnTracker;
import edu.cwru.sepia.util.Configuration;

/**
 * The interface for model, which transitions its state as executing given actions.
 *
 */
public interface Model extends Serializable{
	/**
	 * Have the model create a new world.
	 */
	void createNewWorld();
	/**
	 * Determine whether the current state is terminal.
	 * @return True if the current state is terminal, False otherwise.
	 */
	boolean isTerminated();
	/**
	 * Validate and add actions to the list of actions to execute
	 * @param actions
	 * @param sendingPlayerNumber
	 */
	void addActions(Map<Integer, Action> actions, int sendingPlayerNumber);
	/**
	 * Execute actions and do anything else that needs to be done
	 */
	void executeStep();
	/**
	 * Get current state of the model
	 * @return current state of the model
	 */
	State getState();
	/**
	 * Get the history of the model since the last new world was created.
	 * @return
	 */
	History getHistory();
	void setVerbose(boolean verbosity);
	boolean getVerbose();
	
	void setConfiguration(Configuration configuration);
	Configuration getConfiguration();
	/**
	 * Add the turn tracker to moderate who can move on a given turn.
	 * @param turnTracker
	 */
	void setTurnTracker(TurnTracker turnTracker);
	
}
