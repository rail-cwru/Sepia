package edu.cwru.SimpleRTS.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
/**
 * An implementation of basic planning methods extended to the case of actions that take time to act.
 */
public class DurativePlanner implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private State state;
	public DurativePlanner(State state) {
		this.state = state;
	}
	/**
	 * <pre>
	 * Uses A* to calculate the directions to arrive at the specified
	 * location.
	 * Returns a path if and only if there is a valid path.
	 * When you wish upon A* ...
	 * </pre>
	 * @param u The unit being moved.  Necessary for calculating durative actions
	 * @param startingx
	 * @param startingy
	 * @param endingx
	 * @param endingy
	 * @param tolerancedistance The distance at which to stop trying to seek (IE, 0 is on top of it, 1 is next to it, etc) Note that the distance should count diagonals as 1 dist
	 * @param isFirstMove Whether this is the first move in a larger plan (and thus that the current progress toward a move should be included)
	 * @return A move plan with the success, path, and final x and y coordinates.
	 */
	public MovePlan getDirections(Unit u, int startingx, int startingy, int endingx, int endingy, int tolerancedistance, boolean isFirstMove)
	{
//		System.out.println("calling A* to get the unit at "+u.getxPosition() + ","+u.getyPosition() + " from "+startingx + "," + startingy+" to "+endingx+","+endingy + " within a tolerance of "+tolerancedistance);
		//make a list of nodes that need to be checked
		PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>();
		//make a list of nodes that were already checked
		HashSet<AStarNode> checked = new HashSet<AStarNode>();
		AStarNode bestvalidpath = null;
		//put the starting place in the queue
		queue.offer(new AStarNode(startingx,startingy,DistanceMetrics.chebyshevDistance(startingx, startingy, endingx, endingy)*calculateMaxMoveDuration(u)));
		//mark this as the first place only if this call is the first move.
		boolean firstPlace=isFirstMove;
		//loop as long as you haven't checked everywhere and haven't found a path yet
		while(!queue.isEmpty() && bestvalidpath==null)
		{
			//pull the next unexplored node
			AStarNode currentplace = queue.poll();
			//find the distance to the goal as the crow flies
			int chebydisttogoal = DistanceMetrics.chebyshevDistance(currentplace.x, currentplace.y, endingx, endingy);
			if (tolerancedistance >= chebydisttogoal)
			{
				bestvalidpath=currentplace;
			}
			else //not done
			{
				//put all of the neighbors on the queue
				for (Direction d : Direction.values())
				{
					int newx=currentplace.x + d.xComponent();
					int newy=currentplace.y + d.yComponent();
					
					int newdisttogoal = DistanceMetrics.chebyshevDistance(newx,newy,endingx,endingy);
					//valid if the new state is within max distance and unoccupied 
					if (state.inBounds(newx, newy) && (state.unitAt(newx, newy) ==null && state.resourceAt(newx, newy)==null)) {
						
						int durativedisttogoal = calculateMaxMoveDuration(u)*newdisttogoal;
						int durativestep=calculateMoveDuration(u, currentplace.x,currentplace.y, d);
						//only proceed if the durative step is nonnegative and less than the maximum, otherwise assume impassibility
						if (durativestep >= 1 && durativestep <= calculateMaxMoveDuration(u))
						{
							//If this is the first move, then you can continue based on your current progress
							if (firstPlace)
							{
								Action primitive = Action.createPrimitiveMove(u.ID, d);
								if (primitive.equals(u.getActionProgressPrimitive()))
									durativestep -= u.getActionProgressAmount();
								if (durativestep < 1)
									durativestep = 1;
							}
							AStarNode newnode = new AStarNode(newx, newy, currentplace.g+durativestep, currentplace.g+durativestep+durativedisttogoal, currentplace, d, durativestep);
							if(!checked.contains(newnode))
							{
								queue.offer(newnode);
								checked.add(newnode);
							}
						}
					}
				}
				firstPlace=false;
			}
		}
		if (bestvalidpath == null)
			return new MovePlan();
		else
		{
	//		System.out.println("Found best at:" + bestnode.x + "," + bestnode.y);
			LinkedList<Action> toreturn=new LinkedList<Action>();
			//Track the last x and y positions
			int lastx=bestvalidpath.x;
			int lasty=bestvalidpath.y;
			while(bestvalidpath.previous!=null)
			{
				//always send false for whether to continue an existing action, as that is included in durativesteps
				doDuratives(toreturn, u, Action.createPrimitiveMove(u.ID, bestvalidpath.directionfromprevious), bestvalidpath.durativesteps, false, true);
				bestvalidpath=bestvalidpath.previous;
			}
			return new MovePlan(toreturn,lastx,lasty);
	}

	}
	
