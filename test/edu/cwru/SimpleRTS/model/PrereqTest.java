package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class PrereqTest {

	static SimpleModel model;
	static SimplePlanner planner;
	static List<Template<?>> templates;
	static State state;
	static int player = 0;
	static int upgradeproducerid;
	static int upgradetemplate;
	static int dependingupgradetemplate;
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
		
		
		
		
		upgradetemplate = ((UpgradeTemplate)builder.getTemplate(player, "WeaponOne")).ID;
		dependingupgradetemplate = ((UpgradeTemplate)builder.getTemplate(player, "WeaponTwo")).hashCode();
		{
		Unit u = ((UnitTemplate)builder.getTemplate(player, "Blacksmith")).produceInstance(state);
		upgradeproducerid = u.ID;
		builder.addUnit(u,0,0);
		
		}
		builder.setResourceAmount(player, ResourceType.GOLD, 1000);
		builder.setResourceAmount(player, ResourceType.WOOD, 1000);
		planner = new SimplePlanner(state);
		model = new SimpleModel(state, 5536,null);
		model.setVerbosity(true);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void testUpgradePrereq() {
		assertEquals("Test not laid out properly, one or more upgrades were done already",!state.hasUpgrade(upgradetemplate, player)&&!state.hasUpgrade(dependingupgradetemplate, player),true);
		//Try to do an upgrade that you didn't do the prerequisite for
		LinkedList<Action> plan = planner.planProduce(upgradeproducerid, dependingupgradetemplate);
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
		assertFalse("It executed an upgrade without a prereq",state.hasUpgrade(dependingupgradetemplate, player));
		//Now try to do the normal upgrade
		LinkedList<Action> plan2 = planner.planProduce(upgradeproducerid, upgradetemplate);
		for(Action a : plan2)
		{
//			model.setActions(new Action[]{a});
			{
				Map<Integer, Action> actions = new HashMap<Integer, Action>();
				actions.put(a.getUnitId(),a);
				model.addActions(actions, player);
			}
			model.executeStep();
		}
		assertTrue("Failed to do a normal upgrade",state.hasUpgrade(upgradetemplate, player));
		LinkedList<Action> plan3 = planner.planProduce(upgradeproducerid, dependingupgradetemplate);
		for(Action a : plan3)
		{
//			model.setActions(new Action[]{a});
			{
				Map<Integer, Action> actions = new HashMap<Integer, Action>();
				actions.put(a.getUnitId(),a);
				model.addActions(actions, player);
			}
			model.executeStep();
		}
		assertTrue("Failed to do the upgrade ",state.hasUpgrade(dependingupgradetemplate, player));
		
	}
}
