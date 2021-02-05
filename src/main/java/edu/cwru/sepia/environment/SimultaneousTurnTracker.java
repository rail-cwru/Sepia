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

import java.util.HashSet;
import java.util.Random;

import edu.cwru.sepia.agent.Agent;
/**
 * A simple class that always says it is everybody's turn.
 * @author The Condor
 *
 */
public class SimultaneousTurnTracker implements TurnTracker {
	HashSet<Integer> newlyAddedPlayers;
	HashSet<Integer> playersWhoHaveHadTurns;
	HashSet<Integer> currentPlayers;
	public SimultaneousTurnTracker(Random unused) {
		currentPlayers = new HashSet<Integer>();
		playersWhoHaveHadTurns = new HashSet<Integer>();
		newlyAddedPlayers = new HashSet<Integer>();
	}
	@Override
	public void addPlayer(Integer playerNumber) {
		if (!currentPlayers.contains(playerNumber))
		{
			currentPlayers.add(playerNumber);
			//To prevent silly addPlayer/removePlayer usage from messing this up, only add a new player if it hasn't moved before 
			if (!playersWhoHaveHadTurns.contains(playerNumber))
			{
				newlyAddedPlayers.add(playerNumber);
			}
		}
	}

	@Override
	public void removePlayer(Integer playerNumber) {
		newlyAddedPlayers.remove(playerNumber);
		currentPlayers.remove(playerNumber);
		//don't remove from playersWhoHaveHadTurns, because it doesn't cease to have had a turn.
	}

	@Override
	public void newEpisodeAndStep() {
		newlyAddedPlayers.addAll(currentPlayers); //since new is always a subset of current, this just makes them the same
		playersWhoHaveHadTurns.clear();
	}

	@Override
	public void newStep() {
		playersWhoHaveHadTurns.addAll(newlyAddedPlayers);
		newlyAddedPlayers.clear();
	}

	@Override
	public boolean isAgentsTurn(Agent agent) {
		return true;
	}
	@Override
	public boolean isPlayersTurn(int playerNumber) {
		return true;
	}
	@Override
	public boolean hasHadTurnBefore(int playerNumber) {
		return playersWhoHaveHadTurns.contains(playerNumber);
	}

}
