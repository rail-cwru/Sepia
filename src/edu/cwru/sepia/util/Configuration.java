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
	
	public Object get(String key) {
		return settings.get(key);
	}
	
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return 
	 * @return
	 */
	public <T> T get(String key, T fallback) {
		T property = (T)settings.get(key);
		return property != null  ?  property : fallback;
	}
	
	public String getString(String key) {
		return (String)settings.get(key);
	}
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public String getString(String key, String fallback) {
		String property = (String)settings.get(key);
		return property != null ? property: fallback;
	}
	
	/**
	 * Get a boolean property
	 * @param key
	 * @return
	 */
	public Boolean getBoolean(String key) {
		return (Boolean)settings.get(key);
	}
	
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public Boolean getBoolean(String key, boolean fallback) {
		Boolean property = (Boolean)settings.get(key);
		return property != null  ?  property : fallback;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInt(String key) {
		return (Integer)settings.get(key);
	}
	/**
	 * Get an integer property
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public Integer getInt(String key, int fallback) {
		Integer property = (Integer)settings.get(key);
		return property != null  ?  property : fallback;
	}
	
	public Double getDouble(String key) {
		return (Double)settings.get(key);
	}
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public Double getDouble(String key, Double fallback) {
		Double property = (Double)settings.get(key);
		return property != null ? property: fallback;
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
