package edu.cwru.SimpleRTS.environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.log.ActionLogger.ActionLoggerView;
import edu.cwru.SimpleRTS.log.ActionResultLogger.ActionResultLoggerView;
import edu.cwru.SimpleRTS.log.EventLogger.EventLoggerView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.DeepEquatable;
import edu.cwru.SimpleRTS.util.DeepEquatableUtil;

public class History implements DeepEquatable {
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
	/**
	 * Set/Add a playerHistory directly.<br>
	 * Used internally for loading.
	 * @param ph
	 */
	public void setPlayerHistory(PlayerHistory ph) {
		playerHistories.put(ph.playerNumber, ph);		
	}
	/**
	 * Get the PlayerHistory for a specific player
	 * @param playerNumber
	 * @return The PlayerHistory for the player if it exists, null otherwise.
	 */
	public PlayerHistory getPlayerHistory(int playerNumber) {
		return playerHistories.get(playerNumber);		
	}
	/**
	 * Get all of the player histories 
	 * @return A collection of all non-observer PlayerHistory objects.
	 */
	public Collection<PlayerHistory> getPlayerHistories() {
		return playerHistories.values();		
	}
	/**
	 * Set the playerHistory for the observer directly.<br>
	 * Used internally for loading.
	 * @param oh A player history for the observer.
	 */
	public void setObserverHistory(PlayerHistory oh) {
		observerHistory = oh;		
	}
	/**
	 * The observer history.  This is expected to be at least as complete as the union of all other PlayerHistory objects.
	 * @return A PlayerHistory representing the view of an observer.
	 */
	public PlayerHistory getObserverHistory() {
		return observerHistory;
	}
	public boolean hasFogOfWar() {
		return fogOfWar;
	}
	public void setFogOfWar(boolean fogOfWar) {
		this.fogOfWar = fogOfWar;
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
	
	public void recordPrimitiveFeedback(int player, int stepnumber, ActionResult primitiveFeedback)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getPrimitiveFeedback().addActionResult(stepnumber, primitiveFeedback);
		observerHistory.getPrimitiveFeedback().addActionResult(stepnumber, primitiveFeedback);
	}
	public void recordCommandRecieved(int player, int stepnumber, Action actionRecieved)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getCommandsIssued().addAction(stepnumber, actionRecieved);
		observerHistory.getCommandsIssued().addAction(stepnumber, actionRecieved);
	}
	public void recordCommandFeedback(int player, int stepnumber, ActionResult commandFeedback)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getCommandFeedback().addActionResult(stepnumber, commandFeedback);
		observerHistory.getCommandFeedback().addActionResult(stepnumber, commandFeedback);
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
				playerHistory.getEventLogger().recordUpgrade(state.getTurnNumber(), upgradetemplate.ID, creator.ID, upgradetemplate.getPlayer());
			}
		}
		observerHistory.getEventLogger().recordUpgrade(state.getTurnNumber(), upgradetemplate.ID, creator.ID, upgradetemplate.getPlayer());		
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
	public void recordResourcePickup(Unit u, ResourceNode resource, int amountPickedUp, State state) {
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
				playerHistory.getEventLogger().recordResourcePickup(state.getTurnNumber(), u.ID, 
																  u.getPlayer(), 
																  resource.getResourceType(), 
																  amountPickedUp, 
																  resource.ID, 
																  resource.getType());
			}
		}
		observerHistory.getEventLogger().recordResourcePickup(state.getTurnNumber(), u.ID, 
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
	public void recordResourceNodeExhaustion(ResourceNode r, State state) {
		
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
				playerHistory.getEventLogger().recordResourceNodeExhaustion(state.getTurnNumber(), r.ID, r.getType());
			}
		}
		observerHistory.getEventLogger().recordResourceNodeExhaustion(state.getTurnNumber(), r.ID, r.getType());
	}
	public void recordResourceDropoff(Unit u, Unit townHall, State state) {
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
				playerHistory.getEventLogger().recordResourceDropoff(state.getTurnNumber(), u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
			}
		}
		observerHistory.getEventLogger().recordResourceDropoff(state.getTurnNumber(), u.ID, townHall.ID, u.getPlayer(), u.getCurrentCargoType(), u.getCurrentCargoAmount());
		
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
		public ActionResultLoggerView getPrimitiveFeedback(int playerNumber)
		{
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!hasFogOfWar() || this.player == playerNumber || this.player == Agent.OBSERVER_ID)
			{
				return playerHistories.get(playerNumber).getPrimitiveFeedback().getView();
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
		public ActionLoggerView getCommandsIssued(int playerNumber)
		{
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!hasFogOfWar() || this.player == playerNumber || this.player == Agent.OBSERVER_ID)
			{
				return playerHistories.get(playerNumber).getCommandsIssued().getView();
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
		public ActionResultLoggerView getCommandFeedback(int playerNumber)
		{
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!hasFogOfWar() || this.player == playerNumber || this.player == Agent.OBSERVER_ID)
			{
				return playerHistories.get(playerNumber).getCommandFeedback().getView();
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
	
	}

	public boolean deepEquals(Object other) {
		if (this == other)
			return true;
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		History o = (History)other;
		if (this.fogOfWar != o.fogOfWar)
			return false;
		if (!DeepEquatableUtil.deepEquals(this.observerHistory, o.observerHistory))
			return false;
		if (!DeepEquatableUtil.deepEqualsMap(playerHistories, o.playerHistories))
			return false;
		return true;
	}
	
	
}
