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
 * A subtype of Action, includes CompoundProduction, PrimitiveProduction, PrimitiveBuild
 *
 */
public class ProductionAction extends Action {	
	private static final long serialVersionUID = -2225942140919623162L;
	
	private final int templateid;
	public ProductionAction(int unitid, ActionType type, int templateid)
	{
		super(unitid, type);
		this.templateid = templateid;
	}
	public int getTemplateId()
	{
		return templateid;
	}
	
	@Override
	public String toString() 
	{
		return "ProductionAction [templateid=" + templateid + ", type=" + type
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
			
			ProductionAction aother = (ProductionAction)other;
			return aother.type == type && aother.unitId == unitId && aother.templateid == templateid;
		}
	}
	
	@Override 
	public int hashCode()
	{
		int prime = 61;
		return prime*prime * templateid + prime * type.hashCode() + unitId;
	}
}
