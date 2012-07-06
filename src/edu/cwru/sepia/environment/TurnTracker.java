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
package edu.cwru.sepia.environment;

import edu.cwru.sepia.agent.Agent;
/**
 * An interface for classes that track whose turn it is
 * @author The Condor
 *
 */
public interface TurnTracker {
	/**
	 * Start tracking an additional player
	 * @param playerNumber The playerNumber of the player to start tracking.
	 */
	void addPlayer(Integer playerNumber);
	/**
	 * Stop tracking a player
	 * @param playerNumber The playerNumber of the player to stop tracking.
	 */
	void removePlayer(Integer playerNumber);
	/**
	 * Advance to the first step of a new episode.
	 */
	void newEpisodeAndStep();
	/**
	 * Go to the next step in the current episode.
	 */
	void newStep();
	/**
	 * Check whether it is an agent's turn.  IE: Whether the agent should get a state and a chance to send actions during this step.
	 * @param agent An agent.
	 * @return true if the agent controls a player whose turn it is, false otherwise. 
	 */
	boolean isAgentsTurn(Agent agent);
	/**
	 * Check whether it is an player's turn.  IE: Whether the agent's with that player number should get a state and a chance to send actions during this step.
	 * @param playerNumber An player.
	 * @return true if it is the turn of that player, false otherwise. 
	 */
	boolean isPlayersTurn(int playerNumber);
	/**
	 * Check whether the player has already had a turn this episode.
	 * @param playerNumber
	 * @return true if there was a time after the last call of newEpisodeAndStep() but before the last call of newStep(), during which the tracker was tracking that playerNumber and isAgentsTurn() on an agent with the chosen player number could ever have returned true, false otherwise
	 */
	boolean hasHadTurnBefore(int playerNumber);
	
}
