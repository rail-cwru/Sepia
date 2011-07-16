package edu.cwru.SimpleRTS.model.prerequisite;

import java.util.List;

import edu.cwru.SimpleRTS.environment.State;

/**
 * A prerequisite which stores other prerequisites
 * @author The Condor
 *
 */
public class PrerequisiteHolder implements Prerequisite {
	private List<Prerequisite> prerequisites;
	public PrerequisiteHolder(List<Prerequisite> prerequisites) {
		this.prerequisites = prerequisites;
	}
	@Override
	public boolean isFulfilled(State state) {
		for (Prerequisite p : prerequisites) {
			if (!p.isFulfilled(state)) {
				//Any not being fulfilled is a lack of fulfillment for all
				return false;
			}
		}
		return true;
	}
}
