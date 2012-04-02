package edu.cwru.SimpleRTS.util;

import java.io.File;
import java.io.FileInputStream;
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
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS");
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
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS");
		prefs.clear();
		prefs.node("environment").clear();
		prefs.node("model").clear();
		prefs.flush();
	}
}
