package edu.cwru.SimpleRTS.agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTask;

public class BusynessCoordinator implements Serializable {
	private List<Integer> busy;
	private List<Integer> lazy;
	private int player;
	public BusynessCoordinator(int player) {
		busy = new ArrayList<Integer>();
		lazy = new ArrayList<Integer>();
	}
	public void initialize(StateView state) {
		for (Integer id : state.getAllUnitIds()) {
			UnitView u = state.getUnit(id);
			switch(u.getTask()) {
			case Attack:
			case Build:
			case Move:
			case Gold:
			case Wood:
				busy.add(id);
				break;
			case Idle:
			default:
				lazy.add(id);
				break;
			
			}
		}
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
	public void checkForIdleness(StateView state) {
		List<Integer> onestoidleize = new LinkedList<Integer>();//because you can't alter it within the same loop 
		for (Integer unitID : busy) {
			if (state.getUnit(unitID).getTask() == UnitTask.Idle) {
				onestoidleize.add(unitID);
			}
		}
		for (Integer id : onestoidleize) {
			assignIdle(id);
		}
		System.out.println("Busy: " + busy.size());
		System.out.println("Idle: " + lazy.size());
	}
}
