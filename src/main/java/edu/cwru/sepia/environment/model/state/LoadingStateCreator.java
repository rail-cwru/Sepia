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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class LoadingStateCreator implements StateCreator{
	private static final long	serialVersionUID	= 1L;
	
	String loadfilename;
	public LoadingStateCreator(String loadfilename) {
		this.loadfilename = loadfilename;
	}
	@Override
	public State createState() {
		State state = null;
		ObjectInputStream ois = null;
		
		try {
			ois = new ObjectInputStream(new FileInputStream(loadfilename));
			state = (State)ois.readObject();
			ois.close();
		}
		catch(Exception ex) {
			System.err.print("Could not load \""+new File(loadfilename).getAbsolutePath()+"\" ");
			ex.printStackTrace();
			return null;
		}
		return state;
	}
	
}
