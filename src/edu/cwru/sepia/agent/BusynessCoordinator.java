package edu.cwru.sepia.agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cwru.sepia.environment.State.StateView;
import edu.cwru.sepia.model.unit.Unit.UnitView;

/**
 * A simple container for two lists: units that are doing something and units that aren't.
 * @author The Condor
 *
 */
public class BusynessCoordinator implements Serializable {	
	private static final long serialVersionUID = 7410381053814236332L;
	
	private List<Integer> busy;
	private List<Integer> lazy;
	public BusynessCoordinator() {
		busy = new ArrayList<Integer>();
		lazy = new ArrayList<Integer>();
	}
	public boolean isIdle(Integer id) {
		return lazy.contains(id);
	}
	public boolean isBusy(Integer id) {
		return busy.contains(id);
	}
	public void removeUnit(Integer unitID) {
		busy.remove(unitID);
		lazy.remove(unitID);
	}
	public void assignIdle(Integer unitID) {
		lazy.add(unitID);
		busy.remove(unitID);
	}
	public void assignBusy(Integer unitID) {
		lazy.remove(unitID);
		busy.add(unitID);
	}
}
