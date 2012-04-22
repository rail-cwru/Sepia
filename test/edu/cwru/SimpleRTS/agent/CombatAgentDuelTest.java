package edu.cwru.SimpleRTS.agent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.LessSimpleModel;
import edu.cwru.SimpleRTS.model.SimplePlanner;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;


public class CombatAgentDuelTest {
	static LessSimpleModel model;
	static SimplePlanner planner;
	static List<Template<?>> templates1;
	static List<Template<?>> templates2;
	static State state;
	static int player1 = 0;
	static int player2 = 1;
	@BeforeClass
	public static void loadTemplates() throws Exception {
		
		State.StateBuilder builder = new State.StateBuilder();
		
		
		
		builder.setSize(15,15);
		state = builder.build();
		
		
		templates1 = TypeLoader.loadFromFile("data/unit_templates",player1,state);		
		System.out.println("Sucessfully loaded templates");
		for (Template<?> t : templates1) {
			builder.addTemplate(t);
		}
		templates2 = TypeLoader.loadFromFile("data/unit_templates",player2,state);		
		System.out.println("Sucessfully loaded templates");
		for (Template<?> t : templates2) {
			builder.addTemplate(t);
		}
		
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player1, "Footman")).produceInstance(state);
			u.setxPosition(5);
			u.setyPosition(5);
			builder.addUnit(u,u.getxPosition(),u.getyPosition());
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player1, "Footman")).produceInstance(state);
			u.setxPosition(5);
			u.setyPosition(4);
			builder.addUnit(u,u.getxPosition(),u.getyPosition());
		}
		
		
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player2, "Footman")).produceInstance(state);
			u.setxPosition(6);
			u.setyPosition(5);
			builder.addUnit(u,u.getxPosition(),u.getyPosition());
		}
		{
			Unit u = ((UnitTemplate)builder.getTemplate(player2, "Footman")).produceInstance(state);
			u.setxPosition(6);
			u.setyPosition(4);
			builder.addUnit(u,u.getxPosition(),u.getyPosition());
		}
		
		planner = new SimplePlanner(state);
		model=new LessSimpleModel(state, 1235,null);
		model.setVerbose(true);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() throws IOException, InterruptedException {
		CombatAgent agent1 = new CombatAgent(player1, new String[]{Integer.toString(player2), "false", "false" });
		CombatAgent agent2 = new CombatAgent(player2, new String[]{Integer.toString(player1), "false", "true"});
		for (int step = 0; step<30500; step++)
		{
			Map<Integer,Action> acts1;
			Map<Integer,Action> acts2;
			if (step == 0)
			{
				acts1=agent1.initialStep(model.getState(player1),model.getHistory(player1));
				acts2=agent2.initialStep(model.getState(player2),model.getHistory(player2));
			}
			else
			{
				acts1=agent1.middleStep(model.getState(player1),model.getHistory(player1));
				acts2=agent2.middleStep(model.getState(player2),model.getHistory(player2));
			}
			Collection<Action> actionsimmut1 = acts1.values();
			Collection<Action> actionsimmut2 = acts2.values();
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
			model.addActions(acts1,player1);
			model.addActions(acts2,player2);
			model.executeStep();
		}
	}
}
