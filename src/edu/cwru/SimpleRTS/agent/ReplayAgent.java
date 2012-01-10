package edu.cwru.SimpleRTS.agent;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.cwru.SimpleRTS.Log.ActionLogger;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.LoadingStateCreator;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.environment.StateCreator;

public class ReplayAgent extends Agent {
	private static final long serialVersionUID = 1L;
	private String filename;
	private ActionLogger actions;
	public ReplayAgent(int playernumber, String[] args) {
		super(playernumber);
		this.filename = args[0];
		this.verbose = Boolean.parseBoolean(args[1]);
		StateCreator statecreator = new LoadingStateCreator(filename);
		State state = statecreator.createState();
		actions = state.getActionLog();
	}
	@Override
	public Map<Integer, Action> initialStep(StateView newstate) {
		return pullActions(newstate.getTurnNumber());
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate) {
		return pullActions(newstate.getTurnNumber());
	}
	private Map<Integer, Action> pullActions(int turnnumber) {
		if (verbose)
		{
			System.out.println("Pulling actions for turn "+turnnumber);
		}
		Map<Integer, Action> commands = new HashMap<Integer, Action>();
		for (Action a : actions.getActions(playernum, turnnumber))
		{
			if (verbose)
			{
				System.out.println(a);
			}
			if (commands.containsKey(a.getUnitId()))
			{
				System.err.println("Problem: multiple commands on a single unit");
			}
			commands.put(a.getUnitId(), a);
		}
		return commands;
	}
	@Override
	public void terminalStep(StateView newstate) {
		//nothing to do, this agent doesn't track anything
	}

	public static String getUsage() {
		return "It takes two parameters (--agentparam): filename of state to load, verbosity";
	}
}
