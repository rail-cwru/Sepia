package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionFeedback;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.environment.LoadingStateCreator;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.environment.StateCreator;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.GameMap;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class ActionFeedbackTest {

	static State state;
	static LessSimpleModel model;
	private static final int player1=0;
	private static final int player2=1;
	private static File tempfile;
	@BeforeClass
	public static void setup() throws JSONException, IOException {
		StateBuilder builder = new StateBuilder();
		state = builder.build();
		builder.setSize(32, 32);
		{
			List<Template<?>> templates = TypeLoader.loadFromFile("data/unit_templates",player1,state);
			for(Template<?> t : templates)
			{
				builder.addTemplate(t);
			}
		}
		{
			List<Template<?>> templates = TypeLoader.loadFromFile("data/unit_templates",player2,state);
			for(Template<?> t : templates)
			{
				builder.addTemplate(t);
			}
		}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player1, "Peasant");
				Unit u1 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u1,1,1);
				Unit u2 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u2,7,7);
			}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player2, "Footman");
				Unit u1 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u1,20,4);
			}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player2, "Archer");
				Unit u1 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u1,2,12);
			}
		
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 1, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 1, 2, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 2, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 3, 3, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 0, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 1, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 3, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 4, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 5, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 6, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 7, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 8, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 9, 5, 100,state.nextTargetID()));
//		builder.addResource(new ResourceNode(ResourceNode.Type.GOLD_MINE, 12, 2, 100,state.nextTargetID()));
		tempfile = File.createTempFile("idontcare", ".map");
		GameMap.storeState(tempfile.getPath(), state);
		StateCreator restartthing = new LoadingStateCreator(tempfile.getPath());
		model = new LessSimpleModel(state, 6,restartthing);
		model.setVerbose(true);
	}
	@AfterClass
	public static void removeTempFile()
	{
		if (tempfile!=null)
			tempfile.delete();
	}
	@Test
	public void testOtherPlayerAct() {
		model.createNewWorld();
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		StateView st = model.getState(Agent.OBSERVER_ID);
		
		assertTrue("Initialization failed, player1 should have more units",st.getUnitIds(player1).size()>=2);
		UnitView u = st.getUnit(st.getUnitIds(player1).get(0));
		Action actionsent = Action.createCompoundAttack(u.getID(), 5464);
		actions.put(u.getID(), actionsent);
		model.addActions(actions, player2);
		model.executeStep();
		int roundnumber = model.getState(Agent.OBSERVER_ID).getTurnNumber()-1;
		
		HistoryView v1 = model.getHistory(player1);
		HistoryView v2 = model.getHistory(player2);
		HistoryView vo = model.getHistory(Agent.OBSERVER_ID);
		assertEquals("For CommandIssued: Player 1 should see nothing sent by himself",0,v1.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see exactly one thing",1,v2.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly one thing for player 2",1,vo.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly nothing for player 1",0,vo.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see what he sent",actionsent,v2.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For CommandIssued: Observer should see what player 2 sent",actionsent,vo.getCommandsIssued(player2).getActions(roundnumber).get(0));
		{
			HistoryView t = v1;
			assertEquals("Player1 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player1 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = v2;
			assertEquals("Player2 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player2 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = vo;
			assertEquals("Observer shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Observer shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		assertEquals("For Feedback: Player 1 should see nothing sent by himself",0,v1.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see exactly one thing",1,v2.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly one thing for player 2",1,vo.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly nothing for player 1",0,vo.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see what he sent",actionsent,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Observer should see what player 2 sent",actionsent,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Player 2 should see it was invalid controller",ActionFeedback.INVALIDCONTROLLER,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		assertEquals("For Feedback: Observer should see it was invalid controller",ActionFeedback.INVALIDCONTROLLER,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
	}
	@Test
	public void testOtherUnitAct() {
		model.createNewWorld();
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		StateView st = model.getState(Agent.OBSERVER_ID);
		
		assertTrue("Initialization failed, player2 should have more units",st.getUnitIds(player2).size()>=2);
		UnitView u = st.getUnit(st.getUnitIds(player2).get(0));
		UnitView u2 = st.getUnit(st.getUnitIds(player2).get(1));
		Action actionsent = Action.createCompoundAttack(u.getID(), 6465465);
		actions.put(u2.getID(), actionsent);
		model.addActions(actions, player2);
		model.executeStep();
		int roundnumber = model.getState(Agent.OBSERVER_ID).getTurnNumber()-1;
		
		HistoryView v1 = model.getHistory(player1);
		HistoryView v2 = model.getHistory(player2);
		HistoryView vo = model.getHistory(Agent.OBSERVER_ID);
		assertEquals("For CommandIssued: Player 1 should see nothing sent by himself",0,v1.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see exactly one thing",1,v2.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly one thing for player 2",1,vo.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly nothing for player 1",0,vo.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see what he sent",actionsent,v2.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For CommandIssued: Observer should see what player 2 sent",actionsent,vo.getCommandsIssued(player2).getActions(roundnumber).get(0));
		{
			HistoryView t = v1;
			assertEquals("Player1 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player1 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = v2;
			assertEquals("Player2 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player2 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = vo;
			assertEquals("Observer shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Observer shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		assertEquals("For Feedback: Player 1 should see nothing sent by himself",0,v1.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see exactly one thing",1,v2.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly one thing for player 2",1,vo.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly nothing for player 1",0,vo.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see what he sent",actionsent,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Observer should see what player 2 sent",actionsent,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Player 2 should see it was invalid unit",ActionFeedback.INVALIDUNIT,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		assertEquals("For Feedback: Observer should see it was invalid unit",ActionFeedback.INVALIDUNIT,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
	}
	@Test
	public void testNoUnitAct() {
		model.createNewWorld();
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		StateView st = model.getState(Agent.OBSERVER_ID);
		
//		assertTrue("Initialization failed, player1 should have more units",st.getUnitIds(player1).size()>=2);
		int fakeid = -343443;
		assertNull("Initialization failed, no unit with id "+fakeid+" should exist",st.getUnit(fakeid));
		Action actionsent = Action.createCompoundAttack(fakeid, 6465465);
		actions.put(fakeid, actionsent);
		model.addActions(actions, player2);
		model.executeStep();
		int roundnumber = model.getState(Agent.OBSERVER_ID).getTurnNumber()-1;
		
		HistoryView v1 = model.getHistory(player1);
		HistoryView v2 = model.getHistory(player2);
		HistoryView vo = model.getHistory(Agent.OBSERVER_ID);
		assertEquals("For CommandIssued: Player 1 should see nothing sent by himself",0,v1.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see exactly one thing",1,v2.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly one thing for player 2",1,vo.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly nothing for player 1",0,vo.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see what he sent",actionsent,v2.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For CommandIssued: Observer should see what player 2 sent",actionsent,vo.getCommandsIssued(player2).getActions(roundnumber).get(0));
		{
			HistoryView t = v1;
			assertEquals("Player1 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player1 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = v2;
			assertEquals("Player2 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player2 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = vo;
			assertEquals("Observer shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Observer shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		assertEquals("For Feedback: Player 1 should see nothing sent by himself",0,v1.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see exactly one thing",1,v2.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly one thing for player 2",1,vo.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly nothing for player 1",0,vo.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see what he sent",actionsent,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Observer should see what player 2 sent",actionsent,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Player 2 should see it was invalid unit",ActionFeedback.INVALIDUNIT,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		assertEquals("For Feedback: Observer should see it was invalid unit",ActionFeedback.INVALIDUNIT,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
	}
	@Test
	public void testPrimitiveFail() {
		model.createNewWorld();
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		StateView st = model.getState(Agent.OBSERVER_ID);
		
		assertTrue("Initialization failed, player2 should have more units",st.getUnitIds(player2).size()>=2);
		UnitView u = st.getUnit(st.getUnitIds(player2).get(0));
		int fakeid = -343443;
		assertNull("Initialization failed, no unit with id "+fakeid+" should exist",st.getUnit(fakeid));
		Action actionsent = Action.createPrimitiveAttack(u.getID(), fakeid);
		actions.put(u.getID(), actionsent);
		model.addActions(actions, player2);
		model.executeStep();
		int roundnumber = model.getState(Agent.OBSERVER_ID).getTurnNumber()-1;
		
		HistoryView v1 = model.getHistory(player1);
		HistoryView v2 = model.getHistory(player2);
		HistoryView vo = model.getHistory(Agent.OBSERVER_ID);
		assertEquals("For CommandIssued: Player 1 should see nothing sent by himself",0,v1.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see exactly one thing",1,v2.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly one thing for player 2",1,vo.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly nothing for player 1",0,vo.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see what he sent",actionsent,v2.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For CommandIssued: Observer should see what player 2 sent",actionsent,vo.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For Feedback: Player 1 should see nothing sent by himself",0,v1.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see exactly one thing",1,v2.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly one thing for player 2",1,vo.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly nothing for player 1",0,vo.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see what he sent",actionsent,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Observer should see what player 2 sent",actionsent,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Player 2 should see it was a fail",ActionFeedback.FAILED,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		assertEquals("For Feedback: Observer should see it was a fail",ActionFeedback.FAILED,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		
		{
			HistoryView t = v1;
			assertEquals("Player1 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player1 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = v2;
			assertEquals("Player2 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player2 shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		{
			HistoryView t = vo;
			assertEquals("Observer shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Observer shouldn't see player 2 executing anything",0,t.getActionsExecuted(player2).getActions(roundnumber).size());
		}
		
	}
	@Test
	public void testCompound2Step() {
		model.createNewWorld();
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		StateView st = model.getState(Agent.OBSERVER_ID);
		
		assertTrue("Initialization failed, player2 should have more units",st.getUnitIds(player2).size()>=2);
		UnitView u = st.getUnit(st.getUnitIds(player2).get(0));
		int stx = u.getXPosition();
		int sty = u.getYPosition();
		int enx = stx+2;
		int eny = sty+1;
		Action actionsent = Action.createCompoundMove(u.getID(), enx, eny);
		actions.put(u.getID(), actionsent);
//		System.out.println(Arrays.toString(SimplePlanner.getDirections(st, stx, sty, enx, eny, 0, false).toArray(new Direction[0])));
		Action firstprimitive = Action.createPrimitiveMove(u.getID(),SimplePlanner.getDirections(st, stx, sty, enx, eny, 0, false).get(0));
		Action secondprimitive = Action.createPrimitiveMove(u.getID(),SimplePlanner.getDirections(st, stx, sty, enx, eny, 0, false).get(1));
		model.addActions(actions, player2);
		model.executeStep();
		
		{
		int roundnumber = model.getState(Agent.OBSERVER_ID).getTurnNumber()-1;
		Action thisprimitive = firstprimitive;
		HistoryView v1 = model.getHistory(player1);
		HistoryView v2 = model.getHistory(player2);
		HistoryView vo = model.getHistory(Agent.OBSERVER_ID);
		assertEquals("For CommandIssued: Player 1 should see nothing sent by himself",0,v1.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see exactly one thing",1,v2.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly one thing for player 2",1,vo.getCommandsIssued(player2).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Observer should see exactly nothing for player 1",0,vo.getCommandsIssued(player1).getActions(roundnumber).size());
		assertEquals("For CommandIssued: Player 2 should see what he sent",actionsent,v2.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For CommandIssued: Observer should see what player 2 sent",actionsent,vo.getCommandsIssued(player2).getActions(roundnumber).get(0));
		assertEquals("For Feedback: Player 1 should see nothing sent by himself",0,v1.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see exactly one thing",1,v2.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly one thing for player 2",1,vo.getActionResults(player2).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Observer should see exactly nothing for player 1",0,vo.getActionResults(player1).getActionResults(roundnumber).size());
		assertEquals("For Feedback: Player 2 should see what he sent",actionsent,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Observer should see what player 2 sent",actionsent,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
		assertEquals("For Feedback: Player 2 should see it was an incomplete",ActionFeedback.INCOMPLETE,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		assertEquals("For Feedback: Observer should see it was an incomplete",ActionFeedback.INCOMPLETE,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
		
		{
			HistoryView t = v1;
			assertEquals("Player1 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			//whether player 1 should see player 2 doing something is dependant on whether partial observability is on
		}
		{
			HistoryView t = v2;
			assertEquals("Player2 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Player2 see player 2 executing 1 thing",1,t.getActionsExecuted(player2).getActions(roundnumber).size());
			assertEquals("Player2 should see the thing being executed as a primitive move",thisprimitive,t.getActionsExecuted(player2).getActions(roundnumber).get(0));
		}
		{
			HistoryView t = vo;
			assertEquals("Observer shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
			assertEquals("Observer see player 2 executing 1 thing",1,t.getActionsExecuted(player2).getActions(roundnumber).size());
			assertEquals("Observer should see the thing being executed as a primitive move",thisprimitive,t.getActionsExecuted(player2).getActions(roundnumber).get(0));
		}
		}
		model.executeStep();
		{
			int roundnumber = model.getState(Agent.OBSERVER_ID).getTurnNumber()-1;
			Action thisprimitive = secondprimitive;
			HistoryView v1 = model.getHistory(player1);
			HistoryView v2 = model.getHistory(player2);
			HistoryView vo = model.getHistory(Agent.OBSERVER_ID);
			assertEquals("For CommandIssued: Player 1 should see nothing sent by himself",0,v1.getCommandsIssued(player1).getActions(roundnumber).size());
			assertEquals("For CommandIssued: Player 2 should see exactly nothing sent by himself",0,v2.getCommandsIssued(player2).getActions(roundnumber).size());
			assertEquals("For CommandIssued: Observer should see exactly nothing for player 2",0,vo.getCommandsIssued(player2).getActions(roundnumber).size());
			assertEquals("For CommandIssued: Observer should see exactly nothing for player 1",0,vo.getCommandsIssued(player1).getActions(roundnumber).size());
//			assertEquals("For CommandIssued: Player 2 should see what he sent",actionsent,v2.getCommandsIssued(player2).getActions(roundnumber).get(0));
//			assertEquals("For CommandIssued: Observer should see what player 2 sent",actionsent,vo.getCommandsIssued(player2).getActions(roundnumber).get(0));
			assertEquals("For Feedback: Player 1 should see nothing sent by himself",0,v1.getActionResults(player1).getActionResults(roundnumber).size());
			assertEquals("For Feedback: Player 2 should see exactly one thing",1,v2.getActionResults(player2).getActionResults(roundnumber).size());
			assertEquals("For Feedback: Observer should see exactly one thing for player 2",1,vo.getActionResults(player2).getActionResults(roundnumber).size());
			assertEquals("For Feedback: Observer should see exactly nothing for player 1",0,vo.getActionResults(player1).getActionResults(roundnumber).size());
			assertEquals("For Feedback: Player 2 should see what he sent",actionsent,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
			assertEquals("For Feedback: Observer should see what player 2 sent",actionsent,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getAction());
			assertEquals("For Feedback: Player 2 should see it was a complete",ActionFeedback.COMPLETED,v2.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
			assertEquals("For Feedback: Observer should see it was a complete",ActionFeedback.COMPLETED,vo.getActionResults(player2).getActionResults(roundnumber).get(0).getFeedback());
			
			{
				HistoryView t = v1;
				assertEquals("Player1 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
				//whether player 1 should see player 2 doing something is dependant on whether partial observability is on
			}
			{
				HistoryView t = v2;
				assertEquals("Player2 shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
				assertEquals("Player2 see player 2 executing 1 thing",1,t.getActionsExecuted(player2).getActions(roundnumber).size());
				assertEquals("Player2 should see the thing being executed as a primitive move",thisprimitive,t.getActionsExecuted(player2).getActions(roundnumber).get(0));
			}
			{
				HistoryView t = vo;
				assertEquals("Observer shouldn't see player 1 executing anything",0,t.getActionsExecuted(player1).getActions(roundnumber).size());
				assertEquals("Observer see player 2 executing 1 thing",1,t.getActionsExecuted(player2).getActions(roundnumber).size());
				assertEquals("Observer should see the thing being executed as a primitive move",thisprimitive,t.getActionsExecuted(player2).getActions(roundnumber).get(0));
			}
			}
		
	}
	
}
