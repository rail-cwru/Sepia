package edu.cwru.SimpleRTS.agent.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

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
	VisualAgentControlWindow controlWindow;
	private final Semaphore stepSignal = new Semaphore(1);
	private final ActionListener stepperListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			stepSignal.release();
		}
	};
	
	public VisualAgent(int playernum) {
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
				controlWindow = new VisualAgentControlWindow();
				controlWindow.addStepperListener(stepperListener);
			}					
		}.setAgent(this);
		SwingUtilities.invokeLater(runner);
	}
	
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
		if(screen != null)
			screen.updateState(newstate);
		try {
			stepSignal.acquire();
		} catch (InterruptedException e) {
			System.err.println("Unable to wait for step button to be pressed.");
		}
		ImmutableMap.Builder<Integer, Action> toReturn = actions;
		actions = new ImmutableMap.Builder<Integer, Action>();
		return toReturn;
	}

	@Override
	public ImmutableMap.Builder<Integer, Action> middleStep(StateView newstate) {
		if(screen != null)
			screen.updateState(newstate);
		try {
			stepSignal.acquire();
		} catch (InterruptedException e) {
			System.err.println("Unable to wait for step button to be pressed.");
		}
		ImmutableMap.Builder<Integer, Action> toReturn = actions;
		actions = new ImmutableMap.Builder<Integer, Action>();
		return toReturn;
	}

	@Override
	public void terminalStep(StateView newstate) {
		if(screen != null)
			screen.updateState(newstate);
	}
	
	public void addAction(Action action) {
		actions.put(action.getUnitId(),action);
	}
	
}
