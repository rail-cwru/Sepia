package edu.cwru.SimpleRTS.agent;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.visual.VisualAgent;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.LessSimpleModel;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.model.SimplePlanner;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;


public class ScriptedGoalAgentTest {
	static LessSimpleModel model;
	static SimplePlanner planner;
	static List<Template> templates;
	static State state;
	static int player=0;
	static Unit founder;
	@BeforeClass
	public static void loadTemplates() throws Exception {
		
		State.StateBuilder builder = new State.StateBuilder();
		state = builder.build();
		templates = TypeLoader.loadFromFile("data/unit_templates",player,state);
		System.out.println("Sucessfully loaded templates");
		
		
		
		builder.setSize(15,15);
		for (Template t : templates) {
			builder.addTemplate(t);
		}
		
		
		{
			
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Peasant")).produceInstance(state);
			u.setxPosition(5);
			u.setyPosition(5);
			founder = u;
			builder.addUnit(u,u.getxPosition(),u.getyPosition());
		}
		{
			ResourceNode rn = new ResourceNode(ResourceNode.Type.GOLD_MINE, 2, 2, 70000,state.nextTargetID());
			builder.addResource(rn);
		}
		{
			ResourceNode rn = new ResourceNode(ResourceNode.Type.TREE, 1, 1, 70000,state.nextTargetID());
			builder.addResource(rn);
		}
		
		planner = new SimplePlanner(state);
		model=new LessSimpleModel(state, 1235,null);
		model.setVerbosity(true);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() throws InterruptedException {
		//Get the resources right
		state.depositResources(player, ResourceType.GOLD, 1200);
		state.depositResources(player, ResourceType.WOOD, 800);
		String commands="Build:TownHall:0:0//use your starting peasant to build a town hall\n"+
				"Transfer:1:Idle:Gold//make the builder gather gold\n" +
				"Wait:Gold:500//wait until you have enough gold for a farm\n" +
				"Transfer:1:Gold:Wood//make him gather wood\n" +
				"Wait:Wood:250//until you have enough wood too\n" +
				"Transfer:1:Wood:Idle//then free him up\n" +
				"Build:Farm:-2:2//make him build a farm\n" +
				"Transfer:1:Idle:Gold//make him go back to gold\n" +
				"Produce:Peasant//make a peasant when you can\n" +
				"Transfer:1:Idle:Wood//and put the new guy on woodcutting\n" +
				"Produce:Peasant//make another peasant when you can\n" +
				"Transfer:1:Idle:Gold//and put the new guy on gold\n" +
				"Produce:Peasant//and make another\n" +
				"Transfer:1:Idle:Gold//and put that one on gold too\n" +
				"Wait:Wood:400//when you have enough wood for a barracks\n" +
				"Transfer:1:Wood:Idle//free up the woodcutter to build\n" +
				"Build:Barracks:2:-2//build a barracks\n" +
				"Transfer:1:Idle:Gold//make the builder go to gold\n" +
				"Produce:Footman//make a footman\n"+
				"Attack:All";
		int ncommands = 11;
		BufferedReader commandreader = new BufferedReader(new StringReader(commands));
		ScriptedGoalAgent agent = new ScriptedGoalAgent(0,commandreader, new Random(), true);
		VisualAgent vagent = new VisualAgent(0,new String[]{"true","false"});
		for (int step = 0; step<390; step++)
		{
			System.out.println("--------------------------------------------------");
			System.out.println("---------------------"+step+"------------------------");
			Map<Integer, Action> actionsimmut;
			if (step == 0)
			{
				actionsimmut = agent.initialStep(model.getState(player), model.getHistory(player));
				vagent.initialStep(model.getState(player), model.getHistory(player));
			}
			else
			{
				actionsimmut = agent.middleStep(model.getState(player), model.getHistory(player));
				vagent.middleStep(model.getState(player), model.getHistory(player));
			}
			Action[] actions = new Action[actionsimmut.size()];
			{
				int i = 0;
				for (Action a : actionsimmut.values())
				{
					actions[i] = a;
					i++;
				}
			}
			System.out.println("Actions:");
			for (Action a : actions) {
				System.out.println(a);
			}
			System.out.println("Assets("+state.getUnits(player).values().size()+"):");
			Collection<Unit> units = state.getUnits(player).values();
			for (Unit u : units) {
				System.out.println(u.getTemplate().getName() + " (ID: "+u.ID+") at "+u.getxPosition() + "," + u.getyPosition());
				System.out.println("Carrying: " + u.getCurrentCargoAmount() + " (" + u.getCurrentCargoType() + ")");
			}
			System.out.println("Resources:");
			for (ResourceNode r : state.getResources()) {
				System.out.println(r.getType() + " " + r.getAmountRemaining());
			}
			System.out.println("All agents control a combined " + state.getUnits().values().size() + " units");
			System.out.println(state.getResourceAmount(player, ResourceType.GOLD)+" Gold");
			System.out.println(state.getResourceAmount(player, ResourceType.WOOD)+" Wood");
			System.out.println(state.getSupplyAmount(player)+"/"+state.getSupplyCap(player) + " Food");
			model.addActions(actionsimmut,agent.getPlayerNumber());
			model.executeStep();
		}
	}
}
