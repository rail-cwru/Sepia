package edu.cwru.SimpleRTS.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
/**
 * Manages a list of preferences and allows for saving to/loading from a file.
 * @author Tim
 *
 */
public final class Preferences {
	private static Preferences instance;	
	public static Preferences getInstance() {
		if(instance == null)
			instance = new Preferences();
		return instance;
	}
	
	private File file;
	private HashMap<String,String> preferences;
	
	private Preferences() {
		preferences = new HashMap<String,String>();
	}
	/**
	 * Attempts to load a set of preferences from a file.
	 * @param filename - the name of the file from which to load preferences
	 * @return whether an error occurred when opening the file
	 */
	public boolean load(String filename) {
		try
		{
			file = new File(filename);
			Scanner in = new Scanner(file);
			while(in.hasNextLine())
			{
				String line = in.nextLine();
				String[] parts = line.split("=",2);
				if(parts.length == 2)
					preferences.put(parts[0], parts[1]);
			}
			in.close();
		}
		catch(Exception ex) { return false; }
		return true;
	}
	/**
	 * Stores the current set of preferences to the file set by {@link #load(String)} or {@link #saveAs(String)}.
	 * @return whether or not an error occurred when opening the file
	 */
	public boolean save() {
		try
		{
			PrintWriter out = new PrintWriter(file);
			for(String key : preferences.keySet())
			{
				out.print(key);
				out.print("=");
				out.println(preferences.get(key));
			}
			out.close();
		}
		catch(Exception ex) { return false; }
		return true;
	}
	/**
	 * Sets the name of the file to which the preferences should be saved and then saves.
	 * @param filename - the name of the file to which to save preferences
	 * @return whether there was an error when opening the file
	 */
	public boolean saveAs(String filename) {
		file = new File(filename);
		return save();
	}
	/**
	 * Returns the value of the preference indicated by the given key, or null if no 
	 * such preference is registered. 
	 * @param key - the name of the preference
	 * @return the value of the preference or null
	 */
	public String getPreference(String key) {
		return preferences.get(key);
	}
	/**
	 * Registers or overwrites a preference.
	 * @param key - the name of the preference
	 * @param value - the value of the preference
	 * @return the previous value of the preference if it was overwritten, or null otherwise
	 */
	public String putPreference(String key, String value) {
		return preferences.put(key, value);
	}
	/**
	 * Returns a read-only view of the names of all registered preferences.
	 * @return
	 */
	public Set<String> existingPreferences() {
		return Collections.unmodifiableSet(preferences.keySet());
	}
}
