/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
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
package edu.cwru.sepia.environment.persistence;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.junit.BeforeClass;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.ActionLogger;
import edu.cwru.sepia.environment.model.history.ActionResultLogger;
import edu.cwru.sepia.environment.model.history.EventLogger;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.PlayerHistory;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.state.persistence.ActionAdapter;
import edu.cwru.sepia.environment.state.persistence.generated.XmlAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlDirectedAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlLocatedAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlLocatedProductionAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.sepia.environment.state.persistence.generated.XmlProductionAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.sepia.environment.state.persistence.generated.XmlTargetedAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlTemplate;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUnit;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.sepia.util.Direction;
import edu.cwru.sepia.util.TypeLoader;

public class AdapterTestUtil {

	@SuppressWarnings("rawtypes")
	@BeforeClass
	public static Map<Integer,Template> loadTemplates() throws FileNotFoundException, JSONException {
		Map<Integer,Template> templates = new HashMap<Integer,Template>();
		List<Template<?>> templateList = TypeLoader.loadFromFile("data/unit_templates", 0,new State());
		for(Template t : templateList)
		{
			templates.put(t.ID,t);
		}
		return templates;
	}
	
	public static XmlUnit createExampleUnit(Random r, List<XmlUnitTemplate> unittemplates, List<XmlTemplate> alltemplates) {
		XmlUnit xml = new XmlUnit();
		XmlUnitTemplate chosentemplate = unittemplates.get(r.nextInt(unittemplates.size()));
		xml.setID(r.nextInt());
		if (chosentemplate.isCanGather())
		{
			xml.setCargoAmount(r.nextInt());
			xml.setCargoType(ResourceType.values()[r.nextInt(ResourceType.values().length)]);
		}
		xml.setCurrentHealth(r.nextInt(chosentemplate.getBaseHealth())+1);
		
//		//random template it can make:
//		boolean invalidproduction = true;
//		Integer thingtoproduce = null;
//		while (invalidproduction)
//		{
//			XmlTemplate toproduces = alltemplates.get(r.nextInt(alltemplates.size()));
//			if (chosentemplate.getProduces().contains(toproduces.getName()))
//			{
//				thingtoproduce = toproduces.getID();
//				invalidproduction=false;
//			}
//		}
		xml.setProgressAmount(r.nextInt());
		xml.setProgressPrimitive(createExampleAction(xml.getID(),r));
		xml.setTemplateID(chosentemplate.getID());
		xml.setXPosition(r.nextInt());
		xml.setYPosition(r.nextInt());
		return xml;
	}
	public static XmlAction createExampleAction(int unitId,Random r)
	{
		//pick a random type or null, then pick some random arguments
		int ntypes = ActionType.values().length;
		int typeind=r.nextInt(ntypes+1);
		if (typeind==ntypes)
			return null;
		
		ActionType type = ActionType.values()[typeind];
		switch (type)
		{
		case COMPOUNDATTACK:
		{
			XmlTargetedAction a = new XmlTargetedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTargetId(r.nextInt());
			return a;
		}
		case COMPOUNDDEPOSIT:
		{
			XmlTargetedAction a = new XmlTargetedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTargetId(r.nextInt());
			return a;
		}
		case COMPOUNDMOVE:
		{
			XmlLocatedAction a = new XmlLocatedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setX(r.nextInt());
			a.setY(r.nextInt());
			return a;
		}
		case COMPOUNDGATHER:
		{
			XmlTargetedAction a = new XmlTargetedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTargetId(r.nextInt());
			return a;
		}
		case COMPOUNDPRODUCE:
		{
			XmlProductionAction a = new XmlProductionAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTemplateId(r.nextInt());
			return a;
		}
		case COMPOUNDBUILD:
		{
			XmlLocatedProductionAction a = new XmlLocatedProductionAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setX(r.nextInt());
			a.setY(r.nextInt());
			a.setTemplateId(r.nextInt());
			return a;
		}
		case PRIMITIVEATTACK:
		{
			XmlTargetedAction a = new XmlTargetedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTargetId(r.nextInt());
			return a;
		}
		case PRIMITIVEDEPOSIT:
		{
			XmlDirectedAction a = new XmlDirectedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setDirection(Direction.values()[r.nextInt(Direction.values().length)]);
			return a;
		}
		case PRIMITIVEMOVE:
		{
			XmlDirectedAction a = new XmlDirectedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setDirection(Direction.values()[r.nextInt(Direction.values().length)]);
			return a;
		}
		case PRIMITIVEGATHER:
		{
			XmlDirectedAction a = new XmlDirectedAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setDirection(Direction.values()[r.nextInt(Direction.values().length)]);
			return a;
		}
		case PRIMITIVEPRODUCE:
		{
			XmlProductionAction a = new XmlProductionAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTemplateId(r.nextInt());
			return a;
		}
		case PRIMITIVEBUILD:
		{
			XmlProductionAction a = new XmlProductionAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			a.setTemplateId(r.nextInt());
			return a;
		}
		case FAILED:
		{
			XmlAction a = new XmlAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			return a;
		}
		case FAILEDPERMANENTLY:
		{
			XmlAction a = new XmlAction();
			a.setActionType(type);
			a.setUnitId(unitId);
			return a;
		}
		default:
			throw new RuntimeException("Test is not up to date with action types: "+type +" is not covered");
		}
		
	}
	public static XmlUpgradeTemplate createExampleUpgradeTemplate(Random r, List<Integer> idsofunits, List<Integer> idsofupgrades) {
		XmlUpgradeTemplate xml = new XmlUpgradeTemplate();
		xml.setID(r.nextInt());
		xml.setPiercingAttackChange(r.nextInt());
		xml.setBasicAttackChange(r.nextInt());
		xml.setArmorChange(r.nextInt());
		xml.setHealthChange(r.nextInt());
		xml.setRangeChange(r.nextInt());
		xml.setSightRangeChange(r.nextInt());
		char[] name = new char[r.nextInt(5)+8];
		for (int i = 0; i<name.length;i++)name[i]=(char)('a'+r.nextInt(26));
		xml.setName(new String(name));
		xml.setTimeCost(r.nextInt());
		xml.setWoodCost(r.nextInt());
		for (Integer i : idsofunits)
		{
			if (r.nextBoolean())
				xml.getAffectedUnitTypes().add(i);
		}
		for (Integer i : idsofunits)
		{
			if (r.nextBoolean())
				xml.getUnitPrerequisite().add(i);
		}
		for (Integer i : idsofupgrades)
		{
			if (r.nextBoolean())
				xml.getUpgradePrerequisite().add(i);
		}
		return xml;
	}
	public static XmlUnitTemplate createExampleUnitTemplate(Random r, List<Integer> idsofunits, List<Integer> idsofupgrades) {
		XmlUnitTemplate xml = new XmlUnitTemplate();
		xml.setID(r.nextInt());
		char[] name = new char[r.nextInt(4)+3];
		for (int i = 0; i<name.length;i++)name[i]=(char)('a'+r.nextInt(26));
		xml.setName(new String(name));
		xml.setTimeCost(r.nextInt());
		xml.setWoodCost(r.nextInt());
		xml.setArmor(r.nextInt());
		xml.setBaseAttack(r.nextInt());
		xml.setBaseHealth(r.nextInt(Integer.MAX_VALUE-1)+1);
		xml.setCanAcceptGold(r.nextBoolean());
		xml.setCanAcceptWood(r.nextBoolean());
		xml.setCanBuild(r.nextBoolean());
		xml.setCanGather(r.nextBoolean());
		xml.setCanMove(r.nextBoolean());
		xml.setCharacter((short)('a'+r.nextInt(26)));
		xml.setFoodProvided(r.nextInt());
		xml.setGoldGatherRate(r.nextInt());
		xml.setPiercingAttack(r.nextInt());
		xml.setRange(r.nextInt());
		xml.setSightRange(r.nextInt());
		xml.setWoodGatherRate(r.nextInt());
		xml.setDurationAttack(r.nextInt());
		xml.setDurationMove(r.nextInt());
		xml.setDurationDeposit(r.nextInt());
		xml.setDurationGatherGold(r.nextInt());
		xml.setDurationGatherWood(r.nextInt());
		for (Integer i : idsofunits)
		{
			if (r.nextBoolean())
				xml.getProduces().add(i);
		}
		for (Integer i : idsofupgrades)
		{
			if (r.nextBoolean())
				xml.getProduces().add(i);
		}
		for (Integer i : idsofunits)
		{
			if (r.nextBoolean())
				xml.getUnitPrerequisite().add(i);
		}
		for (Integer i : idsofupgrades)
		{
			if (r.nextBoolean())
				xml.getUpgradePrerequisite().add(i);
		}
		
		return xml;
	}
	public static XmlPlayer createExamplePlayer(Random r) {
		XmlPlayer xml = new XmlPlayer();
		
		List<Integer> targetidssofar = new ArrayList<Integer>();
		List<Integer> unitidssofar=new ArrayList<Integer>();
		List<Integer> upgradeidssofar=new ArrayList<Integer>();
		List<Integer> unittemplateidssofar = new ArrayList<Integer>();
		List<Integer> alltemplateidssofar = new ArrayList<Integer>();
		List<XmlTemplate> alltemplatessofar = new ArrayList<XmlTemplate>();
		List<Integer> upgradetemplateidssofar = new ArrayList<Integer>();
		List<XmlUnitTemplate> unittemplatessofar = new ArrayList<XmlUnitTemplate>();
		for(int i = 0; i < r.nextInt(15)+6; i++)
		{
			if  (r.nextBoolean())
			{
				XmlUpgradeTemplate toadd = createExampleUpgradeTemplate(r,unitidssofar,upgradeidssofar);
				xml.getTemplate().add(toadd);
				upgradeidssofar.add(toadd.getID());
				alltemplateidssofar.add(toadd.getID());
				alltemplatessofar.add(toadd);
				upgradetemplateidssofar.add(toadd.getID());
			}
			else
			{
				XmlUnitTemplate toadd = createExampleUnitTemplate(r,unitidssofar,upgradeidssofar);
				xml.getTemplate().add(toadd);
				unitidssofar.add(toadd.getID());
				alltemplateidssofar.add(toadd.getID());
				alltemplatessofar.add(toadd);
				unittemplateidssofar.add(toadd.getID());
				unittemplatessofar.add(toadd);
			}
		}
		//verify ids and names, if they are not unique, the test isn't valid, need to regenerate stuff again
		for (Integer id :alltemplateidssofar)
		{
			if (alltemplateidssofar.indexOf(id) != alltemplateidssofar.lastIndexOf(id))
			{
				return createExamplePlayer(r);
			}
		}
		for (Integer id: unitidssofar)
		{
			if (unitidssofar.indexOf(id) != unitidssofar.lastIndexOf(id) || upgradeidssofar.contains(id))
			{
				return createExamplePlayer(r);
			}
		}
		for (Integer id : upgradeidssofar)
		{
			if (upgradeidssofar.indexOf(id) != upgradeidssofar.lastIndexOf(id) || unitidssofar.contains(id))
			{
				return createExamplePlayer(r);
			}
		}
		
		int numunits = r.nextInt(4)+3;
		for (int i = 0; i<numunits;i++)
		{
			XmlUnit toadd = createExampleUnit(r,unittemplatessofar, alltemplatessofar);
			xml.getUnit().add(toadd);
			targetidssofar.add(toadd.getID());
		}
		for (Integer id :targetidssofar)
		{
			if (targetidssofar.indexOf(id) != targetidssofar.lastIndexOf(id))
			{
				return createExamplePlayer(r);
			}
		}
		xml.setID(r.nextInt());
		xml.setSupply(r.nextInt());
		xml.setSupplyCap(r.nextInt());
		
		XmlResourceQuantity gold = new XmlResourceQuantity();
		gold.setType(ResourceType.GOLD);
		gold.setQuantity(r.nextInt());
		xml.getResourceAmount().add(gold);
		
		XmlResourceQuantity wood = new XmlResourceQuantity();
		wood.setType(ResourceType.WOOD);
		wood.setQuantity(r.nextInt());		
		xml.getResourceAmount().add(wood);
		
		for (Integer i : upgradetemplateidssofar)
		{
			if (r.nextBoolean())
				xml.getUpgrade().add(i);
		}
		
		return xml;
	}
	
