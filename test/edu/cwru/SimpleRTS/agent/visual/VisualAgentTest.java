package edu.cwru.SimpleRTS.agent.visual;

import java.io.FileNotFoundException;
import java.io.IOException;
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
	private static final int player1=0;
	private static final int player2=1;
	
	@SuppressWarnings("rawtypes")
	@BeforeClass
	public static void setup() throws JSONException, IOException {
		StateBuilder builder = new StateBuilder();
		builder.setSize(32, 32);
		{
			List<Template> templates = TypeLoader.loadFromFile("data/unit_templates",player1);
			for(Template t : templates)
			{
				builder.addTemplate(t);
			}
		}
		{
			List<Template> templates = TypeLoader.loadFromFile("data/unit_templates",player2);
			for(Template t : templates)
			{
				builder.addTemplate(t);
			}
		}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player1, "Peasant");
				Unit u1 = new Unit(ut);
				builder.addUnit(u1,1,1);
				Unit u2 = new Unit(ut);
				builder.addUnit(u2,7,7);
			}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player2, "Footman");
				Unit u1 = new Unit(ut);
				builder.addUnit(u1,20,4);
			}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player2, "Archer");
				Unit u1 = new Unit(ut);
				builder.addUnit(u1,2,12);
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
		model = new SimpleModel(state, 6,null);
		model.setVerbosity(true);
		visualAgent = new VisualAgent(player1,state.getView(player1));
		simpleAgent = new SimpleAgent1(player2);
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
