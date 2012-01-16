package edu.cwru.SimpleRTS.agent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.Log.BirthLog;
import edu.cwru.SimpleRTS.Log.DamageLog;
import edu.cwru.SimpleRTS.Log.DeathLog;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

public class CombatAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Integer> unitOrders;
	private int[] enemies;
	private boolean wanderwhenidle;
	public CombatAgent(int playernum, String[] otherargs) {
		super(playernum);
		//copy the list of enemies
		this.verbose = Boolean.parseBoolean(otherargs[2]);
		String[] enemystrs = otherargs[0].split(" ");
		this.enemies = new int[enemystrs.length];
		for (int i = 0; i<enemies.length;i++) {
			this.enemies[i] = Integer.parseInt(enemystrs[i]);
		}
		this.wanderwhenidle = Boolean.parseBoolean(otherargs[1]);
	}


	/**
	 * Start a new trial.
	 * Uses the StateView, which contains information in logs, resources, and units
	 * Some of the unit information may be in the template
	 * @param newstate
	 * @return
	 */
	@Override
	public Map<Integer, Action> initialStep(StateView newstate) {
		//Do setup things for a new game
			//Clear the unit orders
			unitOrders = new HashMap<Integer, Integer>();
			//Put all of the units into the orders.
			for (Integer uid : newstate.getUnitIds(playernum)) {
				unitOrders.put(uid, null);
			}
			doAggro(newstate);
		Map<Integer, Action> myAction = getAction(newstate);
		return myAction;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate) {
		
		//update its list of units
		for (BirthLog birth : newstate.getEventLog().getBirths(newstate.getEventLog().getLastRound())) {
			if (playernum == birth.getPlayer()) {
				unitOrders.put(birth.getNewUnitID(), null);
			}
		}
		List<Integer> toRemove = new LinkedList<Integer>();
		List<Integer> toUnorder = new LinkedList<Integer>();
		for (DeathLog death : newstate.getEventLog().getDeaths(newstate.getEventLog().getLastRound())) {
			if (playernum == death.getPlayer()) {
				toRemove.add(death.getDeadUnitID());
			}
			if (unitOrders.containsValue(death.getDeadUnitID())) {
				for (Map.Entry<Integer, Integer> order: unitOrders.entrySet()) {
					if (order.getValue() != null && death.getDeadUnitID() == order.getValue()) {
						toUnorder.add(order.getKey());
						
					}
				}
			}
		}
		for (Integer i : toUnorder){
			unitOrders.put(i,null);
		}
		for (Integer i : toRemove) {
			unitOrders.remove(i);
		}
		
		if (verbose)
		{
			//Report the damage dealt by and to your units
			for (DamageLog damagereport : newstate.getEventLog().getDamage(newstate.getEventLog().getLastRound())) {
				if (damagereport.getAttackerController() == playernum) {
					System.out.println(damagereport.getAttackerID() + " hit " + damagereport.getDefenderID() + " for " +damagereport.getDamage()+ " damage");
				}
				if (damagereport.getDefenderController() == playernum) {
					System.out.println(damagereport.getDefenderID() + " was hit by " + damagereport.getAttackerID() + " for " +damagereport.getDamage()+ " damage");
				}
				
			}
		}
		doAggro(newstate);
		return getAction(newstate);
	}

	@Override
	public void terminalStep(StateView newstate) {
		//A non learning agent needn't do anything at the final step
		
	}
	
	
	private Map<Integer, Action> getAction(StateView currentstate) {
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		for (Map.Entry<Integer, Integer> order : unitOrders.entrySet()) {
			if (verbose)
				System.out.println("Combat Agent for plr "+playernum+"'s order: " + order.getKey() + " attacking " + (order.getValue()==null?"noone":order.getValue()));
			if (order.getValue() != null) //if it has a target
			{
				//Assign the unit an action where it attacks it's target
				actions.put(order.getKey(),Action.createCompoundAttack(order.getKey(), order.getValue()));
			}
			else if (wanderwhenidle) {
				int dir = (int)(Math.random()*8);
				Action a = new DirectedAction(order.getKey(), ActionType.PRIMITIVEMOVE, Direction.values()[dir]);
				actions.put(order.getKey(), a);
			}
		}
		return actions;
	}
	private void doAggro(StateView state) {
		for (Map.Entry<Integer, Integer> order : unitOrders.entrySet()) {
			if (order.getValue() == null) //if it has no orders  
			{
				//check all of the other units to check for an enemy that is in sight range
				UnitView u = state.getUnit(order.getKey());
				int ux = u.getXPosition();
				int uy = u.getYPosition();
				int sightradius = u.getTemplateView().getSightRange();
				for (int enemy : enemies) {
					boolean foundsomething = false;
					for (Integer enemyUnitID : state.getUnitIds(enemy)) {
						UnitView enemyUnit = state.getUnit(enemyUnitID);
						//get the chebyshev distance (which is the base distance for warcraft 2)
						if (sightradius > DistanceMetrics.chebyshevDistance(ux, uy, enemyUnit.getXPosition(), enemyUnit.getYPosition()) ) {
							//(if you can see it)
							foundsomething=true;
							unitOrders.put(order.getKey(), enemyUnitID);
							break;
						}
					}
					if (foundsomething)
						break;
				}
			}
		}
	}


	public static String getUsage() {
		
		return "It takes three parameters (--agentparam): a space seperated array of enemy player numbers, a boolean for whether it should wander, and a boolean for verbosity";
	}
	
}
