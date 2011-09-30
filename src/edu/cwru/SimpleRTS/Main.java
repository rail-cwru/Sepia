package edu.cwru.SimpleRTS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.Environment;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.Model;
import edu.cwru.SimpleRTS.model.SimpleModel;

public class Main {
	public static void main(String[] args) {
		if(args.length < 4)
		{
			printUsageAndExit("Not enough arguments");
		}
		State initState = readState(args[0]);
		if(initState == null)
		{
			printUsageAndExit("Unable to read file " + args[0]);
		}
		int numEpisodes = parseInt(args[1]);
		if(numEpisodes < 1)
		{
			printUsageAndExit("Invalid number of episodes " + numEpisodes);
		}
		List<Agent> agents = new LinkedList<Agent>();
		int i = 2;
		while(i < args.length)
		{
			if(!args[i].equals("--agent"))
			{
				printUsageAndExit("Was expecting \"--agent\" as argument " + i + " but got " + args[i] + " instead");
			}
			i++;
			if(i == args.length)
			{
				printUsageAndExit("Agent class name expected, but ran out of arguments");
			}
			Class<?> agentClass = null;
			try {
				agentClass = Class.forName(args[i]);
			} catch (ClassNotFoundException e) {
				printUsageAndExit("Agent class was not found in the classpath. Try using -cp <path to your agent's class file> before -jar");
			}
			i++;
			if(i == args.length)
			{
				printUsageAndExit("Agent's player number was expected, but ran out of arguments");
			}
			int playerNum = parseInt(args[i]);
			if(playerNum < 0)
			{
				printUsageAndExit("Agent " + agentClass.getSimpleName() + "'s player number must be a non-negative integer");
			}
			i++;
			if(i < args.length - 1 && args[i].equals("--loadfrom"))
			{
				Agent agent = readAgent(args[i+1]);
				if(agent == null)
					printUsageAndExit("Unable to read agent from file " + args[i+1]);
				i += 2;
				agents.add(agent);
			}
			else
			{
				try {
					Agent agent = (Agent) agentClass.getConstructor(int.class).newInstance(playerNum);
					agents.add(agent);
				} catch (Exception e) {
					printUsageAndExit("Unable to instantiate a new instance of "+agentClass.getSimpleName());
				}
			}			
		}
		Model model = new SimpleModel(initState, 6);
		Environment env = new Environment(agents.toArray(new Agent[0]),model);
		for(int episode = 0; episode < 100; episode++)
		{
			env.runEpisode();
			env.requestNewEpisode();
		}
	}
	private static void printUsageAndExit(String error) {
		System.out.println(error);
		System.out.println("Usage: java [-cp <path to your agent's class file>] -jar SimpleRTS.jar <model file name> <number of episodes> [[--agent <agent class name> <player number> [--loadfrom <serialized agent file name>]] ...] ");
		System.out.println("\nExample: java -jar SimpleRTS.jar data/map1 10 --agent SimpleAgent1 --agent SimpleAgent1");
		System.out.println("\tThis will load the map stored in the file data/map1 with two new instances of SimpleAgent1 and run 10 episodes");
		System.out.println("Example: java -jar SimpleRTS.jar data/map1 1000 --agent ScriptedGoalAgent --loadfrom agents/script1 --agent SimpleAgent2");
		System.out.println("\tThis will load the map stored in the file data/map with a the ScriptedGoalAgent stored in agents/script1 and a new instance of SimpleAgent2 and run 1000 episodes");
		System.out.println("\nNote: all agents must implement Serializable and contain only primitives and Serializable objects in order to be loadable.");
		System.out.println("Note: agents that are not loaded from a file will be made using a single argument constructor that will take the player number.");
		System.exit(0);
	}
	private static State readState(String filename) {
		State state = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(filename));
			state = (State)ois.readObject();
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
		return state;
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
}
