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
package edu.cwru.sepia.action;

/**
 * A subtype of Action, include CompoundAttack, PrimitiveAttack, CompoundGather, CompoundDeposit
 *
 */
public class TargetedAction extends Action {	
	private static final long serialVersionUID = 5319275698704767319L;
	
	private final int targetid;
	public TargetedAction(int unitid, ActionType type, int targetid)
	{
		super(unitid, type);
		this.targetid = targetid;
	}
	
	public int getTargetId()
	{
		return targetid;
	}
	
	@Override
	public String toString() {
		return "TargetedAction [targetid=" + targetid + ", type=" + type
				+ ", unitId=" + unitId + "]";
	}
	
	@Override 
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		else if (other == null || !this.getClass().equals(other.getClass()))
		{
			return false;
		}
		else
		{
			
			TargetedAction aother = (TargetedAction)other;
			return aother.type == type && aother.unitId == unitId && aother.targetid == targetid;
		}
	}
	
	@Override 
	public int hashCode()
	{
		int prime = 61;
		return prime*prime * targetid + prime * type.hashCode() + unitId;
	}
}
