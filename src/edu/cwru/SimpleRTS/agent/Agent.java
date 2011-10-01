package edu.cwru.SimpleRTS.agent;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.ImmutableMap;


import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
/**
 * The base type for any agent that can interact with the SimpleRTS environment.
 * @author Tim
 *
 */
public abstract class Agent implements Serializable {
	protected boolean verbose; 
	private static int nextID = 0;
	protected final int playernum;
	/**
	 * The highest ID number that has been assigned to an agent
	 * @return
	 */
	public static int maxId() {
		return nextID - 1;
	}
	protected final int ID;
	/**
	 * Assigns this Agent the next available auto-incrementing ID and sets the playernum to the argument.
	 * @param playernum
	 */
	protected Agent(int playernum) {
		ID = nextID++;
		this.playernum=playernum;
		verbose = false;
	}
	/**
	 * Determines whether to print out the action list each time it is chosen by {@link #getAction()}
	 * @param verbosity
	 */
	public void setVerbose(boolean verbosity) {
		verbose = verbosity;
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
	private ImmutableMap.Builder<Integer,Action> chosenActions;
	
	/**
	 * Request the action that the agent will take at this timestep.
	 * The agent should have been asynchronously calculating this.
	 * Must not be called until calculation is done.  You will know that it is done by the latch being passed to acceptState()
	 * @return
	 */
	public final ImmutableMap<Integer,Action> getAction()
	{
		ImmutableMap<Integer, Action> actions = chosenActions.build();
		System.out.println("Agent "+playernum+" is performing actions:");
		if (verbose) {
			Set<Integer> units = actions.keySet();
			for (Integer i : units) {
				System.out.println("\t" + i + " " + actions.get(i));
			}
		}
		return actions;
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
	public abstract ImmutableMap.Builder<Integer,Action> initialStep(State.StateView newstate);

	/**
	 * Accept an intermediate state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract ImmutableMap.Builder<Integer,Action> middleStep(State.StateView newstate);
	/**
	 * Receive notification that the episode has terminated.
	 * @param newstate The final state of the system
	 */
	public abstract void terminalStep(State.StateView newstate);
}
