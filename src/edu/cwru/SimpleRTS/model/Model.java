package edu.cwru.SimpleRTS.model;
import java.io.Serializable;
import java.util.Map;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.environment.State.StateView;
public interface Model extends Serializable{
	void createNewWorld();
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
	StateView getState(int player);
	HistoryView getHistory(int player);
}
