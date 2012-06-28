package edu.cwru.SimpleRTS.util;

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

import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.state.persistence.HistoryAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.StateAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlHistory;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlState;


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








