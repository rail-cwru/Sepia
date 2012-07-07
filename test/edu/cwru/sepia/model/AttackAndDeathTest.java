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
package edu.cwru.sepia.model;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.model.LessSimpleModel;
import edu.cwru.sepia.environment.model.Model;
import edu.cwru.sepia.environment.model.SimplePlanner;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.unit.Unit;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.util.DistanceMetrics;
import edu.cwru.sepia.util.TypeLoader;

public class AttackAndDeathTest {

	static Model model;
	static SimplePlanner planner;
	static List<Template<?>> templates;
	static State state;
	static int player=0;
	static Unit test1target;
	static Unit test1shooter;
	static Unit test2target;
	static Unit test2shooter;
	static Unit test3unit1;
	static Unit test3unit2;
	@BeforeClass
	public static void loadTemplates() throws Exception {
		State.StateBuilder builder = new State.StateBuilder();
		state = builder.build();
		templates = TypeLoader.loadFromFile("data/unit_templates",player,state);		
		System.out.println("Sucessfully loaded templates");
		
		builder.setSize(15,15);
		for (@SuppressWarnings("rawtypes") Template t : templates) {
			builder.addTemplate(t);
		}
		
		
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Footman")).produceInstance(state);
			test1shooter = u;
			builder.addUnit(u,0,0);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Footman")).produceInstance(state);
			test1target = u;
			builder.addUnit(u,0,0);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Archer")).produceInstance(state);
			test2shooter = u;
			builder.addUnit(u,0,0);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Footman")).produceInstance(state);
			test2target = u;
			builder.addUnit(u,0,0);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Footman")).produceInstance(state);
			test3unit1 = u;
			builder.addUnit(u,0,0);
		}
		
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Footman")).produceInstance(state);
			test3unit2= u;
			builder.addUnit(u,0,0);
		}
		planner = new SimplePlanner(state);
		model = new LessSimpleModel(state, 5536,null);
		model.setVerbose(true);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void testDoesDamage() {
		Unit shooter = test1shooter;
		Unit target = test1target;
		shooter.setxPosition(0);
		shooter.setyPosition(0);
		target.setxPosition(0);
		target.setyPosition(1);
		int starthp = target.getCurrentHealth();
		LinkedList<Action> plan = planner.planAttack(shooter.ID, target.ID);
		//Try the first action
		assertTrue("There was no plan",plan.size() > 0);
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		actions.put(shooter.ID,plan.get(0));
		model.addActions(actions, player);
//		model.setActions(new Action[]{plan.get(0)});
		model.executeStep();
		
		
		
		shooter.setxPosition(99);
		shooter.setyPosition(99);
		target.setxPosition(99);
		target.setyPosition(99);
		assertTrue("Damage was not dealt",target.getCurrentHealth() < starthp);
	}
	
	
	
	
	/**
	 * test to see whether a ranged unit out of range will step into range and then fire (dealing damage)
	 */
	@Test
	public void testRangedStepsAndShoots() {
		Unit shooter = test2shooter;
		Unit target = test2target;
		shooter.setxPosition(0);
		shooter.setyPosition(0);
		target.setxPosition(0);
		target.setyPosition(shooter.getTemplate().getRange()+1);
		int starthp = target.getCurrentHealth();
		LinkedList<Action> plan = planner.planAttack(shooter.ID, target.ID);
		//try the first two steps
		System.out.println("Range test, it should step once and fire");
		System.out.println("Current range is "+shooter.getTemplate().getRange());
		for (Action a : plan) {
			System.out.println("Action part: "+a);
		}
		assertTrue("There was no plan",plan.size() > 1);
		int i = 0;
		while (i<plan.size())
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(shooter.ID,plan.get(i));
			model.addActions(actions, player);
//			model.setActions(new Action[]{plan.get(i)});
			model.executeStep();
			i++;
		}
		
		assertTrue("Did not move to exactly range before shooting",DistanceMetrics.chebyshevDistance(shooter.getxPosition(), shooter.getyPosition(), target.getxPosition(), target.getyPosition()) == shooter.getTemplate().getRange());
		assertTrue("Damage was not dealt",target.getCurrentHealth() < starthp);
		shooter.setxPosition(99);
		shooter.setyPosition(99);
		target.setxPosition(99);
		target.setyPosition(99);
	}
	
	
	/**
	 * test to see if two simultaneous deaths work and if the deaths are cleaned up afterwards, so that the units no longer exist in the list
	 */
	@Test
	public void testMutualAnnihilationAndCleanup() {
		Unit unit1 = test3unit1;
		Unit unit2 = test3unit2;
		unit1.setxPosition(0);
		unit1.setyPosition(0);
		unit2.setxPosition(0);
		unit2.setyPosition(1);
		unit1.setHP(1);
		unit2.setHP(1);
		LinkedList<Action> plan1 = planner.planAttack(unit1.ID, unit2.ID);
		LinkedList<Action> plan2 = planner.planAttack(unit2.ID, unit1.ID);
		//try the first two steps
		assertTrue("There was no plan for unit1",plan1.size() > 0);
		assertTrue("There was no plan for unit2",plan2.size() > 0);
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		actions.put(unit1.ID,plan1.get(0));
		actions.put(unit2.ID,plan2.get(0));
		model.addActions(actions, player);
//		model.setActions(new Action[]{plan1.get(0),plan2.get(0)});
		model.executeStep();
		unit1.setxPosition(99);
		unit1.setyPosition(99);
		unit2.setxPosition(99);
		unit2.setyPosition(99);
		assertTrue("Damage was not dealt by unit 1",unit1.getCurrentHealth() <= 1);
		assertTrue("Damage was not dealt by unit 2",unit2.getCurrentHealth() <= 1);
		assertTrue("Unit 1 was not cleaned up", model.getState().getUnit(unit1.ID) == null);
		assertTrue("Unit 2 was not cleaned up", model.getState().getUnit(unit2.ID) == null);

	}
}