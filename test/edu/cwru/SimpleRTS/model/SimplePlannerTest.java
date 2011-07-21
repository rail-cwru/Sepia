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
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.UnitTypeLoader;

public class SimplePlannerTest {

	static SimpleModel model;
	static SimplePlanner planner;
	static List<Template> templates;
	static State state;
	
	@BeforeClass
	public static void loadTemplates() throws Exception {
		templates = UnitTypeLoader.loadFromFile("data/unit_templates");		
		
		State.StateBuilder builder = new State.StateBuilder();
		builder.setSize(64,64);
		state = builder.build();
		planner = new SimplePlanner(state);
		for(Template t : templates)
		{
			if(!(t instanceof UnitTemplate))
				continue;
			UnitTemplate template = (UnitTemplate)t;
			if(template.getUnitName().equals("Peasant"))
			{
				Unit u = template.produceInstance();
				u.setxPosition(10);
				u.setyPosition(10);
				builder.addUnit(u);
			}
		}
		for(int i = 0; i <= 12; i++)
		{
			ResourceNode t = new ResourceNode(ResourceNode.Type.TREE, i, 8, 100);
			builder.addResource(t);
		}
		ResourceNode t = new ResourceNode(ResourceNode.Type.TREE, 7, 2, 100);
		builder.addResource(t);
		t = new ResourceNode(ResourceNode.Type.TREE, 7, 3, 100);
		builder.addResource(t);
		t = new ResourceNode(ResourceNode.Type.TREE, 8, 3, 100);
		builder.addResource(t);
		t = new ResourceNode(ResourceNode.Type.TREE, 8, 4, 100);
		builder.addResource(t);
		t = new ResourceNode(ResourceNode.Type.TREE, 9, 4, 100);
		builder.addResource(t);
		t = new ResourceNode(ResourceNode.Type.TREE, 10, 4, 100);
		builder.addResource(t);
		model = new SimpleModel(state,5536);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void testPlanMove() {
		LinkedList<Action> plan = planner.planMove(0, 1, 10);
		System.out.println("\n\n");
		for(Action a : plan)
		{
			System.out.println(a);
			assertFalse("Unit moved in wrong direction!",((DirectedAction)a).getDirection() == Direction.NORTH);
			assertFalse("Unit moved in wrong direction!",((DirectedAction)a).getDirection() == Direction.NORTHEAST);
			assertFalse("Unit moved in wrong direction!",((DirectedAction)a).getDirection() == Direction.EAST);
			assertFalse("Unit moved in wrong direction!",((DirectedAction)a).getDirection() == Direction.SOUTHEAST);
			assertFalse("Unit moved in wrong direction!",((DirectedAction)a).getDirection() == Direction.SOUTH);
		}
	}
	@Test
	public void textPlanAndExecuteMove() {
		LinkedList<Action> plan = planner.planMove(0, 1, 10);
		for(Action a : plan)
		{
			model.setActions(new Action[]{a});
			model.executeStep();
		}
		Unit u = state.getUnit(0);
		assertEquals("Unit's x position did not match the expected value!",1,u.getxPosition());
		assertEquals("Unit's y position did not match the expected value!",10,u.getyPosition());
	}
	@Test
	public void testPlanMoveObstructed() {
		LinkedList<Action> plan = planner.planMove(0, 10, 1);
		System.out.println("\n\n");
		for(Action a : plan)
		{
			System.out.println(a);
			assertFalse("Unit moved in wrong direction!",((DirectedAction)a).getDirection() == Direction.SOUTH);
		}
	}
	@Test
	public void testPlanAndExecuteMoveObstructed() {
		LinkedList<Action> plan = planner.planMove(0, 10, 1);
		System.out.println("\n\n");
		for(Action a : plan)
		{
			model.setActions(new Action[]{a});
			model.executeStep();
		}
		Unit u = state.getUnit(0);
		assertEquals("Unit's x position did not match the expected value!",10,u.getxPosition());
		assertEquals("Unit's y position did not match the expected value!",1,u.getyPosition());
	}
	@Test
	public void testPlanFollowsShortestPath() {
		LinkedList<Action> plan = planner.planMove(0, 5, 6);
		System.out.println("\n\n");
		for(Action a : plan)
		{
			System.out.println(a);
		}
		assertEquals("Planner did not take shortest path!",8,plan.size());
	}
}
