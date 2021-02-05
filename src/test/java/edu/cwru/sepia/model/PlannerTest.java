/**
 * 	Strategy Engine for Programming Intelligent Agents (SEPIA)
	Copyright (C) 2012 Case Western Reserve University

	This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.model;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.visual.VisualAgent;
import edu.cwru.sepia.environment.model.DurativePlanner;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.State.StateBuilder;
import edu.cwru.sepia.environment.model.state.Tile.TerrainType;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.util.Direction;
import edu.cwru.sepia.util.DistanceMetrics;

/**
 * A test of the planner
 * @author The Condor
 *
 */
public class PlannerTest {
	private final static int TEST_PLAYER = 0;
	private final static int TEST_UNIT_ID = 1;
	private final static int TEST_TEMPLATE_ID = 1;
	//Give everything a sizeable duration so that the planner understands durations
	private final static int DURATION = 7;
	//If the tests are failing, turn this on and see why
	//TODO implement watching via visual agent in the verifyPath method
	final static boolean watch = false;
	static VisualAgent visualization;
	static {
		if (watch) {
			visualization = new VisualAgent(TEST_PLAYER, watch);
		}
	}
	
	
	/**
	 * Make a state that is size by size and has a test unit in the middle
	 * @param size
	 * @return
	 */
	private static State makeBasicState(int size) {
		return makeBasicState(size, size/2, size/2);
	}
	private static State makeBasicState(int size, int unitX, int unitY) {
		StateBuilder stateBuilder = new StateBuilder();
		stateBuilder.setSize(size, size);
		
		UnitTemplate unitTemplate = new UnitTemplate(TEST_TEMPLATE_ID);
		unitTemplate.setDurationAttack(DURATION);
		for (TerrainType terrainType : TerrainType.values()) {
			unitTemplate.setDurationMove(DURATION, terrainType);
		}
		unitTemplate.setDurationGatherWood(DURATION);
		unitTemplate.setDurationGatherGold(DURATION);
		unitTemplate.setDurationDeposit(DURATION);
		unitTemplate.setCharacter('*');
		unitTemplate.setTimeCost(DURATION);
		unitTemplate.addProductionItem(TEST_TEMPLATE_ID);//It can make more of itself
		stateBuilder.addTemplate(unitTemplate);
		Unit unit = new Unit(unitTemplate, TEST_UNIT_ID);
		stateBuilder.addUnit(unit, unitX, unitY);
		return stateBuilder.build();
	}
	/**
	 * @param terrainTypes format is [y][x], so that it looks good when you draw it in the test
	 * @param state
	 */
	private void assignTerrain(TerrainType[][] terrainTypes, State state) {
		for (int x = 0; x < state.getXExtent(); x++) {
			for (int y = 0; y < state.getYExtent(); y++) {
				state.getWorldBuilder().setTileType(x, y, terrainTypes[y][x]);
			}
		}
	}
	/**
	 * @param state
	 * @return
	 */
	private DurativePlanner getPlanner(State state) {
		return new DurativePlanner(state);
	}
	public static int calculateActionDuration(Unit u, int startingx, int startingy, Direction d, State state) {
		return u.getTemplate().getDurationMove(state.terrainAt(startingx + d.xComponent(), startingy + d.yComponent()));
	}
	public static void verifyDestination(State state, int goalX, int goalY, List<Action> path) {
		Unit testUnit = state.getUnit(TEST_UNIT_ID);
		int ongoingActionSteps = 0;
		Action lastAction = null;
		boolean finishedNonMove = false; //We are assuming that non-move actions can only complete once per plan and only as the last action
		//To avoid depending on model, we will do a little modeling logic here
		for (Action action : path) {
			showVisualization(state);
			assertTrue("Switched actions without finishing one", ongoingActionSteps == 0 || lastAction.equals(action));
			assertFalse("Trying to do another action after finishing a non-move action", finishedNonMove);
			ongoingActionSteps++;
			switch(action.getType()) {
			case PRIMITIVEMOVE:
			{	
				Direction moveDirection = ((DirectedAction)action).getDirection();
				assertTrue("Tried to move into an occupied area (moving "+moveDirection+" from " + testUnit.getxPosition()+","+testUnit.getyPosition(), state.positionAvailable(testUnit.getxPosition()+moveDirection.xComponent(), testUnit.getyPosition()+moveDirection.yComponent()));
				if (ongoingActionSteps >= calculateActionDuration(testUnit, testUnit.getxPosition(), testUnit.getyPosition(), moveDirection, state)) {
					ongoingActionSteps = 0;
					state.moveUnit(testUnit, moveDirection);
					lastAction = null;
				}
					
				break;
			}
			case PRIMITIVEGATHER:
			{
				Direction gatherDirection = ((DirectedAction)action).getDirection();
				assertTrue("Tried to gather from nothing", state.resourceAt(testUnit.getxPosition()+gatherDirection.xComponent(), testUnit.getyPosition()+gatherDirection.yComponent()) != null);
				assertEquals("Tried to gather not from target X", testUnit.getxPosition()+gatherDirection.xComponent());
				assertEquals("Tried to gather not from target Y", testUnit.getyPosition()+gatherDirection.yComponent());
				ResourceType targetedType = ResourceNode.Type.getResourceType(state.resourceAt(testUnit.getxPosition()+gatherDirection.xComponent(), testUnit.getyPosition()+gatherDirection.yComponent()).getType());
				int duration;
				switch (targetedType) {
				case GOLD:
					duration = testUnit.getTemplate().getDurationGatherGold();
					break;
				case WOOD:
					duration = testUnit.getTemplate().getDurationGatherWood();
					break;
				default:
					throw new RuntimeException("Need to add new type to enum: "+targetedType);
				}
				if (ongoingActionSteps >= duration) {
					ongoingActionSteps = 0;
					finishedNonMove = true;
				}
				break;
			}
			case PRIMITIVEDEPOSIT:
			{
				Direction depositDirection = ((DirectedAction)action).getDirection();
				assertTrue("Tried to deposit at nothing", state.unitAt(testUnit.getxPosition()+depositDirection.xComponent(), testUnit.getyPosition()+depositDirection.yComponent()) != null);
				assertEquals("Tried to gather not from target X", testUnit.getxPosition()+depositDirection.xComponent());
				assertEquals("Tried to gather not from target Y", testUnit.getyPosition()+depositDirection.yComponent());
				UnitTemplate depositeeTemplate = state.unitAt(testUnit.getxPosition()+depositDirection.xComponent(), testUnit.getyPosition()+depositDirection.yComponent()).getTemplate();
				assertTrue("Tried to deposit in non-depot", depositeeTemplate.canAcceptGold() || depositeeTemplate.canAcceptWood());
				int duration = testUnit.getTemplate().getDurationDeposit();
				if (ongoingActionSteps >= duration) {
					ongoingActionSteps = 0;
					finishedNonMove = true;
				}
				break;
			}
			case PRIMITIVEBUILD:
			{
				int duration = testUnit.getTemplate().getTimeCost(); //It builds itself for the test
				if (ongoingActionSteps >= duration) {
					ongoingActionSteps = 0;
					finishedNonMove = true;
				}
				break;
			}
			case PRIMITIVEPRODUCE:
			{
				int duration = testUnit.getTemplate().getTimeCost(); //It builds itself for the test
				if (ongoingActionSteps >= duration) {
					ongoingActionSteps = 0;
					finishedNonMove = true;
				}
				break;
			}
			case PRIMITIVEATTACK:
			{
				TargetedAction attackAction = (TargetedAction)action;
				assertTrue("Attack when out of range", DistanceMetrics.chebyshevDistance(testUnit, state.getUnit(attackAction.getTargetId())) < testUnit.getTemplate().getRange());
				int duration = testUnit.getTemplate().getDurationAttack();
				if (ongoingActionSteps >= duration) {
					ongoingActionSteps = 0;
					finishedNonMove = true;
				}
				break;
			}
			default:
				throw new RuntimeException("Need to add new primitive type to enum, or compound wrongly used: "+action.getType());
			}
			lastAction = action;
		}
		showVisualization(state);
		assertTrue("Ended with ongoing actions", ongoingActionSteps == 0);
		assertTrue("Ended in the wrong place. Expected "+goalX + "," + goalY +" but unit ended at "+testUnit.getxPosition()+","+testUnit.getyPosition(), goalX == testUnit.getxPosition() && goalY == testUnit.getyPosition());
		
	}
	/**
	 * Look at the state, if possible
	 * @param state
	 */
	private static void showVisualization(State state) {
		if (watch) {
			History history = new History();
			history.addPlayer(TEST_PLAYER);
			visualization.initialStep(state.getView(TEST_PLAYER), history.getView(TEST_PLAYER));
		}
		
	}
	public static void verifyPathLength(List<Action> path, int expectedMoveNumber, int expectedNonMoveEndingNumber, ActionType nonMoveEndingType) {
		assertEquals("Wrong number of actions assigned", expectedMoveNumber + expectedNonMoveEndingNumber, path.size());
		Iterator<Action> pathIterator = path.iterator();
		for (int i = 0; i < expectedMoveNumber; i++) {
			assertTrue("Action "+i+" was supposed to be a move, but too few actions were assigned", pathIterator.hasNext());
			Action actualAction = pathIterator.next();
			assertEquals("Action "+i+" was an unexpected type", ActionType.PRIMITIVEMOVE, actualAction.getType());
		}
		for (int i = expectedMoveNumber; i < expectedNonMoveEndingNumber + expectedMoveNumber ; i++) {
			assertTrue("Action "+i+" was supposed to be a non-move, but too few actions were assigned", pathIterator.hasNext());
			Action actualAction = pathIterator.next();
			assertEquals("Action "+i+" was an unexpected type", nonMoveEndingType, actualAction.getType());
		}
		
	}
	
