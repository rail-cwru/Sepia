package edu.cwru.SimpleRTS.environment;
import edu.cwru.SimpleRTS.agent.*;
import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.model.*;
import java.util.concurrent.*;
public abstract class Environment
{
	public abstract void requestTermination();
	public abstract void requestNewEpisode();
	
	
	private Agent[] connectedagents;
	private Model model;
	public Environment(Agent[] connectedagents, Model model)
	{
		this.connectedagents = connectedagents;
		this.model = model;
	}
	public final void initializeEpisode()
	{
		
	}
	public final void doStep(Action[] action)
	{
		//TODO: update the model with an action
	}
	public final void terminateEpisode()
	{
		//TODO: 
	}
	public final void runEpisode()
	{
		boolean first = true;
		Action[] actions;
		model.createNewWorld();
		while(!model.isTerminated())
		{
			actions = new Action[connectedagents.length];//Just to be safe, put it in a new pointer
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
				actions[i] = connectedagents[i].getAction();
			}
			model.executeActions(actions);
		}
		for (int i = 0; i<connectedagents.length;i++)
		{
			connectedagents[i].terminalStep(model.getState());
		}
		
	}
}
