package edu.cwru.sepia;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.Runner;
import edu.cwru.sepia.environment.StateCreator;
import edu.cwru.sepia.environment.XmlStateCreator;
import edu.cwru.sepia.environment.state.persistence.generated.XmlState;
import edu.cwru.sepia.util.Configuration;
import edu.cwru.sepia.util.ConfigurationValues;
import edu.cwru.sepia.util.KeyValueConfigurationUtil;
import edu.cwru.sepia.util.config.xml.XmlConfiguration;
import edu.cwru.sepia.util.config.xml.XmlKeyValuePair;
import edu.cwru.sepia.util.config.xml.XmlModelParameters;
import edu.cwru.sepia.util.config.xml.XmlPlayer;
import edu.cwru.sepia.util.config.xml.XmlRunner;
import edu.cwru.sepia.util.config.xml.XmlPlayer.AgentClass;

/**
 * An entry point into Sepia that takes an XML configuration file as defined in data/schema/config.xsd.
 * @author tim
 *
 */
public final class Main2 {
	private static final Logger logger = Logger.getLogger(Main2.class.getCanonicalName());

	public static void main(String[] args) {
		if(args.length == 0)
		{
			System.out.println("You must specify a configuration file.");
			return;
		}
				
		JAXBContext context;
		XmlConfiguration xmlConfig = null;
		try 
		{
			context = JAXBContext.newInstance(XmlConfiguration.class);
			xmlConfig = (XmlConfiguration)context.createUnmarshaller().unmarshal(new File(args[0]));
		} 
		catch (JAXBException e1) 
		{
			logger.log(Level.SEVERE, args[0] + " is not a valid XML configuration file.", e1);
			return;
		}
		
		StateCreator stateCreator;
		try
		{
			stateCreator = getStateCreator(xmlConfig);
		}
		catch(JAXBException ex) 
		{
			System.out.println(xmlConfig.getMap() + " is not a valid map file.");
			return;
		}
		
		Agent[] agents = getAgents(xmlConfig);
		if(agents == null)
			return;
		
		Runner runner;
		try
		{
			runner = getRunner(xmlConfig, stateCreator, agents);
		}
		catch (Exception e)
		{
			System.out.println("Unable to instantiate episode runner " + xmlConfig.getRunner().getRunnerClass());
			e.printStackTrace();
			return;
		}
		
		runner.run();
	}
	
	private static StateCreator getStateCreator(XmlConfiguration xmlConfig) throws JAXBException {
		String mapFilename = xmlConfig.getMap();
		JAXBContext context = JAXBContext.newInstance(XmlState.class);
		XmlState state = (XmlState)context.createUnmarshaller().unmarshal(new File(mapFilename));
		return new XmlStateCreator(state);
	}
	
	private static Agent[] getAgents(XmlConfiguration xmlConfig) {
		List<XmlPlayer> players = xmlConfig.getPlayer();
		Agent[] agents = new Agent[players.size()];
		
		for(int i = 0; i < players.size(); i++)
		{
			XmlPlayer player = players.get(i);
			AgentClass agentClass = player.getAgentClass();
			try
			{
				Class<?> classDef = Class.forName(agentClass.getClassName());
				//TODO - handle invalid player Ids
				try 
				{
					
					Agent agent = (Agent) classDef.getConstructor(int.class, String[].class).newInstance(player.getId(), agentClass.getArgument().toArray(new String[0]));
					agents[i] = agent;
				} 
				catch (Exception e) 
				{
					@SuppressWarnings("unchecked")
					Constructor<? extends Agent> constructor = (Constructor<? extends Agent>) classDef.getConstructor(int.class); 
					System.out.println(constructor);
					Agent agent = (Agent)constructor.newInstance(player.getId().intValue());
					agents[i] = agent;
				}
			}
			catch(Exception ex)
			{
				logger.log(Level.SEVERE, "Unable to instantiate " + agentClass.getClassName() + " with arguments " + agentClass.getArgument() + "." + " is not a valid XML configuration file.", ex);
				return null;
			}
			Configuration configuration = null;
			if(player.getPropertyFile() != null)
			{
				try
				{
					configuration = KeyValueConfigurationUtil.load(player.getPropertyFile());
				}
				catch(Exception ex)
				{
					logger.warning("Agent " + i + "'s config file " + player.getPropertyFile() + " is invalid.");
				}
			}
			if(player.getProperty() != null)
			{
				if(configuration == null)
					configuration = new Configuration();
				for(XmlKeyValuePair pair : player.getProperty())
				{
					configuration.put(pair.getName(), pair.getValue());
				}
			}
			if(configuration == null)
				configuration = new Configuration();
			agents[i].setConfiguration(configuration);
		}
		
		return agents;
	}
	
	private static Runner getRunner(XmlConfiguration xmlConfig, StateCreator stateCreator, Agent[] agents) throws ClassNotFoundException, IllegalArgumentException, 
													SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		XmlRunner runner = xmlConfig.getRunner();
		Class<?> runnerClass = Class.forName(runner.getRunnerClass());
		edu.cwru.sepia.util.Configuration config = new edu.cwru.sepia.util.Configuration();
		getModelParameters(xmlConfig, config);
		return (edu.cwru.sepia.environment.Runner)
				runnerClass
				.getConstructor(edu.cwru.sepia.util.Configuration.class, StateCreator.class, Agent[].class)
				.newInstance(config, stateCreator, agents);
	}

	private static void getModelParameters(XmlConfiguration xmlConfig, Configuration configuration) {
		XmlModelParameters modelParameters = xmlConfig.getModelParameters();
		configuration.put(ConfigurationValues.MODEL_CONQUEST.key, modelParameters.isConquest());
		configuration.put(ConfigurationValues.MODEL_MIDAS.key, modelParameters.isMidas());
		configuration.put(ConfigurationValues.MODEL_MANIFEST_DESTINY.key, modelParameters.isManifestDestiny());
		configuration.put(ConfigurationValues.MODEL_TIME_LIMIT.key, modelParameters.getTimeLimit());
		for(XmlKeyValuePair pair : modelParameters.getRequirement())
		{
			configuration.put(pair.getName(), pair.getValue());
		}
	}
	
	private Main2() {}
}
