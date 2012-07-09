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
package edu.cwru.sepia.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesConfigurationLoader {
	public static Configuration loadConfiguration(String filename) throws BackingStoreException {
		clearPrefs();
		loadPrefs(filename);
		return loadConfiguration();
	}
	public static Configuration loadConfiguration() {
		Configuration config = new Configuration();
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("sepia");
		for(ConfigurationValues value : ConfigurationValues.values())
		{
			//Use the periods to navigate the tree structure
			Preferences subprefs = prefs;
			String subkey=value.key;
			while(subkey.contains("."))
			{
				int firstperiod = subkey.indexOf(".");
				subprefs = subprefs.node(subkey.substring(0, firstperiod));
				subkey = subkey.substring(firstperiod+1);
			}
			if(value.type.equals(String.class))
			{
				config.put(value.key, subprefs.get(subkey, null));
			}
			else if(value.type.equals(Boolean.class))
			{
				config.put(value.key, subprefs.getBoolean(subkey, false));
			}
			else if(value.type.equals(Integer.class))
			{
				config.put(value.key, subprefs.getInt(subkey, 0));
			} 
			else if(value.type.equals(Double.class))
			{
				config.put(value.key, subprefs.getDouble(subkey, 0));
			} 
		}
		try
		{
			Preferences model  = prefs.node("model");
			for(String key : model.keys())
			{
				if(key.startsWith("Required"))
					config.put(key, model.getInt(key, 0));
			}
		}
		catch(Exception ex) {}
		return config;
	}
	private static boolean loadPrefs(String arg) {
		try {
			Preferences.importPreferences(new FileInputStream(arg));
			return true;
		} catch (Exception e) {
			System.err.println("Invalid preference file "+new File(arg).getAbsolutePath());
			e.printStackTrace();
			return false;
		}
	}
	private static void clearPrefs() throws BackingStoreException {
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("sepia");
		prefs.clear();
		prefs.node("environment").clear();
		prefs.node("model").clear();
		prefs.flush();
	}
}
