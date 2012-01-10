package edu.cwru.SimpleRTS.agent.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.State.StateView;

public class VisualAgent extends Agent implements ActionListener {

	private static final long serialVersionUID = 1L;

	transient Map<Integer, Action> actions;
	GameScreen screen;
    GamePanel gamePanel;
    ControlPanel controlPanel = new ControlPanel();
    InfoPanel infoPanel = new InfoPanel();
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
	
	protected final boolean humanControllable;
	protected final boolean infoVis;
	
	public VisualAgent(int playernum, String[] otherargs) {
		super(playernum, Boolean.parseBoolean(otherargs[0]));
		humanControllable = Boolean.parseBoolean(otherargs[0]);
		infoVis = Boolean.parseBoolean(otherargs[1]);
		gamePanel = new GamePanel(this);
		actions = new HashMap<Integer, Action>();
		Runnable runner = new Runnable() {
			VisualAgent agent;
			public Runnable setAgent(VisualAgent agent) {
				this.agent = agent;
				return this;
			}
			@Override
			public void run() {
				screen = new GameScreen(gamePanel, controlPanel, infoPanel);
                screen.pack();
				gamePanel.addKeyListener(canvasKeyListener);
				controlPanel.addStepperListener(VisualAgent.this);
			}					
		}.setAgent(this);
		SwingUtilities.invokeLater(runner);
	}
	
	public VisualAgent(int playernum, final StateView initState) {
		super(playernum, false);
		humanControllable = false;
		infoVis = false;
		actions = new HashMap<Integer, Action>();
		Runnable runner = new Runnable() {
			VisualAgent agent;
			public Runnable setAgent(VisualAgent agent) {
				this.agent = agent;
				return this;
			}
			@Override
			public void run() {
				screen = new GameScreen(gamePanel, controlPanel, infoPanel);
                screen.pack();
				gamePanel.updateState(initState);
			}					
		}.setAgent(this);
		SwingUtilities.invokeLater(runner);
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate) {
        return middleStep(newstate);
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate) {
		if(gamePanel != null)
			gamePanel.updateState(newstate);
		try {
			stepSignal.acquire();
		} catch (InterruptedException e) {
			System.err.println("Unable to wait for step button to be pressed.");
		}
		Map<Integer, Action> toReturn = actions;
		actions = new HashMap<Integer, Action>();
		return toReturn;
	}

	@Override
	public void terminalStep(StateView newstate) {
		if(gamePanel != null)
			gamePanel.updateState(newstate);
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
		return "It takes three parameters (--agentparam): a boolean for whether it can be controlled by human, a boolean for whether visualize info for units";
	}
}
