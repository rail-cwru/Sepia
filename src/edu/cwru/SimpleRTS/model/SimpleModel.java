package edu.cwru.SimpleRTS.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.prefs.Preferences;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionFeedback;
import edu.cwru.SimpleRTS.action.ActionQueue;
import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.LocatedAction;
import edu.cwru.SimpleRTS.action.LocatedProductionAction;
import edu.cwru.SimpleRTS.action.ProductionAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.LoadingStateCreator;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.StateCreator;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTask;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
import edu.cwru.SimpleRTS.util.GameMap;
/**
 * <pre>
 * A "Simple" Model.
 * This model is sequential, processing most actions one at a time, and resolves
 * conflicts in this manner.
 * This model assumes all primitive actions take exactly one step to complete, 
 * and will disregard all evidence to the contrary.
 * </pre>
 */
public class SimpleModel implements Model {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -8289868580233478749L;
	private Random rand;
	private History history;
	private State state;
	private HashMap<Unit, ActionQueue> queuedActions;
	private SimplePlanner planner;
	private StateCreator restartTactic;
	private boolean verbose;
	public SimpleModel(State init, int seed, StateCreator restartTactic) {
		state = init;
		history = new History();
		for (Integer i : state.getPlayers())
			history.addPlayer(i);
		rand = new Random(seed);
		planner = new SimplePlanner(init);
		queuedActions = new HashMap<Unit, ActionQueue>();
		this.restartTactic = restartTactic;
		verbose = false;
	}
	public void setVerbosity(boolean verbose) {
		this.verbose = verbose;
	}
	

	
	@Override
	public void createNewWorld() {
		state = restartTactic.createState();
		history = new History();
		for (Integer i : state.getPlayers())
			history.addPlayer(i);
		queuedActions = new HashMap<Unit, ActionQueue>();
		planner = new SimplePlanner(state);
	}
	
