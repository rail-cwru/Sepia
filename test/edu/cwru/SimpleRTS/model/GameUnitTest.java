package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.UnitTypeLoader;
/**
 * Contains tests relating to the creation and manipulation of units
 * @author Tim
 *
 */
public class GameUnitTest {
	SimpleModel model;
	List<Template> templates;

	/**
	 * Creates an initial state with one of each unit type.
	 */
	@Before
	public void setup() throws Exception {
		templates = UnitTypeLoader.loadFromFile("data/unit_templates");
		State.StateBuilder builder = new StateBuilder();
		int x = 0;
		int y = 0;
		builder.setSize(30,30);
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
	@Test
	public void test1() {
		List<Unit> allUnits = model.getState().getUnits();
		DirectedAction a = new DirectedAction(1, ActionType.PRIMITIVEMOVE, Direction.SOUTHEAST);
		model.setActions(new Action[]{a});
		model.executeStep();
		Unit u = model.getState().getUnit(1);
		assertEquals("Unit was not in expected row!",6,u.getxPosition());
		assertEquals("Unit was not in expected column!",6,u.getyPosition());
		
	}
}
