package edu.cwru.SimpleRTS.environment;

import edu.cwru.SimpleRTS.Log.ActionLogger;
import edu.cwru.SimpleRTS.Log.ActionResultLogger;
import edu.cwru.SimpleRTS.Log.EventLogger;
/**
 * The history specific to a player.
 * Contains the events seen by the player, as well as loggers related to the actions of the corresponding player.
 * @author The Condor
 *
 */
public class PlayerHistory
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
	public ActionLogger getActionsExecuted()
	{
		return actionsExecuted;
	}
	public ActionResultLogger getActionProgress()
	{
		return actionProgress;
	}
	public EventLogger getEventLogger()
	{
		return eventsSeen;
	}
}
