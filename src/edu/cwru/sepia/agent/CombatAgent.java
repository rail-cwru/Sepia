/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.environment.model.history.BirthLog;
import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.DeathLog;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.Direction;
import edu.cwru.sepia.util.DistanceMetrics;

public class CombatAgent extends Agent{
	private static final long serialVersionUID = 1L;
	
	/**
	 * A list of units by id that also contains orders given to each unit
	 */
	private Map<Integer, Action> unitOrders;
	/**
	 * The player numbers that this guy attacks
	 */
	private int[] enemies;
	private boolean wanderwhenidle;	
	private int lastStepMovedIn;
	
	public CombatAgent(int playernum, String[] otherargs) {
		super(playernum);
		if (otherargs == null || otherargs.length == 0)
		{
			setDefaults();
		}
		else
		{
			//copy the list of enemies
			this.verbose = Boolean.parseBoolean(otherargs[2]);
			String[] enemystrs = otherargs[0].split(" ");
			this.enemies = new int[enemystrs.length];
			for (int i = 0; i<enemies.length;i++) {
				this.enemies[i] = Integer.parseInt(enemystrs[i]);
			}
			this.wanderwhenidle = Boolean.parseBoolean(otherargs[1]);
		}
	}
	/**
	 * Construct a combat agent.
	 * @param playerNum The player number of this agent
	 * @param enemies The player numbers of enemies
	 * @param wanderWhileIdle Whether to move in random directions when you have nothing better to do.
	 * @param verbose Verbosity 
	 */
	public CombatAgent(int playerNum, int[] enemies, boolean wanderWhileIdle, boolean verbose) {
		super(playerNum);
		this.enemies=new int[enemies.length];
		System.arraycopy(enemies, 0, this.enemies, 0, enemies.length);
		this.wanderwhenidle=wanderWhileIdle;
		this.verbose = verbose;
	}
	
	public CombatAgent(int playerNum) {
		super(playerNum);
		setDefaults();
	}
	
