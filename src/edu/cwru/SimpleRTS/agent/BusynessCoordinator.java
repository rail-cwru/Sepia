package edu.cwru.SimpleRTS.agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

public class BusynessCoordinator implements Serializable {
	
	private static final long serialVersionUID = 7410381053814236332L;
	
	private List<Integer> busy;
	private List<Integer> lazy;
	private int player;
	public BusynessCoordinator(int player) {
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
