package edu.cwru.SimpleRTS.model;

import java.util.LinkedList;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.Upgrade;
/**
 * An implementation of basic planning methods 
 * @author Tim
 *
 */
public class SimplePlanner {
	private State state;
	public SimplePlanner(State state) {
		this.state = state;
	}
	/**
	 * Uses A* to calculate the primitive moves to arrive at the specified location.
	 * @param startingx
	 * @param startingy
	 * @param endingx
	 * @param endingy
	 * @param distance The distance at which to stop trying to seek (IE, either next to it, or the range)
	 * @return Some primitive (IE, based on directions) moves that bring you within distance of the ending x and y
	 */
	public LinkedList<Action> planMove(int startingx, int startingy, int endingx, int endingy, int distance)
	{
		//TODO: put A* implementation here
		return null;
	}
	/**
	 * Uses {@link #calculatePrimitiveMoves(int, int, int, int, int)} to get a list of moves to get to the target,
	 * then adds an attack command.
	 * @param actor
	 * @param target
	 * @param distance
	 * @return A series of actions that move the actor to the target and attacks the target
	 */
	public LinkedList<Action> planAttack(Unit actor, Unit target, int distance) {
		return null;
	}
	
	/**
	 * Uses {@link #calculatePrimitiveMoves(int, int, int, int, int)} to get a list of moves to get to the target,
	 * then adds a gather command.
	 * @param actor
	 * @param target
	 * @param distance
	 * @return A series of actions that move the actor to the target and gathers from the target
	 */
	public LinkedList<Action> planGather(Unit actor, Resource target, int distance) {
		return null;		
	}
	
	public LinkedList<Action> planBuild(Unit actor, int targetX, int targetY, int distance, UnitTemplate target) {
		return null;
	}
	
	public LinkedList<Action> planProduce(Unit actor, UnitTemplate target) {
		return null;		
	}
	
	public LinkedList<Action> planUpgrade(Unit actor, Template<Upgrade> target) {
		return null;
		
	}
}
