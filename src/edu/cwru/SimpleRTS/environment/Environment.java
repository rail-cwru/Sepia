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
			connectedagents[i].terminalStep(model.getState(connectedagents[i].getPlayerNumber()));
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
		ArrayList<Action> actions = new ArrayList<Action>(model.getState(Agent.OBSERVER_ID).getAllUnitIds().size());
		for(int i = 0; i<connectedagents.length;i++)
		{
			CountDownLatch latch = new CountDownLatch(1);
			if (step == 0)
			{
				connectedagents[i].acceptInitialState(model.getState(connectedagents[i].getPlayerNumber()), latch);
			}
			else
			{
				connectedagents[i].acceptMiddleState(model.getState(connectedagents[i].getPlayerNumber()), latch);
			}
			latch.await();
			Map<Integer,Action> actionMapTemp = connectedagents[i].getAction();
			Map<Integer,Action> actionMap = new HashMap<Integer,Action>();
			for(Integer key : actionMapTemp.keySet())
			{
				actionMap.put(key,actionMapTemp.get(key));
			}
			
			for(Integer unitId : actionMap.keySet())
			{
				Action a = actionMap.get(unitId);
				//If the unit is not the same as in the action, ignore the action
				if (a.getUnitId() != unitId)
					continue;
				//If the unit does not exist, ignore the action
				if (model.getState(Agent.OBSERVER_ID).getUnit(unitId) == null)
					continue;
				//If the unit is not the player's, ignore the action
					if(model.getState(Agent.OBSERVER_ID).getUnit(unitId).getTemplateView().getPlayer() != connectedagents[i].getPlayerNumber())
						continue;
				
				actions.add(a);
			}
		}
		model.setActions(actions.toArray(new Action[0]));
		model.executeStep();
		step++;
		return model.isTerminated();
	}
	public int getStepNumber() {
		return step;
	}
}
