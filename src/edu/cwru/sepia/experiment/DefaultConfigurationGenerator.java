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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DefaultConfigurationGenerator {
	public static void main(String[] args) throws FileNotFoundException, IOException, BackingStoreException {
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("sepia");
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
		Preferences modelPrefs = prefs.node("environment").node("model");
		modelPrefs.clear();
		modelPrefs.putBoolean("Conquest", true);
		modelPrefs.putBoolean("Midas", false);
		modelPrefs.putBoolean("ManifestDestiny", false);
		modelPrefs.putInt("TimeLimit", 65535);
	}
}
