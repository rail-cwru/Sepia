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
package edu.cwru.sepia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import edu.cwru.sepia.environment.History;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.state.persistence.HistoryAdapter;
import edu.cwru.sepia.environment.state.persistence.StateAdapter;
import edu.cwru.sepia.environment.state.persistence.generated.XmlHistory;
import edu.cwru.sepia.environment.state.persistence.generated.XmlState;


/**
 * store maps and status into files; 
 * load maps and status from files.
 * @author Feng
 *
 */
public final class GameMap {
		
	private GameMap() {}
	
	public static void storeState(String filename, State state) {
		if(filename.contains(".map"))
		{
			try {
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
				outputStream.writeObject(state);
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				JAXBContext context = JAXBContext.newInstance(XmlState.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty("jaxb.formatted.output", true);
				StateAdapter adapter = new StateAdapter();
				PrintWriter writer = new PrintWriter(new File(filename));
				marshaller.marshal(adapter.toXml(state), writer);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static State loadState(String filename) {
		State state = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
			state = (State) inputStream.readObject();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public static void storeHistory(String filename, History history) {
		if(filename.contains(".map"))
		{
			try {
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
				outputStream.writeObject(history);
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				JAXBContext context = JAXBContext.newInstance(XmlHistory.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty("jaxb.formatted.output", true);
				PrintWriter writer = new PrintWriter(new File(filename));
				marshaller.marshal(HistoryAdapter.toXml(history), writer);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static History loadHistory(String filename) {
		History history = null;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
			history = (History) inputStream.readObject();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return history;
	}
}