//	{
////		System.out.println("Getting Directions from " + startingx + "," + startingy + " to " + endingx + "," + endingy);
//		PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>();
//		HashSet<AStarNode> checked = new HashSet<AStarNode>();
//		boolean collidesatend = !state.inBounds(endingx, endingy) || (state.unitAt(endingx, endingy)!=null || state.resourceAt(endingx, endingy)!=null);
//		AStarNode bestnode= null;
//		queue.offer(new AStarNode(startingx,startingy,Math.max(Math.abs(startingx-endingx), Math.abs(startingy-endingy))));
////		I haven't the foggiest idea why this used to have these next two lines
////		if(distance == 0)
////			distance = state.getXExtent()*state.getYExtent();
//		while (queue.size()>0&&bestnode==null)
//		{
//			//Grab a node
//			AStarNode currentnode = queue.poll();
//			int currentdistancetogoal = DistanceMetrics.chebyshevDistance(currentnode.x, currentnode.y, endingx, endingy);
//			//Check if you are done
//			if (tolerancedistance >= currentdistancetogoal || (!cancollideonfinal && currentdistancetogoal == 1 &&collidesatend))
//			{
//				bestnode = currentnode;
//				break;
//			}
//			for (Direction d : Direction.values())
//			{
//				int newx=currentnode.x + d.xComponent();
//				int newy=currentnode.y + d.yComponent();
//				
//				int newdisttogoal = Math.max(Math.abs(newx-endingx),Math.abs(newy-endingy));
//				//valid if the new state is within max distance and is in bounds and either there is no collision or it is at the target 
//				if (state.inBounds(newx, newy) && 
//						(
//								(state.unitAt(newx, newy) ==null && state.resourceAt(newx, newy)==null) || 
//								(cancollideonfinal && newdisttogoal==0)
//						)
//					)
//				{
//					AStarNode newnode = new AStarNode(newx, newy, currentnode.g+1, currentnode.g+1+newdisttogoal, currentnode, d);
//					if(!checked.contains(newnode))
//					{
//						queue.offer(newnode);
//						checked.add(newnode);
//					}
//				}
//				
//			}
//		}
//		if (bestnode == null)
//			return null;
//		else
//		{
////			System.out.println("Found best at:" + bestnode.x + "," + bestnode.y);
//			LinkedList<Direction> toreturn=new LinkedList<Direction>();
//			while(bestnode.previous!=null)
//			{
//				toreturn.addFirst(bestnode.directionfromprevious);
//				bestnode=bestnode.previous;
//			}
//			return toreturn;
//		}
//	}
	/**
	 * Uses {@link #getDirections(int, int, int, int, int, boolean)} to get directions to the specified place and {@link #planMove(Unit, LinkedList<Direction>)} to follow them. 
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	public LinkedList<Action> planMove(Unit actor, int x, int y) {
		if (!state.inBounds(x, y)) {
			return planPermanentFail(actor.ID);
		}
		//plan a route to onto the resource
		MovePlan plan = getDirections(actor, actor.getxPosition(), actor.getyPosition(), x, y, 0,true);
		if (!plan.succeeded)
		{//No path to the unit
			return planFail(actor.ID);
		}
		return plan.actions;
	}
	
	public LinkedList<Action> planFail(int actor) {
		LinkedList<Action> failact= new LinkedList<Action>();
		failact.add(Action.createFail(actor));
		return failact;
	}
	public LinkedList<Action> planPermanentFail(int actor) {
		LinkedList<Action> failact= new LinkedList<Action>();
		failact.add(Action.createPermanentFail(actor));
		return failact;
	}
	public LinkedList<Action> planMove(int i, int x, int y) {
		return planMove(state.getUnit(i),x,y);
	}
	/**
	 * Uses {@link #getDirections(StateView, int, int, int, int, int, boolean)} to get directions to the specified place and {@link #planMove(Unit, LinkedList<Direction>)} to follow them.
	 * then adds an attack command.
	 * @param actor
	 * @param target
	 * @return A series of actions that move the actor to the target and attacks the target
	 */
	public LinkedList<Action> planAttack(Unit actor, Unit target) {
		if (target == null)
		{
			return planPermanentFail(actor.ID);
		}
		//plan a route to onto the resource
		MovePlan plan = getDirections(actor, actor.getxPosition(), actor.getyPosition(), target.getxPosition(), target.getyPosition(), actor.getTemplate().getRange(),true);
		if (!plan.succeeded)
		{//No path to the unit
			return planFail(actor.ID);
		}
		boolean noPreviousProgress = plan.actions.isEmpty();
		Action primitivetorepeat = Action.createPrimitiveAttack(actor.ID, target.ID);
		int nrepeats = calculateAttackDuration(actor,target);
		doDuratives(plan.actions,actor,primitivetorepeat, nrepeats, noPreviousProgress, false);
		return plan.actions;
	}
	public LinkedList<Action> planAttack(int actor, int target) {
		Unit targetunit = state.getUnit(target);
		
		return planAttack(state.getUnit(actor),targetunit);
	}
	
	
	
	/**
	 * Plan a compound deposit action in which the unit moves next to the target town hall and then deposits into it
	 * @param actor
	 * @param target
	 * @param distance
	 * @return 
	 */
	public LinkedList<Action> planDeposit(Unit actor, Unit target) {
		if (target == null)
		{
			return planPermanentFail(actor.ID);
		}
		//plan a route to onto the resource
		MovePlan plan = getDirections(actor, actor.getxPosition(), actor.getyPosition(), target.getxPosition(), target.getyPosition(), 1,true);
		if (!plan.succeeded)
		{//No path to the unit
			return planFail(actor.ID);
		}
		boolean noPreviousProgress = plan.actions.isEmpty();
		Action primitivetorepeat = Action.createPrimitiveDeposit(actor.ID, Direction.getDirection(target.getxPosition() - plan.finalx, target.getyPosition() - plan.finaly));
		int nrepeats = calculateDepositDuration(actor,target);
		doDuratives(plan.actions,actor,primitivetorepeat, nrepeats, noPreviousProgress, false);
		return plan.actions;
	}
	public LinkedList<Action> planDeposit(int actor, int target) {
		return planDeposit(state.getUnit(actor),state.getUnit(target));
	}
	
	
	
	/**
	 * Plan a compound gather action in which the unit moves next to the target node and then gathers from it.
	 * @param actor
	 * @param target
	 * @return A series of actions that move the actor to the target and gathers from the target
	 */
	public LinkedList<Action> planGather(Unit actor, ResourceNode target) {
		
		if (target == null)
		{
			return planPermanentFail(actor.ID);
		}
		//plan a route to onto the resource
		MovePlan plan = getDirections(actor, actor.getxPosition(), actor.getyPosition(), target.getxPosition(), target.getyPosition(), 1,true);
		if (!plan.succeeded)
		{//No path to the unit
			return planFail(actor.ID);
		}
		boolean noPreviousProgress = plan.actions.isEmpty();
		int xDirection = target.getxPosition() - plan.finalx;
		int yDirection = target.getyPosition() - plan.finaly;
		Action primitivetorepeat = Action.createPrimitiveGather(actor.ID, Direction.getDirection(xDirection,yDirection));
		int nrepeats = calculateGatherDuration(actor,target);
		doDuratives(plan.actions,actor,primitivetorepeat, nrepeats, noPreviousProgress, false);
		return plan.actions;
	}
	public LinkedList<Action> planGather(int actor, int target) {
		return planGather(state.getUnit(actor),state.getResource(target));
	}
	/**
	 * Plan a compound build action in which the unit moves to a target and then 
	 * @param actor
	 * @param targetX
	 * @param targetY
	 * @param template
	 * @return
	 */
	public LinkedList<Action> planBuild(Unit actor, int targetX, int targetY, UnitTemplate template) {
		MovePlan plan = getDirections(actor, actor.getxPosition(), actor.getyPosition(), targetX, targetY, 0, true);
		if (!plan.succeeded)
		{//No path to the unit
			return planFail(actor.ID);
		}
		boolean noPreviousProgress = plan.actions.isEmpty();
		//needs to know how much building on the target template the unit already has done
		Action primitivetorepeat = Action.createPrimitiveBuild(actor.ID, template.ID);
		int nrepeats = calculateProductionDuration(actor,template);
		doDuratives(plan.actions,actor,primitivetorepeat, nrepeats, noPreviousProgress, false);
		return plan.actions;
	}
	public LinkedList<Action> planBuild(int actor, int targetX, int targetY, int template) {
		return planBuild(state.getUnit(actor),targetX,targetY,(UnitTemplate)state.getTemplate(template));
	}
	
	public LinkedList<Action> planProduce(Unit actor, @SuppressWarnings("rawtypes") Template template) {
		LinkedList<Action> plan = new LinkedList<Action>();
		//needs to know how much building on the target template the unit already has done
		Action primitivetorepeat = Action.createPrimitiveProduction(actor.ID, template.ID);
		int nrepeats = calculateProductionDuration(actor,template);
		doDuratives(plan,actor,primitivetorepeat, nrepeats, true, false);
		return plan;
	}
	public LinkedList<Action> planProduce(int actor, int template) {
		return planProduce(state.getUnit(actor),state.getTemplate(template));
	}
	/**
	 * Add a sequence of primitive actions to a list.
	 * This can add to either the front or the back of the list and is capable of adding only enough to complete a partially completed action.
	 * @param listToAddTo The list of actions.  This list will have primitives added to it's tail.
	 * @param unit The unit that will do the action.
	 * @param primitive The primitive action which will be repeated until done.
	 * @param duration Full duration of the action in question.
	 * @param immediatelyNext Whether the action is immediately next, and thus whether the unit's current status should be considered.
	 * @param addToFront Whether to add the new actions to the front or back
	 */
	private void doDuratives(LinkedList<Action> listToAddTo, Unit unit, Action primitive, final int duration, final boolean immediatelyNext, final boolean addToFront)
	{
		int previousduration;
		if (immediatelyNext && primitive.equals(unit.getActionProgressPrimitive())) {
			previousduration = unit.getActionProgressAmount();
		}
		else {
			previousduration = 0;
		}
		if (previousduration < 0)
			previousduration = 0;
		
		for (int i = duration; i > previousduration; i--)
		{
			if (addToFront)
				listToAddTo.addFirst(primitive);
			else
				listToAddTo.addLast(primitive);
		}
	}
	/**
	 * <pre>
	 * Calculate the move duration for a unit.
	 * These values must tie to {@link #calculateMaxMoveDuration}
	 * </pre>
	 * @param u
	 * @param startingx
	 * @param startingy
	 * @param d
	 * @return
	 */
	public static int calculateMoveDuration(Unit u, int startingx, int startingy, Direction d) {
		return u.getTemplate().getDurationMove();
		//TODO: Remove this
	}
	/**
	 * <pre>
	 * Calculate the maximum move possible for a Unit.
	 * These values must tie to {@link #calculateMoveDuration}
	 * </pre>
	 * 
	 * @param u The unit whose maximum move we are probing.
	 * @return
	 */
	public static int calculateMaxMoveDuration(Unit u) {
		return u.getTemplate().getDurationMove();
		//TODO: Remove this
	}
	/**
	 * Calculate the duration of a 
	 * @param u
	 * @param townhall
	 * @return
	 */
	public static int calculateDepositDuration(Unit u, Unit townhall) {
		return u.getTemplate().getDurationDeposit();
	}
	/**
	 * A simple calculation for determining the amount 
	 * @param u
	 * @param target
	 * @return
	 */
	public static int calculateGatherDuration(Unit u, ResourceNode target) {
		switch (target.getType())
		{
		case GOLD_MINE:
			return u.getTemplate().getDurationGatherGold();
		case TREE:
			return u.getTemplate().getDurationGatherWood();
		default:
			throw new IllegalArgumentException(target.getType() + " is not a type supported by calculateGatherDuration()");
		}
	}
	/**
	 * This is a temporary debug method, remove it when you are done
	 */
	public static int calculateAttackDuration(Unit u, Unit target) {
		return u.getTemplate().getDurationAttack();
	}
	/**
	 * This is a temporary debug method, remove it when you are done
	 */
	public static int calculateProductionDuration(Unit u, @SuppressWarnings("rawtypes") Template t) {
		return t.getTimeCost();
	}
	/**
	 * A simple structure used in the construction of plans. 
	 * @author The Condor
	 *
	 */
	public static class MovePlan
	{
		public final boolean succeeded;
		public LinkedList<Action> actions;
		public int finalx;
		public int finaly;
		public MovePlan()
		{
			succeeded = false;
		}
		public MovePlan(LinkedList<Action> actions, int finalx, int finaly)
		{
			succeeded = true;
			this.actions = actions;
			this.finalx = finalx;
			this.finaly = finaly;
		}
		
	}
}
