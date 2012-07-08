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

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.LessSimpleModel;
import edu.cwru.sepia.environment.model.Model;
import edu.cwru.sepia.environment.model.SimplePlanner;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.model.state.UpgradeTemplate;
import edu.cwru.sepia.util.TypeLoader;

public class ProduceTest {

	static Model model;
	static SimplePlanner planner;
	static List<Template<?>> templates;
	static State state;
	static int player = 0;
	static int unitproducerid;
	static int unitproducedtemplate;
	static int upgradeproducer1id;
	static int upgradeproducer2id;
	static int upgradeproducedtemplate;
	static int upgradeproducedtemplate2;
	@BeforeClass
	public static void loadTemplates() throws Exception {
		State.StateBuilder builder = new State.StateBuilder();
		builder.setSize(15,15);
		state = builder.build();
		templates = TypeLoader.loadFromFile("data/unit_templates",player,state);		
		System.out.println("Sucessfully loaded templates");
		
		for (Template<?> t : templates) {
			builder.addTemplate(t);
		}
		
		
		
		
		unitproducedtemplate = ((UnitTemplate)builder.getTemplate(player, "Footman")).ID;
		upgradeproducedtemplate = ((UpgradeTemplate)builder.getTemplate(player, "WeaponOne")).hashCode();
		upgradeproducedtemplate2 = ((UpgradeTemplate)builder.getTemplate(player, "ArmorOne")).hashCode();
		{
		Unit u = ((UnitTemplate)builder.getTemplate(player, "Barracks")).produceInstance(state);
		unitproducerid = u.ID;
		builder.addUnit(u,0,0);
		
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Blacksmith")).produceInstance(state);
			upgradeproducer1id = u.ID;
			builder.addUnit(u,0,1);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Blacksmith")).produceInstance(state);
			upgradeproducer2id = u.ID;
			builder.addUnit(u,0,2);
		}

		builder.setSupplyCap(player, 10);
		builder.setResourceAmount(player, ResourceType.GOLD, 99999);
		builder.setResourceAmount(player, ResourceType.WOOD, 99999);
		planner = new SimplePlanner(state);
		model = new LessSimpleModel(state, 5536,null);
		model.setVerbose(true);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void testProduce() {
		//count the number of footmen
		int numfootmen=0;
		for (Unit u : state.getUnits(player).values())
		{
			if (u.getTemplate().getName().equals("Footman"))
				numfootmen++;
		}
		LinkedList<Action> plan = planner.planProduce(unitproducerid, unitproducedtemplate);
		for(Action a : plan)
		{
//			model.setActions(new Action[]{a});
			{
				Map<Integer, Action> actions = new HashMap<Integer, Action>();
				actions.put(a.getUnitId(),a);
				model.addActions(actions, player);
			}
			model.executeStep();
		}
		int numnewfootmen=0;
		for (Unit u : state.getUnits(player).values())
		{
			if (u.getTemplate().getName().equals("Footman"))
				numnewfootmen++;
		}
		assertEquals("Did not increase the number of footmen",numfootmen+1, numnewfootmen);
	}

	@Test
	public void testUpgrade() {
		UnitTemplate t = (UnitTemplate)state.getTemplate(player, "Footman");
		int oldattack = t.getBasicAttack();
		LinkedList<Action> plan = planner.planProduce(upgradeproducer1id, upgradeproducedtemplate);
		for(Action a : plan)
		{
//			model.setActions(new Action[]{a});
			{
				Map<Integer, Action> actions = new HashMap<Integer, Action>();
				actions.put(a.getUnitId(),a);
				model.addActions(actions, player);
			}
			model.executeStep();
		}
		int newattack = t.getBasicAttack();
		assertEquals("Did not increase the attack by one",oldattack+1, newattack);
	}
	@Test
	public void testDoubleUpgrade() {
		UnitTemplate t = (UnitTemplate)state.getTemplate(player, "Footman");
		int olddefense = t.getArmor();
		LinkedList<Action> plan1 = planner.planProduce(upgradeproducer1id, upgradeproducedtemplate2);
		LinkedList<Action> plan2 = planner.planProduce(upgradeproducer2id, upgradeproducedtemplate2);
		for(int i = 0; i < Math.max(plan1.size(), plan2.size());i++)
		{
			{
				Map<Integer, Action> actions = new HashMap<Integer, Action>();
			
			if (i<plan1.size())
				actions.put(plan1.get(i).getUnitId(),plan1.get(i));
			if (i<plan2.size())
				actions.put(plan2.get(i).getUnitId(),plan2.get(i));
			model.addActions(actions, player);
			}
			model.executeStep();
		}
		int newdefense = t.getArmor();
		assertEquals("Did not increase the defense by exactly one",olddefense+1, newdefense);
	}
}
