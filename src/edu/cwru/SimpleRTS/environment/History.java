package edu.cwru.SimpleRTS.environment;

import java.util.HashMap;
import java.util.Map;

import edu.cwru.SimpleRTS.Log.ActionLogger.ActionLoggerView;
import edu.cwru.SimpleRTS.Log.ActionResultLogger.ActionResultLoggerView;
import edu.cwru.SimpleRTS.Log.EventLogger.EventLoggerView;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public class History {
	//partial observability for actions, needn't be the same as for states (state partial observability determines eventlogger stuff)
	private boolean fogOfWar;
	private Map<Integer,PlayerHistory> playerHistories;
	//A history available to observers, stores everything that happened
	private PlayerHistory observerHistory;
	public History()
	{
		playerHistories = new HashMap<Integer, PlayerHistory>();
		observerHistory = new PlayerHistory(Agent.OBSERVER_ID);
	}
	public void addPlayer(int i) {
		playerHistories.put(i, new PlayerHistory(i));
	}
//	
//	public void setRevealedResources(boolean revealedResources) {
//		if (revealedResources) {
//			//only need to do something if it is a change, or you risk duplicates
//			if (!this.revealedResources)
//			{
//				this.revealedResources = true;
//				for (ResourceNode resource : resourceNodes) {
//					revealResource(resource);
//				}
//			}
//		}
//		else {
//			this.revealedResources = false;
//			for(PlayerState s : playerStates.values())
//			{
//				s.getEventLogger().eraseResourceNodeReveals();
//			}
//			observerState.getEventLogger().eraseResourceNodeReveals();
//		}
//	}
//	public boolean getRevealedResources()
//	{
//		return revealedResources;
//	}
//	private void revealResource(ResourceNode resource) {
//		for(PlayerState s : playerStates.values())
//		{
//			s.getEventLogger().recordResourceNodeReveal(resource.getxPosition(), 
//														resource.getyPosition(), 
//														resource.getType());
//		}
//		observerState.getEventLogger().recordResourceNodeReveal(resource.getxPosition(), 
//																resource.getyPosition(), 
//																resource.getType());
//	}
	
	public void recordPrimitiveExecuted(int player, int stepnumber, Action actionExecuted)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getActionsExecuted().addAction(stepnumber, actionExecuted);
	}
	public void recordCommandRecieved(int player, int stepnumber, Action actionRecieved)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getCommandsIssued().addAction(stepnumber, actionRecieved);
	}
	public void recordActionFeedback(int player, int stepnumber, ActionResult actionFeedback)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getActionProgress().addActionResult(stepnumber, actionFeedback);
	}
	public void recordBirth(Unit newunit, Unit builder, State state) {
		int x = newunit.getxPosition();
		int y = newunit.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player))
			{
				getEventLog(player).recordBirth(newunit.ID, builder.ID, newunit.getPlayer());
			}
		}*/
		for(PlayerHistory playerHistory : playerHistories.values())
		{
			if (state.canSee(x, y, playerHistory.playerNumber))
			{
				playerHistory.getEventLogger().recordBirth(state.getTurnNumber(), newunit.ID, builder.ID, newunit.getPlayer());
			}
		}
		observerHistory.getEventLogger().recordBirth(state.getTurnNumber(), newunit.ID, builder.ID, newunit.getPlayer());
	}
	public void recordUpgrade(UpgradeTemplate upgradetemplate, Unit creator, State state) {
		
		int x = creator.getxPosition();
		int y = creator.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player))
			{
				getEventLog(player).recordUpgrade(upgradetemplate.ID, upgradetemplate.getPlayer());
			}
		}*/
		for(PlayerHistory playerHistory : playerHistories.values())
		{
			if (state.canSee(x, y, playerHistory.playerNumber))
			{
				playerHistory.getEventLogger().recordUpgrade(state.getTurnNumber(), upgradetemplate.ID, upgradetemplate.getPlayer());
			}
		}
		observerHistory.getEventLogger().recordUpgrade(state.getTurnNumber(), upgradetemplate.ID, upgradetemplate.getPlayer());		
	}
	public void recordDamage(Unit u, Unit target, int damage, State state) {
		
		int x = target.getxPosition();
		int y = target.getyPosition();
		int x2 = u.getxPosition();
		int y2 = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player) || state.canSee(x2, y2, player))
			{
				getEventLog(player).recordDamage(u.ID, u.getPlayer(), target.ID, target.getPlayer(), damage);
			}
		}*/
		for(PlayerHistory playerHistory : playerHistories.values())
		{
			int player = playerHistory.playerNumber;
			if (state.canSee(x, y, player) || state.canSee(x2, y2, player))
			{
				playerHistory.getEventLogger().recordDamage(state.getTurnNumber(), u.ID, u.getPlayer(), target.ID, target.getPlayer(), damage);
			}
		}
		observerHistory.getEventLogger().recordDamage(state.getTurnNumber(), u.ID, u.getPlayer(), target.ID, target.getPlayer(), damage);		
	}
	public void recordPickupResource(Unit u, ResourceNode resource, int amountPickedUp, State state) {
		int x = resource.getxPosition();
		int y = resource.getyPosition();
		int x2 = u.getxPosition();
		int y2 = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player) || state.canSee(x2, y2, player))
			{
				getEventLog(player).recordPickupResource(u.ID, u.getPlayer(), resource.getResourceType(), amountPickedUp, resource.ID, resource.getType());;
			}
		}*/
		for(PlayerHistory playerHistory : playerHistories.values())
		{
			int player = playerHistory.playerNumber;
			if (state.canSee(x, y, player) || state.canSee(x2, y2, player))
			{
				playerHistory.getEventLogger().recordPickupResource(state.getTurnNumber(), u.ID, 
																  u.getPlayer(), 
																  resource.getResourceType(), 
																  amountPickedUp, 
																  resource.ID, 
																  resource.getType());
			}
		}
		observerHistory.getEventLogger().recordPickupResource(state.getTurnNumber(), u.ID, 
														    u.getPlayer(), 
														    resource.getResourceType(), 
														    amountPickedUp, 
														    resource.ID, 
														    resource.getType());
		
	}
	public void recordDeath(Unit u, State state) {
		int x = u.getxPosition();
		int y = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player))
			{
				getEventLog(player).recordDeath(u.ID,u.getPlayer());
			}
		}*/

		for(PlayerHistory playerHistory : playerHistories.values())
		{
			if (state.canSee(x, y, playerHistory.playerNumber))
			{
				playerHistory.getEventLogger().recordDeath(state.getTurnNumber(), u.ID,u.getPlayer());
			}
		}
		observerHistory.getEventLogger().recordDeath(state.getTurnNumber(), u.ID,u.getPlayer());
	}
	public void recordExhaustedResourceNode(ResourceNode r, State state) {
		
		int x = r.getxPosition();
		int y = r.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player))
			{
				getEventLog(player).recordExhaustedResourceNode(r.ID, r.getType());
			}
		}*/
		for(PlayerHistory playerHistory : playerHistories.values())
		{
			if (state.canSee(x, y, playerHistory.playerNumber))
			{
				playerHistory.getEventLogger().recordExhaustedResourceNode(state.getTurnNumber(), r.ID, r.getType());
			}
		}
		observerHistory.getEventLogger().recordExhaustedResourceNode(state.getTurnNumber(), r.ID, r.getType());
	}
	public void recordDropoffResource(Unit u, Unit townHall, State state) {
		int x = townHall.getxPosition();
		int y = townHall.getyPosition();
		int x2 = u.getxPosition();
		int y2 = u.getyPosition();
		//Don't change the iterating thing to "player" because it won't do the observer properly
		/*for (Integer player : eventlogs.keySet())
		{
			if (state.canSee(x, y, player) || state.canSee(x2, y2, player))
			{
				getEventLog(player).recordDropoffResource(u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
			}
		}*/
		for(PlayerHistory playerHistory : playerHistories.values())
		{
			int player = playerHistory.playerNumber;
			if (state.canSee(x, y, player) || state.canSee(x2, y2, player))
			{
				playerHistory.getEventLogger().recordDropoffResource(state.getTurnNumber(), u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
			}
		}
		observerHistory.getEventLogger().recordDropoffResource(state.getTurnNumber(), u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
		
	}
	public HistoryView getView(int player) {
		return new HistoryView(player);
	}
	public class HistoryView
	{
		private int player;
		public HistoryView(int player)
		{
			this.player = player;
		}
		/**
		 * Get a logger containing all events observable to the agent.  Contents depend on the observability of the State
		 * @return
		 */
		public EventLoggerView getEventLogger()
		{
			if (this.player == Agent.OBSERVER_ID)
				return new EventLoggerView(observerHistory.getEventLogger());
			//Observability is put in the calculation
			return new EventLoggerView(playerHistories.get(player).getEventLogger());
		}
		public ActionLoggerView getActionsExecuted(int playerNumber)
		{
			if (this.player == Agent.OBSERVER_ID)
				return observerHistory.getActionsExecuted().getView();
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!fogOfWar || this.player == playerNumber)
			{
				return playerHistories.get(playerNumber).getActionsExecuted().getView();
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
		public ActionLoggerView getCommandsIssued(int playerNumber)
		{
			if (this.player == Agent.OBSERVER_ID)
				return observerHistory.getCommandsIssued().getView();
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!fogOfWar || this.player == playerNumber)
			{
				return playerHistories.get(playerNumber).getCommandsIssued().getView();
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
		public ActionResultLoggerView getActionResults(int playerNumber)
		{
			if (this.player == Agent.OBSERVER_ID)
				return observerHistory.getActionProgress().getView();
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!fogOfWar || this.player == playerNumber)
			{
				return playerHistories.get(playerNumber).getActionProgress().getView();
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
	
	}
	
}
