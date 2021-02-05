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

public enum ConfigurationValues {
	MODEL_CONQUEST("environment.model.Conquest", Boolean.class, false),
	MODEL_MIDAS("environment.model.Midas", Boolean.class, false),
	MODEL_MANIFEST_DESTINY("environment.model.ManifestDestiny", Boolean.class, false),
	MODEL_TIME_LIMIT("environment.model.TimeLimit", Integer.class, 1 << 16),
	MODEL_REQUIRED_GOLD("environment.model.RequiredGold", Integer.class, 0),
	MODEL_REQUIRED_WOOD("environment.model.RequiredWood", Integer.class, 0),
	ENVIRONMENT_EPISODES("experiment.NumEpisodes", Integer.class, 1),
	ENVIRONMENT_EPISODES_PER_SAVE("experiment.EpisodesPerSave", Integer.class, 1),
	ENVIRONMENT_SAVE_AGENTS("experiment.SaveAgents", Boolean.class, false)
	;
	public final String key;
	public final Class<?> type;
	private final Object defaultValue;
	
	private <T> ConfigurationValues(String key, Class<T> type, T defaultValue) {
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	public String getStringValue(Configuration configuration) {
		if(configuration.containsKey(key))
			return configuration.getString(key);
		return (String)defaultValue;
	}

	public boolean getBooleanValue(Configuration configuration) {
		if(configuration.containsKey(key))
			return configuration.getBoolean(key);
		return (Boolean)defaultValue;
	}
	
	public int getIntValue(Configuration configuration) {
		if(configuration.containsKey(key))
			return configuration.getInt(key);
		return (Integer)defaultValue;
	}
	
	public double getDoubleValue(Configuration configuration) {
		if(configuration.containsKey(key))
			return configuration.getDouble(key);
		return (Double)defaultValue;
	}
}
