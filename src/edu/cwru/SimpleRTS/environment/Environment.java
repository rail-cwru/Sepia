package edu.cwru.SimpleRTS.environment;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.agent.ThreadIntermediary;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.LessSimpleModel;
import edu.cwru.SimpleRTS.model.Model;

/**
 * Simulates the environment component in a standard Reinforcement Learning setting. 
 * One can run episodes from this class.
 *
 */
public class Environment
{
	private static final Logger logger = Logger.getLogger(Environment.class.getCanonicalName());
	
	public void forceNewEpisode() {
		step = 0;
		model.createNewWorld();
		turnTracker.newEpisodeAndStep();
	}
	
	private final int DELAY_MS;
	private Agent[] connectedagents;
	private ThreadIntermediary[] agentIntermediaries;
	private Model model;
	private int step;
	private TurnTracker turnTracker;
	public Environment(Agent[] connectedagents, Model model, int seed) {
		this(connectedagents, model, readTurnTrackerFromPrefs(seed));
	}
	/**
	 * Do some reflection to read the type of tracker to use and call it's constructor with a random if possible, and failing that, no argument.
	 * @param seed
	 * @return
	 */
	private static TurnTracker readTurnTrackerFromPrefs(int seed) {
		TurnTracker toReturn=null;
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS").node("environment");
		String trackerName = prefs.get("TurnTracker","edu.cwru.SimpleRTS.environment.SimultaneousTurnTracker");
		Class<?> trackerClass = null;
		try 
		{
			trackerClass = Class.forName(trackerName);
		} 
		catch (ClassNotFoundException e) 
		{
			logger.log(Level.SEVERE, "Unable to find class for TurnTracker " + trackerName, e);
		}
		try 
		{
			toReturn = (TurnTracker)trackerClass.getConstructor(Random.class).newInstance(new Random(seed));
		} 
		catch (Exception e) 
		{
			try 
			{
				toReturn = (TurnTracker)trackerClass.getConstructor().newInstance();
			} 
			catch (Exception e1) 
			{
				logger.log(Level.SEVERE, "Unable to create an instance of TurnTracker " + trackerName, e);
			}
		}
		if (toReturn == null)
		{
			logger.info("No TurnTracker was specified or instantiation failed; using SimultaneousTurnTracker");
			return new SimultaneousTurnTracker(new Random(seed));
		}
		else
			return toReturn;
	}
	public Environment(Agent[] connectedagents, Model model, TurnTracker turnTracker) {
		this.connectedagents = connectedagents;
		agentIntermediaries = new ThreadIntermediary[connectedagents.length];
		for (int ag = 0; ag < connectedagents.length; ag++)
		{
			agentIntermediaries[ag] = new ThreadIntermediary(connectedagents[ag]);
			new Thread(agentIntermediaries[ag]).start();
		}
		this.model = model;
		
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS").node("environment");
		DELAY_MS=prefs.getInt("InterruptTime",-1);
		this.turnTracker = turnTracker;
		Integer[] players = model.getState().getView(Agent.OBSERVER_ID).getPlayerNumbers();
		for (Integer player : players)
		{
			turnTracker.addPlayer(player);
		}
		turnTracker.newEpisodeAndStep();
		model.setTurnTracker(turnTracker);
	}
	public final Agent[] getAgents() {
		return connectedagents;
	}
	
	public final Model getModel() {
		return model;
	}
	
	public final void runEpisode() throws InterruptedException
	{
		logger.fine("Running a new episode");
		forceNewEpisode();
		
		while(!isTerminated())
		{
			step();
		} 
		terminalStep();
		logger.fine("Episode terminated");
	}
	public boolean isTerminated() {
		return model.isTerminated();
	}
	/**
	 * Step through an episode
	 * @return Return whether it has terminated.
	 * @throws InterruptedException
	 */
	public boolean step() throws InterruptedException {
		//grab states and histories
		StateView[] states = new StateView[connectedagents.length];
		History.HistoryView[] histories = new History.HistoryView[connectedagents.length];
		CountDownLatch[] actionLatches = new CountDownLatch[connectedagents.length];
		boolean[] isAgentsTurn = new boolean[connectedagents.length];
		long[] endTimes = new long[connectedagents.length];
		for(int ag = 0; ag<connectedagents.length;ag++)
		{
			isAgentsTurn[ag]=turnTracker.isAgentsTurn(connectedagents[ag]);
			if (isAgentsTurn[ag])
			{
				int playerNumber = connectedagents[ag].getPlayerNumber();
				states[ag] = model.getState().getView(playerNumber);
				histories[ag] = model.getHistory().getView(playerNumber);
				endTimes[ag] = System.currentTimeMillis() + DELAY_MS;
			}
		}
		//And run them
		for (int ag = 0; ag<connectedagents.length; ag++)
		{
			if (isAgentsTurn[ag])
			{
				if(logger.isLoggable(Level.FINER))
				{
					logger.finer("Step " + step + ": Agent with player number: " + 
								 connectedagents[ag].getPlayerNumber() + "'s turn.  " + 
								 (turnTracker.hasHadTurnBefore(connectedagents[ag].getPlayerNumber()) ? 
										 "Has had turn" : 
										 "First turn"));
				}
				actionLatches[ag] = agentIntermediaries[ag].submitState(states[ag], histories[ag], turnTracker.hasHadTurnBefore(connectedagents[ag].getPlayerNumber())?ThreadIntermediary.StateType.MIDDLE:ThreadIntermediary.StateType.INITIAL);
			}
		}
		for (int ag = 0; ag<connectedagents.length; ag++)
		{
			if (isAgentsTurn[ag])
			{
				//Wait for the actions to be ready
				if (DELAY_MS >= 0)
				{
					//if there is a positive delay, only give it that long to process
					actionLatches[ag].await(endTimes[ag] - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
				}
				else
				{
					//if the delay is negative (IE: nonsense), wait as long as you need
					actionLatches[ag].await();
				}
				
				//Get the responses
				Map<Integer,Action> actionMapTemp = agentIntermediaries[ag].retrieveActions();
				if (actionMapTemp != null) //If there were responses
				{
					Map<Integer,Action> actionMap = new HashMap<Integer,Action>();
					for(Integer key : actionMapTemp.keySet())
					{
						actionMap.put(key,actionMapTemp.get(key));
					}
					model.addActions(actionMap, connectedagents[ag].getPlayerNumber());
				}
			}
		}
		logger.fine("Executing one step of the model");
		model.executeStep();
		step++;
		logger.fine("Notifying TurnTracker of new step");
		turnTracker.newStep();
		return model.isTerminated();
	}
	public final void terminalStep() {
		logger.fine("Notifying agents of terminal step");
		for (int i = 0; i<connectedagents.length;i++)
		{
			int playerNumber = connectedagents[i].getPlayerNumber();
			connectedagents[i].terminalStep(model.getState().getView(playerNumber), model.getHistory().getView(playerNumber));
		}
	}
	public int getStepNumber() {
		return step;
	}
}
