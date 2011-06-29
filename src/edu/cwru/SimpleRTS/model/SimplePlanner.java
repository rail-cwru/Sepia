package edu.cwru.SimpleRTS.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

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
	 * Uses A* to calculate the directions to arrive at the specified location.
	 * If there is no path, it returns null  (it does not do a best effort)
	 * When you wish upon A* ...
	 * @param startingx
	 * @param startingy
	 * @param endingx
	 * @param endingy
	 * @param distance The distance at which to stop trying to seek (IE, 0 is on top of it, 1 is next to it, etc) Note that the distance should count diagonals as 1 dist
	 * @param cancollideonfinal Whether or not to allow a collision on the final move (allowing it to be used for gathering by telling it the last direction)
	 * @return Some primitive (IE, based on directions) moves that bring you within distance of the ending x and y
	 */
	public LinkedList<Direction> getDirections(int startingx, int startingy, int endingx, int endingy, int distance, boolean cancollideonfinal)
	{
		PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>();
		HashSet<AStarNode> checked = new HashSet<AStarNode>();
		AStarNode bestnode=null;
		while (queue.size()>0&&bestnode==null)
		{
			AStarNode currentnode = queue.poll();
			for (Direction d : Direction.values())
			{
				int newx=currentnode.x + d.xComponent();
				int newy=currentnode.y + d.yComponent();
				
				int distfromgoal = Math.max(Math.abs(newx-endingx),Math.abs(newy-endingy));
				//valid if the new state is in bounds and either there is no collision or it is at the target 
				if (state.inBounds(newx, newy)&&(state.unitAt(newx, newy)==null && state.resourceAt(newx, newy)==null||cancollideonfinal&&distfromgoal==0))
				{
					AStarNode newnode = new AStarNode(newx, newy, currentnode.g+1, currentnode.g+1+distfromgoal, currentnode, d);
					
					
					if (distfromgoal<=distance)
					{
						bestnode = newnode;
						break;
					}
				}
				
			}
		}
		if (bestnode == null)
			return null;
		else
		{
			LinkedList<Direction> toreturn=new LinkedList<Direction>();
			while(bestnode.previous!=null)
			{
				toreturn.addFirst(bestnode.directionfromprevious);
				bestnode=bestnode.previous;
			}
			return toreturn;
		}
	}
	/**
	 * Uses {@link #getDirections(int, int, int, int, int, boolean)} to get directions to the specified place and {@link #planMove(Unit, LinkedList<Direction>)} to follow them. 
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	public LinkedList<Action> planMove(Unit actor, int x, int y) {
		LinkedList<Direction> directions = getDirections(actor.getxPosition(), actor.getyPosition(),x,y,0,false);
		if (directions == null)
			return null;
		else
			return planMove(actor,directions);
	}
	/**
	 * Build primitive moves following the path made by the directions
	 * @param actor
	 * @param path
	 * @return
	 */
	public LinkedList<Action> planMove(Unit actor, LinkedList<Direction> path) {
		LinkedList<Action> moves = new LinkedList<Action>();
		Direction nextDirection;
		while ((nextDirection=path.pollFirst())!=null)
		{
			moves.addLast(Action.createPrimitiveMove(actor.hashCode(), nextDirection));
		}
		return moves;
	}
	/**
	 * Uses {@link #getDirections(int, int, int, int, int, boolean)} to get directions to the specified place and {@link #planMove(Unit, LinkedList<Direction>)} to follow them.
	 * then adds an attack command.
	 * @param actor
	 * @param target
	 * @return A series of actions that move the actor to the target and attacks the target
	 */
	public LinkedList<Action> planAttack(Unit actor, Unit target) {
		LinkedList<Direction> directions = getDirections(actor.getxPosition(), actor.getyPosition(),target.getxPosition(),target.getyPosition(),actor.getTemplate().getRange(),false);
		if (directions == null)
			return null;
		LinkedList<Action> plan = planMove(actor,directions);
		plan.addLast(Action.createCompoundAttack(actor.hashCode(), actor.hashCode()));
		return plan;
	}
	
	/**
	 * Uses {@link #getDirections(int, int, int, int, int, boolean)} to get directions to the specified place and {@link #planMove(Unit, LinkedList<Direction>)} to move almost there.
	 * then Then adds the final direction with a gather.
	 * @param actor
	 * @param target
	 * @param distance
	 * @return A series of actions that move the actor to the target and gathers from the target
	 */
	public LinkedList<Action> planGather(Unit actor, Resource target, int distance) {
		//plan a route to onto the resource
		//This requires that planmove handle a 0 distance move as having the final primative move not be affected by collisions
		//if the above requirement is violated, this will not work
		LinkedList<Direction> directions = getDirections(actor.getxPosition(), actor.getyPosition(),target.getxPosition(),target.getyPosition(),0,true);
		if (directions==null || directions.size()<1)
			return null;
		Direction finaldirection = directions.pollLast();
		LinkedList<Action> plan = planMove(actor, directions);
		plan.addLast(Action.createPrimitiveGather(actor.ID, finaldirection));
		return plan;
	}
	
	public LinkedList<Action> planBuild(Unit actor, int targetX, int targetY, int distance, UnitTemplate target) {
		//needs to know how much building on the target template the unit already has done
		return null;
	}
	
	public LinkedList<Action> planProduce(Unit actor, UnitTemplate target) {
		//needs to know how much producing on the target template the unit already has done
		return null;		
	}
	
	public LinkedList<Action> planUpgrade(Unit actor, Template<Upgrade> target) {
		//needs to know how much producing on the target template the unit already has done
		return null;
		
	}
	
	
}
