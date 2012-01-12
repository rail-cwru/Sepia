package edu.cwru.SimpleRTS.agent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
/**
 * The base type for any agent that can interact with the SimpleRTS environment.
 * @author Tim
 *
 */
public abstract class Agent implements Serializable {
	protected boolean verbose; 
	public static final int OBSERVER_ID = -999;
	private static int nextID = 0;
	/**
	 * Doesn't really need to be called usually, as you construct agents when you run the main method, and they are not part of the state
	 * @param minID
	 */
	public static void reserveIDsUpTo(int minID)
	{
		if (nextID <= minID)
			nextID = minID+1;
	}
//	private final int[] enemynums;
	protected final int playernum;
	// map: agentID -> flag, if this flag set false, then we will ignore this agent when checking terminal condition. 
	
	
	protected final int ID;
	/**
	 * Assigns this Agent the next available auto-incrementing ID and sets the playernum to the argument.
	 * @param playernum
	 */
	protected Agent(int playernum) {
		this(playernum, true);
	}
	
	protected Agent(int playernum , boolean countsTowardTermination) {
		ID = nextID++;
		this.playernum=playernum;
//		this.enemynums = new int[enemynums.length];
//		System.arraycopy(enemynums, 0, this.enemynums, 0, enemynums.length);
		verbose = false;
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
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Agent))
			return false;
		return ID == ((Agent)o).ID;
	}
	
	
	//Action selection and such
	protected Map<Integer,Action> chosenActions;
	
	/**
	 * Request the action that the agent will take at this timestep.
	 * The agent should have been asynchronously calculating this.
	 * Must not be called until calculation is done.  You will know that it is done by the latch being passed to acceptState()
	 * @return
	 */
	public final Map<Integer,Action> getAction()
	{
		
		if (verbose) {
			System.out.println("Agent "+playernum+" is performing actions:");
			Set<Integer> units = chosenActions.keySet();
			for (Integer i : units) {
				System.out.println("\t" + i + " " + chosenActions.get(i));
			}
		}
		return chosenActions;
	}
	/**
	 * Accept the first state of an episode and begin calculating a response for it
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public final void acceptInitialState(State.StateView newstate, CountDownLatch onofflatch)
	{
		chosenActions = initialStep(newstate);
		onofflatch.countDown();
	}
	/**
	 * Accept a state and begin calculating a response for it
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public final void acceptMiddleState(State.StateView newstate, CountDownLatch onofflatch)
	{
		chosenActions = middleStep(newstate);
		onofflatch.countDown();
	}
	/**
	 * Accept the final state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public final void acceptTerminalState(State.StateView newstate, CountDownLatch onofflatch)
	{
		terminalStep(newstate);
		onofflatch.countDown();
	}

	/**
	 * Accept the initial state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract Map<Integer,Action> initialStep(State.StateView newstate);

	/**
	 * Accept an intermediate state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract Map<Integer,Action> middleStep(State.StateView newstate);
	/**
	 * Receive notification that the episode has terminated.
	 * @param newstate The final state of the system
	 */
	public abstract void terminalStep(State.StateView newstate);
}
