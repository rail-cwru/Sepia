package edu.cwru.SimpleRTS.agent;

import java.util.HashMap;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.LoadingStateCreator;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.environment.StateCreator;
import edu.cwru.SimpleRTS.log.ActionLogger;

/**
 * A non-functional agent that will, upon completion, act as an example for implementing the capability of learning through actions done in old states
 * @author The Condor
 *
 */
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
		actions = null;//state.getActionLog();
		//StateView view = state.getView(Agent.OBSERVER_ID);
	}
	@Override
	public Map<Integer, Action> initialStep(StateView newstate, History.HistoryView statehistory) {
		return pullActions(newstate.getTurnNumber());
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate, History.HistoryView statehistory) {
		return pullActions(newstate.getTurnNumber());
	}
	private Map<Integer, Action> pullActions(int turnnumber) {
		if (verbose)
		{
			System.out.println("Pulling actions for turn "+turnnumber);
		}
		Map<Integer, Action> commands = new HashMap<Integer, Action>();
		for (Action a : actions.getActions(turnnumber).values())
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
	public void terminalStep(StateView newstate, History.HistoryView statehistory) {
		//nothing to do, this agent doesn't track anything
	}

	public static String getUsage() {
		return "It takes two parameters (--agentparam): filename of state to load, verbosity";
	}
}
