package edu.cwru.SimpleRTS.agent.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableMap;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.State.StateView;

public class VisualAgent extends Agent implements ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient ImmutableMap.Builder<Integer, Action> actions;
	GameScreen screen;
	VisualAgentControlWindow controlWindow;
	private final Semaphore stepSignal = new Semaphore(0);
	private final KeyAdapter canvasKeyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
//			System.out.println(e.getKeyCode());
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				stepSignal.drainPermits();
				stepSignal.release();
			}
		}
	};
	
	public VisualAgent(int playernum) {
		super(playernum, false);
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
				screen.addCanvasKeyListener(canvasKeyListener);
				controlWindow = new VisualAgentControlWindow();
				controlWindow.addStepperListener(VisualAgent.this);
			}					
		}.setAgent(this);
		SwingUtilities.invokeLater(runner);
	}
	
	public VisualAgent(int playernum, final StateView initState) {
		super(playernum, false);
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
		JOptionPane.showMessageDialog(null, "Congratulations! You finished the task!");
	}
	
	public void addAction(Action action) {
		actions.put(action.getUnitId(),action);
	}
	
    @Override
    public void actionPerformed(ActionEvent e) {
        stepSignal.drainPermits();
        stepSignal.release();
    }

	public static String getUsage() {
		return "None";
	}
}
