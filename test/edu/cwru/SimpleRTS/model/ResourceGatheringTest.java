package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
	
	static SimpleModel model;
	static List<Template> templates;
	static Configuration configuration;

	/**
	 * Creates an initial state with one of each unit type.
	 */
	@BeforeClass
	public static void setup() throws Exception {
		templates = TypeLoader.loadFromFile("data/unit_templates");
		State.StateBuilder builder = new StateBuilder();
		builder.setSize(64,64);
		for(Template t : templates)
		{
			if(!(t instanceof UnitTemplate))
				continue;
			UnitTemplate template = (UnitTemplate)t;
			if(template.getUnitName().equals("TownHall"))
			{
				Unit u = template.produceInstance();
				u.setxPosition(10);
				u.setyPosition(10);
				builder.addUnit(u);
			}
			else if(template.getUnitName().equals("Peasant"))
			{
				Unit u = template.produceInstance();
				u.setxPosition(12);
				u.setyPosition(12);
				builder.addUnit(u);
			}
		}
		ResourceNode t = new ResourceNode(ResourceNode.Type.TREE, 11, 8, 100);
		ResourceNode g = new ResourceNode(ResourceNode.Type.GOLD_MINE, 11, 12, 5000);
		builder.addResource(t);
		builder.addResource(g);
		model = new SimpleModel(builder.build(), 5336);
		(configuration = Configuration.getInstance()).put(ResourceNode.Type.TREE+"GatherRate", 20+"");
		configuration.put(ResourceNode.Type.GOLD_MINE+"GatherRate", 50+"");
	}
	@Test
	public void test1() {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.NORTHWEST);
		model.setActions(new Action[]{a});
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.NORTH);
		model.setActions(new Action[]{a});
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.NORTH);
		model.setActions(new Action[]{a});
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEGATHER,Direction.NORTH);
		model.setActions(new Action[]{a});
		model.executeStep();
		UnitView u = model.getState().getUnit(1);
		assertEquals("Unit did not receive the correct resource!",ResourceNode.Type.TREE,u.getCargoType());
		assertEquals("Unit did not receive the correct amount of resource!",20,u.getCargoAmount());
	}
	@Test
	public void test2() {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEDEPOSIT,Direction.SOUTHWEST);
		UnitView u = model.getState().getUnit(a.getUnitId());
		int oldTreeAmount = model.getState().getResourceAmount(u.getPlayer(), ResourceType.WOOD);
		int cargoAmount = u.getCargoAmount();
		model.setActions(new Action[]{a});
		model.executeStep();
		assertEquals("Resource amount did not increase by expected amount!", oldTreeAmount+cargoAmount,
						model.getState().getResourceAmount(u.getPlayer(), ResourceType.WOOD));
	}
	@Test
	public void test3() {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEMOVE,Direction.SOUTH);
		Action[] actions = new Action[]{a};
		model.setActions(actions);
		model.executeStep();
		model.setActions(actions);
		model.executeStep();
		a = new DirectedAction(1,ActionType.PRIMITIVEGATHER,Direction.SOUTH);
		model.setActions(new Action[]{a});
		model.executeStep();
		UnitView u = model.getState().getUnit(1);
		assertEquals("Unit did not receive the correct resource!",ResourceType.GOLD,u.getCargoType());
		assertEquals("Unit did not receive the correct amount of resource!",50,u.getCargoAmount());
	}
	@Test
	public void test4() {
		Action a = new DirectedAction(1,ActionType.PRIMITIVEDEPOSIT,Direction.NORTHWEST);
		UnitView u = model.getState().getUnit(a.getUnitId());
		int oldTreeAmount = model.getState().getResourceAmount(u.getPlayer(), ResourceType.GOLD);
		int cargoAmount = u.getCargoAmount();
		model.setActions(new Action[]{a});
		model.executeStep();
		assertEquals("Resource amount did not increase by expected amount!", oldTreeAmount+cargoAmount,
						model.getState().getResourceAmount(u.getPlayer(), ResourceType.GOLD));
	}
}
