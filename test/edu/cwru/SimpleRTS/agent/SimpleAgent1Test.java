package edu.cwru.SimpleRTS.agent;

import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.environment.Environment;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.Model;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class SimpleAgent1Test {
	
	static Agent[] agents;
	static State state;
	static Model model;
	static Environment env;
	
	@BeforeClass
	public static void setup() throws JSONException, IOException {
		agents = new Agent[]{new SimpleAgent1(0),new SimpleAgent1(1)};
		StateBuilder builder = new StateBuilder();
		List<Template> templates = TypeLoader.loadFromFile("data/unit_templates",0);
		for(Template t : templates)
		{
			if(!(t instanceof UnitTemplate))
				continue;
			UnitTemplate ut = (UnitTemplate)t;
			if(!"Footman".equals(ut.getUnitName()))
				continue;
			Unit u1 = new Unit(ut);
			//u1.setPlayer(0);
			u1.setxPosition(1);
			u1.setyPosition(1);
			builder.addUnit(u1);
			Unit u2 = new Unit(ut);
			//u2.setPlayer(1);
			u2.setxPosition(7);
			u2.setyPosition(7);
			builder.addUnit(u2);
			break;
		}
		builder.setSize(8, 8);
		state = builder.build();
		model = new SimpleModel(state, 6);
		env = new Environment(agents, model);
	}
	@Test
	public void runEpisode() {
		while(!env.isTerminated())
		{
			env.step();
			Unit u1 = state.getUnit(0);
			Unit u2 = state.getUnit(1);
			System.out.println("---"+env.getStepNumber()+"---");
			System.out.printf("Unit 1: x=%d y=%d hp=%d\n",u1.getxPosition(),u1.getyPosition(),u1.getCurrentHealth());
			System.out.printf("Unit 2: x=%d y=%d hp=%d\n",u2.getxPosition(),u2.getyPosition(),u2.getCurrentHealth());
			System.out.println(state.getTextString());
			if(env.getStepNumber() > 100)
				break;
		}
		assertFalse(env.getStepNumber() > 100);
	}
}
