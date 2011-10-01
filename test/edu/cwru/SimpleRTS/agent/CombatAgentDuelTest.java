package edu.cwru.SimpleRTS.agent;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableCollection;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.model.SimplePlanner;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
import edu.cwru.SimpleRTS.util.TypeLoader;


public class CombatAgentDuelTest {
	static SimpleModel model;
	static SimplePlanner planner;
	static List<Template> templates1;
	static List<Template> templates2;
	static State state;
	static int player1 = 0;
	static int player2 = 1;
	@BeforeClass
	public static void loadTemplates() throws Exception {
		
		State.StateBuilder builder = new State.StateBuilder();
		
		
		
		builder.setSize(15,15);
		
		
		
		templates1 = TypeLoader.loadFromFile("data/unit_templates",player1);		
		System.out.println("Sucessfully loaded templates");
		for (Template t : templates1) {
			builder.addTemplate(t, player1);
		}
		templates2 = TypeLoader.loadFromFile("data/unit_templates",player2);		
		System.out.println("Sucessfully loaded templates");
		for (Template t : templates2) {
			builder.addTemplate(t, player2);
		}
		
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player1, "Footman")).produceInstance();
			u.setxPosition(5);
			u.setyPosition(5);
			builder.addUnit(u);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player1, "Footman")).produceInstance();
			u.setxPosition(5);
			u.setyPosition(4);
			builder.addUnit(u);
		}
		
		
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player2, "Footman")).produceInstance();
			u.setxPosition(6);
			u.setyPosition(5);
			builder.addUnit(u);
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player2, "Footman")).produceInstance();
			u.setxPosition(6);
			u.setyPosition(4);
			builder.addUnit(u);
		}
		state = builder.build();
		planner = new SimplePlanner(state);
		model=new SimpleModel(state, 1235);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() throws IOException, InterruptedException {
		CombatAgent agent1 = new CombatAgent(player1, new int[]{player2},false);
		CombatAgent agent2 = new CombatAgent(player2, new int[]{player1},true);
		for (int step = 0; step<305; step++)
		{
			CountDownLatch latch1 = new CountDownLatch(1);
			CountDownLatch latch2 = new CountDownLatch(1);
			if (step == 0)
			{
				agent1.acceptInitialState(model.getState(), latch1);
				agent2.acceptInitialState(model.getState(), latch2);
			}
			else
			{
				agent1.acceptMiddleState(model.getState(), latch1);
				agent2.acceptMiddleState(model.getState(), latch2);
			}
			latch1.await();
			latch2.await();
			ImmutableCollection<Action> actionsimmut1 = agent1.getAction().values();
			ImmutableCollection<Action> actionsimmut2 = agent2.getAction().values();
			Action[] actions = new Action[actionsimmut1.size() + actionsimmut2.size()];
			{
				int i = 0;
				for (Action a : actionsimmut1)
				{
					actions[i] = a;
					i++;
				}
				for (Action a : actionsimmut2)
				{
					actions[i] = a;
					i++;
				}
			}
			System.out.println("Actions:");
			for (Action a : actions) {
				System.out.println(a);
			}
			System.out.println(state.getTextString());
			new BufferedReader(new InputStreamReader(System.in)).readLine();
//			System.out.println("Assets("+state.getUnits(player1).values().size()+"):");
//			Collection<Unit> units = state.getUnits(player1).values();
//			for (Unit u : units) {
//				System.out.println(u.getTemplate().getName() + " (ID: "+u.ID+") at "+u.getxPosition() + "," + u.getyPosition());
//				System.out.println("Carrying: " + u.getCurrentCargoAmount() + " (" + u.getCurrentCargoType() + ")");
//			}
//			System.out.println("All agents control a combined " + state.getUnits().values().size() + " units");
//			System.out.println(state.getResourceAmount(player, ResourceType.GOLD)+" Gold");
//			System.out.println(state.getResourceAmount(player, ResourceType.WOOD)+" Wood");
//			System.out.println(state.getSupplyAmount(player)+"/"+state.getSupplyCap(player) + " Food");
			model.setActions(actions);
			model.executeStep();
		}
	}
}
