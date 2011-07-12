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
		
	}
	
	
	private Agent[] connectedagents;
	private Model model;
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
	
	
	/**
	 * A basic save
	 * Feel free to change the argument
	 * @param w
	 * @return
	 */
	public boolean saveState(BufferedWriter w) {
		return false;
	}
	/**
	 * A basic load
	 * Feel free to change the argument
	 * @param r
	 * @return
	 */
	public boolean loadState(BufferedReader r) {
		return false;		
	}
	public final State.StateView getState()
	{
		return model.getState();
	}
	public final void runEpisode()
	{
		boolean first = true;
		ArrayList<Action> actions = new ArrayList<Action>(model.getState().getAllUnitIds().size());
		model.createNewWorld();
		while(!model.isTerminated())
		{
			for(int i = 0; i<connectedagents.length;i++)
			{
				CountDownLatch latch = new CountDownLatch(1);
				if (first)
				{
					connectedagents[i].acceptInitialState(model.getState(), latch);
					first = false;
				}
				else
				{
					connectedagents[i].acceptMiddleState(model.getState(), latch);
				}
				try
				{
					latch.await();
				}
				catch(InterruptedException e)
				{
					//TODO: handle this somehow
				}
				ImmutableMap<Integer,Action> actionMap = connectedagents[i].getAction();
				for(Integer unitId : actionMap.keySet())
				{
					if(model.getState().getUnit(unitId).getPlayer() != i)
						continue;
					Action a = actionMap.get(unitId);
				}
			}
			model.setActions(actions.toArray(new Action[0]));
			model.executeStep();
			actions.clear();
		}
		for (int i = 0; i<connectedagents.length;i++)
		{
			connectedagents[i].terminalStep(model.getState());
		}
		
	}
}
