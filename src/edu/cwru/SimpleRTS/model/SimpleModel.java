package edu.cwru.SimpleRTS.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
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
		int numLivePlayers = 0;
		for(int i = 0; i <= Agent.maxId() && numLivePlayers < 2; i++)
		{
			for(Unit u : state.getUnits(i))
			{
				if(u.getCurrentHealth() > 0)
				{
					numLivePlayers++;
					break;
				}
			}
		}
		return numLivePlayers > 1;
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
		Unit actor = state.getUnit(action.getUnitId());
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
				LocatedAction aMove = (LocatedAction)action;
				primitives = planner.planMove(actor, aMove.getX(), aMove.getY());
				break;
			case COMPOUNDGATHER:
				TargetedAction aGather = (TargetedAction)action;
				int resourceId = aGather.getTargetId();
				primitives = planner.planGather(actor, state.getResource(resourceId), 0);
				break;
			case COMPOUNDATTACK:
				TargetedAction aAttack = (TargetedAction)action;
				int unitId = aAttack.getTargetId();
				primitives = planner.planAttack(actor, state.getUnit(unitId));
				break;
			case COMPOUNDPRODUCE:
				ProductionAction aProduce = (ProductionAction)action;
				int unitTemplateId = aProduce.getTemplateId();
				primitives = planner.planProduce(actor, (UnitTemplate)state.getTemplate(unitTemplateId));
				break;
			case COMPOUNDUPGRADE:
				ProductionAction aUpgrade = (ProductionAction)action;
				int upgradeTemplateId = aUpgrade.getTemplateId();
				primitives = planner.planUpgrade(actor, (UpgradeTemplate)state.getTemplate(upgradeTemplateId));
				break;
			case COMPOUNDBUILD:
				LocatedProductionAction aBuild = (LocatedProductionAction)action;
				int buildTemplateId = aBuild.getTemplateId();
				primitives = planner.planBuild(actor, aBuild.getX(), aBuild.getY(), (UnitTemplate)state.getTemplate(buildTemplateId));
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
				// should it be "while" instead of "if" ?? 
				// well, do you mean that every round, only one primitive action can be taken by one agent?
				// so, even the agent returns a compound action, only the first primitive action will be executed?
				// ---Feng
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
						Unit townHall = state.unitAt(xPrime, yPrime);
						if(townHall == null || !"TownHall".equals(townHall.getTemplate().getUnitName()))
						{
							queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
							break;
						}
						int agent = u.getPlayer();
						state.addResourceAmount(agent, u.getCurrentCargoType(), u.getCurrentCargoAmount());
						u.clearCargo();
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
					case PRIMITIVEBUILD:
					{
						UnitTemplate template = (UnitTemplate)state.getTemplate(((ProductionAction)a).getTemplateId());
						u.incrementProduction(template);
						if (template.timeCost == u.getAmountProduced())
						{
							Unit building = template.produceInstance();
							building.setxPosition(x);
							building.setyPosition(y);
							
							state.addUnit(building);
						}
						int[] newxy = state.getClosestPosition(x,y);
						u.setxPosition(newxy[0]);
						u.setyPosition(newxy[1]);
						break;
					}
					case PRIMITIVEPRODUCE:
					{
						UnitTemplate template = (UnitTemplate)state.getTemplate(((ProductionAction)a).getTemplateId());
						u.incrementProduction(template);
						if (template.timeCost == u.getAmountProduced())
						{
							Unit produced = template.produceInstance();
							int[] newxy = state.getClosestPosition(x,y);
							produced.setxPosition(newxy[0]);
							produced.setyPosition(newxy[1]);
							state.addUnit(produced);
						}
						
						break;
					}
					
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
