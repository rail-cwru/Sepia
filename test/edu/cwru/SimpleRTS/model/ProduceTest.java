package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class ProduceTest {

	static SimpleModel model;
	static SimplePlanner planner;
	static List<Template> templates;
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
		templates = TypeLoader.loadFromFile("data/unit_templates",player);		
		System.out.println("Sucessfully loaded templates");
		State.StateBuilder builder = new State.StateBuilder();
		builder.setSize(15,15);
		for (Template t : templates) {
			builder.addTemplate(t, player);
		}
		
		
		
		
		unitproducedtemplate = ((UnitTemplate)builder.getTemplate(player, "Footman")).ID;
		upgradeproducedtemplate = ((UpgradeTemplate)builder.getTemplate(player, "WeaponOne")).hashCode();
		upgradeproducedtemplate2 = ((UpgradeTemplate)builder.getTemplate(player, "ArmorOne")).hashCode();
		{
		Unit u = ((UnitTemplate)builder.getTemplate(player, "Barracks")).produceInstance();
		u.setxPosition(0);
		u.setyPosition(0);
		unitproducerid = u.ID;
		builder.addUnit(u);
		
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Blacksmith")).produceInstance();
			u.setxPosition(0);
			u.setyPosition(1);
			upgradeproducer1id = u.ID;
			builder.addUnit(u);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Blacksmith")).produceInstance();
			u.setxPosition(0);
			u.setyPosition(2);
			upgradeproducer2id = u.ID;
			builder.addUnit(u);
		}

		builder.setSupplyCap(player, 10);
		builder.setResourceAmount(player, ResourceType.GOLD, 1200);
		builder.setResourceAmount(player, ResourceType.WOOD, 800);
		state = builder.build();
		planner = new SimplePlanner(state);
		model = new SimpleModel(state, 5536);
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
			model.setActions(new Action[]{a});
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
		int oldattack = t.getBasicAttackLow();
		LinkedList<Action> plan = planner.planProduce(upgradeproducer1id, upgradeproducedtemplate);
		for(Action a : plan)
		{
			model.setActions(new Action[]{a});
			model.executeStep();
		}
		int newattack = t.getBasicAttackLow();
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
			Action[] action;
			if (i<plan1.size() && i<plan2.size())
				action = new Action[]{plan1.get(i),plan2.get(i)};
			else if (i<plan1.size())
				action = new Action[]{plan1.get(i)};
			else if (i<plan2.size())
				action = new Action[]{plan2.get(i)};
			else
				action= new Action[0];//should never reach this
			model.setActions(action);
			model.executeStep();
		}
		int newdefense = t.getArmor();
		assertEquals("Did not increase the defense by exactly one",olddefense+1, newdefense);
	}
}