	public static History createExampleHistory(Random r) {
		final int playerDiff = 20;
		final int minPlayers = 5;
		History h = new History();
		h.setFogOfWar(r.nextBoolean());
		h.setObserverHistory(createExamplePlayerHistory(r,Agent.OBSERVER_ID));
		int numPlayers = r.nextInt(playerDiff)+minPlayers;
		for (int i = 0; i<numPlayers; i++)
		{
			//Give it an unused random number
			int newplayernum;
			while (h.getPlayerHistory(newplayernum = r.nextInt(Integer.MAX_VALUE))!=null)
				;
			
			h.setPlayerHistory(createExamplePlayerHistory(r,newplayernum));
		}
		return h;
	}

	private static PlayerHistory createExamplePlayerHistory(Random r,
			int newplayernum) {
		PlayerHistory h = new PlayerHistory(newplayernum);
		h.setEventLogger(createExampleEventLogger(r));
		h.setCommandFeedback(createExampleActionResultLogger(r));
		h.setPrimitivesExecuted(createExampleActionResultLogger(r));
		h.setCommandsIssued(createExampleActionLogger(r));
		return h;
	}

	private static ActionLogger createExampleActionLogger(Random r) {
		ActionLogger al = new ActionLogger();
		final double zeroChanceSteps = 0.1;
		final int numStepsDiff = 100;
		final double zeroChanceActions = 0.1;
		final int numActionsDiff = 100;
		
		int numSteps;
		if (r.nextDouble()<zeroChanceSteps)
			numSteps = 0;
		else
			numSteps= r.nextInt(numStepsDiff);
		
		for (int i = 0; i<numSteps; i++) {
			int numActions;
			if (r.nextDouble()<zeroChanceActions)
				numActions = 0;
			else
				numActions= r.nextInt(numActionsDiff);
			for (int j = 0; j<numActions; j++)
				al.addAction(i, ActionAdapter.fromXml(createExampleAction(r.nextInt(), r)));
		}
		return al;
	}

