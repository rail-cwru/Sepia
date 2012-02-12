package edu.cwru.SimpleRTS.action;

public enum ActionFeedback {
	/**
	 * Indicates that the action has been successfully completed
	 */
	COMPLETED, 
	/**
	 * Indicates that the action is incomplete
	 */
	INCOMPLETE,
	/**
	 * Indicates that the action cannot be completed
	 */
	FAILED,
	/**
	 * Indicates that the action is incomplete and had to recalculate multiple times, indicating the possibility of being stuck
	 */
	INCOMPLETEMAYBESTUCK,
	/**
	 * Indicates that the action was issued to a unit not controlled by the player
	 */
	INVALIDCONTROLLER,
	/**
	 * Indicates that the action was mapped to a unit other than the one that the action refers to
	 */
	INVALIDUNIT,
	/**
	 * Indicates that the action was improperly constructed
	 */
	INVALIDTYPE
	
}