	@Override
	public boolean isTerminated() {
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS").node("model");
		boolean terminated = true;
		if(prefs.getBoolean("Conquest", false))
			terminated = conquestTerminated();
		if(terminated && prefs.getBoolean("Midas", false))
			terminated = resourceGatheringTerminated(prefs);
		if(terminated && prefs.getBoolean("ManifestDestiny", false))
			terminated = buildingTerminated(prefs);
		terminated = terminated || state.getTurnNumber() > prefs.getInt("TimeLimit", Integer.MAX_VALUE);
		return terminated;
	}
	private boolean conquestTerminated() {
		int numLivePlayers = 0;
		for(Integer player : state.getPlayers())
		{
			if(state.getUnits(player).size() == 0)
			{
				continue;
			}
			for(Unit u : state.getUnits(player).values())
			{
				if(u.getCurrentHealth() > 0)
				{
					numLivePlayers++;
					break;
				}
			}
			if (numLivePlayers > 1)
				break;
			
		}
		return numLivePlayers <= 1;
	}
	private boolean resourceGatheringTerminated(Preferences prefs) {
		boolean resourcesGathered = true;
		int gold = prefs.getInt("RequiredGold", 0);
		int wood = prefs.getInt("RequiredWood", 0);
//		System.out.println("Agent.maxId() " + Agent.maxId());
		for(Integer player : state.getPlayers())
		{
			resourcesGathered = state.getResourceAmount(player, ResourceType.GOLD) >= gold &&
								state.getResourceAmount(player, ResourceType.WOOD) >= wood;
		}
		return resourcesGathered;
	}
	private boolean buildingTerminated(Preferences prefs) {
		boolean built = true;
		for(Integer i : state.getPlayers())
		{
			built = true;
			for(Template template : state.getTemplates(i).values())
			{
				int required = prefs.getInt("Required"+template.getName()+"Player"+i, 0);
				int actual = 0;
				if (required>0) //Only check if you need to find at least one
				{
					for(Unit u : state.getUnits(i).values())
					{
						if(u.getTemplate().equals(template))
							actual++;
						if(actual >= required) //if you found enough of a type of unit, you can stop looking for more
							break;
					}
				}
//				System.out.println("Player "+i+" has at least "+actual + "{"+template.getName()+"}s"+" (needed "+required+")");
				built = built && (actual >= required);
				if (!built) //if you haven't built one of the requirements, you can't have built all of them
					break;
			}
			if (built)
				break;
		}
		return built;
	}
	@Override
	public void addActions(Map<Integer, Action> actions, int sendingPlayerNumber) {
		for (Entry<Integer, Action> aent : actions.entrySet()) {
			
			int unitId = aent.getKey();
			Action a = aent.getValue();
			history.recordCommandRecieved(sendingPlayerNumber, state.getTurnNumber(), a);
			//If the unit is not the same as in the action, ignore the action
			if (a.getUnitId() != unitId)
			{
				history.recordActionFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDUNIT));
				continue;
			}
			//If the unit does not exist, ignore the action
			else if (state.getUnit(unitId) == null)
			{
				history.recordActionFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDUNIT));
				continue;
			}
			//If the unit is not the player's, ignore the action
			else if(state.getUnit(unitId).getPlayer() != sendingPlayerNumber)
			{
				history.recordActionFeedback(sendingPlayerNumber, state.getTurnNumber(), new ActionResult(a,ActionFeedback.INVALIDCONTROLLER));
				continue;
			}
			else
			{//Valid
				Unit actor = state.getUnit(unitId);
				ActionQueue queue = new ActionQueue(a, calculatePrimitives(a));
				queuedActions.put(actor, queue);
			}
			
			
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
			case FAILED:
				//The only primitive action needed to execute a primitive action is itself
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
				primitives = planner.planGather(actor, state.getResource(resourceId));
				break;
			case COMPOUNDATTACK:
				TargetedAction aAttack = (TargetedAction)action;
				int targetId = aAttack.getTargetId();
				primitives = planner.planAttack(actor, state.getUnit(targetId));
				break;
			case COMPOUNDPRODUCE:
				ProductionAction aProduce = (ProductionAction)action;
				int unitTemplateId = aProduce.getTemplateId();
				primitives = planner.planProduce(actor, (UnitTemplate)state.getTemplate(unitTemplateId));
				break;
			case COMPOUNDBUILD:
				LocatedProductionAction aBuild = (LocatedProductionAction)action;
				int buildTemplateId = aBuild.getTemplateId();
				primitives = planner.planBuild(actor, aBuild.getX(), aBuild.getY(), (UnitTemplate)state.getTemplate(buildTemplateId));
				break;
			case COMPOUNDDEPOSIT:
				TargetedAction aDeposit = (TargetedAction)action;
				int depotId = aDeposit.getTargetId();
				primitives = planner.planDeposit(actor, state.getUnit(depotId));
				break;
			default:
				primitives = null;
			
		}
		return primitives;
	}
	@Override
	public void executeStep() {
		
		//Set each agent to have no task
		for (Unit u : state.getUnits().values()) {
			u.deprecateOldView();
			u.setTask(UnitTask.Idle);
		}
		//Set each template to not keep the old view
		for (Integer player : state.getPlayers())
			for (Template t : state.getTemplates(player).values())
				t.deprecateOldView();
		
		//Run the Action
		for(ActionQueue queuedact : queuedActions.values()) 
		{
			if (verbose)
				System.out.println("Doing full action: "+queuedact.getFullAction());
			//Pull out the primitive
			if (queuedact.hasNext()) 
				// should it be "while" instead of "if" ?? 
				// well, do you mean that every round, only one primitive action can be taken by one agent?
				// so, even the agent returns a compound action, only the first primitive action will be executed?
				// ---Feng
				//if is right, it pops the first each time -Scott
			{
				Action a = queuedact.popPrimitive();
				if (verbose)
					System.out.println("Doing primative action: "+a);
				//Execute it
				Unit u = state.getUnit(a.getUnitId());
				
				if (u != null)
				{
					//Set the tasks and grab the common features
					int x = u.getxPosition();
					int y = u.getyPosition();
					int xPrime = 0;
					int yPrime = 0;
					Action fullact = queuedact.getFullAction();
					switch (fullact.getType()) {
					case PRIMITIVEMOVE:
					{
						u.setTask(UnitTask.Move);
						break;
					}
					case PRIMITIVEGATHER:
					{
						Direction d = ((DirectedAction)a).getDirection();
						xPrime = x + d.xComponent();
						yPrime = y + d.yComponent();				
						ResourceNode r = state.resourceAt(xPrime,yPrime);
						if (r!=null)
							u.setTask(r.getType()==ResourceNode.Type.GOLD_MINE?UnitTask.Gold:UnitTask.Wood);
						else
							u.setTask(UnitTask.Idle);
						break;
					}
					case COMPOUNDMOVE:
					{
						u.setTask(UnitTask.Move);
						break;
					}
					case COMPOUNDPRODUCE:
					{	
						u.setTask(UnitTask.Build);
						break;
					}
					case PRIMITIVEPRODUCE:
					{
						u.setTask(UnitTask.Build);
						break;
					}
					case COMPOUNDBUILD:
					{	
						u.setTask(UnitTask.Build);
						break;
					}
					case PRIMITIVEBUILD:
					{	
						u.setTask(UnitTask.Build);
						break;
					}
					case COMPOUNDATTACK:
					{
						u.setTask(UnitTask.Attack);
						break;
					}
					case PRIMITIVEATTACK:
					{
						u.setTask(UnitTask.Attack);
						break;
					}
					case PRIMITIVEDEPOSIT:
					{
						if (u.getCurrentCargoAmount() > 0)
							u.setTask(u.getCurrentCargoType()==ResourceType.GOLD?UnitTask.Gold:UnitTask.Wood);
						else
							u.setTask(UnitTask.Idle);
						break;
					}
					case COMPOUNDGATHER:
					{
						TargetedAction thisact = ((TargetedAction)fullact);
						ResourceNode r = state.getResource(thisact.getTargetId());
						if (r != null)
							u.setTask(r.getType()==ResourceNode.Type.GOLD_MINE?UnitTask.Gold:UnitTask.Wood);
						else
							u.setTask(UnitTask.Idle);
						break;
					}
					}
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
					
					
					//Gather the last of the information and actually execute the actions
					int timestried=0;
					boolean failedtry=true;
					boolean wrongtype=false;
					boolean fullisprimitive=ActionType.isPrimitive(a.getType());
					//recalculate and try again once if it has failed, so long as the full action is not primitive (since primitives will recalculate to the same as before) and not the wrong type (since something is wrong if it is the wrong type)
					while ((timestried<1 || timestried<2&&failedtry && !fullisprimitive) &&!wrongtype)
					{
						timestried++;
						failedtry = false;
						switch(a.getType())
						{
							case PRIMITIVEMOVE:
								if (!(a instanceof DirectedAction))
								{
									wrongtype=true;
									break;
								}
								if(state.inBounds(xPrime, yPrime) && u.canMove() && empty(xPrime,yPrime)) {
									state.moveUnit(u, ((DirectedAction)a).getDirection());
								}
								else {
									failedtry=true;
									queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
								}
								break;
							case PRIMITIVEGATHER:
								if (!(a instanceof DirectedAction))
								{
									wrongtype=true;
									break;
								}
								boolean failed=false;
								ResourceNode resource = state.resourceAt(xPrime, yPrime);
								if(resource == null) {
									failed=true;
								}
								else if(!u.canGather()) {
									failed=true;
								}
								else {
									int amountPickedUp = resource.reduceAmountRemaining(u.getTemplate().getGatherRate(resource.getType()));
									u.setCargo(resource.getResourceType(), amountPickedUp);
									history.recordPickupResource(u, resource, amountPickedUp, state);
								}
								if (failed) {
									failedtry=true;
									queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
								}
								break;
							case PRIMITIVEDEPOSIT:
								if (!(a instanceof DirectedAction))
								{
									wrongtype=true;
									break;
								}
								//only can do a primative if you are in the right position
									Unit townHall = state.unitAt(xPrime, yPrime);
									boolean canAccept=false;
									if (townHall!=null && townHall.getPlayer() == u.getPlayer())
									{
										if (u.getCurrentCargoType() == ResourceType.GOLD && townHall.getTemplate().canAcceptGold())
											canAccept=true;
										else if (u.getCurrentCargoType() == ResourceType.WOOD && townHall.getTemplate().canAcceptWood())
											canAccept=true;
									}
									if(!canAccept)
									{
										failedtry=true;
										queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
										break;
									}
									else {
										int agent = u.getPlayer();
										history.recordDropoffResource(u, townHall, state);
										state.depositResources(agent, u.getCurrentCargoType(), u.getCurrentCargoAmount());
										u.clearCargo();
										
										break;
									}
							case PRIMITIVEATTACK:
								if (!(a instanceof TargetedAction))
								{
									wrongtype=true;
									break;
								}
								Unit target = state.getUnit(((TargetedAction)a).getTargetId());
								if (target!=null)
								{
									if (u.getTemplate().getRange() >= DistanceMetrics.chebyshevDistance(u.getxPosition(),u.getyPosition(), target.getxPosition(), target.getyPosition()))
									{
										int damage = calculateDamage(u,target);
										history.recordDamage(u, target, damage, state);
										target.setHP(Math.max(target.getCurrentHealth()-damage,0));
									}
									else //out of range
									{
										failedtry=true;
										queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
									}
								}
								else
								{
									failedtry=true;
									queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
								}
								break;
							case PRIMITIVEBUILD:
							{
								if (!(a instanceof ProductionAction))
								{
									wrongtype=true;
									break;
								}
								if (queuedact.getFullAction().getType() == ActionType.COMPOUNDBUILD && queuedact.getFullAction() instanceof LocatedProductionAction)
								{
									LocatedProductionAction fullbuild = (LocatedProductionAction) queuedact.getFullAction();
									if (fullbuild.getX() != u.getxPosition() || fullbuild.getY() != u.getyPosition())
									{
										failedtry=true;
										queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
										break;
									}
								}
								UnitTemplate template = (UnitTemplate)state.getTemplate(((ProductionAction)a).getTemplateId());
								if (u.getTemplate().canProduce(template) && template.canProduce(state.getView(Agent.OBSERVER_ID)))
								{
									
										Unit building = template.produceInstance(state);
	//									System.out.println("Checking on bug: unit with id "+u.ID);
	//									System.out.println(state.getUnit(u.ID));
										int[] newxy = state.getClosestPosition(x,y);
										if (state.tryProduceUnit(building,newxy[0],newxy[1]))
										{
	//									System.out.println(state.getUnit(u.ID));
										history.recordBirth(building, u, state);
										
	//									System.out.println(state.getUnit(u.ID));
										}
									
								}
								else //it can't produce the appropriate thing, or the thing's prereqs aren't met
								{
									failedtry=true;
								}
								
								break;
							}
							case PRIMITIVEPRODUCE:
							{
								if (!(a instanceof ProductionAction))
								{
									wrongtype=true;
									break;
								}
								Template template = state.getTemplate(((ProductionAction)a).getTemplateId());
								//check if it is even capable of producing the
								if (u.getTemplate().canProduce(template) && template.canProduce(state.getView(Agent.OBSERVER_ID)))
								{
									if (template instanceof UnitTemplate)
									{
										Unit produced = ((UnitTemplate)template).produceInstance(state);
										int[] newxy = state.getClosestPosition(x,y);
										if (state.tryProduceUnit(produced,newxy[0],newxy[1]))
										{
											history.recordBirth(produced, u, state);
										}
									}
									else if (template instanceof UpgradeTemplate) {
										UpgradeTemplate upgradetemplate = ((UpgradeTemplate)template);
										if (state.tryProduceUpgrade(upgradetemplate.produceInstance(state)))
										{
											history.recordUpgrade(upgradetemplate,u, state);
										}
									}
								}
								else//can't produce it, or prereqs aren't met
								{
									failedtry=true;
								}
//								System.out.println(template.getName() + " takes "+template.timeCost);
//								System.out.println("Produced"+u.getAmountProduced());
								
								
								break;
							}
							case FAILED:
							{
								failedtry=true;
								queuedact.resetPrimitives(calculatePrimitives(queuedact.getFullAction()));
								break;
							}
							case FAILEDPERMANENTLY:
							{
								u.setTask(UnitTask.Idle);
								break;
							}
						}
						if (wrongtype)
						{
							//if it had the wrong type, then either the planner is bugged (unlikely) or the user provided a bad primitive action
							//either way, record it as failed and toss it
							history.recordActionFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(queuedact.getFullAction(),ActionFeedback.INVALIDTYPE));
							queuedActions.remove(queuedact.getFullAction());
						}
						else if (!failedtry && a.getType() != ActionType.FAILEDPERMANENTLY)
						{
							history.recordPrimitiveExecuted(u.getPlayer(), state.getTurnNumber(), a);
							if (!queuedact.hasNext())
							{
								history.recordActionFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(queuedact.getFullAction(),ActionFeedback.COMPLETED));
								queuedActions.remove(queuedact.getFullAction());
							}
							else
							{
								history.recordActionFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(queuedact.getFullAction(),ActionFeedback.INCOMPLETE));
							}
						}
						else if (a.getType()==ActionType.FAILEDPERMANENTLY || failedtry && fullisprimitive)
						{
							history.recordActionFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(queuedact.getFullAction(),ActionFeedback.FAILED));
							queuedActions.remove(queuedact.getFullAction());
							
						}
						else
						{
							history.recordActionFeedback(u.getPlayer(), state.getTurnNumber(), new ActionResult(queuedact.getFullAction(),ActionFeedback.INCOMPLETEMAYBESTUCK));
						}
					}
				}
			}
		}
		
		
		//Take all the dead units and clear them
		//Find the dead units
		Map<Integer, Unit> allunits = state.getUnits();
		List<Integer> dead= new ArrayList<Integer>(allunits.size());
		for (Unit u : allunits.values()) {
			if (u.getCurrentHealth() <= 0)
			{
				history.recordDeath(u, state);
				dead.add(u.ID);
			}
		}
		//Remove them
		for (int uid : dead)
		{
			state.removeUnit(uid);
		}
		//Take all of the used up resources and get rid of them
		List<ResourceNode> allnodes = state.getResources();
		List<Integer> usedup= new ArrayList<Integer>(allnodes.size());
		for (ResourceNode r : allnodes) {
			if (r.getAmountRemaining() <= 0)
			{
				history.recordExhaustedResourceNode(r, state);
				usedup.add(r.ID);
			}
		}
		//Remove the used up resource nodes
		for (int rid : usedup)
		{
			
			state.removeResourceNode(rid);
		}
		
		state.incrementTurn();
	}

	private int calculateDamage(Unit attacker, Unit defender)
	{
		int armor = defender.getTemplate().getArmor();
		int damage;
		int basic_damage;
		int piercing_damage;

		basic_damage = attacker.getTemplate().getBasicAttack();
		piercing_damage = attacker.getTemplate().getPiercingAttack();
//		if (bloodlust) {
//			basic_damage *= 2;
//			piercing_damage *= 2;
//		}

		damage = (basic_damage - armor) > 1 ?
			(basic_damage - armor) : 1;
		damage += piercing_damage;
		damage -= rand.nextInt() % ((damage + 2) / 2);

		return damage;
	}
	private boolean empty(int x, int y) {
		return state.unitAt(x, y) == null && state.resourceAt(x, y) == null;
	}
	@Override
	public State.StateView getState(int player) {
		return state.getView(player);
	}
	@Override
	public History.HistoryView getHistory(int player) {
		return history.getView(player);
	}
	public void save(String filename) {
		GameMap.storeState(filename, state);
	}
}
