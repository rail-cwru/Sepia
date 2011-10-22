package edu.cwru.SimpleRTS.environment;
import edu.cwru.SimpleRTS.agent.*;
import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.model.*;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.io.*;

import javax.swing.SpringLayout.Constraints;

import com.google.common.collect.ImmutableMap;
public class Environment
{
	public void requestTermination() {
		
	}
	public void requestNewEpisode() {
		step = 0;
	}
	
	
	private Agent[] connectedagents;
	private Model model;
	private int step;
	public Environment(Agent[] connectedagents, Model model)
	{
		this.connectedagents = connectedagents;
		this.model = model;
	}
	
	public final Agent[] getAgents() {
		return connectedagents;
	}
	
	public final Model getModel() {
		return model;
	}
	
	public final State.StateView getState()
	{
		return model.getState();
	}
	public final void runEpisode() throws InterruptedException
	{
		model.createNewWorld();
		while(!model.isTerminated())
		{
			step();
		}
		for (int i = 0; i<connectedagents.length;i++)
		{
			connectedagents[i].terminalStep(model.getState());
		}
		
	}
	public boolean isTerminated() {
		return model.isTerminated();
	}
	public boolean step() throws InterruptedException {
		ArrayList<Action> actions = new ArrayList<Action>(model.getState().getAllUnitIds().size());
		for(int i = 0; i<connectedagents.length;i++)
		{
			CountDownLatch latch = new CountDownLatch(1);
			if (step == 0)
			{
				connectedagents[i].acceptInitialState(model.getState(), latch);
			}
			else
			{
				connectedagents[i].acceptMiddleState(model.getState(), latch);
			}
			latch.await();
			ImmutableMap<Integer,Action> actionMap = connectedagents[i].getAction();
			for(Integer unitId : actionMap.keySet())
			{
				Action a = actionMap.get(unitId);
				//If the unit is not the same as in the action, ignore the action
				if (a.getUnitId() != unitId)
					continue;
				//If the unit does not exist, ignore the action
				if (model.getState().getUnit(unitId) == null)
					continue;
				//If the unit is not the player's, ignore the action
				if(model.getState().getUnit(unitId).getPlayer() != connectedagents[i].getPlayerNumber())
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