	public static void testDirectional(Unit unit, ActionType actionType, int targetX, int targetY) {
		
	}
	public static Unit createUnit(State state, int unitId) {
		UnitTemplate unitTemplate = new UnitTemplate(state.nextTemplateID());
		System.out.println(unitTemplate);
		return new Unit(unitTemplate, unitId);
	}
	@Test public void moveToSelf() {
		int durationMove = 7;
		State state = makeBasicState(3);
		((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(durationMove, TerrainType.LAND);
		DurativePlanner planner = getPlanner(state);
		Action action = Action.createCompoundMove(TEST_UNIT_ID, 1, 1);
		List<Action> plannedActions = planner.planMove(TEST_UNIT_ID, 1, 1);
		verifyDestination(state, 1, 1, plannedActions);
		verifyPathLength(plannedActions, 0*durationMove, 0, null);
	}
	@Test public void moveToPosition() {
		//Make a 6x6 with the unit at 2,2
		int durationMove = 7;
		State state = makeBasicState(6);
		((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(durationMove, TerrainType.LAND);
		DurativePlanner planner = getPlanner(state);
		Action action = Action.createCompoundMove(TEST_UNIT_ID, 5, 4);
		List<Action> plannedActions = planner.planMove(TEST_UNIT_ID, 5, 4);
		verifyDestination(state, 5, 4, plannedActions);
		verifyPathLength(plannedActions, 2*durationMove, 0, null);
	}
	
	/**
	 *  To each edge and corner and beyond
	 */
	@Test public void moveBounds() {
		int durationMove = 7;
		for (int destinationX : new int[]{0, 75, 100}) {
			for (int destinationY : new int[]{0, 75, 100}) {
				State state = makeBasicState(101);
				DurativePlanner planner = getPlanner(state);
				List<Action> plannedActions = planner.planMove(TEST_UNIT_ID, destinationX, destinationY);
				verifyDestination(state, destinationX, destinationY, plannedActions);
				verifyPathLength(plannedActions, durationMove * DistanceMetrics.chebyshevDistance(50, 50, destinationX, destinationY), 0, null);
			}
		}
	}
	
	
	
//	@Test public void moveOntoObject();//Specific behavior may be optional
//	
//	
//	@Test public void attackSelf();
//	@Test public void attackInRange();
//	@Test public void attackPlusMove();
//	@Test public void attackOverWall();
//	
//	
//	@Test public void gatherInRange();
//	@Test public void gatherPlusMove();
//	@Test public void gatherSurrounded();
//	
//	//Same for deposit as gather
//	
//	@Test public void durationMove();
//	@Test public void durationProduce();

//	@Test public void durationBuild() {}
//	@Test public void durationGather();
//	@Test public void durationDeposit();
//	@Test public void durationAttack();
//	
//	@Test public void resourceAvoid();
//	@Test public void unitAvoid();
//	@Test public void trappedByResourceAndUnitAndEdge();
//	@Test public void smallMaze();
//	
//	@Test public void buildOnSelf();
//	@Test public void buildPlusMove();
//	@Test public void buildSurrounded();
//	
	
	@Test public void avoidImpassibleTerrain() {
		State state = makeBasicState(5, 4, 3);
		((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(-1, TerrainType.WATER);
		((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(DURATION, TerrainType.LAND);
		TerrainType N = TerrainType.WATER;
		TerrainType Y = TerrainType.LAND;
		assignTerrain(new TerrainType[][]{	{Y,Y,Y,Y,Y},
											{Y,N,Y,N,N},
											{Y,N,Y,N,N},
											{N,Y,N,Y,Y},
											{N,N,Y,N,N}
									
									}, state);
		DurativePlanner planner = getPlanner(state);
		List<Action> plannedActions = planner.planMove(TEST_UNIT_ID, 4, 0);
		verifyDestination(state, 4, 0, plannedActions);
		verifyPathLength(plannedActions, 5*DURATION, 0, null);
	}
	
//
	@Test public void PathOnTerrain(){
		State state = makeBasicState(5, 4, 0);
		int aDuration = 1;
		int bDuration = 2;
		int cDuration = 3;
		int dDuration = 4;
		TerrainType A = TerrainType.LAND;((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(aDuration, TerrainType.LAND);
		TerrainType B = TerrainType.CLIFF;((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(bDuration, TerrainType.CLIFF);
		TerrainType C = TerrainType.SHALLOWS;((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(cDuration, TerrainType.SHALLOWS);
		TerrainType D = TerrainType.STUFF;((UnitTemplate)state.getTemplate(TEST_TEMPLATE_ID)).setDurationMove(dDuration, TerrainType.STUFF);
		assignTerrain(new TerrainType[][]{	{A,A,A,A,A},
											{A,A,C,D,D},
											{B,A,B,B,B},
											{D,C,D,A,D},
											{D,A,A,A,A}
									
									}, state);
		DurativePlanner planner = getPlanner(state);
		List<Action> plannedActions = planner.planMove(TEST_UNIT_ID, 2, 4);
		verifyDestination(state, 2, 4, plannedActions);
		verifyPathLength(plannedActions, aDuration+aDuration+aDuration+bDuration+aDuration+aDuration, 0, null);
	}
	
	
	//@Test continueMove
	//@Test continueAttack
	//@Test continue...
	//@Test unableToContinueContinue (make sure that it doesn't do it wrong when you try to continue a move that you won't be doing or that you won't do next (like if you have some of a primitive attack done, but you need to take another step to continue, then you shouldn't be able to continue)
//	//--------------
//	//OPTIONAL
//	//--------------
//	//These tests are not to enforce behavior and are therefore subject to change without warning, do not write code that relies on these cases.
//	//The optional tests' purpose is to act as an indicator that the behavior has changed in some way.
//	
//	@Test public void optionalGatherNoDistance();
//	@Test public void optionalDepositNoDistance();
//	@Test public void optionalDepositSelf();
	/**
	 *  Beyond each edge and corner
	 *  Expected behavior is presently to give a plan that says , but new behavior that caused it to go as close as possible, with an indicator that the path was incomplete, would not be unwelcome.
	 */
	@Test public void moveOutOfBounds() {
		int durationMove = 9;
		
		for (int destinationX : new int[]{-100, -1, 75, 101, 500}) {
			for (int destinationY : new int[]{-100, -1, 75, 101, 500}) {
				if (destinationX == destinationY && destinationX == 75) {
					//Specifically exclude the one that is all the way in bounds
					continue;
				}
				State state = makeBasicState(101);
				DurativePlanner planner = getPlanner(state);
				List<Action> plannedActions = planner.planMove(TEST_UNIT_ID, destinationX, destinationY);
				assertEquals("Out of bounds should yield a single failedPermanently action, wrong size (it was" + plannedActions + ")", 1, plannedActions.size());
				assertEquals("Out of bounds should yield a single failedPermanently action, wrong content", Action.createPermanentFail(TEST_UNIT_ID), plannedActions.get(0));
				
			}
		}
	}
	
}
