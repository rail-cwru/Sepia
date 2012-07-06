package edu.cwru.sepia.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.visual.VisualAgent;
import edu.cwru.sepia.environment.*;
import edu.cwru.sepia.environment.State.StateBuilder;
import edu.cwru.sepia.model.Direction;
import edu.cwru.sepia.model.LessSimpleModel;
import edu.cwru.sepia.model.Model;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.resource.ResourceNode;
import edu.cwru.sepia.model.resource.ResourceType;
import edu.cwru.sepia.model.unit.Unit;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.model.upgrade.UpgradeTemplate;
import edu.cwru.sepia.util.GameMap;
import edu.cwru.sepia.util.TypeLoader;

/**
 * Contains tests based on known action conflicts.
 * @author The Condor
 *
 */
public class ConflictTest {
	//many of these tests should probably test action postconditions
	
	static int seed = 3;
	@BeforeClass
	public static void setup() throws Exception {
		
	}
	@Test
	public void twoMoves() throws FileNotFoundException, JSONException
	{
		//test with different players
		twoMoves(0,1);
		//test with the same player
		twoMoves(0,0);
	}
	@Test
	public void oneMoves() throws FileNotFoundException, JSONException
	{
		//test with different players
		oneMoves(0,1);
		//test with the same player
		oneMoves(0,0);
	}
	@Test
	public void threeMoves() throws FileNotFoundException, JSONException
	{
		//test with different players
		threeMoves(0,1,2);
		//test with the same player
		threeMoves(0,0,0);
	}
public void oneMoves(int player1, int player2) throws FileNotFoundException, JSONException {
		
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		s.addUnit(u1, 0, 1);
		s.addUnit(u2, 1, 0);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveMove(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		assertTrue("One should have succeeded, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==1);
		
	}
	public void twoMoves(int player1, int player2) throws FileNotFoundException, JSONException {
		
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		s.addUnit(u1, 0, 1);
		s.addUnit(u2, 1, 0);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveMove(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveMove(u2.ID, Direction.SOUTH));
			model.addActions(act2, player2);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		assertTrue("They should have failed, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==0);
		assertTrue("They should have failed, but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==0);
	}
	public void threeMoves(int player1, int player2, int player3) throws FileNotFoundException, JSONException {
		
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Footman")).produceInstance(s);
		s.addUnit(u1, 0, 1);
		s.addUnit(u2, 1, 0);
		s.addUnit(u3, 0, 0);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveMove(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveMove(u2.ID, Direction.SOUTH));
			model.addActions(act2, player2);
		}
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveMove(u3.ID, Direction.SOUTHEAST));
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		assertTrue("They should have failed, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==0);
		assertTrue("They should have failed, but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==0);
		assertTrue("They should have failed, but did not",model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==0);
	}
	/**
	 * Test the situation where one unit moves out of range as the other tries to attack it.
	 * Expect both to succeed.
	 * @throws FileNotFoundException
	 * @throws JSONException
	 */
	@Test
	public void shootAndKillWhileMovingAway() throws FileNotFoundException, JSONException {
		int player1 = 0;
		int player2 = 1;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		u2.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 1, 1);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveAttack(u1.ID, u2.ID));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveMove(u2.ID, Direction.SOUTHEAST));
			model.addActions(act2, player2);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==1);
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==1);
		assertNull("The target should be dead but isn't",model.getState().getView(player1).getUnit(u2.ID));
	}
	/**
	 * Test the situation where two units are on a collision course, but one will be shot dead.
	 * Also, the unit that will be dead is moving away from the shooter, such that it will be out of range.
	 * Expect the attack to succeed, but the two moves to make each other fail.
	 * @throws FileNotFoundException
	 * @throws JSONException
	 */
	@Test
	public void shootAndKillWhileMovingAwayButStillBlockOtherMove() throws FileNotFoundException, JSONException {
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		u2.setHP(1);//cripple it so it dies in one shot
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Footman")).produceInstance(s);
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 1, 1);
		s.addUnit(u2, 3, 3);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveAttack(u1.ID, u2.ID));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveMove(u2.ID, Direction.NORTHWEST));//to 2,2
			model.addActions(act2, player2);
		}
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveMove(u3.ID, Direction.SOUTHEAST));//to 2,2
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==1);
		assertTrue("Move should have failed, but did not. it is now at "+u2.getxPosition()+","+u2.getyPosition(),model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==0);
		assertTrue("Move should have failed, but did not. it is now at "+u3.getxPosition()+","+u3.getyPosition(),model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==0);
		assertNull("The target should be dead but isn't",model.getState().getView(player1).getUnit(u2.ID));
	}
	/**
	 * Test the situation where a unit moves to a place with a unit that will move out of the way.
	 * Expect the moving in unit to fail and the moving out unit to succeed.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void moveToVacatingSpot() throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Footman")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		u2.setHP(1);//cripple it so it dies in one shot
		u3.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 1, 0);
//		s.addUnit(u3, 1, 0);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveMove(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveMove(u2.ID, Direction.EAST));
			model.addActions(act2, player2);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player2, lastturn).toArray(new ActionResult[0])));
		assertTrue("One should have failed, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==0);
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==1);
	}
	/**
	 * Test the situation where a unit is trying to move into a space containing a unit that is about to die.
	 * Expect the attack that kills the unit to succeed, the stationary unit to not try anything, and the moving unit to fail.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void moveToSpotOfDeadGuy() throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Archer")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		u2.setHP(1);//cripple it so it dies in one shot
		u3.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 1, 0);
		s.addUnit(u3, 1, 0);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveMove(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveAttack(u3.ID, u2.ID));
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player2, lastturn).toArray(new ActionResult[0])));
		assertTrue("One should have failed, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==0);
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==1);
		assertNull("The target should be dead but isn't",model.getState().getView(Agent.OBSERVER_ID).getUnit(u2.ID));
	}
	/**
	 * Test the situation where a unit is trying to move into a space containing a node that is about to be exhausted.
	 * Expect the gather that exhausts the resource to succeed, and the moving unit to fail.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void moveToSpotOfExhaustingResource() throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		ResourceNode r2= new ResourceNode(ResourceNode.Type.GOLD_MINE, 1, 0, 1, s.nextTargetID());
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Peasant")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		u3.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addResource(r2);
		s.addUnit(u3, 1, 1);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveMove(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveGather(u3.ID, Direction.NORTH));
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player3, lastturn).toArray(new ActionResult[0])));
//		System.out.println(u1.getxPosition()+","+u1.getyPosition());
//		System.out.println(r2.getxPosition()+","+r2.getyPosition());
		assertTrue("Move should have failed, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==0);
		assertTrue("Gather should have succeeded, but did not",model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==1);
		assertNull("The node should be exhausted but isn't",model.getState().getView(Agent.OBSERVER_ID).getResourceNode(r2.ID));
	}
	
	/**
	 * Test one unit gathering more than exists in a node.
	 * Expected to succeed.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void oneGatherTooMuch() throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		ResourceNode r2= new ResourceNode(ResourceNode.Type.GOLD_MINE, 1, 0, 1, s.nextTargetID());
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Peasant")).produceInstance(s);
		u3.setHP(1);//cripple it so it dies in one shot
		s.addResource(r2);
		s.addUnit(u3, 1, 1);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveGather(u3.ID, Direction.NORTH));
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player3, lastturn).toArray(new ActionResult[0])));
//		System.out.println(u1.getxPosition()+","+u1.getyPosition());
//		System.out.println(r2.getxPosition()+","+r2.getyPosition());
		assertTrue("Gather should have succeeded, but did not",model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==1);
		assertNull("The node should be exhausted but isn't",model.getState().getView(Agent.OBSERVER_ID).getResourceNode(r2.ID));
	}
	@Test
	public void twoGather() throws FileNotFoundException, JSONException
	{
		twoGather(Amount.MORETHANATTEMPT);
		twoGather(Amount.LESSTHANONEATTEMPT);
		twoGather(Amount.LESSTHANALLATTEMPT);
		twoGather(Amount.THESAMEASATTEMPT);
	}
	/**
	 * Test two units gathering from the same node.
	 * Expected to succeed when there is more than attempt or equal to attempt, and fail in both cases where there is less.
	 * @param amount
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	public void twoGather(Amount amount) throws FileNotFoundException, JSONException
	{
		
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Peasant")).produceInstance(s);
		
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Peasant")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		u3.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u3, 1, 1);
		int ramount = -1;
		ResourceNode.Type rtype = ResourceNode.Type.GOLD_MINE;
		switch (amount)
		{
		case LESSTHANALLATTEMPT:
			ramount = u1.getTemplate().getGatherRate(rtype)+u3.getTemplate().getGatherRate(rtype)-1;
			break;
		case LESSTHANONEATTEMPT:
			ramount = Math.min(u1.getTemplate().getGatherRate(rtype),u3.getTemplate().getGatherRate(rtype))-1;
			break;
		case MORETHANATTEMPT:
			ramount = u1.getTemplate().getGatherRate(rtype)+u3.getTemplate().getGatherRate(rtype)+1;
			break;
		case THESAMEASATTEMPT:
			ramount = u1.getTemplate().getGatherRate(rtype)+u3.getTemplate().getGatherRate(rtype);
			break;
		}
		ResourceNode r2= new ResourceNode(rtype, 1, 0, ramount, s.nextTargetID());
		s.addResource(r2);
		
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveGather(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveGather(u3.ID, Direction.NORTH));
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player1, lastturn).toArray(new ActionResult[0])));
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player3, lastturn).toArray(new ActionResult[0])));
//		System.out.println(u1.getxPosition()+","+u1.getyPosition());
//		System.out.println(r2.getxPosition()+","+r2.getyPosition());
		boolean shouldhavesucceeded = amount == Amount.MORETHANATTEMPT || amount == Amount.THESAMEASATTEMPT;
		boolean nodeshouldremain = amount != Amount.THESAMEASATTEMPT;
		assertTrue("On "+amount+" Gather1 should have "+(shouldhavesucceeded?"succeeded":"failed")+", but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==(shouldhavesucceeded?1:0));
		assertTrue("On "+amount+" Gather2 should have "+(shouldhavesucceeded?"succeeded":"failed")+", but did not",model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==(shouldhavesucceeded?1:0));
		assertTrue("On "+amount+" The node should be "+(nodeshouldremain?"still there":"exhausted")+" but isn't",(nodeshouldremain?model.getState().getView(Agent.OBSERVER_ID).getResourceNode(r2.ID)!=null:model.getState().getView(Agent.OBSERVER_ID).getResourceNode(r2.ID)==null));
	}
	@Test
	public void threeGather() throws FileNotFoundException, JSONException
	{
		threeGather(Amount.MORETHANATTEMPT);
		threeGather(Amount.LESSTHANONEATTEMPT);
		threeGather(Amount.LESSTHANALLATTEMPT);
		threeGather(Amount.THESAMEASATTEMPT);
	}
	/**
	 * Test three units gathering from the same node.  Important because a third should not make others succeed.
	 * Expected to succeed when there is more than attempt or equal to attempt, and fail in both cases where there is less.
	 * @param amount
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	public void threeGather(Amount amount) throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Peasant")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Peasant")).produceInstance(s);
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Peasant")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		u3.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 2, 0);
		s.addUnit(u3, 1, 1);
		int ramount = -1;
		ResourceNode.Type rtype = ResourceNode.Type.GOLD_MINE;
		switch (amount)
		{
		case LESSTHANALLATTEMPT:
			ramount = u1.getTemplate().getGatherRate(rtype)+u2.getTemplate().getGatherRate(rtype)+u3.getTemplate().getGatherRate(rtype)-1;
			break;
		case LESSTHANONEATTEMPT:
			ramount = Math.min(Math.min(u1.getTemplate().getGatherRate(rtype),u2.getTemplate().getGatherRate(rtype)),u3.getTemplate().getGatherRate(rtype))-1;
			break;
		case MORETHANATTEMPT:
			ramount = u1.getTemplate().getGatherRate(rtype)+u2.getTemplate().getGatherRate(rtype)+u3.getTemplate().getGatherRate(rtype)+1;
			break;
		case THESAMEASATTEMPT:
			ramount = u1.getTemplate().getGatherRate(rtype)+u2.getTemplate().getGatherRate(rtype)+u3.getTemplate().getGatherRate(rtype);
			break;
		}
		ResourceNode r2= new ResourceNode(rtype, 1, 0, ramount, s.nextTargetID());
		s.addResource(r2);
		
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveGather(u1.ID, Direction.EAST));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveGather(u2.ID, Direction.WEST));
			model.addActions(act2, player2);
		}
		{
			Map<Integer,Action> act3 = new HashMap<Integer,Action>();
			act3.put(u3.ID, Action.createPrimitiveGather(u3.ID, Direction.NORTH));
			model.addActions(act3, player3);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player1, lastturn).toArray(new ActionResult[0])));
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player3, lastturn).toArray(new ActionResult[0])));
//		System.out.println(u1.getxPosition()+","+u1.getyPosition());
//		System.out.println(r2.getxPosition()+","+r2.getyPosition());
		boolean shouldhavesucceeded = amount == Amount.MORETHANATTEMPT || amount == Amount.THESAMEASATTEMPT;
		boolean nodeshouldremain = amount != Amount.THESAMEASATTEMPT;
		assertTrue("On "+amount+" Gather1 should have "+(shouldhavesucceeded?"succeeded":"failed")+", but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==(shouldhavesucceeded?1:0));
		assertTrue("On "+amount+" Gather2 should have "+(shouldhavesucceeded?"succeeded":"failed")+", but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==(shouldhavesucceeded?1:0));
		assertTrue("On "+amount+" Gather3 should have "+(shouldhavesucceeded?"succeeded":"failed")+", but did not",model.getHistory().getView(player3).getPrimitiveFeedback(player3, lastturn).size()==(shouldhavesucceeded?1:0));
		assertTrue("On "+amount+" The node should be "+(nodeshouldremain?"still there":"exhausted")+" but isn't",(nodeshouldremain?model.getState().getView(Agent.OBSERVER_ID).getResourceNode(r2.ID)!=null:model.getState().getView(Agent.OBSERVER_ID).getResourceNode(r2.ID)==null));
	}
	/**
	 * Test the situation where you try to produce with two units and there is no room left.
	 * Expect both to fail.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void ProduceRoomLeft() throws FileNotFoundException, JSONException
	{
		for (Amount amount : Amount.values())
		{
		NProduceMRoomLeft(3,1,amount);
		NProduceMRoomLeft(3,0,amount);
		NProduceMRoomLeft(6,2,amount);
		NProduceMRoomLeft(6,6,amount);
		}
	}
	/**
	 * Test the situation where there is one tile left and two are trying to fill it.
	 * Do this by filling n tiles with town halls to make peasants and all but m of the rest with farms, so there is ample food.
	 * Expect m to succeed, n-m to fail.  This should draw resources for m of them.
	 * When you lack sufficient resources, all should fail.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	public void NProduceMRoomLeft(int n, int m, Amount amount) throws FileNotFoundException, JSONException
	{
		//This will fail if you try to use it with massive massive numbers of tiles left and/or number of productions
		int player1=0;
		State s = new State();
		s.setSize(30, 30);
		s.addPlayer(player1);
		//give a specific amount of resources to ensure 
		
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		UnitTemplate tht=(UnitTemplate)s.getTemplate(player1, "TownHall");
		UnitTemplate ft=(UnitTemplate)s.getTemplate(player1, "Farm");
		UnitTemplate pt=(UnitTemplate)s.getTemplate(player1, "Peasant");
		int expectedwoodatend=343433;
		int ramount;
		int amountthatwillbemade;
		switch (amount)
		{
		case LESSTHANALLATTEMPT:
			ramount = pt.getGoldCost()*n-1;
			amountthatwillbemade=0;
			break;
		case LESSTHANONEATTEMPT:
			ramount = pt.getGoldCost()-1;
			amountthatwillbemade=0;
			break;
		case MORETHANATTEMPT:
			ramount = pt.getGoldCost()*n+1;
			amountthatwillbemade=m;
			break;
		case THESAMEASATTEMPT:
			ramount = pt.getGoldCost()*n;
			amountthatwillbemade=m;
			break;
		default:
			throw new RuntimeException(amount + " is not supported");	
		}
		if (ramount<0)
			ramount = 0;
		s.addResourceAmount(player1, ResourceType.GOLD, ramount);
		s.addResourceAmount(player1, ResourceType.WOOD, expectedwoodatend);
		int expectedgoldatend=ramount-pt.getGoldCost()*amountthatwillbemade;
		
		
		Unit[] ths = new Unit[n];
		int numhallstoplace=n;
		int numfarmstoplace=s.getXExtent()*s.getYExtent()-n-m;
		spaceitr: for (int x = 0; x<s.getXExtent();x++)
			for (int y = 0; y<s.getYExtent();y++)
			{
				if (numhallstoplace > 0)
				{
					numhallstoplace--;
					Unit newth = tht.produceInstance(s);
					s.addUnit(newth, x, y);
					ths[numhallstoplace]=newth;
				}
				else
				{
					if (numfarmstoplace > 0)
					{
						numfarmstoplace--;
						Unit newf = ft.produceInstance(s);
						s.addUnit(newf, x, y);
					}
					else
						break spaceitr;
				}
			}
		
		Model model= new LessSimpleModel(s, seed, null);
		Map<Integer,Action> acts = new HashMap<Integer,Action>();
		for (Unit th : ths)
		{
			acts.put(th.ID, Action.createCompoundProduction(th.ID, pt.ID));
		}
		model.addActions(acts, player1);
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		int amountactuallysucceeded=model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size();
//		new VisualAgent(player1,new String[]{"true","true"}).initialStep(model.getState().getView(player1), model.getHistory().getView(player1));
		assertTrue(amountthatwillbemade+" should have been made, but "+amountactuallysucceeded+" did",amountactuallysucceeded==amountthatwillbemade);
		assertTrue(expectedgoldatend+" gold should be left",expectedgoldatend == s.getResourceAmount(player1, ResourceType.GOLD));
		assertTrue(expectedwoodatend+" wood should be left",expectedwoodatend == s.getResourceAmount(player1, ResourceType.WOOD));
		assertTrue(amountthatwillbemade+" food should be used",amountthatwillbemade == s.getSupplyAmount(player1));
	}
	/**
	 * Test the situation where there is no move left because the last space is being moved into.
	 */
	@Test
	public void TwoProduceNoRoomLeftBecauseMove()
	{
		throw new RuntimeException("Not implemented yet");
	}
	/**
	 * Test the situation where there is one room left after the spaces claimed by move
	 */
	@Test
	public void TwoProduceOneRoomLeftBecauseMove()
	{
		throw new RuntimeException("Not implemented yet");
	}
	/**
	 * Test a series of situations where the amount of resources left are in various amounts.
	 * Expect that whenever any resource is overdrawn, everything that takes that resource fails, but things that don't take that resource should not fail unless that fails another resource.
	 * Expect that resources are altered for successful actions.
	 * Also expect that resources are not subtracted for failed actions.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void productionSituations() throws FileNotFoundException, JSONException
	{
		for (Amount goldamount : Amount.values())
			for (Amount woodamount : Amount.values())
				for (Amount foodamount : Amount.values())
				{
					productionSituations(1,2,3,4,5,6,7,8,goldamount,woodamount,foodamount);
				}
	}
	public void productionSituations(int numnone, int numwoodonly, int numfoodonly, int numgoldonly, int numgoldandfood, int numwoodandfood, int numgoldandwood, int numgoldwoodandfood, Amount goldamount, Amount woodamount, Amount foodamount) throws FileNotFoundException, JSONException
	{
		//uses a mixture of gold,wood,food named variables and 3-arrays
		int player1=0;
		StateBuilder sb = new StateBuilder();
		State s = sb.build();
		s.setSize(999, 999);
		s.addPlayer(player1);
		//make a set for each permutation of gold on, wood on, food on
		int goldeach=100;int woodeach=100;int foodeach=1;//set amounts used if any
		boolean[][] resourcesused = new boolean[][]{{false,false,false},{false,false,true},{false,true,false},{false,true,true},{true,false,false},{true,false,true},{true,true,false},{true,true,true}};
		int[] numofeachtypetomake = new int[]{numnone,numfoodonly,numwoodonly, numwoodandfood,numgoldonly,numgoldandfood,numgoldandwood,numgoldwoodandfood};
		int[] idofeachtype= new int[8];
		
		int[] numusingresource = new int[3];
		for (int i = 0; i<resourcesused.length;i++)
			for(int j = 0; j<resourcesused[i].length;j++)
			{
				if (resourcesused[i][j])
					numusingresource[j]+=numofeachtypetomake[i];
			}
		
		boolean notenoughgold;
		boolean notenoughwood;
		boolean notenoughfood;
		int goldresourcestart;
		int woodresourcestart;
		int foodresourcestart;
		//will be making some specialized units here
		switch (goldamount)
		{
		case LESSTHANALLATTEMPT:
			goldresourcestart = goldeach*numusingresource[0]-1;
			notenoughgold=true;
			break;
		case LESSTHANONEATTEMPT:
			goldresourcestart = goldeach-1;
			notenoughgold=true;
			break;
		case MORETHANATTEMPT:
			goldresourcestart = goldeach*numusingresource[0]+1;
			notenoughgold=false;
			break;
		case THESAMEASATTEMPT:
			goldresourcestart = goldeach*numusingresource[0];
			notenoughgold=false;
			break;
		default:
			throw new RuntimeException(goldamount + " is not supported");	
		}
		switch (woodamount)
		{
		case LESSTHANALLATTEMPT:
			woodresourcestart = woodeach*numusingresource[1]-1;
			notenoughwood=true;
			break;
		case LESSTHANONEATTEMPT:
			woodresourcestart = woodeach-1;
			notenoughwood=true;
			break;
		case MORETHANATTEMPT:
			woodresourcestart = woodeach*numusingresource[1]+1;
			notenoughwood=false;
			break;
		case THESAMEASATTEMPT:
			woodresourcestart = woodeach*numusingresource[1];
			notenoughwood=false;
			break;
		default:
			throw new RuntimeException(woodamount + " is not supported");	
		}
		switch (foodamount)
		{
		case LESSTHANALLATTEMPT:
			foodresourcestart = foodeach*numusingresource[2]-1;
			notenoughfood=true;
			break;
		case LESSTHANONEATTEMPT:
			foodresourcestart = foodeach-1;
			notenoughfood=true;
			break;
		case MORETHANATTEMPT:
			foodresourcestart = foodeach*numusingresource[2]+1;
			notenoughfood=false;
			break;
		case THESAMEASATTEMPT:
			foodresourcestart = foodeach*numusingresource[2];
			notenoughfood=false;
			break;
		default:
			throw new RuntimeException(foodamount + " is not supported");	
		}
		s.addResourceAmount(player1, ResourceType.GOLD, goldresourcestart);
		s.addResourceAmount(player1, ResourceType.WOOD, woodresourcestart);
		sb.setSupplyCap(player1, foodresourcestart);
//		System.out.println(s.getSupplyCap(player1));
		List<UnitTemplate> tunittemplates=new ArrayList<UnitTemplate>();//a temporary array so that we can convert names to ids
		
		UnitTemplate btemplate= new UnitTemplate(s.nextTemplateID());
		btemplate.setCanMove(false);
		btemplate.setCanBuild(false);
		btemplate.setCanGather(false);
		btemplate.setBaseHealth(1);
		btemplate.setArmor(1);
		btemplate.setCharacter('b');
		btemplate.setBasicAttack(3);
		btemplate.setPiercingAttack(9);
		btemplate.setRange(2);
		btemplate.setSightRange(5);
		btemplate.setTimeCost(1);
		btemplate.setFoodProvided(0);
		btemplate.setCanAcceptGold(false);
		btemplate.setCanAcceptWood(false);
		btemplate.setWoodGatherRate(2);
		btemplate.setGoldGatherRate(3);
		btemplate.setGoldCost(0);
		btemplate.setWoodCost(0);
		btemplate.setFoodCost(0);
		btemplate.setName("Builder");
		btemplate.setPlayer(player1);
			for (int i = 0; i<resourcesused.length;i++)
			{
				UnitTemplate template = new UnitTemplate(s.nextTemplateID());
				
					template.setCanMove(true);
					template.setCanBuild(false);
					template.setCanGather(false);
					template.setBaseHealth(1);
					template.setArmor(1);
					template.setCharacter('?');
					template.setBasicAttack(3);
					template.setPiercingAttack(9);
					template.setRange(2);
					template.setSightRange(5);
					template.setTimeCost(1);
					template.setFoodProvided(0);
					template.setCanAcceptGold(false);
					template.setCanAcceptWood(false);
					template.setWoodGatherRate(2);
					template.setGoldGatherRate(3);
				template.setGoldCost(resourcesused[i][0]?goldeach:0);
				template.setWoodCost(resourcesused[i][1]?woodeach:0);
				template.setFoodCost(resourcesused[i][2]?foodeach:0);
				template.setName((resourcesused[i][0]?"g":"")+(resourcesused[i][1]?"w":"")+(resourcesused[i][2]?"f":""));
				template.setPlayer(player1);
				btemplate.addProductionItem(template.ID);
				s.addTemplate(template);
				tunittemplates.add(template);
				idofeachtype[i]=template.ID;
			}
		s.addTemplate(btemplate);
		tunittemplates.add(btemplate);
		
		
	
		
		
		
		
		//make a array of ids of things to construct
		int totaltomake = 0; for (int numtomake : numofeachtypetomake) totaltomake+=numtomake;
		int[] constructid=new int[totaltomake];
		int itr = 0;
		for (int i = 0; i<numofeachtypetomake.length;i++)
		{
			for (int j = 0; j<numofeachtypetomake[i];j++)
				constructid[itr++]=idofeachtype[i];
		}
		
		//place some production buildings
		int numprodtoplace = totaltomake;
		Unit[] prods = new Unit[totaltomake];
		spaceitr: for (int x = 0; x<s.getXExtent();x++)
			for (int y = 0; y<s.getYExtent();y++)
			{
				if (numprodtoplace > 0)
				{
					numprodtoplace--;
					Unit newth = btemplate.produceInstance(s);
					s.addUnit(newth, x, y);
					prods[numprodtoplace]=newth;
				}
				else
				{
					break spaceitr;
				}
			}
		
		Model model= new LessSimpleModel(s, seed, null);
		Map<Integer,Action> acts = new HashMap<Integer,Action>();
		itr=0;
		//make the actions
		for (Unit constr : prods)
		{
			acts.put(constr.ID, Action.createCompoundProduction(constr.ID, constructid[itr++]));
		}
		model.addActions(acts, player1);
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		
		//figure out what we expect to happen
		int[] expectedtomake = new int[numofeachtypetomake.length];
		int expectedgold=0;
		int expectedwood=0;
		int expectedfood=0;
		for (int i = 0; i<expectedtomake.length;i++)
		{
			expectedtomake[i]=((!resourcesused[i][0]||!notenoughgold)&&(!resourcesused[i][1]||!notenoughwood)&&(!resourcesused[i][2]||!notenoughfood))?numofeachtypetomake[i]:0;
		}
		int totalexpected=0;for (int i = 0; i<expectedtomake.length;i++) totalexpected+=expectedtomake[i];
//		new VisualAgent(player1,new String[]{"true","true"}).initialStep(model.getState().getView(player1), model.getHistory().getView(player1));
		int totalactuallydone = model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size();
		assertTrue(totalexpected+" should have succeeded, but "+totalactuallydone+" did",totalactuallydone==totalexpected);
	}
	
	@Test
	public void produceUpgradesInLimitedSpaceSituation()
	{
		throw new RuntimeException("Not implemented yet");
	}
	/**
	 * Test a series of situations where the amount of resources left are in various amounts and some of them fail.
	 * Expect that whenever any resource is overdrawn, everything that takes that resource fails, but things that don't take that resource should not fail unless that fails another resource.
	 * Expect that no resources are drawn when things fail due to non-placement.
	 */
	@Test
	public void productionSituationsWithPartialRoom()
	{
		throw new RuntimeException("Not implemented yet");
	}
	/**
	 * Test a situation for each resource (food, wood, gold) where a farm is built or a unit deposits resources at the same time as a production action.
	 * Expect the production to always fail.
	 */
	@Test
	public void productionGather()
	{
		throw new RuntimeException("Not implemented yet");
	}
	/**
	 * Test the situation where two units each shoot the same unit, which would be killed by either individually.
	 * Both should succeed and the unit should be dead.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void overKill() throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		int player3 = 2;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		if (player3!=player1 && player3 != player2)
		{
			s.addPlayer(player3);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player3, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		Unit u3= ((UnitTemplate)s.getTemplate(player3, "Footman")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		u2.setHP(1);//cripple it so it dies in one shot
		u3.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 1, 1);
		s.addUnit(u3, 1, 0);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveAttack(u1.ID, u3.ID));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveAttack(u2.ID, u3.ID));
			model.addActions(act2, player2);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
//		System.out.println(Arrays.toString(model.getHistory().getView(Agent.OBSERVER_ID, player2, lastturn).toArray(new ActionResult[0])));
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==1);
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==1);
		assertNull("The target should be dead but isn't",model.getState().getView(Agent.OBSERVER_ID).getUnit(u3.ID));
	}
	/**
	 * Test the situation where two units each shoot each other, both weak enough to die.
	 * Both should succeed and both should die.
	 * @throws JSONException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void mutualKill() throws FileNotFoundException, JSONException
	{
		int player1 = 0;
		int player2 = 1;
		State s = new State();
		s.setSize(999, 999);
		s.addPlayer(player1);
		for (Template t : TypeLoader.loadFromFile("data/unit_templates", player1, s))
		{
			s.addTemplate(t);
		}
		if (player2!=player1)
		{
			s.addPlayer(player2);
			for (Template t : TypeLoader.loadFromFile("data/unit_templates", player2, s))
			{
				s.addTemplate(t);
			}
		}
		Unit u1= ((UnitTemplate)s.getTemplate(player1, "Footman")).produceInstance(s);
		u1.setHP(1);//cripple it so it dies in one shot
		Unit u2= ((UnitTemplate)s.getTemplate(player2, "Footman")).produceInstance(s);
		u2.setHP(1);//cripple it so it dies in one shot
		s.addUnit(u1, 0, 0);
		s.addUnit(u2, 1, 1);
		Model model= new LessSimpleModel(s, seed, null);
		{
			Map<Integer,Action> act1 = new HashMap<Integer,Action>();
			act1.put(u1.ID, Action.createPrimitiveAttack(u1.ID, u2.ID));
			model.addActions(act1, player1);
		}
		{
			Map<Integer,Action> act2 = new HashMap<Integer,Action>();
			act2.put(u2.ID, Action.createPrimitiveAttack(u2.ID, u1.ID));
			model.addActions(act2, player2);
		}
		model.executeStep();
		int lastturn = s.getTurnNumber()-1;
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player1).getPrimitiveFeedback(player1, lastturn).size()==1);
		assertTrue("They should have succeeded, but did not",model.getHistory().getView(player2).getPrimitiveFeedback(player2, lastturn).size()==1);
		assertNull("The target should be dead but isn't",model.getState().getView(Agent.OBSERVER_ID).getUnit(u1.ID));
		assertNull("The target should be dead but isn't",model.getState().getView(Agent.OBSERVER_ID).getUnit(u2.ID));
	}
	
	private static enum Amount{
		MORETHANATTEMPT,
		LESSTHANONEATTEMPT,
		LESSTHANALLATTEMPT,
		THESAMEASATTEMPT
		
	}
}
