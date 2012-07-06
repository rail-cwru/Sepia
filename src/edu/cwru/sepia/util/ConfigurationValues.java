package edu.cwru.sepia.util;

public enum ConfigurationValues {
	MODEL_CONQUEST("model.Conquest", Boolean.class, false),
	MODEL_MIDAS("model.Midas", Boolean.class, false),
	MODEL_MANIFEST_DESTINY("model.ManifestDestiny", Boolean.class, false),
	MODEL_TIME_LIMIT("model.TimeLimit", Integer.class, 1 << 16),
	MODEL_REQUIRED_GOLD("model.RequiredGold", Integer.class, 0),
	MODEL_REQUIRED_WOOD("model.RequiredWood", Integer.class, 0),
	ENVIRONMENT_EPISODES("environment.NumEpisodes", Integer.class, 1),
	ENVIRONMENT_EPISODES_PER_SAVE("environment.EpisodesPerSave", Integer.class, 1),
	ENVIRONMENT_SAVE_AGENTS("environment.SaveAgents", Boolean.class, false)
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
