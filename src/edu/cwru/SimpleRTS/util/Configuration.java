package edu.cwru.SimpleRTS.util;

import java.util.HashMap;
import java.util.Set;
/**
 * Manages a list of configurable properties and allows for saving to/loading from a file.
 * @author Tim
 *
 */
public class Configuration {
	
	private HashMap<String,Object> settings;
	
	public Configuration() {
		settings = new HashMap<String,Object>();
	}
	
	public String getString(String key) {
		return (String)settings.get(key);
	}
	
	public boolean getBoolean(String key) {
		return (Boolean)settings.get(key);
	}
	
	public int getInt(String key) {
		return (Integer)settings.get(key);
	}
	
	public double getDouble(String key) {
		return (Double)settings.get(key);
	}
	
	public void put(String key, String value) {
		settings.put(key, value);
	}
	
	public void put(String key, boolean value) {
		settings.put(key, value);
	}
	
	public void put(String key, int value) {
		settings.put(key, value);
	}
	
	public void put(String key, double value) {
		settings.put(key, value);
	}
	
	public <T> void put(String key, T value) {
		settings.put(key, value);
	}
	
	public Set<String> getKeys() {
		return settings.keySet();
	}
	
	public boolean containsKey(String key) {
		return settings.containsKey(key);
	}
	
	@Override
	public String toString() {
		return settings.toString();
	}
}
