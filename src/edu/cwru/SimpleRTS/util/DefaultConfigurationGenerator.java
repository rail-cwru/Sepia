package edu.cwru.SimpleRTS.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DefaultConfigurationGenerator {
	public static void main(String[] args) throws FileNotFoundException, IOException, BackingStoreException {
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS");
		configureEnvPrefs(prefs);
		configureModelPrefs(prefs);
		prefs.exportSubtree(System.out);
		prefs.exportSubtree(new FileOutputStream("data/defaultConfig.xml"));
	}
	private static void configureEnvPrefs(Preferences prefs) throws BackingStoreException {
		Preferences envPrefs = prefs.node("environment");
		envPrefs.clear();
		envPrefs.putInt("NumEpisodes",10);		
	}
	private static void configureModelPrefs(Preferences prefs) throws BackingStoreException {
		Preferences modelPrefs = prefs.node("model");
		modelPrefs.clear();
		modelPrefs.putBoolean("Conquest", true);
		modelPrefs.putBoolean("Midas", false);
		modelPrefs.putBoolean("ManifestDestiny", false);
		modelPrefs.putInt("TimeLimit", 65535);
	}
}
