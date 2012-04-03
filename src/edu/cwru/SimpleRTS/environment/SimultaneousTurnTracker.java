package edu.cwru.SimpleRTS.environment;

import java.util.HashSet;
import java.util.Random;

import edu.cwru.SimpleRTS.agent.Agent;
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
