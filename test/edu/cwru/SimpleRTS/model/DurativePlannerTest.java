package edu.cwru.SimpleRTS.model;

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


import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionFeedback;
import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.agent.visual.VisualAgent;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
import edu.cwru.SimpleRTS.util.GameMap;
import edu.cwru.SimpleRTS.util.TypeLoader;

/**
 * Contains tests based on durative actions actually happening and transitioning properly
 * @author The Condor
 *
 */
public class DurativePlannerTest {
	
	/**
	 * A debug bypass allowing visualization to investigate failures
	 */
	final static boolean visualize=false;
	private static Agent vagent;
	static int seed = 3;
	static int player1=0;
	@BeforeClass
	public static void setup() throws Exception {
		if (visualize)
			vagent = new VisualAgent(player1, new String[]{"false","true"});
	}
	/**
	 * Test basic pathfinding in the presence of durative actions
	 */
	@Test
	public void testAStarNavigation()
	{
		throw new RuntimeException("Not Implemented yet");
	}
	/**
	 * Test the ability to plan a move that continues properly
	 */
	@Test
	public void testContinuationMove()
	{
		//By testing multiple directions of head starts, it ensures that the tie breaking is working properly
		for (Direction d : Direction.values())
		{
			testContinuationMove(6, 0, d);
			testContinuationMove(30, 2, d);
		}
		
	}
	
	/**
	 * This also checks to ensure proper tiebreaking.
	 * @param u
	 * @param distanceBeforeFinal The distance to move before the final action.
	 */
	public void testContinuationMove(int distance, int headStart, Direction headStartDirection)
	{
		assertTrue(distance >= 1);
		resetState();
		
		Unit u = superUnit.produceInstance(state);
		state.addUnit(u, 0,0);
		int finalX=u.getxPosition()+distance;
		int finalY=u.getyPosition()+distance/2;
		//Create a compound move 
		Action toTest = Action.createCompoundMove(u.ID, finalX, finalY);
		
		if (headStart!=0)
		{
			int directedX=u.getxPosition()+headStartDirection.xComponent();
			int directedY=u.getyPosition()+headStartDirection.yComponent();
			boolean headStartMatters = state.inBounds(directedX, directedY) && DistanceMetrics.chebyshevDistance(u.getxPosition(), u.getyPosition(), finalX, finalY) > DistanceMetrics.chebyshevDistance(directedX,directedY, finalX, finalY);
			if (!headStartMatters)
			{
				headStart = 0;
			}
			else
			{
				u.setDurativeStatus(Action.createPrimitiveMove(u.ID, headStartDirection), headStart);
			}
		}
		testCompoundDurativeCompletion(toTest, distance-1, u.getTemplate().getDurationMove(), u.getTemplate().getDurationMove(), headStart);
		
	}
	
	@Test
	public void testContinuationGather()
	{
		//By testing multiple directions of head starts, it ensures that the tie breaking is working properly
		for (Direction d : Direction.values())
		{
			for (ResourceNode.Type type : ResourceNode.Type.values())
			{
			testContinuationGather(type, 6, 0, d);
			testContinuationGather(type, 30, 2, d);
			}
		}
		
	}
	
