package edu.cwru.SimpleRTS.environment;

import edu.cwru.SimpleRTS.log.ActionLogger;
import edu.cwru.SimpleRTS.log.ActionResultLogger;
import edu.cwru.SimpleRTS.log.EventLogger;
import edu.cwru.SimpleRTS.util.DeepEquatable;
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
	private ActionLogger actionsExecuted;
	private ActionResultLogger actionProgress;
	public PlayerHistory(int playerNumber)
	{
		this.playerNumber = playerNumber;
		this.eventsSeen = new EventLogger();
		this.actionsExecuted = new ActionLogger();
		this.commandsIssued = new ActionLogger();
		this.actionProgress = new ActionResultLogger();
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
	public ActionLogger getActionsExecuted()
	{
		return actionsExecuted;
	}
	/**
	 * Directly set the actions executed log.
	 * Useful for saving/loading.
	 * @param actionsExecuted
	 */
	public void setActionsExecuted(ActionLogger actionsExecuted) {
		this.actionsExecuted = actionsExecuted;
	}
	public ActionResultLogger getActionProgress()
	{
		return actionProgress;
	}
	/**
	 * Directly set the action progress/results log.
	 * Useful for saving/loading.
	 * @param actionProgress
	 */
	public void setActionProgress(ActionResultLogger actionProgress) {
		this.actionProgress = actionProgress;
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
			boolean thisnull = this.actionsExecuted == null;
			boolean othernull = o.actionsExecuted == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!actionsExecuted.deepEquals(o.actionsExecuted))
					return false;
			}
		}
		{
			boolean thisnull = this.actionProgress == null;
			boolean othernull = o.actionProgress == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!actionProgress.deepEquals(o.actionProgress))
					return false;
			}
		}
		
		return true;
	}
	
	
}
