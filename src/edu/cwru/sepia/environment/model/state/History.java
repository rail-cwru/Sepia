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
package edu.cwru.sepia.environment.model.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.log.BirthLog;
import edu.cwru.sepia.log.DamageLog;
import edu.cwru.sepia.log.DeathLog;
import edu.cwru.sepia.log.ResourceDropoffLog;
import edu.cwru.sepia.log.ResourceNodeExhaustionLog;
import edu.cwru.sepia.log.ResourcePickupLog;
import edu.cwru.sepia.log.RevealedResourceNodeLog;
import edu.cwru.sepia.log.UpgradeLog;
import edu.cwru.sepia.util.DeepEquatable;
import edu.cwru.sepia.util.DeepEquatableUtil;

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
	
	public void recordPrimitiveFeedback(int player, int stepnumber, ActionResult primitiveFeedback)
	{
		if (!playerHistories.containsKey(player))
			throw new IllegalArgumentException("Invalid player, history doesn't contain a record of that player.");
		playerHistories.get(player).getPrimitiveFeedback().addActionResult(stepnumber, primitiveFeedback);
		observerHistory.getPrimitiveFeedback().addActionResult(stepnumber, primitiveFeedback);
	}
	public void recordCommandRecieved(int player, int stepnumber, int unitID, Action actionRecieved)
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
		 * Return a list of UpgradeLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of UpgradeLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<UpgradeLog> getUpgradeLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getUpgrades(stepNumber);
		}
		/**
		 * Return a list of BirthLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of BirthLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<BirthLog> getBirthLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getBirths(stepNumber);
		}
		/**
		 * Return a list of DeathLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of DeathLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<DeathLog> getDeathLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getDeaths(stepNumber);
		}
		/**
		 * Return a list of DamageLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of DamageLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<DamageLog> getDamageLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getDamage(stepNumber);
		}
		/**
		 * Return a list of ResourceNodeExhaustionLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of ResourceNodeExhaustionLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<ResourceNodeExhaustionLog> getResourceNodeExhaustionLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getResourceNodeExhaustions(stepNumber);
		}
		/**
		 * Return a list of ResourcePickupLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of ResourcePickupLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<ResourcePickupLog> getResourcePickupLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getResourcePickups(stepNumber);
		}
		/**
		 * Return a list of ResourceDropoffLog corresponding to events witnessed by this player during the specified step. 
		 * @param stepNumber
		 * @return An unmodifiable list of ResourceDropoffLog objects that occurred during the specified step and were observed by the player whose HistoryView this is. 
		 */
		public List<ResourceDropoffLog> getResourceDropoffLogs(int stepNumber)
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getResourceDropoffs(stepNumber);
		}
		/**
		 * Return a list of RevealedResourceNodeLog corresponding to events witnessed by this player. 
		 * @param stepNumber
		 * @return An unmodifiable list of RevealedResourceNodeLog objects observed by the player whose HistoryView this is. 
		 */
		public List<RevealedResourceNodeLog> getRevealedResourceNodeLogs()
		{
			PlayerHistory playerHistory;
			if (this.player == Agent.OBSERVER_ID)
				playerHistory = observerHistory;
			else
				playerHistory = playerHistories.get(player);
			return playerHistory.getEventLogger().getRevealedResourceNodes();
		}
		/**
		 * Get a map of ActionResult objects that correspond to the unit ids mapped to primitive Actions attempted by the Model in resolving the commands issued by the specifed playerNumber during execution of the specified stepNumber. <br>
		 * <br>This returns null if the actions of that playerNumber are not visible to the player whose HistoryView this is.  This happens only when fog of war (partial observability) is turned on, and only when the viewing player is not an observer.
		 * <br>The primitive actions covered in these results are used to execute a (possibly non-strict) subset of the actions covered in the CommandResults for the same step, as some commands may be so flawed as to not correspond to any primitives.
		 * <br>Primitive actions used to execute commands will be displayed here even if the command is a primitive action itself.
		 * 
		 * @param playerNumber the player number whose feedback should be returned
		 * @param stepNumber the step number that the feedback occurred in.
		 * @return null if the playerNumber is not visible to the viewing player, a map of Integers (unit ids) to their ActionResult objects corresponding to the primitive Actions used during the specified stepNumber to execute the commands of the specified playerNumber otherwise.
		 */
		public Map<Integer ,ActionResult> getPrimitiveFeedback(int playerNumber, int stepNumber)
		{
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!hasFogOfWar() || this.player == playerNumber || this.player == Agent.OBSERVER_ID)
			{
				return playerHistories.get(playerNumber).getPrimitiveFeedback().getActionResults(stepNumber);
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
		/**
		 * Get a map of Action objects that were received by the Model during the specified stepNumber from agents controlling the specified playerNumber, mapped by the unit ids of the units the action
		 * <br>This returns null if the actions of that playerNumber are not visible to the player whose HistoryView this is.  This happens only when fog of war (partial observability) is turned on, and only when the viewing player is not an observer.
		 * <br>The actions in the list are a subset of the actions covered by CommandFeedback, as the model may use and generate feedback for commands issued during previous steps.
		 * @param playerNumber the player number issuing commands
		 * @param stepNumber the step number when commands were issued
		 * @return null if the playerNumber is not visible to the viewing player, a list of Action objects received as commands during the specified stepNumber from agents controlling the specified playerNumber otherwise.
		 */
		public Map<Integer, Action> getCommandsIssued(int playerNumber, int stepNumber)
		{
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!hasFogOfWar() || this.player == playerNumber || this.player == Agent.OBSERVER_ID)
			{
				return playerHistories.get(playerNumber).getCommandsIssued().getActions(stepNumber);
			}
			//otherwise, you get nothing
			{
				return null;
			}
		}
		/**
		 * Get a map of ActionResult objects covering unit ids mapped to their commands from agents controlling the specified playerNumber that the Model attempted to execute during the specified stepNumber.
		 * <br>This returns null if the actions of that playerNumber are not visible to the player whose HistoryView this is.  This happens only when fog of war (partial observability) is turned on, and only when the viewing player is not an observer.
		 * <br>The actions in the list are a superset of the actions covered by CommandsIssued, as the model may use and generate feedback for commands issued during previous steps.
		 * <br>The actions in the list are a superset of the actions whose primitive components are covered in PrmitiveFeedback, as some commands may be too flawed to lead to primitive components being calculated.
		 * <br>The commands that the model attempts to execute will be included here even if they are primitive actions.
		 * 
		 * @param playerNumber the player number whose commands this list is based on
		 * @param stepNumber the step number when the commands' execution was attempted
		 * @return null if the playerNumber is not visible to the viewing player, a map of Integer unit ids to ActionResult objects corresponding the commands of the specified playerNumber that the Model attempted to execute during the specified stepNumber
		 */
		public Map<Integer, ActionResult> getCommandFeedback(int playerNumber, int stepNumber) {
			//if it is fully observable, or if this is an observer, or if it is asking for this player, then you can get the actual one
			if (!hasFogOfWar() || this.player == playerNumber || this.player == Agent.OBSERVER_ID)
			{
				return playerHistories.get(playerNumber).getCommandFeedback().getActionResults(stepNumber);
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
