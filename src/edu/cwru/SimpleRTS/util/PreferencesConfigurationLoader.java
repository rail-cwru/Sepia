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
			if(value.type.equals(String.class))
			{
				config.put(value.key, prefs.get(value.key, null));
			}
			else if(value.type.equals(Boolean.class))
			{
				config.put(value.key, prefs.getBoolean(value.key, false));
			}
			else if(value.type.equals(Integer.class))
			{
				config.put(value.key, prefs.getInt(value.key, 0));
			} 
			else if(value.type.equals(Double.class))
			{
				config.put(value.key, prefs.getDouble(value.key, 0));
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
	}
}
