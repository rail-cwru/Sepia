package edu.cwru.SimpleRTS.agent;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.action.*;
import java.util.concurrent.*;
public abstract class Agent {
	private static int nextID = 0;
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
	private Action chosenaction;
	
	/**
	 * Request the action that the agent will take at this timestep.
	 * The agent should have been asynchronously calculating this.
	 * Must not be called until calculation is done.  You will know that it is done by the latch being passed to acceptState()
	 * @return
	 */
	public final Action getAction()
	{
		return chosenaction;
	}
	/**
	 * Accept the first state of an episode and begin calculating a response for it
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public final void acceptInitialState(State.StateView newstate, CountDownLatch onofflatch)
	{
		chosenaction = initialStep(newstate);
		onofflatch.countDown();
	}
	/**
	 * Accept a state and begin calculating a response for it
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public final void acceptMiddleState(State.StateView newstate, CountDownLatch onofflatch)
	{
		chosenaction = middleStep(newstate);
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
	
	public abstract Action initialStep(State.StateView newstate);
	public abstract Action middleStep(State.StateView newstate);
	public abstract void terminalStep(State.StateView newstate);
}
