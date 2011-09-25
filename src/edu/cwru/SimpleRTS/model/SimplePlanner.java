package edu.cwru.SimpleRTS.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.Upgrade;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
/**
 * An implementation of basic planning methods 
 * @author Scott
 * @author Tim
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
		boolean collidesatend = !state.inBounds(endingx, endingy) || (state.unitAt(endingx, endingy)!=null || state.resourceAt(endingx, endingy)!=null);
		AStarNode bestnode= null;
		queue.offer(new AStarNode(startingx,startingy,Math.max(Math.abs(startingx-endingx), Math.abs(startingy-endingy))));
//		I haven't the foggiest idea why this used to have these next two lines
//		if(distance == 0)
//			distance = state.getXExtent()*state.getYExtent();
		while (queue.size()>0&&bestnode==null)
		{
			AStarNode currentnode = queue.poll();
			
				
			int currentdistance = Math.max(Math.abs(currentnode.x-endingx),Math.abs(currentnode.y-endingy));
			if (distance == 0 && (currentdistance == 0) || (!cancollideonfinal && currentdistance == 1 &&collidesatend))
			{
				bestnode = currentnode;
				break;
			}
			for (Direction d : Direction.values())
			{
				int newx=currentnode.x + d.xComponent();
				int newy=currentnode.y + d.yComponent();
				
				int distfromgoal = Math.max(Math.abs(newx-endingx),Math.abs(newy-endingy));
				//valid if the new state is within max distance and is in bounds and either there is no collision or it is at the target 
				if ((distance == 0 || distfromgoal <= distance) && state.inBounds(newx, newy) && 
						(
								(state.unitAt(newx, newy)==null && state.resourceAt(newx, newy)==null) || 
								(cancollideonfinal && distfromgoal==0)
						)
					)
				{
					AStarNode newnode = new AStarNode(newx, newy, currentnode.g+1, currentnode.g+1+distfromgoal, currentnode, d);
					if(!checked.contains(newnode))
					{
						queue.offer(newnode);
						checked.add(newnode);
					}
					
					if ((distfromgoal == distance) || (!cancollideonfinal && distance == 0 && distfromgoal == 1 && collidesatend))
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
//			System.out.println("Found best at:" + bestnode.x + "," + bestnode.y);
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
		LinkedList<Direction> directions = getDirections(actor.getxPosition(), actor.getyPosition(),x,y,0,true);
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
		while (!path.isEmpty())
		{
			moves.addLast(Action.createPrimitiveMove(actor.hashCode(), path.removeFirst()));
		}
		return moves;
	}
	public LinkedList<Action> planMove(int i, int x, int y) {
		return planMove(state.getUnit(i),x,y);
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
		plan.addLast(new TargetedAction(actor.hashCode(),ActionType.PRIMITIVEATTACK,target.hashCode()));
		return plan;
	}
	public LinkedList<Action> planAttack(int actor, int target) {
		return planAttack(state.getUnit(actor),state.getUnit(target));
	}
	/**
	 * Uses {@link #getDirections(int, int, int, int, int, boolean)} to get directions to the specified place and {@link #planMove(Unit, LinkedList<Direction>)} to move almost there.
	 * then Then adds the final direction with a gather.
	 * @param actor
	 * @param target
	 * @param distance
	 * @return A series of actions that move the actor to the target and gathers from the target
	 */
	public LinkedList<Action> planGather(Unit actor, ResourceNode target, int distance) {
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
	public LinkedList<Action> planGather(int actor, int target, int distance) {
		return planGather(state.getUnit(actor),state.getResource(target),distance);
	}
	
	public LinkedList<Action> planBuild(Unit actor, int targetX, int targetY, UnitTemplate template) {
		LinkedList<Action> plan = new LinkedList<Action>();
		//it must be already in the same place for the production to count
		if (actor.getxPosition() == targetX && actor.getyPosition() == targetY)
		{//if it is in the same place
			//needs to know how much building on the target template the unit already has done
			if (actor.getCurrentProductionID() == template.ID)
			{//if it is building the same thing
				//then make it keep building it
				int amountleft = template.hashCode() - actor.getAmountProduced();
				for (int i = 0; i<amountleft; i++)
				{
					plan.addLast(Action.createPrimitiveBuild(actor.hashCode(), template.hashCode()));
				}
			}
			else
			{//if it is making somthing else
				for (int i = template.timeCost - 1; i>=0; i--)
				{
					plan.addLast(Action.createPrimitiveBuild(actor.hashCode(), template.hashCode()));
				}
			}
		}
		else
		{
			System.out.println("Guy at "+actor.getxPosition() + "," + actor.getyPosition()+" Building thing at "+targetX+","+ targetY);
			plan = planMove(actor, getDirections(actor.getxPosition(), actor.getyPosition(), targetX, targetY, 0, false));
			for (int i = template.timeCost - 1; i>=0; i--)
			{
				plan.addLast(Action.createPrimitiveBuild(actor.hashCode(), template.hashCode()));
			}
		}
		return plan;
	}
	public LinkedList<Action> planBuild(int actor, int targetX, int targetY, int template) {
		return planBuild(state.getUnit(actor),targetX,targetY,(UnitTemplate)state.getTemplate(template));
	}
	
	public LinkedList<Action> planProduce(Unit actor, Template template) {
		LinkedList<Action> plan = new LinkedList<Action>();
		//needs to know how much building on the target template the unit already has done
		if (actor.getCurrentProductionID() == template.ID)
		{//if it is building the same thing
			//then make it keep building it
			int amountleft = template.hashCode() - actor.getAmountProduced();
			for (int i = 0; i<amountleft; i++)
			{
				plan.addLast(Action.createPrimitiveProduction(actor.hashCode(), template.hashCode()));
			}
		}
		else
		{//if it is making somthing else
			for (int i = template.timeCost - 1; i>=0; i--)
			{
				plan.addLast(Action.createPrimitiveProduction(actor.hashCode(), template.hashCode()));
			}
		}
		return plan;
	}
	public LinkedList<Action> planProduce(int actor, int template) {
		return planProduce(state.getUnit(actor),state.getTemplate(template));
	}
	
	
}
