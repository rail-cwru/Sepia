package edu.cwru.SimpleRTS.agent;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.ThreadIntermediary.ViewAndNextLatch;
import edu.cwru.SimpleRTS.agent.visual.VisualLog;
import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
/**
 * The base type for any agent that can interact with the SimpleRTS environment.
 * @author Tim
 *
 */
public abstract class Agent implements Serializable {
	protected VisualLog visualLog;
	protected boolean verbose; 
	public static final int OBSERVER_ID = -999;
//	private final int[] enemynums;
	protected final int playernum;
	// map: agentID -> flag, if this flag set false, then we will ignore this agent when checking terminal condition. 
	
	
	/**
	 * Assigns this Agent the next available auto-incrementing ID and sets the playernum to the argument.
	 * @param playernum
	 */
	public Agent(int playernum) {
		this(playernum, true);
	}
	
	protected Agent(int playernum , boolean countsTowardTermination) {
		this.playernum=playernum;
//		this.enemynums = new int[enemynums.length];
//		System.arraycopy(enemynums, 0, this.enemynums, 0, enemynums.length);
		verbose = false;
	}
	
	/**
	 * Write a line to the visual log maintained by this agent.
	 * If it is not initialized, this calls initializeVisualAgent.
	 * @param newline The line to write
	 */
	public void writeLineVisual(String newline)
	{
		if (visualLog == null)
			initializeVisualLog();
		visualLog.writeLine(newline);
		visualLog.repaint();
	}
	/**
	 * Clear the visual log maintained by this agent.
	 * If it is not initialized, this calls initializeVisualAgent.
	 */
	public void clearVisualLog()
	{
		if (visualLog == null)
			initializeVisualLog();
		visualLog.clearLog();
		visualLog.repaint();
	}
	/**
	 * If the visual log is initialized, then close the window and release the reference.
	 */
	public void closeVisualLog()
	{
		if (visualLog!=null)
		{
			visualLog.setVisible(false);
			visualLog.dispose();
			visualLog=null;
		}
	}
	/**
	 * Initialize the visual log, constructing it.
	 */
	protected void initializeVisualLog()
	{
		if (visualLog==null)
			visualLog = new VisualLog(this.toString(), 300, 300);
		visualLog.writeLine("This log belongs to "+this.toString());
	}
	/**
	 * Resize the visual log to a new size.
	 * @param width
	 * @param height
	 */
	public void setVisualLogDimensions(int width, int height)
	{
		visualLog.setPreferredSize(new Dimension(width,height));
	}
	/**
	 * Return the usage of any additional parameters
	 * @return
	 */
	public static String getUsage() {
		return "Unknown, it was not defined";
	}
	/**
	 * Determines whether to print out the action list each time it is chosen by {@link #getAction()}
	 * @param verbosity
	 */
	public void setVerbose(boolean verbosity) {
		verbose = verbosity;
	}
	/**
	 * Get the player number that this agent controls
	 * @return
	 */
	public int getPlayerNumber() {
		return playernum;
	}
	
	
	
//	private ThreadIntermediary intermediary;
//	/**
//	 * Set the shared memory intermediary used in parallelization.
//	 * @param intermediary
//	 */
//	public void setIntermediary(ThreadIntermediary intermediary) {
//		this.intermediary = intermediary;
//	}
//	public void start() throws InterruptedException {
//		
//		while (true) {
//			if (intermediary == null)
//				throw new IllegalStateException("Intermediary is null, you must set an intermediary");
//			ViewAndNextLatch viewAndLatch = intermediary.retrieveState();
//			StateView state = viewAndLatch.stateView;
//			HistoryView history = viewAndLatch.historyView;
//			CountDownLatch latch = viewAndLatch.nextStateLatch;
//			ThreadIntermediary.StateType type = viewAndLatch.stateType;
//			//If there is a current state
//			if (state != null && history!=null) {
//				switch (type) {
//				case INITIAL:
//					initialStep(state, history);
//					break;
//				case MIDDLE:
//					middleStep(state, history);
//					break;
//				case TERMINAL:
//					terminalStep(state, history);
//					break;
//				default:
//					throw new IllegalArgumentException("State type \""+type+"\" is not supported by agent.");	
//				}
//			}
//			latch.await();
//		}
//	}
//	//Action selection and such.  Implementers should not have access to this
//	private Map<Integer,Action> chosenActions;
//	
//	/**
//	 * Request the action that the agent will take at this timestep.
//	 * The agent should have been asynchronously calculating this.
//	 * Must not be called until calculation is done.  You will know that it is done by the latch being passed to acceptState()
//	 * @return
//	 */
//	public final Map<Integer,Action> getAction()
//	{
//		
//		if (verbose) {
//			System.out.println("Agent "+playernum+" is performing actions:");
//			Set<Integer> units = chosenActions.keySet();
//			for (Integer i : units) {
//				System.out.println("\t" + i + " " + chosenActions.get(i));
//			}
//		}
//		return chosenActions;
//	}
//	/**
//	 * Accept the first state of an episode and begin calculating a response for it
//	 * @param newstate The new state of the system
//	 * @param statehistory The logs of events and actions leading to this state
//	 * @param onofflatch A countdown latch used to synchonize completion
//	 */
//	public final void acceptInitialState(State.StateView newstate, History.HistoryView statehistory, CountDownLatch onofflatch)
//	{
//		chosenActions = initialStep(newstate, statehistory);
//		onofflatch.countDown();
//	}
//	/**
//	 * Accept a state and begin calculating a response for it
//	 * @param newstate The new state of the system
//	 * @param historyView 
//	 * @param statehistory The logs of events and actions leading to this state
//	 * @param onofflatch A countdown latch used to synchonize completion
//	 */
//	public final void acceptMiddleState(State.StateView newstate, HistoryView statehistory, CountDownLatch onofflatch)
//	{
//		chosenActions = middleStep(newstate, statehistory);
//		onofflatch.countDown();
//	}
//	/**
//	 * Accept the final state of an episode
//	 * @param newstate The new state of the system
//	 * @param statehistory The logs of events and actions leading to this state
//	 * @param onofflatch A countdown latch used to synchonize completion
//	 */
//	public final void acceptTerminalState(State.StateView newstate, History.HistoryView statehistory, CountDownLatch onofflatch)
//	{
//		terminalStep(newstate,statehistory);
//		onofflatch.countDown();
//	}
	

	/**
	 * Accept the initial state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract Map<Integer,Action> initialStep(State.StateView newstate, History.HistoryView statehistory);

	/**
	 * Accept an intermediate state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract Map<Integer,Action> middleStep(State.StateView newstate, History.HistoryView statehistory);
	/**
	 * Receive notification that the episode has terminated.
	 * @param newstate The final state of the system
	 */
	public abstract void terminalStep(State.StateView newstate, History.HistoryView statehistory);
	
	
}
