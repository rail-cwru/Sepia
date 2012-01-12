package edu.cwru.SimpleRTS.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
/**
 * Note: as of 9/9/11, the addAction function is being called based on the controller of the unit, rather than directly from the player issuing the order
 * @author The Condor
 *
 */
public class ActionLogger implements Serializable {
	Map<Integer, List<List<Action>>> actions; //Map of playernum -> (List by round number of (List of actions done by that player in that round))
	int roundnumber;
	public ActionLogger () {
		actions = new HashMap<Integer, List<List<Action>>>();
		roundnumber=-1;
		nextRound();
	}
	public void nextRound()
	{
		
		roundnumber++;
		for (int playerid : actions.keySet()) {
			List<List<Action>> actionsets=actions.get(playerid);
			actionsets.add(roundnumber,new ArrayList<Action>());
		}
//		System.out.println("Action logger logging the start of round "+roundnumber);
	}
	public void addPlayer(int playernumber) {
		List<List<Action>> actionset = new ArrayList<List<Action>>();
		actions.put(playernumber, actionset);
		for (int i = 0; i<roundnumber+1;i++)
		{
			actionset.add(i,new ArrayList<Action>());
		}
//		System.out.println("ActionLogger adding another player "+playernumber);
	}
	public void addAction(int playernum, Action action) {
		if (!actions.containsKey(playernum))
		{
			addPlayer(playernum);
		}
		actions.get(playernum).get(roundnumber).add(action);
//		System.out.println("ActionLogger logging action "+action);
	}
	/**
	 * Get the actions of a player
	 * @param playernum
	 * @param roundnumber
	 * @return an unmodifiable list of actions performed by a player in a specific round (or an empty list if the player or round is not found)
	 */
	public List<Action> getActions(int playernum, int roundnumber) {
		if (!actions.containsKey(playernum) || roundnumber<0 || roundnumber > this.roundnumber) {
			System.out.println("ActionLogger could not find an appropriate log");
			return new ArrayList<Action>();
		}
		else {
			return Collections.unmodifiableList(actions.get(playernum).get(roundnumber));
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
		result = prime * result + roundnumber;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionLogger other = (ActionLogger) obj;
		if (actions == null) {
			if (other.actions != null)
				return false;
		} else if (!actions.equals(other.actions))
			return false;
		if (roundnumber != other.roundnumber)
			return false;
		return true;
	}
	
}
