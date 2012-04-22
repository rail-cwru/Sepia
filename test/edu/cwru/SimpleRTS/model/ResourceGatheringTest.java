package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;


import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.Configuration;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class ResourceGatheringTest {
	
	static Model model;
	static List<Template<?>> templates;
	static Configuration configuration;
	static int player = 0;
	/**
	 * Creates an initial state with one of each unit type.
	 */
	@BeforeClass
	public static void setup() throws Exception {
		State.StateBuilder builder = new StateBuilder();
		builder.setSize(64,64);
		State state = builder.build();
		templates = TypeLoader.loadFromFile("data/unit_templates",player,state);
		
		for(Template<?> t : templates)
		{
			if(!(t instanceof UnitTemplate))
				continue;
			UnitTemplate template = (UnitTemplate)t;
			if(template.getName().equals("TownHall"))
			{
				Unit u = template.produceInstance(state);
				builder.addUnit(u,10,10);
			}
			else if(template.getName().equals("Peasant"))
			{
				Unit u = template.produceInstance(state);
				builder.addUnit(u,12,12);
			}
		}
		ResourceNode t = new ResourceNode(ResourceNode.Type.TREE, 11, 8, 100,state.nextTargetID());
		ResourceNode g = new ResourceNode(ResourceNode.Type.GOLD_MINE, 11, 12, 5000,state.nextTargetID());
		builder.addResource(t);
		builder.addResource(g);
		model = new LessSimpleModel(state, 5336,null);
		model.setVerbose(true);
		configuration = new Configuration();
		configuration.put(ResourceNode.Type.TREE+"GatherRate", 20+"");
		configuration.put(ResourceNode.Type.GOLD_MINE+"GatherRate", 50+"");
	}
	@Test
	public void test1()  {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.NORTHWEST);
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.NORTH);
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.NORTH);
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEGATHER,Direction.NORTH);
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		UnitView u = model.getState(player).getUnit(1);
		assertEquals("Unit did not receive the correct resource!",ResourceType.WOOD,u.getCargoType());
		assertEquals("Unit did not receive the correct amount of resource!",20,u.getCargoAmount());
	}
	@Test
	public void test2()  {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEDEPOSIT,Direction.SOUTHWEST);
		UnitView u = model.getState(player).getUnit(a.getUnitId());
		int oldTreeAmount = model.getState(player).getResourceAmount(u.getTemplateView().getPlayer(), ResourceType.WOOD);
		int cargoAmount = u.getCargoAmount();
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		assertEquals("Resource amount did not increase by expected amount!", oldTreeAmount+cargoAmount,
						(int)model.getState(player).getResourceAmount(u.getTemplateView().getPlayer(), ResourceType.WOOD));
	}
	@Test
	public void test3() {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.SOUTH);
//		Action[] actions = new Action[]{a};
//		model.setActions(actions);
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
//		model.setActions(actions);
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEGATHER,Direction.SOUTH);
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		UnitView u = model.getState(player).getUnit(1);
		assertEquals("Unit did not receive the correct resource!",ResourceType.GOLD,u.getCargoType());
		assertEquals("Unit did not receive the correct amount of resource!",50,u.getCargoAmount());
	}
	@Test
	public void test4() {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEDEPOSIT,Direction.NORTHWEST);
		UnitView u = model.getState(player).getUnit(a.getUnitId());
		int oldTreeAmount = model.getState(player).getResourceAmount(u.getTemplateView().getPlayer(), ResourceType.GOLD);
		int cargoAmount = u.getCargoAmount();
//		model.setActions(new Action[]{a});
		{
			Map<Integer, Action> actions = new HashMap<Integer, Action>();
			actions.put(a.getUnitId(),a);
			model.addActions(actions, player);
		}
		model.executeStep();
		assertEquals("Resource amount did not increase by expected amount!", oldTreeAmount+cargoAmount,
						(int)model.getState(player).getResourceAmount(u.getTemplateView().getPlayer(), ResourceType.GOLD));
	}
}
