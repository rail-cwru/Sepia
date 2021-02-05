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
package edu.cwru.sepia.environment.model.history;

import java.io.Serializable;

import edu.cwru.sepia.util.DeepEquatable;

/**
 * Logs the upgrade of something
 *
 */
public class UpgradeLog implements Serializable, DeepEquatable {
	private static final long	serialVersionUID	= 1L;
	private int upgradetemplateid;
	private int producingunitid;
	private int player;
	public UpgradeLog(int upgradetemplateid, int producingunitid, int player) {
		this.upgradetemplateid = upgradetemplateid;
		this.producingunitid = producingunitid;
		this.player = player;
	}
	public int getUpgradeTemplateID() {
		return upgradetemplateid;
	}
	public int getController() {
		return player;
	}
	public int getProducingUnitID() {
		return producingunitid;
	}
	@Override public boolean equals(Object other) {
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		UpgradeLog o = (UpgradeLog)other;
		if (upgradetemplateid != o.upgradetemplateid)
			return false;
		if (producingunitid != o.producingunitid)
			return false;
		if (player != o.player)
			return false;
		return true;
	}
	@Override public int hashCode() {
		int product = 1;
		int sum = 0;
		int prime = 31;
		sum += (product = product*prime)*upgradetemplateid;
		sum += (product = product*prime)*producingunitid;
		sum += (product = product*prime)*player;
		return sum;
	}
	@Override public boolean deepEquals(Object other) {
		return equals(other);
	}
}
