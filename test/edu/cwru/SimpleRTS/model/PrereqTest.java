package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionFeedback;
import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class PrereqTest {

	static SimpleModel model;
	static SimplePlanner planner;
	static List<Template<?>> templates;
	static State state;
	static int player = 0;
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void testGoodAndGood()
	{
		Random r = new Random();
		testPrereq(makeArray(r,5,5),makeArray(r,3,3),true);
		testPrereq(makeArray(r,3,3),makeArray(r,7,7),true);
		testPrereq(makeArray(r,1,1),makeArray(r,12,12),true);
		testPrereq(makeArray(r,12,12),makeArray(r,1,1),true);
	}
	@Test
	public void testNothingAndGood()
	{
		Random r = new Random();
		testPrereq(new boolean[0],makeArray(r,3,3),true);
		testPrereq(makeArray(r,3,3),new boolean[0],true);
		testPrereq(new boolean[0],makeArray(r,12,12),true);
		testPrereq(makeArray(r,12,12),new boolean[0],true);
	}
	@Test
	public void testNothingAndBad()
	{
		Random r = new Random();
		testPrereq(makeArray(r,3,0),new boolean[0],true);
		testPrereq(new boolean[0],makeArray(r,3,1),true);
		testPrereq(new boolean[0],makeArray(r,12,0),true);
		testPrereq(makeArray(r,12,8),new boolean[0],true);
	}
	@Test
	public void testGoodAndBad()
	{
		Random r = new Random();
		testPrereq(makeArray(r,5,5),makeArray(r,3,1),true);
		testPrereq(makeArray(r,3,0),makeArray(r,7,7),true);
		testPrereq(makeArray(r,1,1),makeArray(r,12,0),true);
		testPrereq(makeArray(r,12,8),makeArray(r,4,4),true);
	}
	@Test
	public void testBadAndBad()
	{
		Random r = new Random();
		testPrereq(makeArray(r,5,4),makeArray(r,3,1),true);
		testPrereq(makeArray(r,3,0),makeArray(r,7,2),true);
		testPrereq(makeArray(r,1,0),makeArray(r,12,0),true);
		testPrereq(makeArray(r,12,8),makeArray(r,4,2),true);
	}
	@Test
	public void testNothingAndNothing()
	{
		Random r = new Random();
		testPrereq(new boolean[0],new boolean[0],true);
	}
	private boolean[] makeArray(Random r, int howMany, int howManyGood) {
		boolean[] toReturn = new boolean[howMany];
		//pick several random ones
		int[] order = new int[howMany];
		for (int i = 0; i<howMany;i++)
			order[i]=i;
		for (int i = howMany-1; howManyGood>0; i--,howManyGood--) {
			//pick one
			int indtopick = r.nextInt(i+1);
			int t = order[indtopick];
			order[indtopick]=order[i];
			order[i]=t;
			//the one previously at indtopick is marked, and put at a late index so it will be out of reach
			toReturn[t]=true;
		}
		return toReturn;
	}
	/**
	 * 
	 * @param whichupgrades which prerequisite upgrades should be researched
	 * @param whichunits which prerequisite units should exist 
	 * @param unit
	 */
	public void testPrereq(boolean[] whichupgrades, boolean[] whichunits, boolean unit) {
		State.StateBuilder builder = new State.StateBuilder();
		builder.setSize(3,Math.max(whichupgrades.length, whichunits.length));
		state = builder.build();
		System.out.println("Sucessfully loaded templates");
		
		List<UnitTemplate> unittemplates= new ArrayList<UnitTemplate>();
		List<UpgradeTemplate> upgradetemplates=new ArrayList<UpgradeTemplate>();
		
		UnitTemplate producertemplate;
		
		int numearlier = 0;
		Template<?> producee;
		//Add the thing being produced
		{
			producee = new UnitTemplate(state.nextTemplateID());
			producee.setName("Producee");
			builder.addTemplate(producee);
			unittemplates.add((UnitTemplate)producee);
			numearlier++;
		}
		//Add the producer
		{
			producertemplate = new UnitTemplate(state.nextTemplateID());
			producertemplate.setName("Producer");
			builder.addTemplate(producertemplate);
			producertemplate.addProductionItem(producee.ID);
			unittemplates.add(producertemplate);
				numearlier++;
		}
		for (int i = 0; i<whichupgrades.length;i++)
		{
			UpgradeTemplate newTemplate = new UpgradeTemplate(state.nextTemplateID());
			newTemplate.setName("Upgrade"+i);
			newTemplate.setPlayer(player);
			producee.addUpgradePrerequisite(newTemplate.ID);
			builder.addTemplate(newTemplate);
			upgradetemplates.add(newTemplate);
			producertemplate.addUpgradePrerequisite(newTemplate.ID);
		}
		for (int i = 0; i<whichunits.length;i++)
		{
			UnitTemplate newTemplate = new UnitTemplate(state.nextTemplateID());
			newTemplate.setName("Unit"+i);
			newTemplate.setPlayer(player);
			producee.addBuildPrerequisite(newTemplate.ID);
			builder.addTemplate(newTemplate);
			unittemplates.add(newTemplate);
			producertemplate.addBuildPrerequisite(newTemplate.ID);
		}
		for (int i = 0; i<whichupgrades.length;i++)
			if (whichupgrades[i])
				state.addUpgrade(upgradetemplates.get(i).produceInstance(state));
		for (int i = 0; i<whichunits.length;i++)
			if (whichunits[i])
				state.addUnit(unittemplates.get(i+numearlier).produceInstance(state),1,i);
		System.out.println(state.getTemplates(player));
		System.out.println(state.getUnits());
		Unit producer;
		producer = producertemplate.produceInstance(state);
		state.addUnit(producer, 0, 0);
		builder.setResourceAmount(player, ResourceType.GOLD, 1000);
		builder.setResourceAmount(player, ResourceType.WOOD, 1000);
		planner = new SimplePlanner(state);
		model = new SimpleModel(state, 5536,null);
		model.setVerbose(true);
		//Try to do an upgrade that you didn't do the prerequisite for
		LinkedList<Action> plan = planner.planProduce(producer, producee);
		for(Action a : plan)
		{
//			model.setActions(new Action[]{a});
			{
				Map<Integer, Action> actions = new HashMap<Integer, Action>();
				actions.put(a.getUnitId(),a);
				model.addActions(actions, player);
			}
			model.executeStep();
			System.out.println(model.getHistory().getView(player).getCommandFeedback(player, model.getState().getTurnNumber()-1).get(0));
		}
		boolean shouldsucceed=true;
		for (boolean b : whichunits)
			if (!b)
				shouldsucceed=false;
		for (boolean b : whichupgrades)
			if (!b)
				shouldsucceed=false;
		List<ActionResult> results = model.getHistory().getView(player).getCommandFeedback(player, model.getState().getTurnNumber()-1);
		assertTrue("Problem in setup, should be only one result",results.size()==1);
		if (shouldsucceed)
			assertTrue("It failed when it should have succeded",(results.get(0).getFeedback()==ActionFeedback.COMPLETED));
		else
			assertTrue("It succeeded when it shouldn't have",(results.get(0).getFeedback()!=ActionFeedback.COMPLETED));
		
	}
}
