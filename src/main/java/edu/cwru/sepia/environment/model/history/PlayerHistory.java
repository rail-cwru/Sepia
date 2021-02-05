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
package edu.cwru.sepia.environment.model.history;

import edu.cwru.sepia.util.DeepEquatable;
/**
 * The history specific to a player.
 * Contains the events seen by the player, as well as loggers related to the actions of the corresponding player.
 * @author The Condor
 *
 */
public class PlayerHistory implements DeepEquatable
{
	final int playerNumber;
	private EventLogger eventsSeen;
	private ActionLogger commandsIssued;
	private ActionResultLogger primitiveFeedback;
	private ActionResultLogger commandFeedback;
	public PlayerHistory(int playerNumber)
	{
		this.playerNumber = playerNumber;
		this.eventsSeen = new EventLogger();
		this.primitiveFeedback = new ActionResultLogger();
		this.commandsIssued = new ActionLogger();
		this.commandFeedback = new ActionResultLogger();
		this.eventsSeen = new EventLogger();
	}
	public ActionLogger getCommandsIssued()
	{
		return commandsIssued;
	}
	/**
	 * Directly set the commands issued.
	 * Useful for saving/loading
	 * @param commandsIssued
	 */
	public void setCommandsIssued(ActionLogger commandsIssued) {
		this.commandsIssued = commandsIssued;
	}
	public ActionResultLogger getPrimitiveFeedback()
	{
		return primitiveFeedback;
	}
	/**
	 * Directly set the actions executed log.
	 * Useful for saving/loading.
	 * @param actionsExecuted
	 */
	public void setPrimitivesExecuted(ActionResultLogger actionsExecuted) {
		this.primitiveFeedback = actionsExecuted;
	}
	public ActionResultLogger getCommandFeedback()
	{
		return commandFeedback;
	}
	/**
	 * Directly set the action progress/results log.
	 * Useful for saving/loading.
	 * @param commandFeedback
	 */
	public void setCommandFeedback(ActionResultLogger commandFeedback) {
		this.commandFeedback = commandFeedback;
	}
	public EventLogger getEventLogger()
	{
		return eventsSeen;
	}
	/**
	 * Directly set the event logger.
	 * Useful for saving/loading.
	 * @param eventLogger
	 */
	public void setEventLogger(EventLogger eventLogger) {
		this.eventsSeen = eventLogger;
	}
	/**
	 * Get the number of the player whose history is recorded here.
	 * @return
	 */
	public int getPlayerNumber()
	{
		return playerNumber;
	}
	public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		PlayerHistory o = (PlayerHistory)other;
		if (this.playerNumber != o.playerNumber)
			return false;
		{
			boolean thisnull = this.eventsSeen == null;
			boolean othernull = o.eventsSeen == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!eventsSeen.deepEquals(o.eventsSeen))
					return false;
			}
		}
		{
			boolean thisnull = this.commandsIssued == null;
			boolean othernull = o.commandsIssued == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!commandsIssued.deepEquals(o.commandsIssued))
					return false;
			}
		}
		{
			boolean thisnull = this.primitiveFeedback == null;
			boolean othernull = o.primitiveFeedback == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!primitiveFeedback.deepEquals(o.primitiveFeedback))
					return false;
			}
		}
		{
			boolean thisnull = this.commandFeedback == null;
			boolean othernull = o.commandFeedback == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!commandFeedback.deepEquals(o.commandFeedback))
					return false;
			}
		}
		
		return true;
	}
	
	
}
