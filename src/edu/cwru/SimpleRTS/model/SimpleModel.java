package edu.cwru.SimpleRTS.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.Configuration;

public class SimpleModel implements Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8289868580233478749L;
	private Random rand;
	private State state;
	private HashMap<Unit, ActionQueue> queuedPrimitives;
	private SimplePlanner planner;
	public SimpleModel(State init, int seed) {
		state = init;
		rand = new Random(seed);
		planner = new SimplePlanner(init);
		queuedPrimitives = new HashMap<Unit, ActionQueue>();
	}
	
	
	@Override
	public void createNewWorld() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActions(Action[] action) {
		for (Action a : action) {
			//NOTE: maybe make this not recalculate actions automatically
			Unit actor = state.getUnit(a.getUnitId());
			ActionQueue queue = new ActionQueue(a, calculatePrimitives(a));
			queuedPrimitives.put(actor, queue);
		}
	}
	private LinkedList<Action> calculatePrimitives(Action action) {
		LinkedList<Action> primitives = null;
		//TODO: add additional actions to complete this
		switch (action.getType()) {
			case PRIMITIVEMOVE:
			case PRIMITIVEATTACK:
			case PRIMITIVEGATHER:
			case PRIMITIVEDEPOSIT:
			case PRIMITIVEBUILD:
			case PRIMITIVEPRODUCE:
			case PRIMITIVEUPGRADE:
				primitives = new LinkedList<Action>();
				primitives.add(action);
				break;
			case COMPOUNDMOVE:
				Unit acter = state.getUnit(action.getUnitId());
				LocatedAction a = (LocatedAction)action;
				primitives = planner.planMove(acter.getxPosition(), acter.getyPosition(),a.getX(),a.getY(),0);
				break;
			default:
				primitives = null;
			
		}
		return primitives;
	}
	@Override
	public void executeStep() {
		
		//Run the Action
		//TODO: make things happen appropriately in the case of dead unit, dead target, etc
		//TODO: add the remaining primitive actions
		for(ActionQueue queuedact : queuedPrimitives.values()) 
		{
			//Pull out the primitive
			if (queuedact.hasNext())
			{
				Action a = queuedact.popPrimitive();
				//Execute it
				Unit u = state.getUnit(a.getUnitId());
				int x = u.getxPosition();
				int y = u.getyPosition();
				int xPrime = 0;
				int yPrime = 0;
				if(a instanceof DirectedAction)
				{
					Direction d = ((DirectedAction)a).getDirection();
					xPrime = x + d.xComponent();
					yPrime = y + d.yComponent();
				}
				else if(a instanceof LocatedAction)
				{
					xPrime = x + ((LocatedAction)a).getX();
					yPrime = y + ((LocatedAction)a).getY();
				}
				switch(a.getType())
				{
					case PRIMITIVEMOVE:
						if(state.inBounds(xPrime, yPrime) && u.canMove() && empty(xPrime,yPrime)) {
							u.setxPosition(xPrime);
							u.setyPosition(yPrime);
						}
						else {
							queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
						}
						break;
					case PRIMITIVEGATHER:
						boolean failed=false;
						Resource resource = state.resourceAt(xPrime, yPrime);
						if(resource == null) {
							failed=true;
						}
						else if(!u.canGather()) {
							failed=true;
						}
						else {
							int amountToExtract = Integer.parseInt(Configuration.getInstance().get(
																			resource.getType()+"GatherRate"));
							amountToExtract = Math.min(amountToExtract, resource.getAmountRemaining());
							u.pickUpResource(resource.getType(), amountToExtract);
							resource.setAmountRemaining(resource.getAmountRemaining()-amountToExtract);
						}
						if (failed) {
							queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
						}
						break;
					case PRIMITIVEDEPOSIT:
						Unit actor = state.getUnit(a.getUnitId());
						Unit townHall = state.unitAt(xPrime, yPrime);
						if(townHall == null || !"TownHall".equals(townHall.getTemplate().getUnitName()))
						{
							queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
							break;
						}
						int agent = actor.getPlayer();
						state.addResourceAmount(agent, actor.getCurrentCargoType(), actor.getCurrentCargoAmount());
						actor.clearCargo();
						break;
					case PRIMITIVEATTACK:
						Unit target = state.getUnit(((TargetedAction)a).getTargetId());
						if (u.getTemplate().getRange() >= getRange(u, target))
						{
							int damage = calculateDamage(u,target);
							//TODO: log damage
							target.takeDamage(damage);
						}
						else
							queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
						break;
				}
			}
		}
		// Declare Deaths
		
		
	}
	/**
	 * Get the range between two units, which is a chebyshev distance (IE, like manhattan, but with diagonals too)
	 * @param unit1
	 * @param unit2
	 * @return
	 */
	private int getRange(Unit unit1, Unit unit2) {
		return Math.max(Math.abs(unit1.getxPosition()-unit2.getxPosition()), Math.abs(unit1.getyPosition()-unit2.getyPosition()));
	}

	private int calculateDamage(Unit attacker, Unit defender)
	{
		int basic = attacker.getTemplate().getBasicAttackDiff() != 0 ?rand.nextInt(attacker.getTemplate().getBasicAttackDiff())+attacker.getTemplate().getBasicAttackLow():attacker.getTemplate().getBasicAttackLow();
		int b = rand.nextBoolean()?basic:(int)Math.ceil(basic/2);
		int p = rand.nextBoolean()?attacker.getTemplate().getPiercingAttack():(int)Math.ceil(attacker.getTemplate().getPiercingAttack()/2);
		return Math.max(0, b-defender.getTemplate().getArmor())+p;
	}
	private boolean empty(int x, int y) {
		return state.unitAt(x, y) == null && state.resourceAt(x, y) == null;
	}
	@Override
	public State.StateView getState() {
		return state.getView();
	}

}
