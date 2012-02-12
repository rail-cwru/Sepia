package edu.cwru.SimpleRTS.environment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.model.Model;
public class Environment
{
	public void forceNewEpisode() {
		step = 0;
		model.createNewWorld();
	}
	
	
	private Agent[] connectedagents;
	private Model model;
	private int step;
	public Environment(Agent[] connectedagents, Model model)
	{
		this.connectedagents = connectedagents;
		this.model = model;
	}
	
	/*
	 * I removed these because it seemed like a security hole
	 */
//	public final Agent[] getAgents() {
//		return connectedagents;
//	}
//	
//	public final Model getModel() {
//		return model;
//	}
//	
//	public final State.StateView getState()
//	{
//		return model.getState();
//	}
	public final void runEpisode() throws InterruptedException
	{
		model.createNewWorld();
		step = 0;
		while(!isTerminated())
		{
			step();
		} 
		for (int i = 0; i<connectedagents.length;i++)
		{
			int playerNumber = connectedagents[i].getPlayerNumber();
			connectedagents[i].terminalStep(model.getState(playerNumber), model.getHistory(playerNumber));
		}
		
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
		CountDownLatch[] latches = new CountDownLatch[connectedagents.length];
		for(int i = 0; i<connectedagents.length;i++)
		{
			int playerNumber = connectedagents[i].getPlayerNumber();
			latches[i]= new CountDownLatch(1);
			if (step == 0)
			{
				connectedagents[i].acceptInitialState(model.getState(playerNumber), model.getHistory(playerNumber), latches[i]);
			}
			else
			{
				connectedagents[i].acceptMiddleState(model.getState(playerNumber), model.getHistory(playerNumber), latches[i]);
			}
			
		}
		for (int i = 0; i<connectedagents.length; i++)
		{
			latches[i].await();
			Map<Integer,Action> actionMapTemp = connectedagents[i].getAction();
			Map<Integer,Action> actionMap = new HashMap<Integer,Action>();
			for(Integer key : actionMapTemp.keySet())
			{
				actionMap.put(key,actionMapTemp.get(key));
			}
			model.addActions(actionMap, connectedagents[i].getPlayerNumber());
		}
		model.executeStep();
		step++;
		return model.isTerminated();
	}
	public int getStepNumber() {
		return step;
	}
}
