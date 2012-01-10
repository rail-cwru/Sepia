package edu.cwru.SimpleRTS.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadingStateCreator implements StateCreator{

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
