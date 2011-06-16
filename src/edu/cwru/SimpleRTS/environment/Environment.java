package edu.cwru.SimpleRTS.environment;
import edu.cwru.SimpleRTS.agent.*;
import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.model.*;
import java.util.concurrent.*;
import java.io.*;
import edu.cwru.SimpleRTS.environment.configuration.Configuration;
public abstract class Environment
{
	public abstract void requestTermination();
	public abstract void requestNewEpisode();
	
	
	private Agent[] connectedagents;
	private Model model;
	private Configuration configuration;
	public Environment(Agent[] connectedagents, Model model, Configuration configuration)
	{
		this.connectedagents = connectedagents;
		this.model = model;
		this.configuration = configuration;
	}
	/**
	 * A basic save
	 * Feel free to change the argument
	 * @param w
	 * @return
	 */
	public abstract boolean saveState(BufferedWriter w);
	/**
	 * A basic load
	 * Feel free to change the argument
	 * @param r
	 * @return
	 */
	public abstract boolean loadState(BufferedReader r);
	public final State getState()
	{
		return model.getState();
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
