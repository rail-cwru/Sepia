package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;
/**
 * Contains tests relating to the creation and manipulation of units
 * @author Tim
 *
 */
public class GameUnitTest {
	static SimpleModel model;
	static List<Template> templates;

	/**
	 * Creates an initial state with one of each unit type.
	 */
	@BeforeClass
	public static void setup() throws Exception {
		templates = TypeLoader.loadFromFile("data/unit_templates",0);
		State.StateBuilder builder = new StateBuilder();
		int x = 0;
		int y = 0;
		builder.setSize(64,64);
		for(Template t : templates)
		{
			if(!(t instanceof UnitTemplate))
				continue;
			Unit u = ((UnitTemplate)t).produceInstance();
			u.setxPosition(x);
			x += 5;
			u.setyPosition(y);
			y += 5;
			builder.addUnit(u);
		}
		model = new SimpleModel(builder.build(), 5336);
	}
	/**
	 * Move unit 1 Southeast
	 */
	@Test
	public void test1() {
		DirectedAction a = new DirectedAction(1, ActionType.PRIMITIVEMOVE, Direction.SOUTHEAST);
		model.setActions(new Action[]{a});
		model.executeStep();
		Unit.UnitView u = model.getState().getUnit(1);
		assertEquals("Unit was not in expected row!",6,u.getXPosition());
		assertEquals("Unit was not in expected column!",6,u.getYPosition());		
	}
	/**
	 * Move unit 2 Northwest unitl it bumps into unit 1
	 */
	@Test
	public void test2() {
		Action a = new DirectedAction(2, ActionType.PRIMITIVEMOVE, Direction.NORTHWEST);
		Action[] actions = new Action[]{a};
		model.setActions(actions);
		model.executeStep();
		Unit.UnitView u = model.getState().getUnit(2);
		assertEquals("Unit was not in expected column!",9,u.getXPosition());
		assertEquals("Unit was not in expected row!",9,u.getYPosition());
		model.setActions(actions);
		model.executeStep();
		assertEquals("Unit was not in expected column!",8,u.getXPosition());
		assertEquals("Unit was not in expected row!",8,u.getYPosition());
		model.setActions(actions);
		model.executeStep();
		assertEquals("Unit was not in expected column!",7,u.getXPosition());
		assertEquals("Unit was not in expected row!",7,u.getYPosition());
		model.setActions(actions);
		model.executeStep();
		assertEquals("Unit was not in expected column!",7,u.getXPosition());
		assertEquals("Unit was not in expected row!",7,u.getYPosition());
		
	}
	/**
	 * Have unit 2 attack unit 1
	 */
	@Test
	public void test3() {
		Unit.UnitView u2 = model.getState().getUnit(1);
		int hp = u2.getHP();
		Action a = new TargetedAction(2, ActionType.PRIMITIVEATTACK, 1);
		Action[] actions = new Action[]{a};
		model.setActions(actions);
		model.executeStep();
		assertTrue("Attack failed!",hp > u2.getHP());
		System.out.printf("Attack reduced unit 1's HP from %d to %d\n",hp,u2.getHP());		
	}
	/**
	 * Have unit 2 move away from unit 1, then try to attack from beyond its maximum range
	 */
	@Test
	public void test4() {
		Action a = new DirectedAction(2, ActionType.PRIMITIVEMOVE, Direction.SOUTH);
		Action[] actions = new Action[]{a};
		model.setActions(actions);
		model.executeStep();
		Unit.UnitView u = model.getState().getUnit(2);
		assertEquals("Unit was not in expected column!",7,u.getXPosition());
		assertEquals("Unit was not in expected row!",8,u.getYPosition());		
		Unit.UnitView u2 = model.getState().getUnit(2);
		int hp = u2.getHP();
		a = new TargetedAction(1, ActionType.PRIMITIVEATTACK, 2);
		actions = new Action[]{a};
		model.setActions(actions);
		model.executeStep();
		assertTrue("Attack range check failed!",hp == u2.getHP());	
	}
}