	/**
	 * This also checks to ensure proper tiebreaking.
	 * @param u
	 * @param distanceBeforeFinal The distance to move before the final action.
	 */
	public void testContinuationGather(ResourceNode.Type type, int distance, int headStart, Direction headStartDirection)
	{
		assertTrue(distance >= 1);
		resetState();
		
		Unit u = superUnit.produceInstance(state);
		state.addUnit(u, 0,0);
		int finalX=u.getxPosition()+distance;
		int finalY=u.getyPosition()+distance/2;
		ResourceNode rn = new ResourceNode(type,finalX,finalY,5005,state.nextTargetID());
		state.addResource(rn);
		//Create a compound move 
		Action toTest = Action.createCompoundGather(u.ID, rn.ID);
		
		if (headStart!=0)
		{
			int directedX=u.getxPosition()+headStartDirection.xComponent();
			int directedY=u.getyPosition()+headStartDirection.yComponent();
			boolean headStartMatters = state.inBounds(directedX, directedY) && DistanceMetrics.chebyshevDistance(u.getxPosition(), u.getyPosition(), finalX, finalY) > DistanceMetrics.chebyshevDistance(directedX,directedY, finalX, finalY);
			if (!headStartMatters)
			{
				headStart = 0;
			}
			else
			{
				if (distance > 1)
					u.setDurativeStatus(Action.createPrimitiveMove(u.ID, headStartDirection), headStart);
				else
					u.setDurativeStatus(Action.createPrimitiveGather(u.ID, headStartDirection), headStart);
			}
		}
		int gatherDuration;
		if (type==ResourceNode.Type.GOLD_MINE)
			gatherDuration = u.getTemplate().getDurationGatherGold();
		else if (type==ResourceNode.Type.TREE)
			gatherDuration = u.getTemplate().getDurationGatherWood();
		else
			throw new RuntimeException(type+" unsupported");
		testCompoundDurativeCompletion(toTest, distance-1, u.getTemplate().getDurationMove(), gatherDuration, headStart);
		
	}
	private void testCompoundDurativeCompletion(Action action, int distanceTravelBeforeFinal, int moveDuration, int finalActionDuration, int preappliedHeadStart) {
		//Some of the following may be overly complicated.  This should give more hooks when debugging.
		boolean noPriorMove = distanceTravelBeforeFinal==0;
		int firstActionDuration = (noPriorMove?finalActionDuration:moveDuration);
		assertTrue("Initialization problem: must have a nonnegative but smaller head start than the basic duration",preappliedHeadStart<firstActionDuration && preappliedHeadStart>=0);
		int remainingDurationInFirstAfterHeadStart = firstActionDuration-preappliedHeadStart;
		int expectedNumIterations=distanceTravelBeforeFinal*moveDuration+finalActionDuration-preappliedHeadStart;
		int expectedNumIterationsPriorToFinal = noPriorMove?0:expectedNumIterations-finalActionDuration;
		int niterations=0;
		boolean done = false;
		Unit toAct = state.getUnit(action.getUnitId());
		
		//put the compound action in
		Map<Integer,Action> acts = new HashMap<Integer,Action>();
		acts.put(action.getUnitId(), action);
		model.addActions(acts, player1);
		while (!done)
		{
			niterations++;
			
			
			done = niterations == expectedNumIterations;
			//Calculate how much progress you expect to have
				//if you haven't completed the first action just return
				//otherwise (have completed first action)
					//if you are on the final action, then the progress is that action's duration minus the amount that needs to be done before the end (modulo to make the final action have no progress)
					//otherwise not on final action or first action, so take the number of iterations since the first action and modulo the duration of a move
			int expectedProgress = remainingDurationInFirstAfterHeadStart>niterations
											?(niterations+preappliedHeadStart)
											:(expectedNumIterationsPriorToFinal<niterations
													?(finalActionDuration-(expectedNumIterations-niterations))%finalActionDuration
													:(niterations-remainingDurationInFirstAfterHeadStart)%moveDuration);
			
			if (visualize)
			{
				if (niterations==0)
					vagent.initialStep(state.getView(player1), model.getHistory().getView(player1));
				else
					vagent.middleStep(state.getView(player1), model.getHistory().getView(player1));
			}
			//continue running the compound action
			model.executeStep();
			System.out.println(toAct.getxPosition()+","+toAct.getyPosition());
			//examine the results
			{
				int lastround = state.getTurnNumber()-1;
				HistoryView history = model.getHistory().getView(Agent.OBSERVER_ID);
				List<ActionResult> feedbacklist = history.getCommandFeedback(player1, lastround);
				//verify that there is only one action with feedback, and it is this action, then get it
//				assertTrue("Problem (maybe not fully related to duration), should have only one feedback in a round",feedbacklist.size()==1);
//				ActionResult feedback = feedbacklist.get(0);
//				assertTrue("Problem (maybe not fully related to duration), should have feedback on the right action ("+action+"), but it gave feedback on "+feedback.getAction(),action.equals(feedback.getAction()));
//				System.out.println(feedback);
				for (ActionResult feedback : feedbacklist)
					System.out.println(feedback);
				ActionResult feedback = feedbacklist.get(0);
				assertTrue("The "+niterations+"th attempt should leave progress of "+expectedProgress+", but the progress was "+toAct.getActionProgressAmount(),toAct.getActionProgressAmount()==expectedProgress);
				if (expectedProgress==0)
				{
					assertNull("The "+niterations+"th attempt should have finished the primitive subaction, resetting progress (so the primitive should be null), but the primitive showing is "+toAct.getActionProgressPrimitive(),toAct.getActionProgressPrimitive());
				}
				else
				{
					assertTrue("The "+niterations+"th attempt should not have finished the primitive subaction, and thus there should be some progress",toAct.getActionProgressPrimitive()!=null);
				}
				if (done)
				{
					assertTrue("The "+niterations+"th attempt should have finished the compound action, so feedback should have been COMPLETE, but it was "+feedback.getFeedback(),ActionFeedback.COMPLETED==feedback.getFeedback());
				}
				else
				{
					assertTrue("The "+niterations+"th attempt should not have finished the compound action, so feedback should have been INCOMPLETE, but it was "+feedback.getFeedback(),ActionFeedback.INCOMPLETE==feedback.getFeedback());
				}
				
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
		state.setSize(100, 100);
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
		superUnit.setDurationAttack(13);
		superUnit.setDurationDeposit(17);
		superUnit.setDurationMove(7);
		superUnit.setDurationGatherGold(5);
		superUnit.setDurationGatherWood(11);
		superUnit.setCharacter('?');
		superUnit.setName("TheUnit");
		superUnit.setPlayer(player1);
		unittemplates.add(superUnit);
		{
			targetProduceUnit = new UnitTemplate(state.nextTemplateID());
			targetProduceUnit.setTimeCost(19);
			targetProduceUnit.setFoodProvided(0);
			targetProduceUnit.setGoldCost(0);
			targetProduceUnit.setWoodCost(0);
			targetProduceUnit.setFoodCost(0);
			targetProduceUnit.setName("ProducedUnit");
			targetProduceUnit.setPlayer(player1);
			superUnit.addProductionItem(targetProduceUnit.getName());
			unittemplates.add(targetProduceUnit);
		}
		{
			targetProduceUpgrade = new UpgradeTemplate(state.nextTemplateID());
			targetProduceUpgrade.setTimeCost(23);
			targetProduceUpgrade.setGoldCost(0);
			targetProduceUpgrade.setWoodCost(0);
			targetProduceUpgrade.setFoodCost(0);
			targetProduceUpgrade.setName("ProducedUpgrade");
			targetProduceUpgrade.setPlayer(player1);
			superUnit.addProductionItem(targetProduceUpgrade.getName());
			upgradetemplates.add(targetProduceUpgrade);
		}
		
		
		
	
		
		
		
		//convert the names to ids
		for(UnitTemplate u : unittemplates)
			u.namesToIds(unittemplates, upgradetemplates);
		for(UpgradeTemplate u : upgradetemplates)
			u.namesToIds(unittemplates, upgradetemplates);
		
		//toss the templates into the state
		for(UnitTemplate u : unittemplates)
			state.addTemplate(u);
		for(UpgradeTemplate u : upgradetemplates)
			state.addTemplate(u);
		model = new LessSimpleModel(state, seed, null);
	}
}
