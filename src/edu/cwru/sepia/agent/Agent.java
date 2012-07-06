/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.agent;
import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.visual.VisualLog;
import edu.cwru.sepia.environment.History;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.util.Configuration;
/**
 * The base type for any agent that can interact with the Sepia environment.
 * @author Tim
 *
 */
public abstract class Agent implements Serializable {
	private static final long	serialVersionUID	= 1L;
	public static final int OBSERVER_ID = -999;
	
	/*
	 * An optional, lazily instantiated logger that writes to a Swing GUI
	 */
	protected VisualLog visualLog;
	protected boolean verbose; 
	protected final int playernum;
	protected Configuration configuration;
	// map: agentID -> flag, if this flag set false, then we will ignore this agent when checking terminal condition. 
	
	
	/**
	 * Create a new Agent to control a player.
	 * @param playernum The player number controlled by this agent.
	 */
	public Agent(int playernum) {
		this.playernum = playernum;
		verbose = false;
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * Write a line to the visual log maintained by this agent.
	 * If it is not initialized, this calls initializeVisualAgent.
	 * @param newline The line to write
	 */
	public void writeLineVisual(String newline)
	{
		if (visualLog == null)
			initializeVisualLog();
		visualLog.writeLine(newline);
		visualLog.repaint();
	}
	/**
	 * Clear the visual log maintained by this agent.
	 * If it is not initialized, this calls initializeVisualAgent.
	 */
	public void clearVisualLog()
	{
		if (visualLog == null)
			initializeVisualLog();
		visualLog.clearLog();
		visualLog.repaint();
	}
	/**
	 * If the visual log is initialized, then close the window and release the reference.
	 */
	public void closeVisualLog()
	{
		if (visualLog!=null)
		{
			visualLog.setVisible(false);
			visualLog.dispose();
			visualLog=null;
		}
	}
	/**
	 * Initialize the visual log, constructing it.
	 */
	protected void initializeVisualLog()
	{
		if (visualLog==null)
			visualLog = new VisualLog(this.toString(), 300, 300);
		visualLog.writeLine("This log belongs to "+this.toString());
	}
	/**
	 * Resize the visual log to a new size.
	 * @param width
	 * @param height
	 */
	public void setVisualLogDimensions(int width, int height)
	{
		visualLog.setPreferredSize(new Dimension(width,height));
	}
	/**
	 * Return the usage of any additional parameters
	 * @return
	 */
	public static String getUsage() {
		return "Unknown, it was not defined";
	}
	/**
	 * Determines whether to print out the action list each time it is chosen by {@link #getAction()}
	 * @param verbosity
	 */
	public void setVerbose(boolean verbosity) {
		verbose = verbosity;
	}
	/**
	 * Get the player number that this agent controls
	 * @return
	 */
	public int getPlayerNumber() {
		return playernum;
	}

	/**
	 * Accept the initial state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract Map<Integer,Action> initialStep(State.StateView newstate, History.HistoryView statehistory);

	/**
	 * Accept an intermediate state of an episode
	 * @param newstate The new state of the system
	 * @param onofflatch A countdown latch used to synchonize completion
	 */
	public abstract Map<Integer,Action> middleStep(State.StateView newstate, History.HistoryView statehistory);
	/**
	 * Receive notification that the episode has terminated.
	 * @param newstate The final state of the system
	 */
	public abstract void terminalStep(State.StateView newstate, History.HistoryView statehistory);
	
	/**
	 * Save data accumulated by the agent.
	 * @see {@link #loadPlayerData(InputStream)}
	 * @param os An output stream, such as to a file.
	 */
	public abstract void savePlayerData(OutputStream os);
	/**
	 * Load data stored by the agent.
	 * @see {@link #savePlayerData(OutputStream)}
	 * @param is An input stream, such as from a file.
	 */
	public abstract void loadPlayerData(InputStream is);
}
