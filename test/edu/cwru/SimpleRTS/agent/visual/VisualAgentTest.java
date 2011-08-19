package edu.cwru.SimpleRTS.agent.visual;

import java.io.FileNotFoundException;
import java.util.List;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.agent.SimpleAgent1;
import edu.cwru.SimpleRTS.environment.Environment;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class VisualAgentTest {

	static State state;
	static SimpleModel model;
	static VisualAgent visualAgent;
	static SimpleAgent1 simpleAgent;
	static Environment env;
	
	@SuppressWarnings("rawtypes")
	@BeforeClass
	public static void setup() throws FileNotFoundException, JSONException {
		StateBuilder builder = new StateBuilder();
		builder.setSize(32, 32);
		List<Template> templates = TypeLoader.loadFromFile("data/unit_templates");
		for(Template t : templates)
		{
			if(!(t instanceof UnitTemplate))
				continue;
			UnitTemplate ut = (UnitTemplate)t;
			if("Peasant".equals(ut.getUnitName()))
			{
				Unit u1 = new Unit(ut);
				u1.setPlayer(0);
				u1.setxPosition(1);
				u1.setyPosition(1);
				builder.addUnit(u1);
				Unit u2 = new Unit(ut);
				u2.setPlayer(0);
				u2.setxPosition(7);
				u2.setyPosition(7);
				builder.addUnit(u2);
			}
			else if("Footman".equals(ut.getUnitName()))
			{
				Unit u1 = new Unit(ut);
				u1.setPlayer(1);
				u1.setxPosition(20);
				u1.setyPosition(4);
				builder.addUnit(u1);
			}
			else if("Archer".equals(ut.getUnitName()))
			{
				Unit u1 = new Unit(ut);
				u1.setPlayer(1);
				u1.setxPosition(2);
				u1.setyPosition(12);
				builder.addUnit(u1);
			}
		}
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 1, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 1, 2, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 2, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 3, 3, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 0, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 1, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 3, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 4, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 5, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 6, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 7, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 8, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 9, 5, 100));
		builder.addResource(new ResourceNode(ResourceNode.Type.GOLD_MINE, 12, 2, 100));
		state = builder.build();
		model = new SimpleModel(state, 6);
		visualAgent = new VisualAgent(0,state.getView());
		simpleAgent = new SimpleAgent1(1);
		env = new Environment(new Agent[]{visualAgent,simpleAgent}, model);
	}
	@Test
	public void display() {
		while(true);
	}
	public static void main(String args[]) {
	      org.junit.runner.JUnitCore.main("edu.cwru.SimpleRTS.agent.visual.VisualAgentTest");
	}
}
