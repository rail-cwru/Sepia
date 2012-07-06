/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
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
