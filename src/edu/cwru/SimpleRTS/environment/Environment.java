package edu.cwru.SimpleRTS.environment;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.State.StateView;
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
		//grab states and histories
		StateView states[] = new StateView[connectedagents.length];
		History.HistoryView histories[] = new History.HistoryView[connectedagents.length];
		AgentThreader[] actioncalculators= new AgentThreader[connectedagents.length];
		for(int i = 0; i<connectedagents.length;i++)
		{
			int playerNumber = connectedagents[i].getPlayerNumber();
			states[i] = model.getState(playerNumber);
			histories[i] = model.getHistory(playerNumber);
		}
		for(int i = 0; i<connectedagents.length;i++)
		{
			actioncalculators[i]= new AgentThreader(connectedagents[i], step==0?WhichStep.INITIAL:WhichStep.MIDDLE, states[i], histories[i]);
		}
//		for (Thread t : Thread.getAllStackTraces().keySet())
//		{
////			if (t.getName().contains("edu"))
//				System.out.println("\t"+t.getName()+"\t"+t.getId()+"\t"+ManagementFactory.getThreadMXBean().getThreadCpuTime(t.getId()));
//		}
		for (int i = 0; i<connectedagents.length; i++)
		{
			Map<Integer,Action> actionMapTemp = actioncalculators[i].getActions();
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
	private static enum WhichStep{
		INITIAL,MIDDLE,TERMINAL;
	}
	/**
	 * A class that starts a thread to run Agent.initialStep, Agent.middleStep, or Agent.terminalStep
	 *
	 */
	private static class AgentThreader {
		/**
		 * How many milliseconds to wait before ignoring the agent and forging ahead.  Negative means that it will wait forever.
		 * May cause concurrency problems in Agents if they aren't well prepared, or, potentially, a pileup of threads
		 */
		private static final long maximumtimetowait=-1;
		private final long timestarted;
		private final CountDownLatch latch;
		private final State.StateView newstate;
		private final History.HistoryView statehistory;
		private final Agent agent;
		private Map<Integer,Action> actions;
		public AgentThreader(Agent agentin, WhichStep type, State.StateView newstatein, History.HistoryView statehistoryin)
		{
			this.newstate = newstatein;
			this.statehistory = statehistoryin;
			this.latch = new CountDownLatch(1);
			this.agent = agentin;
			switch (type)
			{
			case MIDDLE:
			{
				Thread t =new Thread(new Runnable(){public void run(){actions=agent.middleStep(newstate, statehistory);latch.countDown();}});
				t.setName(agent.toString());
				t.start();
				break;
			}
			case INITIAL:
			{
				Thread t = new Thread(new Runnable(){public void run(){actions=agent.initialStep(newstate, statehistory);latch.countDown();}});
				t.setName(agent.toString());
				t.start();
				break;
			}
			case TERMINAL:
			{
				Thread t = new Thread(new Runnable(){public void run(){agent.terminalStep(newstate, statehistory);latch.countDown();}});
				t.setName(agent.toString());
				t.start();
				break;
			}
			}
			timestarted = System.currentTimeMillis();
		}
		/**
		 * Waits for the agent step to finish, then returns the actions if the step was not terminal
		 * @return The actions if the step was not terminal, null if the step was terminal
		 */
		public Map<Integer,Action> getActions()
		{
			try {
				if (maximumtimetowait>=0)
				{
					latch.await(maximumtimetowait-(System.currentTimeMillis()-timestarted), TimeUnit.MILLISECONDS);
				}
				else
				{
					latch.await();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if (latch.getCount()==0)
				return actions;
			else
				return new HashMap<Integer,Action>();
		}
	}
}