	private static ActionResultLogger createExampleActionResultLogger(Random r) {
		ActionResultLogger al = new ActionResultLogger();
		final double zeroChanceSteps = 0.1;
		final int numStepsDiff = 100;
		final double zeroChanceActions = 0.1;
		final int numActionsDiff = 100;
		
		int numSteps;
		if (r.nextDouble()<zeroChanceSteps)
			numSteps = 0;
		else
			numSteps= r.nextInt(numStepsDiff);
		
		for (int i = 0; i<numSteps; i++) {
			int numActions;
			if (r.nextDouble()<zeroChanceActions)
				numActions = 0;
			else
				numActions= r.nextInt(numActionsDiff);
			for (int j = 0; j<numActions; j++)
				al.addActionResult(i, new ActionResult(ActionAdapter.fromXml(createExampleAction(r.nextInt(), r)),ActionFeedback.values()[r.nextInt(ActionFeedback.values().length)]));
		}
		return al;
	}

	private static EventLogger createExampleEventLogger(Random r) {
		EventLogger el = new EventLogger();
		final double zeroChanceSteps = 0.1;
		final int numStepsDiff = 100;
		final double zeroChanceLogs = 0.1;
		final int numLogsDiff = 100;
		
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordBirth(i, r.nextInt(), r.nextInt(), r.nextInt());
			}
		}
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordDamage(i, r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt());
			}
		}
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordDeath(i, r.nextInt(), r.nextInt());
			}
		}
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordResourceDropoff(i, r.nextInt(), r.nextInt(), r.nextInt(), ResourceType.values()[r.nextInt(ResourceType.values().length)], r.nextInt());
			}
		}
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordResourceNodeExhaustion(i, r.nextInt(), ResourceNode.Type.values()[r.nextInt(ResourceNode.Type.values().length)]);
			}
		}
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordResourcePickup(i, r.nextInt(), r.nextInt(), ResourceType.values()[r.nextInt(ResourceType.values().length)], r.nextInt(), r.nextInt(), ResourceNode.Type.values()[r.nextInt(ResourceNode.Type.values().length)]);
			}
		}
		{
			int numSteps;
			if (r.nextDouble()<zeroChanceSteps)
				numSteps = 0;
			else
				numSteps= r.nextInt(numStepsDiff);
			
			for (int i = 0; i<numSteps; i++) {
				int numLogs;
				if (r.nextDouble()<zeroChanceLogs)
					numLogs = 0;
				else
					numLogs= r.nextInt(numLogsDiff);
				for (int j = 0; j<numLogs; j++)
					el.recordUpgrade(i, r.nextInt(), r.nextInt(), r.nextInt());
			}
		}
		{
			int numLogs;
			if (r.nextDouble()<zeroChanceLogs)
				numLogs = 0;
			else
				numLogs= r.nextInt(numLogsDiff);
			for (int j = 0; j<numLogs; j++)
				el.recordRevealedResourceNode(r.nextInt(), r.nextInt(), ResourceNode.Type.values()[r.nextInt(ResourceNode.Type.values().length)]);
		}
		return el;
	}
}
