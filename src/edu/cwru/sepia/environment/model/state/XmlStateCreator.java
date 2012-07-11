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
package edu.cwru.sepia.environment.model.state;

import edu.cwru.sepia.environment.model.persistence.generated.XmlState;
import edu.cwru.sepia.environment.state.persistence.StateAdapter;

public class XmlStateCreator implements StateCreator {
	private static final long serialVersionUID = 1L;
	
	private XmlState state;
	private StateAdapter adapter;
	
	public XmlStateCreator(XmlState state) {
		this.state = state;
		adapter = new StateAdapter();
	}
	
	@Override
	public State createState() {		
		try
		{
			return adapter.fromXml(state);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

}
