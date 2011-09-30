package edu.cwru.SimpleRTS.agent;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.Log.BirthLog;
import edu.cwru.SimpleRTS.Log.DamageLog;
import edu.cwru.SimpleRTS.Log.DeathLog;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

public class CombatAgent extends Agent{

	Map<Integer, Integer> unitOrders;
	int[] enemies;
	boolean verbose;
	protected CombatAgent(int playernum, int[] enemies, boolean verbose) {
		super(playernum);
		//copy the list of enemies
		this.verbose = verbose;
		this.enemies = new int[enemies.length];
		for (int i = 0; i<enemies.length;i++) {
			this.enemies[i] = enemies[i];
		}
	}

	/**
	 * Start a new trial.
	 * Uses the StateView, which contains information in logs, resources, and units
	 * Some of the unit information may be in the template
	 * @param newstate
	 * @return
	 */
	@Override
	public Builder<Integer, Action> initialStep(StateView newstate) {
		//Do setup things for a new game
			//Clear the unit orders
			unitOrders = new HashMap<Integer, Integer>();
			//Put all of the units into the orders.
			for (Integer uid : newstate.getUnitIds(playernum)) {
				if (verbose)
					System.out.println("Adding " + uid);
				unitOrders.put(uid, null);
			}
			doAggro(newstate);
		Builder<Integer, Action> myAction = getAction(newstate);
		return myAction;
	}

	@Override
	public Builder<Integer, Action> middleStep(StateView newstate) {
		
		//update its list of units
		for (BirthLog birth : newstate.getEventLog().getBirths(newstate.getEventLog().getCurrentRound())) {
			if (playernum == birth.getPlayer()) {
				unitOrders.put(birth.getNewUnitID(), null);
			}
		}
		for (DeathLog death : newstate.getEventLog().getDeaths(newstate.getEventLog().getCurrentRound())) {
			if (playernum == death.getPlayer()) {
				unitOrders.remove(death.getDeadUnitID());
			}
			if (unitOrders.containsValue(death.getDeadUnitID())) {
				for (Map.Entry<Integer, Integer> order: unitOrders.entrySet()) {
					if (order.getValue() == death.getDeadUnitID()) {
						unitOrders.put(order.getKey(),null);
					}
				}
			}
		}
		if (verbose)
		{
			//Report the damage dealt by and to your units
			for (DamageLog damagereport : newstate.getEventLog().getDamage(newstate.getEventLog().getCurrentRound())) {
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
	
	
	private Builder<Integer, Action> getAction(StateView currentstate) {
		Builder<Integer, Action> actions = new ImmutableMap.Builder<Integer, Action>();
		for (Map.Entry<Integer, Integer> order : unitOrders.entrySet()) {
			if (verbose)
				System.out.println("Order: " + order.getKey() + " attacking " + (order.getValue()==null?"noone":order.getValue()));
			if (order.getValue() != null) //if it has a target
			{
				//Assign the unit an action where it attacks it's target
				actions.put(order.getKey(),Action.createCompoundAttack(order.getKey(), order.getValue()));
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
	
}
