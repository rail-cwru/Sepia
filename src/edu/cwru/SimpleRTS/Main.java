package edu.cwru.SimpleRTS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.Environment;
import edu.cwru.SimpleRTS.environment.LoadingStateCreator;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.SimpleModel;

public class Main {
	public static void main(String[] args) throws BackingStoreException, IOException, InterruptedException {
		if(args.length < 3 || (args.length > 0 && args[0].equals("--prefs") && args.length < 5))
		{
			printUsage("Not enough arguments");
			return;
		}
		int i = 0;
		if(args[i].equals("--config"))
		{
			if(!loadPrefs(args[i+1]))
			{
				printUsage("Invalid filename for preferences "+args[i+1]);
				return;
			}
			Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS").exportSubtree(System.out);
			i += 2;
		}
		else
		{
			clearPrefs();
		}
		String statefilename = args[i];
		State initState = new LoadingStateCreator(statefilename).createState();
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
			int playerNum = parseInt(args[i]);
			if(playerNum < 0)
			{
				printUsage("Agent " + agentClass.getSimpleName() + "'s player number must be a non-negative integer");
				return;
			}
			i++;
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
				try {
					Agent agent = (Agent) agentClass.getConstructor(int.class).newInstance(playerNum);
					agents.add(agent);
				} catch (Exception e) {
					printUsage("Unable to instantiate a new instance of "+agentClass.getSimpleName());
					return;
				}
			}			
		}
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS").node("environment");
		int numEpisodes = Math.max(1, prefs.getInt("NumEpisodes", 1));
		int episodesPerSave = prefs.getInt("EpisodesPerSave", 0);
		boolean saveAgents = prefs.getBoolean("SaveAgents", false);
		//just to make the directory
		File firstFile = new File("saves/state0");
		firstFile.mkdirs();
		
		SimpleModel model = new SimpleModel(initState, 6,new LoadingStateCreator(statefilename));
		Environment env = new Environment(agents.toArray(new Agent[0]),model);
		for(int episode = 0; episode < numEpisodes; episode++)
		{
			env.runEpisode();
			if(episodesPerSave > 0 && episode % episodesPerSave == 0)
			{
				model.save("saves/state"+episode);
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
			env.requestNewEpisode();
		}
	}
	private static void printUsage(String error) {
		System.out.println(error);
		System.out.println("Usage: java [-cp <path to your agent's class file>] -jar SimpleRTS.jar [--config configurationFile] <map file name> [[--agent <agent class name> <player number> [--loadfrom <serialized agent file name>]] ...] ");
		System.out.println("\nExample: java -jar SimpleRTS.jar data/map1 --agent SimpleAgent1 0 --agent SimpleAgent1 1");
		System.out.println("\tThis will load the map stored in the file data/map1 with two new instances of SimpleAgent1 and run 10 episodes");
		System.out.println("Example: java -jar SimpleRTS.jar data/map1 --agent ScriptedGoalAgent 0 --loadfrom agents/script1 --agent SimpleAgent2 1");
		System.out.println("\tThis will load the map stored in the file data/map with a the ScriptedGoalAgent stored in agents/script1 and a new instance of SimpleAgent2 and run 1000 episodes");
		System.out.println("\nNote: all agents must implement Serializable and contain only primitives and Serializable objects in order to be loadable.");
		System.out.println("Note: agents that are not loaded from a file will be made using a single argument constructor that will take the player number.");
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
		} catch(Exception e) {
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
