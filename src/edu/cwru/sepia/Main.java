package edu.cwru.sepia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.Environment;
import edu.cwru.sepia.environment.LoadingStateCreator;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.StateCreator;
import edu.cwru.sepia.environment.XmlStateCreator;
import edu.cwru.sepia.environment.state.persistence.generated.XmlState;
import edu.cwru.sepia.model.LessSimpleModel;
import edu.cwru.sepia.util.Configuration;
import edu.cwru.sepia.util.ConfigurationValues;
import edu.cwru.sepia.util.PreferencesConfigurationLoader;

/**
 * An entry point into Sepia that takes command line arguments.
 * @author tim
 *
 */
public final class Main {
	public static void main(String[] args) throws BackingStoreException, IOException, InterruptedException {
		
		// play resource collection sample
//		String para = "--config data/midasConfig.xml data/rc_3m5t.xml --agent edu.cwru.sepia.agent.RCAgent 0";
//		String para = "--config data/midasConfig.xml data/rc_3m5t.xml --agent edu.cwru.sepia.agent.RCAgent 0 --agent edu.cwru.sepia.agent.visual.VisualAgent 0 --agentparam true --agentparam true";
		
		// use matlab agent sample
//		String para = "--config data/midasConfig.xml data/rc_3m5t.xml --agent edu.cwru.sepia.agent.MatlabAgent 0";
		
		// play maze sample
//		String para = "--config data/mazeConfig.xml data/maze_16x16n.map --agent edu.cwru.sepia.agent.visual.VisualAgent 0 --agentparam true --agentparam true";
		
		// produce peasant and footman sample
//		String para = "--config data/mazeConfig.xml data/produce_3p2f.map --agent edu.cwru.sepia.agent.visual.VisualAgent 0 --agentparam true --agentparam true";
		

//		args=para.split(" +");
		
		String configfile = null;
		
		if(args.length < 3 || (args.length > 0 && args[0].equals("--prefs") && args.length < 5))
		{
			printUsage("Not enough arguments");
			return;
		}
		int i = 0;
		if(args[i].equals("--config"))
		{
			configfile = args[i+1];
			if(!new File(configfile).exists())
			{
				configfile=null;
				printUsage("Invalid filename for preferences "+configfile);
				return;
			}
//			//print out the preferences
//			Preferences.userRoot().node("edu").node("cwru").node("sepia").exportSubtree(System.out);
			i += 2;
		}
		else
		{
			clearPrefs();
		}
		String statefilename = args[i];
		StateCreator stateCreator = null;
		State initState = null;
		if (!new File(statefilename).exists())
		{
			printUsage("File " + statefilename + " does not exist");
		}
		if(statefilename.contains(".map"))
		{
			 stateCreator = new LoadingStateCreator(statefilename);
			 initState = stateCreator.createState();
		}
		if(initState == null)
		{			
			JAXBContext context;
			XmlState xml = null;
			try {
				context = JAXBContext.newInstance(XmlState.class);
				xml = (XmlState)context.createUnmarshaller().unmarshal(new File(statefilename));
			} catch (JAXBException e1) {
				printUsage(statefilename + " is not a valid XML file describing a state.");
				return;
			}
			
			stateCreator = new XmlStateCreator(xml);
			initState = stateCreator.createState();
		}
		if(initState == null)
		{
			printUsage("Unable to read file " + args[i]);
			return;
		}
		i++;
		List<Agent> agents = new ArrayList<Agent>();
		while(i < args.length)
		{
			if(!args[i].equals("--agent"))
			{
				printUsage("Was expecting \"--agent\" as argument " + i + " but got " + args[i] + " instead");
				return;
			}
			String[] extraparams=new String[0];
			i++;
			if(i == args.length)
			{
				printUsage("Agent class name expected, but ran out of arguments");
				return;
			}
			Class<?> agentClass = null;
			try {
				agentClass = Class.forName(args[i]);
			} catch (ClassNotFoundException e) {
				printUsage("Agent class was not found in the classpath. Try using -cp <path to your agent's class file> before -jar");
				return;
			}
			i++;
			if(i == args.length)
			{
				printUsage("Agent's player number was expected, but ran out of arguments");
				return;
			}
			int playerNum;
			String playerNumInput = args[i];
			if (playerNumInput.equalsIgnoreCase("Observer"))
				playerNum = Agent.OBSERVER_ID;
			else
				playerNum = parseInt(playerNumInput);
			
			if(playerNum != Agent.OBSERVER_ID && playerNum < 0)
			{
				printUsage("Agent " + agentClass.getSimpleName() + "'s player number must be a non-negative integer");
				return;
			}
			else
			{
				//make sure it is a valid player
				boolean valid = playerNum == Agent.OBSERVER_ID;
				
				
				if (!valid)
				{
					Integer[] validPlayers = initState.getPlayers();
					for (int pind = 0; !valid&&pind< validPlayers.length; pind++)
					{
						if (validPlayers[pind]==playerNum)
						{
							valid = true;
						}
					}
				}
				if (!valid)
				{
					String validPlayerStr="[";
					Integer[] validPlayers = initState.getPlayers();
					for (int pind = 0; pind< validPlayers.length; pind++)
					{
						validPlayerStr += "\""+validPlayers[pind]+"\", ";
					}
					validPlayerStr+="\"Observer\"]";
					printUsage("Agent " + agentClass.getSimpleName() + "'s player number must be one of the players in the map file\nYou put \""+playerNumInput+"\" but it needs to be one of "+validPlayerStr);
				}
				
				
			}
			i++;
			while (i < args.length - 1 && args[i].equals("--agentparam"))
			{
				String[] newextraparams = new String[extraparams.length+1];
				for (int itr = 0; itr < extraparams.length; itr++) {
					newextraparams[itr] = extraparams[itr];
				}
				newextraparams[newextraparams.length-1]=args[i+1];
				extraparams = newextraparams;
				i += 2;
			}
			if(i < args.length - 1 && args[i].equals("--loadfrom"))
			{
				Agent agent = readAgent(args[i+1]);
				if(agent == null)
				{
					printUsage("Unable to read agent from file " + args[i+1]);
					return;
				}
				i += 2;
				agents.add(agent);
			}
			else
			{
				System.out.println("Making an agent of class "+agentClass.getName() + " with number " + playerNum + " and extra parameters "+Arrays.toString(extraparams));
				try {
					
					Agent agent = (Agent) agentClass.getConstructor(int.class, String[].class).newInstance(playerNum,extraparams);
					agents.add(agent);
				} catch (Exception e) {
					try {
						Agent agent = (Agent) agentClass.getConstructor(int.class).newInstance(playerNum);
						agents.add(agent);
					}
					catch (Exception e1)
					{
					try
					{
						printUsage("Unable to instantiate a new instance of "+agentClass.getSimpleName() + "\n"+"It requires the following additional parameters: " + (String)agentClass.getMethod("getUsage").invoke(null));
					}
					catch (Exception e2)
					{
						e.printStackTrace();
						e1.printStackTrace();
						printUsage("Unable to instantiate a new instance of "+agentClass.getSimpleName());
						return;
					}
					return;
					}
				}
			}			
		}
		Configuration configuration;
		if (configfile!=null)
			configuration = PreferencesConfigurationLoader.loadConfiguration(configfile);
		else
			configuration = PreferencesConfigurationLoader.loadConfiguration();
		int numEpisodes = ConfigurationValues.ENVIRONMENT_EPISODES.getIntValue(configuration);
		int episodesPerSave = ConfigurationValues.ENVIRONMENT_EPISODES_PER_SAVE.getIntValue(configuration);
		boolean saveAgents = ConfigurationValues.ENVIRONMENT_SAVE_AGENTS.getBooleanValue(configuration);;
		//just to make the directory
		File firstFile = new File("saves");
		firstFile.mkdirs();
		int seed = 7;
		LessSimpleModel model = new LessSimpleModel(initState, seed, stateCreator);
		Environment env = new Environment(agents.toArray(new Agent[0]),model, seed);
		for(int episode = 0; episode < numEpisodes; episode++)
		{
			//System.out.println("\n=======> Start running episode " + episode);
			env.runEpisode();
			if(episodesPerSave > 0 && episode % episodesPerSave == 0)
			{
				model.save("saves/state"+episode+".SRTSsav");
				for(int j = 0; saveAgents && j < agents.size(); j++)
				{
					try {
						ObjectOutputStream agentOut = new ObjectOutputStream(new FileOutputStream("saves/agent"+j+"-"+episode));
						agentOut.writeObject(agents.get(i));
						agentOut.close();
					}
					catch(Exception ex) {
						System.out.println("Unable to save agent "+j);
					}
				}
			}
		}
	}
	private static void printUsage(String error) {
		System.out.println(error);
		System.out.println("\nUsage: java [-cp <path to your agent's class file>];Sepia.jar] edu.cwru.sepia.Main [--config configurationFile] <map file name> [[--agent <agent class name> <player number> [--agentparam otherparameter]* [--loadfrom <serialized agent file name>]] ...] ");
		System.out.println("\nExample: --config data/defaultConfig.xml \"data/com_4f4a2kv4f4a2k.xml\" --agent edu.cwru.sepia.agent.visual.VisualAgent 0 --agentparam false --agentparam true --agent edu.cwru.sepia.agent.SimpleAgent1 0");
		System.out.println("\nNote: all agents must implement Serializable and contain only primitives and Serializable objects in order to be loadable.");
		System.out.println("Note: agents that are not loaded from a file will be made using a single argument constructor that will take the player number.");
		System.out.println("See doc/manual.html for more information");
		System.exit(-1);
	}
	private static Agent readAgent(String filename) {
		Agent agent = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(filename));
			agent = (Agent)ois.readObject();
		}
		catch(Exception ex) {
			return null;
		}
		finally {
			try {
				ois.close();
			} catch (IOException e) {
			}
		}
		return agent;
	}
	private static int parseInt(String arg) {
		try {
			return Integer.parseInt(arg);
		}
		catch(NumberFormatException ex) {
			return -1;
		}
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
	}
	
	private Main() {}
}
