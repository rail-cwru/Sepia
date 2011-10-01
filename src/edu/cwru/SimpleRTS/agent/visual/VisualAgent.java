package edu.cwru.SimpleRTS.agent.visual;

import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableMap;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.State.StateView;

public class VisualAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient ImmutableMap.Builder<Integer, Action> actions;
	GameScreen screen;
	
	public VisualAgent(int playernum, final StateView initState) {
		super(playernum);
		actions = new ImmutableMap.Builder<Integer, Action>();
		Runnable runner = new Runnable() {
			VisualAgent agent;
			public Runnable setAgent(VisualAgent agent) {
				this.agent = agent;
				return this;
			}
			@Override
			public void run() {
				screen = new GameScreen(agent);
				screen.updateState(initState);
			}					
		}.setAgent(this);
		SwingUtilities.invokeLater(runner);
	}

	@Override
	public ImmutableMap.Builder<Integer, Action> initialStep(StateView newstate) {
		ImmutableMap.Builder<Integer, Action> toReturn = actions;
		actions = new ImmutableMap.Builder<Integer, Action>();
		return toReturn;
	}

	@Override
	public ImmutableMap.Builder<Integer, Action> middleStep(StateView newstate) {
		ImmutableMap.Builder<Integer, Action> toReturn = actions;
		actions = new ImmutableMap.Builder<Integer, Action>();
		return toReturn;
	}

	@Override
	public void terminalStep(StateView newstate) {
		screen.close();
	}
	
	public void addAction(Action action) {
		actions.put(action.getUnitId(),action);
	}
	
}
