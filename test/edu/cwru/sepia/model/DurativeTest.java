package edu.cwru.sepia.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.visual.VisualAgent;
import edu.cwru.sepia.environment.*;
import edu.cwru.sepia.environment.History.HistoryView;
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
 * <pre>
 * Contains tests based on durative actions actually happening and transitioning properly.
 * Tests revolve around four basic questions:
 * Does a sequence of actions do something iff the time is right?
 * Does completion reset progress (IE, do things work right after a completion)?
 * Do various kinds of failures properly reset progress?
 * Does interruption handle properly?
 * 
 * This should NOT cover the effects of duratives on claims and failures. 
 * </pre>
 * @author The Condor
 *
 */
public class DurativeTest {
	//many of these tests should probably test action postconditions
	
	static int seed = 3;
	static final int player1=0;
	
	@Test
	public void testMoveOnceNoHeadStart()
	{
		testMove(Direction.NORTH, 1, 0, 1);
		testMove(Direction.NORTH, 7, 0, 1);
	}
	@Test
	public void testMoveMultipleNoHeadStart()
	{
		testMove(Direction.NORTH, 1, 0, 80);
		testMove(Direction.NORTH, 7, 0, 17);
	}
	@Test
	public void testMoveMultipleWithHeadStart()
	{
		testMove(Direction.NORTH, 7, 3, 13);
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testMove(Direction d, int basicDuration, int headStart, int numConsecutiveTries) {
		resetState();
		//Change the template to make the duration right
		superUnit.setDurationMove(basicDuration);
		
		final Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		final int initX = u.getxPosition();
		final int initY = u.getyPosition();
		Action toTest = Action.createPrimitiveMove(u.ID, d);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				state.transportUnit(u, initX, initY);
			}};
		testDurativeCompletion(resetStrat, u,toTest,basicDuration,headStart,numConsecutiveTries);
	}
	
	@Test
	public void testGatherOnceNoHeadStart()
	{
		for (ResourceNode.Type type : ResourceNode.Type.values())
		{
			testGather(type, Direction.NORTH, 1, 0, 1);
			testGather(type, Direction.NORTH, 7, 0, 1);
		}
	}
	@Test
	public void testGatherMultipleNoHeadStart()
	{
		for (ResourceNode.Type type : ResourceNode.Type.values())
		{
			testGather(type,Direction.NORTH, 1, 0, 80);
			testGather(type,Direction.NORTH, 7, 0, 17);
		}
	}
	@Test
	public void testGatherMultipleWithHeadStart()
	{
		for (ResourceNode.Type type : ResourceNode.Type.values())
		{
			testGather(type,Direction.NORTH, 7, 3, 13);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testGather(ResourceNode.Type type, Direction d, int basicDuration, int headStart, int numConsecutiveTries) {
		resetState();
		//Change the template to make the duration right
		superUnit.setDurationGatherGold(basicDuration);
		superUnit.setDurationGatherWood(basicDuration);
		
		Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		Action toTest = Action.createPrimitiveGather(u.ID, d);
		final int largeNumber = Integer.MAX_VALUE;
		final ResourceNode rn = new ResourceNode(type, u.getxPosition()+d.xComponent(), u.getyPosition()+d.yComponent(), largeNumber, state.nextTargetID());
		state.addResource(rn);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				rn.reduceAmountRemaining(rn.getAmountRemaining()-largeNumber);
			}};
		testDurativeCompletion(resetStrat,u,toTest,basicDuration,headStart,numConsecutiveTries);
	}
	
	@Test
	public void testDepositOnceNoHeadStart()
	{
		for (ResourceType type : ResourceType.values())
		{
			testDeposit(type, Direction.NORTH, 1, 0, 1);
			testDeposit(type, Direction.NORTH, 7, 0, 1);
		}
	}
	@Test
	public void testDepositMultipleNoHeadStart()
	{
		for (ResourceType type : ResourceType.values())
		{
			testDeposit(type,Direction.NORTH, 1, 0, 80);
			testDeposit(type,Direction.NORTH, 7, 0, 17);
		}
	}
	@Test
	public void testDepositMultipleWithHeadStart()
	{
		for (ResourceType type : ResourceType.values())
		{
			testDeposit(type,Direction.NORTH, 7, 3, 13);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testDeposit(final ResourceType type, Direction d, int basicDuration, int headStart, int numConsecutiveTries) {
		resetState();
		//Change the template to make the duration right
		superUnit.setDurationDeposit(basicDuration);
		
		final Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		Action toTest = Action.createPrimitiveDeposit(u.ID, d);
		final int someNumber = 33;
		Unit th = superUnit.produceInstance(state);
		state.addUnit(th,u.getxPosition()+d.xComponent(),u.getyPosition()+d.yComponent());

		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				u.setCargo(type, someNumber);
			}};
		testDurativeCompletion(resetStrat,u,toTest,basicDuration,headStart,numConsecutiveTries);
	}
	
	@Test
	public void testAttackOnceNoHeadStart()
	{
		{
			testAttack(1, 0, 1);
			testAttack(7, 0, 1);
		}
	}
	@Test
	public void testAttackMultipleNoHeadStart()
	{
		{
			testAttack(1, 0, 80);
			testAttack(7, 0, 17);
		}
	}
	@Test
	public void testAttackMultipleWithHeadStart()
	{
		{
			testAttack(7, 3, 13);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testAttack(int basicDuration, int headStart, int numConsecutiveTries) {
		resetState();
		//Change the template to make the duration right
		superUnit.setDurationAttack(basicDuration);
		
		Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		final Unit target = superUnit.produceInstance(state);
		int[] xy = state.getClosestPosition(u.getxPosition(), u.getyPosition());
		state.addUnit(target, state.getXExtent()/2, state.getYExtent()/2);
		final int largeNumber=50000;
		target.setHP(largeNumber);
		Action toTest = Action.createPrimitiveAttack(u.ID, target.ID);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				//reset hp to the large number
				target.setHP(largeNumber);
			}};
		testDurativeCompletion(resetStrat,u,toTest,basicDuration,headStart,numConsecutiveTries);
	}
	
	@Test
	public void testProduceOnceNoHeadStart()
	{
		for (boolean makingunit : new boolean[]{true,false})
			for (boolean buildnotproduce : new boolean[]{true,false})
		{
			testProduce(makingunit, buildnotproduce, 1, 0, 1);
			testProduce(makingunit, buildnotproduce, 7, 0, 1);
		}
	}
	@Test
	public void testProduceMultipleNoHeadStart()
	{
		for (boolean makingunit : new boolean[]{true,false})
			for (boolean buildnotproduce : new boolean[]{true,false})
		{
			testProduce(makingunit, buildnotproduce, 1, 0, 80);
			testProduce(makingunit, buildnotproduce, 7, 0, 17);
		}
	}
	@Test
	public void testProduceMultipleWithHeadStart()
	{
		for (boolean makingunit : new boolean[]{true,false})
			for (boolean buildnotproduce : new boolean[]{true,false}) 
			{
				testProduce(makingunit, buildnotproduce, 7, 3, 13);
			}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testProduce(boolean makingunit, boolean buildnotproduce, int basicDuration, int headStart, int numConsecutiveTries) {
		resetState();
		
		//make it do the right thing between building and producing
		Template tomake = makingunit?targetProduceUnit:targetProduceUpgrade;
		superUnit.setCanBuild(buildnotproduce);
		//Change the template to make the duration right
		tomake.setTimeCost(basicDuration);
		
		final Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		Action toTest = buildnotproduce?Action.createPrimitiveBuild(u.ID, tomake.ID):Action.createPrimitiveProduction(u.ID, tomake.ID);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				//clear out all units other than the building/producing unit
				LinkedList<Integer> toremove=null;
				for (Unit possibleother : state.getUnits().values())
				{
					if (u.ID != possibleother.ID)
					{
						if (toremove == null) //lazy initialization
						{
							toremove = new LinkedList<Integer>();
						}
						toremove.add(possibleother.ID);
					}
				}
				if (toremove!=null)
					for(Integer id : toremove)
					{
						state.removeUnit(id);
					}
			}};
		testDurativeCompletion(resetStrat,u,toTest,basicDuration,headStart,numConsecutiveTries);
	}
	
	@Test
	public void testInterruptAndSuspensionMove()
	{
		for (ActionType type : ActionType.values())
		{
			if (ActionType.isPrimitive(type) && type != ActionType.FAILED && type != ActionType.FAILEDPERMANENTLY)
				testInterruptAndSuspensionMove(Direction.SOUTH, type);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testInterruptAndSuspensionMove(Direction d, ActionType type) {
		resetState();
		int interruptingDuration=500;
		//Change the template to make the duration right
		superUnit.setDurationMove(interruptingDuration);
		final Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		final int initX = u.getxPosition();
		final int initY = u.getyPosition();
		Action toTest = Action.createPrimitiveMove(u.ID, d);
		ResetStrategy resetStrat = new ResetStrategy(){
			@Override
			public void reset() {
				state.transportUnit(u, initX, initY);
			}};
		Action toInterrupt;
		switch (type)
		{
		case PRIMITIVEATTACK:
			toInterrupt = Action.createPrimitiveAttack(u.ID, d.ordinal());
			break;
		case PRIMITIVEBUILD:
			toInterrupt = Action.createPrimitiveBuild(u.ID, d.hashCode());
			break;
		case PRIMITIVEPRODUCE:
			toInterrupt = Action.createPrimitiveProduction(u.ID, d.ordinal());
			break;
		case PRIMITIVEGATHER:
			toInterrupt = Action.createPrimitiveGather(u.ID, d);
			break;
		case PRIMITIVEDEPOSIT:
			toInterrupt = Action.createPrimitiveDeposit(u.ID, d);
			break;
		case PRIMITIVEMOVE:
			toInterrupt = Action.createPrimitiveMove(u.ID, d.values()[(d.ordinal()+1)%d.values().length]);
			break;
		default:
			throw new IllegalArgumentException("This function was not built to handle "+type);
		}
		testDurativeSeparation(u, toTest);
		testDurativeInterruption(resetStrat, u, toInterrupt, toTest, interruptingDuration);
		
	}
	
	
	
	@Test
	public void testInterruptAndSuspensionGather()
	{
		for (ActionType type : ActionType.values())
		{
			if (ActionType.isPrimitive(type) && type != ActionType.FAILED && type != ActionType.FAILEDPERMANENTLY)
				for (ResourceNode.Type rntype : ResourceNode.Type.values())
					testInterruptAndSuspensionGather(Direction.SOUTH, rntype, type);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testInterruptAndSuspensionGather(Direction d, ResourceNode.Type type, ActionType atype) {
		resetState();
		int basicDuration = 500;
		//Change the template to make the duration right
		superUnit.setDurationGatherGold(basicDuration);
		superUnit.setDurationGatherWood(basicDuration);
		
		Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		Action toTest = Action.createPrimitiveGather(u.ID, d);
		final int largeNumber = Integer.MAX_VALUE;
		final ResourceNode rn = new ResourceNode(type, u.getxPosition()+d.xComponent(), u.getyPosition()+d.yComponent(), largeNumber, state.nextTargetID());
		state.addResource(rn);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				rn.reduceAmountRemaining(rn.getAmountRemaining()-largeNumber);
			}};
		Action toInterrupt;
		switch (atype)
		{
		case PRIMITIVEATTACK:
			toInterrupt = Action.createPrimitiveAttack(u.ID, d.ordinal());
			break;
		case PRIMITIVEBUILD:
			toInterrupt = Action.createPrimitiveBuild(u.ID, d.hashCode());
			break;
		case PRIMITIVEPRODUCE:
			toInterrupt = Action.createPrimitiveProduction(u.ID, d.ordinal());
			break;
		case PRIMITIVEGATHER:
			toInterrupt = Action.createPrimitiveGather(u.ID, d.values()[(d.ordinal()+1)%d.values().length]);
			break;
		case PRIMITIVEDEPOSIT:
			toInterrupt = Action.createPrimitiveDeposit(u.ID, d);
			break;
		case PRIMITIVEMOVE:
			toInterrupt = Action.createPrimitiveMove(u.ID, d);
			break;
		default:
			throw new IllegalArgumentException("This function was not built to handle "+atype);
		}
		testDurativeSeparation(u, toTest);
		testDurativeInterruption(resetStrat, u, toInterrupt, toTest, basicDuration);
		
	}
	
	@Test
	public void testInterruptAndSuspensionDeposit()
	{
		for (ActionType type : ActionType.values())
		{
			if (ActionType.isPrimitive(type) && type != ActionType.FAILED && type != ActionType.FAILEDPERMANENTLY)
				for (ResourceType rntype : ResourceType.values())
					testInterruptAndSuspensionDeposit(Direction.SOUTH, rntype, type);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testInterruptAndSuspensionDeposit(Direction d, final ResourceType type, ActionType atype) {
		resetState();
		int basicDuration = 500;
		//Change the template to make the duration right
		superUnit.setDurationDeposit(basicDuration);
		
		final Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		Action toTest = Action.createPrimitiveDeposit(u.ID, d);
		final int someNumber = 33;
		Unit th = superUnit.produceInstance(state);
		state.addUnit(th,u.getxPosition()+d.xComponent(),u.getyPosition()+d.yComponent());

		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				u.setCargo(type, someNumber);
			}};
		Action toInterrupt;
		switch (atype)
		{
		case PRIMITIVEATTACK:
			toInterrupt = Action.createPrimitiveAttack(u.ID, d.ordinal());
			break;
		case PRIMITIVEBUILD:
			toInterrupt = Action.createPrimitiveBuild(u.ID, d.hashCode());
			break;
		case PRIMITIVEPRODUCE:
			toInterrupt = Action.createPrimitiveProduction(u.ID, d.ordinal());
			break;
		case PRIMITIVEDEPOSIT:
			toInterrupt = Action.createPrimitiveDeposit(u.ID, d.values()[(d.ordinal()+1)%d.values().length]);
			break;
		case PRIMITIVEGATHER:
			toInterrupt = Action.createPrimitiveGather(u.ID, d);
			break;
		case PRIMITIVEMOVE:
			toInterrupt = Action.createPrimitiveMove(u.ID, d);
			break;
		default:
			throw new IllegalArgumentException("This function was not built to handle "+atype);
		}
		testDurativeSeparation(u, toTest);
		testDurativeInterruption(resetStrat, u, toInterrupt, toTest, basicDuration);
		
	}
	
	@Test
	public void testInterruptAndSuspensionAttack()
	{
		for (ActionType type : ActionType.values())
		{
			if (ActionType.isPrimitive(type) && type != ActionType.FAILED && type != ActionType.FAILEDPERMANENTLY)
				testInterruptAndSuspensionAttack(type);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testInterruptAndSuspensionAttack(ActionType type) {
		int basicDuration=500;
		resetState();
		//Change the template to make the duration right
		superUnit.setDurationAttack(basicDuration);
		
		Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		final Unit target = superUnit.produceInstance(state);
		int[] xy = state.getClosestPosition(u.getxPosition(), u.getyPosition());
		state.addUnit(target, state.getXExtent()/2, state.getYExtent()/2);
		final int largeNumber=50000;
		target.setHP(largeNumber);
		Action toTest = Action.createPrimitiveAttack(u.ID, target.ID);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				//reset hp to the large number
				target.setHP(largeNumber);
			}};
		Action toInterrupt;
		switch (type)
		{
		case PRIMITIVEATTACK:
			toInterrupt = Action.createPrimitiveAttack(u.ID, target.ID+1);
			break;
		case PRIMITIVEBUILD:
			toInterrupt = Action.createPrimitiveBuild(u.ID, target.ID);
			break;
		case PRIMITIVEPRODUCE:
			toInterrupt = Action.createPrimitiveProduction(u.ID, target.ID);
			break;
		case PRIMITIVEGATHER:
			toInterrupt = Action.createPrimitiveGather(u.ID, Direction.values()[(target.ID)%Direction.values().length]);
			break;
		case PRIMITIVEDEPOSIT:
			toInterrupt = Action.createPrimitiveDeposit(u.ID, Direction.values()[(target.ID)%Direction.values().length]);
			break;
		case PRIMITIVEMOVE:
			toInterrupt = Action.createPrimitiveMove(u.ID, Direction.values()[(target.ID)%Direction.values().length]);
			break;
		default:
			throw new IllegalArgumentException("This function was not built to handle "+type);
		}
		testDurativeSeparation(u, toTest);
		testDurativeInterruption(resetStrat, u, toInterrupt, toTest, basicDuration);
		
	}
	
	@Test
	public void testInterruptAndSuspensionProduce()
	{
		for (ActionType type : ActionType.values())
		{
			if (ActionType.isPrimitive(type) && type != ActionType.FAILED && type != ActionType.FAILEDPERMANENTLY)
				for (boolean makingunit : new boolean[]{true,false})
					for (boolean buildnotproduce : new boolean[]{true,false}) 
							testInterruptAndSuspensionProduce(type, makingunit, buildnotproduce);
		}
	}
	/**
	 * A slight abstraction for the other tests to use
	 */
	private void testInterruptAndSuspensionProduce(ActionType type, boolean makingunit, boolean buildnotproduce) {
		int basicDuration=500;
		resetState();
		
		//make it do the right thing between building and producing
		Template tomake = makingunit?targetProduceUnit:targetProduceUpgrade;
		superUnit.setCanBuild(buildnotproduce);
		//Change the template to make the duration right
		tomake.setTimeCost(basicDuration);
		
		final Unit u = superUnit.produceInstance(state);
		state.addUnit(u, state.getXExtent()/2, state.getYExtent()/2);
		Action toTest = buildnotproduce?Action.createPrimitiveBuild(u.ID, tomake.ID):Action.createPrimitiveProduction(u.ID, tomake.ID);
		ResetStrategy resetStrat = new ResetStrategy(){

			@Override
			public void reset() {
				//clear out all units other than the building/producing unit
				LinkedList<Integer> toremove=null;
				for (Unit possibleother : state.getUnits().values())
				{
					if (u.ID != possibleother.ID)
					{
						if (toremove == null) //lazy initialization
						{
							toremove = new LinkedList<Integer>();
						}
						toremove.add(possibleother.ID);
					}
				}
				if (toremove!=null)
					for(Integer id : toremove)
					{
						state.removeUnit(id);
					}
			}};
		Action toInterrupt;
		switch (type)
		{
		case PRIMITIVEATTACK:
			toInterrupt = Action.createPrimitiveAttack(u.ID, tomake.ID);
			break;
		case PRIMITIVEBUILD:
			toInterrupt = Action.createPrimitiveBuild(u.ID, tomake.ID+1);
			break;
		case PRIMITIVEPRODUCE:
			toInterrupt = Action.createPrimitiveProduction(u.ID, tomake.ID+1);
			break;
		case PRIMITIVEGATHER:
			toInterrupt = Action.createPrimitiveGather(u.ID, Direction.values()[(tomake.ID)%Direction.values().length]);
			break;
		case PRIMITIVEDEPOSIT:
			toInterrupt = Action.createPrimitiveDeposit(u.ID, Direction.values()[(tomake.ID)%Direction.values().length]);
			break;
		case PRIMITIVEMOVE:
			toInterrupt = Action.createPrimitiveMove(u.ID, Direction.values()[(tomake.ID)%Direction.values().length]);
			break;
		default:
			throw new IllegalArgumentException("This function was not built to handle "+type);
		}
		testDurativeSeparation(u, toTest);
		testDurativeInterruption(resetStrat, u, toInterrupt, toTest, basicDuration);
		
	}
	
//	private static class ActAndResetSuccessAndResetFailure
//	{
//		public final Action action;
//		public final ResetStrategy successResetter;
//		public final ResetStrategy failureResetter;
//		public ActAndResetSuccessAndResetFailure(Action action, ResetStrategy successResetter, ResetStrategy failureResetter)
//		{
//			this.action = action;
//			this.successResetter = successResetter;
//			this.failureResetter = failureResetter;
//		}
//	}
	
	/**
	 *
	 * A large abstraction, it repeats an single action many times and tests several things.
	 * It tests the feedback (incomplete vs complete) after each step.
	 * It tests the amount and identity after each step.
	 *
	 * @param resetStrategy A strategy that will be done every step to ensure that the action never fails.
	 * @param toAct The unit in the action
	 * @param action The action the unit should do.
	 * @param basicDuration The duration that the action should take each time.
	 * @param headStart The amount of head start to include on the first action.  This will be applied during execution, and thus need not be preapplied.
	 * @param numConsecutiveTries The number of times to repeat cycles of the action.
	 */
	private void testDurativeCompletion(ResetStrategy resetStrategy, Unit toAct, Action action, int basicDuration, int headStart, int numConsecutiveTries) {
		assertTrue("Initialization problem: must have a nonnegative but smaller head start than the basic duration",headStart<basicDuration && headStart>=0);
		
		//Give it the head start
		if (headStart==0)
			toAct.setDurativeStatus(null, 0);
		else //there is a headstart
			toAct.setDurativeStatus(action, headStart);
		int remainingDurationInFirst=basicDuration-headStart;
		
		for (int i = 1; i<=numConsecutiveTries*basicDuration; i++) //start at 1 and use a <= to make i be the number of times the action will have been done afterward
		{
			//Do action relevant things
			resetStrategy.reset();
			
			
			//Calculate if it should complete
				//for the first round, since it might have a restart, directly check equality, if i is the remainingDuration
				//for the rest, subtract out the first round, and do a modulus calculation
			int expectedDuration = remainingDurationInFirst>i?(i+headStart):((i-remainingDurationInFirst) % basicDuration);
			
			//put the action into the state
			Map<Integer,Action> acts = new HashMap<Integer,Action>();
			acts.put(toAct.ID, action);
			model.addActions(acts, player1);
			
			//run the actions
			model.executeStep();
			
			//examine the results
			{
				int lastround = state.getTurnNumber()-1;
				HistoryView history = model.getHistory().getView(Agent.OBSERVER_ID);
				Map<Integer, ActionResult> feedbacklist = history.getCommandFeedback(player1, lastround);
				//verify that there is only one action with feedback, and it is this action, then get it
				assertTrue("Problem (maybe not fully related to duration), should have only one feedback in a round",feedbacklist.size()==1);
				ActionResult feedback = feedbacklist.values().toArray(new ActionResult[0])[0];
				assertTrue("Problem (maybe not fully related to duration), should have feedback on the right action ("+action+"), but it gave feedback on "+feedback.getAction(),action.equals(feedback.getAction()));
				assertTrue("The "+i+"th attempt should leave progress of "+expectedDuration+", but the progress was "+toAct.getActionProgressAmount(),toAct.getActionProgressAmount()==expectedDuration);
				if (expectedDuration==0)
				{
					assertNull("The "+i+"th attempt should have finished the action, resetting progress (so the primitive should be null), but the primitive showing is "+toAct.getActionProgressPrimitive(),toAct.getActionProgressPrimitive());
					assertTrue("The "+i+"th attempt should have finished the action, so feedback should have been COMPLETE, but it was "+feedback.getFeedback(),ActionFeedback.COMPLETED==feedback.getFeedback());
				}
				else
				{
					assertTrue("The "+i+"th attempt should not have finished the action, and thus there should be some progress, but the action given was "+action + " and the progress was on "+toAct.getActionProgressPrimitive(),action.equals(toAct.getActionProgressPrimitive()));
					assertTrue("The "+i+"th attempt should not have finished the action, so feedback should have been INCOMPLETE, but it was "+feedback.getFeedback(),ActionFeedback.INCOMPLETE==feedback.getFeedback());
				}
				
			}
			
		}
	}
	
	/**
	 * Tests an action interrupting another.
	 * @param resetStrategy A strategy to ensure that the interrupting action will never fail.
	 * @param toAct
	 * @param interruptedAction
	 * @param interruptingAction
	 * @param interruptingActionDuration
	 */
	private void testDurativeInterruption(ResetStrategy resetStrategy, Unit toAct, Action interruptedAction, Action interruptingAction, int interruptingActionDuration) {
		assertTrue("Actions must be non-equal, or it won't be an interruption.  "+interruptedAction + " and " + interruptingAction + " are equal",!interruptingAction.equals(interruptedAction));
		
		toAct.setDurativeStatus(interruptedAction, 1);
		int i=1;
		{
			resetStrategy.reset();
			
			
			//Calculate if it should complete
				//This should only happen if the interrupting action will take only one duration
			int expectedDuration = i % interruptingActionDuration;
			
			//put the action into the state
			Map<Integer,Action> acts = new HashMap<Integer,Action>();
			acts.put(toAct.ID, interruptingAction);
			model.addActions(acts, player1);
			
			//run the actions
			model.executeStep();
			
			//examine the results
			{
				int lastround = state.getTurnNumber()-1;
				HistoryView history = model.getHistory().getView(Agent.OBSERVER_ID);
				Map<Integer, ActionResult> feedbacklist = history.getCommandFeedback(player1, lastround);
				//verify that there is only one action with feedback, and it is this action, then get it
				assertTrue("Problem (maybe not fully related to duration), should have only one feedback in a round",feedbacklist.size()==1);
				ActionResult feedback = feedbacklist.values().toArray(new ActionResult[0])[0];
				assertTrue("Problem (maybe not fully related to duration), should have feedback on the right action ("+interruptingAction+"), but it gave feedback on "+feedback.getAction(),interruptingAction.equals(feedback.getAction()));
				assertTrue("The "+i+"th attempt should leave progress of "+expectedDuration+", but the progress was "+toAct.getActionProgressAmount(),toAct.getActionProgressAmount()==expectedDuration);
				if (expectedDuration==0)
				{
					assertNull("The "+i+"th attempt should have finished the action, resetting progress (so the primitive should be null), but the primitive showing is "+toAct.getActionProgressPrimitive(),toAct.getActionProgressPrimitive());
					assertTrue("The "+i+"th attempt should have finished the action, so feedback should have been COMPLETE, but it was "+feedback.getFeedback(),ActionFeedback.COMPLETED==feedback.getFeedback());
				}
				else
				{
					assertTrue("The "+i+"th attempt should not have finished the action, and thus there should be some progress, but the action given was "+interruptingAction + " and the progress was on "+toAct.getActionProgressPrimitive(),interruptingAction.equals(toAct.getActionProgressPrimitive()));
					assertTrue("The "+i+"th attempt should not have finished the action, so feedback should have been INCOMPLETE, but it was "+feedback.getFeedback(),ActionFeedback.INCOMPLETE==feedback.getFeedback());
				}
				
			}
		}
	}
	
	/**
	 * Tests an action not being called during a call.
	 * @param toAct
	 * @param interruptedAction
	 */
	private void testDurativeSeparation(Unit toAct, Action interruptedAction) {
		toAct.setDurativeStatus(interruptedAction, 1);
		int i=1;
		{
			//Calculate if it should complete
				//This should only happen if the interrupting action will take only one duration
			int expectedDuration = 0;
			
			//run the (lack of) actions
			model.executeStep();
			
			//examine the results
			{
				int lastround = state.getTurnNumber()-1;
				HistoryView history = model.getHistory().getView(Agent.OBSERVER_ID);
				Map<Integer, ActionResult> feedbacklist = history.getCommandFeedback(player1, lastround);
				assertTrue("Problem (maybe not fully related to duration), should have only no feedback as nothing was done",feedbacklist.size()==0);
				assertTrue("The "+i+"th attempt should leave progress of "+expectedDuration+", but the progress was "+toAct.getActionProgressAmount(),toAct.getActionProgressAmount()==expectedDuration);
				assertNull("The "+i+"th attempt should have finished the action, resetting progress (so the primitive should be null), but the primitive showing is "+toAct.getActionProgressPrimitive(),toAct.getActionProgressPrimitive());
				
			}
		}
	}
	
	/**
	 * A unit that can produce gather deposit and move
	 */
	private UnitTemplate superUnit;
	private UpgradeTemplate targetProduceUpgrade;
	private UnitTemplate targetProduceUnit;
	private State state;
	private Model model;
	private void resetState() {
		
		StateBuilder sb = new StateBuilder();
		state = sb.build();
		state.setSize(999, 999);
		state.addPlayer(player1);
		List<UnitTemplate> unittemplates = new ArrayList<UnitTemplate>();
		List<UpgradeTemplate> upgradetemplates = new ArrayList<UpgradeTemplate>();
		superUnit = new UnitTemplate(state.nextTemplateID());
		
		superUnit.setCanMove(true);
		superUnit.setCanBuild(false);
		superUnit.setCanGather(false);
		superUnit.setBaseHealth(1);
		superUnit.setBasicAttack(3);
		superUnit.setPiercingAttack(9);
		superUnit.setRange(500);
		superUnit.setSightRange(500);
		superUnit.setTimeCost(1);
		superUnit.setFoodProvided(0);
		superUnit.setCanAcceptGold(true);
		superUnit.setCanAcceptWood(true);
		superUnit.setCanGather(true);
		superUnit.setWoodGatherRate(2);
		superUnit.setGoldGatherRate(3);
		superUnit.setGoldCost(0);
		superUnit.setWoodCost(0);
		superUnit.setFoodCost(0);
		superUnit.setName("TheUnit");
		superUnit.setPlayer(player1);
		unittemplates.add(superUnit);
		{
			targetProduceUnit = new UnitTemplate(state.nextTemplateID());
			targetProduceUnit.setTimeCost(1);
			targetProduceUnit.setFoodProvided(0);
			targetProduceUnit.setGoldCost(0);
			targetProduceUnit.setWoodCost(0);
			targetProduceUnit.setFoodCost(0);
			targetProduceUnit.setName("ProducedUnit");
			targetProduceUnit.setPlayer(player1);
			superUnit.addProductionItem(targetProduceUnit.ID);
			unittemplates.add(targetProduceUnit);
		}
		{
			targetProduceUpgrade = new UpgradeTemplate(state.nextTemplateID());
			targetProduceUpgrade.setTimeCost(1);
			targetProduceUpgrade.setGoldCost(0);
			targetProduceUpgrade.setWoodCost(0);
			targetProduceUpgrade.setFoodCost(0);
			targetProduceUpgrade.setName("ProducedUpgrade");
			targetProduceUpgrade.setPlayer(player1);
			superUnit.addProductionItem(targetProduceUpgrade.ID);
			upgradetemplates.add(targetProduceUpgrade);
		}
		
		
		
	
		
		
		
		//toss the templates into the state
		for(UnitTemplate u : unittemplates)
			state.addTemplate(u);
		for(UpgradeTemplate u : upgradetemplates)
			state.addTemplate(u);
		model = new LessSimpleModel(state, seed, null);
	}
	@Test
	public void testFailures()
	{
		throw new RuntimeException("This was not implemented because failing and suspending should have the same basic effect");
	}
	
	/**
	 * Used to reset relevant parts of the state each step so that the action never fails.
	 */
	private static interface ResetStrategy
	{
		public void reset();
	}
}
