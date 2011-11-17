package edu.cwru.SimpleRTS.model.prerequisite;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;

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
	public PrerequisiteHolder() {
		prerequisites = new ArrayList<Prerequisite>();
	}
	@Override
	public boolean isFulfilled(StateView state) {
		
		for (Prerequisite p : prerequisites) {
			System.out.println("Checking: " + p);
			if (!p.isFulfilled(state)) {
				//Any not being fulfilled is a lack of fulfillment for all
				//System.out.println("Prerequisite Not Fulfilled");
				return false;
			}
			//System.out.println("Prerequisite is fulfilled");
		}
		//System.out.println("All prereqs fulfilled");
		return true;
	}
	/**
	 * Add a prerequisite, for use in constructing the holder
	 */
	public void addPrerequisite(Prerequisite prereq) {
		prerequisites.add(prereq);
	}
}