	/**
	 * Set the parameters to the default values.
	 * For enemies, they cannot be immediately set, so leave them as null to be interpreted later.
	 */
	private void setDefaults()
	{
		verbose = false;
		wanderwhenidle = true;
		enemies = null;
	}
	/**
	 * Start a new trial.
	 * Uses the StateView, which contains information in logs, resources, and units
	 * Some of the unit information may be in the template
	 * @param newstate
	 * @return
	 */
	@Override
	public Map<Integer, Action> initialStep(StateView newstate, History.HistoryView statehistory) {
		//Do setup things for a new game
		
		//if no enemies were set, then everyone else is the enemy
		if (enemies == null)
		{
			//actually count the enemies, just in case there is some kind of duplicate or if your player number isn't there
			int numenemies = 0;
			for (Integer i : newstate.getPlayerNumbers())
			{
				if (i!=getPlayerNumber())
				{
					numenemies++;
				}
			}
			enemies = new int[numenemies];
			int itr = 0;
			for (Integer i : newstate.getPlayerNumbers())
			{
				if (i!=getPlayerNumber())
				{
					enemies[itr++]=i;
				}
			}
		}
		
			//Clear the unit orders
			unitOrders = new HashMap<Integer, Action>();
			//Put all of the units into the orders.
			for (Integer uid : newstate.getUnitIds(playernum)) {
				unitOrders.put(uid, null);
			}
			doAggro(newstate);
		Map<Integer, Action> myAction = getAction(newstate);
		lastStepMovedIn = newstate.getTurnNumber();
		return myAction;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate, History.HistoryView statehistory) {
		
		//Read in the logs for every step that occurred since it was last this player's turn
		for (int stepToRead = lastStepMovedIn; stepToRead < newstate.getTurnNumber(); stepToRead++)
		{
			//update its list of units
			for (BirthLog birth : statehistory.getBirthLogs(stepToRead)) {
				if (playernum == birth.getController()) {
					unitOrders.put(birth.getNewUnitID(), null);
				}
			}
			List<Integer> toRemove = new LinkedList<Integer>();
			List<Integer> toUnorder = new LinkedList<Integer>();
			for (DeathLog death : statehistory.getDeathLogs(stepToRead)) {
				//Check if the dead unit is mine
				if (playernum == death.getController()) {
					toRemove.add(death.getDeadUnitID());
				}
				//check if anyone is attacking the dead unit, and tell them to stop
					for (Map.Entry<Integer, Action> order: unitOrders.entrySet()) {
						
						if (order.getValue()!=null)
						{
							Action attackthedeadunit = Action.createCompoundAttack(order.getKey(), death.getDeadUnitID());
							if (attackthedeadunit.equals(order.getValue())) {
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
				for (DamageLog damagereport : statehistory.getDamageLogs(stepToRead)) {
					if (damagereport.getAttackerController() == playernum) {
						writeLineVisual(damagereport.getAttackerID() + " hit " + damagereport.getDefenderID() + " for " +damagereport.getDamage()+ " damage");
					}
					if (damagereport.getDefenderController() == playernum) {
						writeLineVisual(damagereport.getDefenderID() + " was hit by " + damagereport.getAttackerID() + " for " +damagereport.getDamage()+ " damage");
					}
					
				}
			}
			//Update it's list of orders by checking for completions and failures and removing those
			for (ActionResult feedback : statehistory.getCommandFeedback(playernum, stepToRead).values())
			{
				
				if (feedback.getFeedback() != ActionFeedback.INCOMPLETE)//Everything but incomplete is some form of failure or complete
				{
					//because the feedback mixes primitive feedback on duratives and compound feedback on primitives, need to check if it is the right action
					Action action = feedback.getAction();
					int unitid = action.getUnitId();
					Action order = unitOrders.get(unitid);		//if this gives nullpointer, then there was some failure in registering units with unitOrders
					//check if the completion is the same level as the order
					if (action.equals(order))
					{
						//remove the order, as it is complete or failed
						unitOrders.put(unitid, null);
					}
				}
			}
		}
		//Calculate what the orders should be
		doAggro(newstate);
		
		lastStepMovedIn = newstate.getTurnNumber();
		return getAction(newstate);
	}

	@Override
	public void terminalStep(StateView newstate, History.HistoryView statehistory) {
		//A non learning agent needn't do anything at the final step
		lastStepMovedIn = newstate.getTurnNumber();
	}
	
	
	private Map<Integer, Action> getAction(StateView currentstate) {
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		for (Map.Entry<Integer, Action> order : unitOrders.entrySet()) {
			if (verbose)
				writeLineVisual("Combat Agent for plr "+playernum+"'s order: " + order.getKey() + " is to use " + order.getValue());
			if (order.getValue() != null) //if it has an order
			{
				//Assign the unit its action
				actions.put(order.getKey(), order.getValue());
			}

		}
		return actions;
	}
	private void doAggro(StateView state) {
		for (Map.Entry<Integer, Action> order : unitOrders.entrySet()) {
			if (order.getValue() == null) //if it has no orders  
			{
				//check all of the other units to check for an enemy that is in sight range
				UnitView u = state.getUnit(order.getKey());
				int ux = u.getXPosition();
				int uy = u.getYPosition();
				int sightradius = u.getTemplateView().getSightRange();
				boolean foundsomething = false;
				for (int enemy : enemies) {
					
					for (Integer enemyUnitID : state.getUnitIds(enemy)) {
						UnitView enemyUnit = state.getUnit(enemyUnitID);
						//get the chebyshev distance (which is the base distance for warcraft 2)
						if (sightradius > DistanceMetrics.chebyshevDistance(ux, uy, enemyUnit.getXPosition(), enemyUnit.getYPosition()) ) {
							//(if you can see it)
							foundsomething=true;
							unitOrders.put(order.getKey(), Action.createCompoundAttack(order.getKey(), enemyUnitID));
							break;
						}
					}
					if (foundsomething)
						break;
				}
				if (!foundsomething)
				{
					//couldn't find an enemy, so wander maybe
					if (wanderwhenidle)
					{
						Direction direction = Direction.values()[(int)(Math.random()*Direction.values().length)];
						int newx = ux+direction.xComponent();
						int newy = uy+direction.yComponent();
						Action a = Action.createCompoundMove(u.getID(), newx, newy);
						unitOrders.put(order.getKey(), a);
					}
				}
				
			}
		}
	}


	public static String getUsage() {
		
		return "It takes three parameters (--agentparam): a space seperated array of enemy player numbers, a boolean for whether it should wander, and a boolean for verbosity";
	}
	@Override
	public void savePlayerData(OutputStream os) {
		//this agent lacks learning and so has nothing to persist.
		
	}
	@Override
	public void loadPlayerData(InputStream is) {
		//this agent lacks learning and so has nothing to persist.
	}
	
}
