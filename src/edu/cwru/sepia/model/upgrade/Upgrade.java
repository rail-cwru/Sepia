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
package edu.cwru.sepia.model.upgrade;

/**
 * An instance of an upgrade template.
 * As it has no existence in the game world,
 * all relevant data is stored in its template.
 */
public class Upgrade 
{
	private UpgradeTemplate template;
	public Upgrade(UpgradeTemplate template)
	{
		this.template = template;
	}
	public UpgradeTemplate getTemplate() {
		return template;
	}
}
