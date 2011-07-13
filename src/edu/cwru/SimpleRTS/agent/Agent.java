package edu.cwru.SimpleRTS.agent;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.action.*;
import java.util.concurrent.*;

import com.google.common.collect.ImmutableMap;
public abstract class Agent {
	private static int nextID = 0;
	public static int maxId() {
		return nextID - 1;
	}
	protected final int ID;
	
	protected Agent() {
		ID = nextID++;
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
		return chosenActions.build();
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
	
	public abstract ImmutableMap.Builder<Integer,Action> initialStep(State.StateView newstate);
	public abstract ImmutableMap.Builder<Integer,Action> middleStep(State.StateView newstate);
	public abstract void terminalStep(State.StateView newstate);
}
