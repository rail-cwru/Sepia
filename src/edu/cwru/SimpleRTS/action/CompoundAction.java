package edu.cwru.SimpleRTS.action;

import java.util.LinkedList;

/**
 * This class contains complex actions that are composed of multiple primitive actions.
 * @author Tim
 *
 */
public class CompoundAction extends Action {
	protected LinkedList<PrimitiveAction> actionSequence;
	
	public CompoundAction() {
		actionSequence = new LinkedList<PrimitiveAction>();
	}
	public PrimitiveAction getNextAction() {
		return actionSequence.removeFirst();
	}
}
