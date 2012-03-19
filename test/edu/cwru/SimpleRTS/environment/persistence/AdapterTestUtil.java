package edu.cwru.SimpleRTS.environment.persistence;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.junit.BeforeClass;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlDirectedAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlLocatedAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlLocatedProductionAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlProductionAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlTargetedAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.UnitTask;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class AdapterTestUtil {

	@SuppressWarnings("rawtypes")
	@BeforeClass
	public static Map<Integer,Template> loadTemplates() throws FileNotFoundException, JSONException {
		Map<Integer,Template> templates = new HashMap<Integer,Template>();
		List<UnitTemplate> templateList = TypeLoader.loadUnitsFromFile("data/unit_templates", 0,new State());
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
		xml.setCurrentHealth(r.nextInt(chosentemplate.getBaseHealth()));
		
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
		xml.setUnitTask(UnitTask.values()[r.nextInt(UnitTask.values().length)]);
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
	public static XmlUpgradeTemplate createExampleUpgradeTemplate(Random r, List<String> namesofunits, List<String> namesofupgrades) {
		XmlUpgradeTemplate xml = new XmlUpgradeTemplate();
		xml.setID(r.nextInt());
		xml.setAttackChange(r.nextInt());
		xml.setDefenseChange(r.nextInt());
		char[] name = new char[r.nextInt(5)+8];
		for (int i = 0; i<name.length;i++)name[i]=(char)('a'+r.nextInt(26));
		xml.setName(new String(name));
		xml.setTimeCost(r.nextInt());
		xml.setWoodCost(r.nextInt());
		for (String s : namesofunits)
		{
			if (r.nextBoolean())
				xml.getAffectedUnitTypes().add(s);
		}
		for (String s : namesofunits)
		{
			if (r.nextBoolean())
				xml.getUnitPrerequisite().add(s);
		}
		for (String s : namesofupgrades)
		{
			if (r.nextBoolean())
				xml.getUpgradePrerequisite().add(s);
		}
		return xml;
	}
	public static XmlUnitTemplate createExampleUnitTemplate(Random r, List<String> namesofunits, List<String> namesofupgrades) {
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
		xml.setDurationGoldGather(r.nextInt());
		xml.setDurationWoodGather(r.nextInt());
		for (String s : namesofunits)
		{
			if (r.nextBoolean())
				xml.getProduces().add(s);
		}
		for (String s : namesofupgrades)
		{
			if (r.nextBoolean())
				xml.getProduces().add(s);
		}
		for (String s : namesofunits)
		{
			if (r.nextBoolean())
				xml.getUnitPrerequisite().add(s);
		}
		for (String s : namesofupgrades)
		{
			if (r.nextBoolean())
				xml.getUpgradePrerequisite().add(s);
		}
		
		return xml;
	}
	public static XmlPlayer createExamplePlayer(Random r) {
		XmlPlayer xml = new XmlPlayer();
		
		List<Integer> targetidssofar = new ArrayList<Integer>();
		List<String> unitnamessofar=new ArrayList<String>();
		List<String> upgradenamessofar=new ArrayList<String>();
		List<Integer> unittemplateidssofar = new ArrayList<Integer>();
		List<Integer> alltemplateidssofar = new ArrayList<Integer>();
		List<XmlTemplate> alltemplatessofar = new ArrayList<XmlTemplate>();
		List<Integer> upgradetemplateidssofar = new ArrayList<Integer>();
		List<XmlUnitTemplate> unittemplatessofar = new ArrayList<XmlUnitTemplate>();
		for(int i = 0; i < r.nextInt(15)+6; i++)
		{
			if  (r.nextBoolean())
			{
				XmlUpgradeTemplate toadd = createExampleUpgradeTemplate(r,unitnamessofar,upgradenamessofar);
				xml.getTemplate().add(toadd);
				upgradenamessofar.add(toadd.getName());
				alltemplateidssofar.add(toadd.getID());
				alltemplatessofar.add(toadd);
				upgradetemplateidssofar.add(toadd.getID());
			}
			else
			{
				XmlUnitTemplate toadd = createExampleUnitTemplate(r,unitnamessofar,upgradenamessofar);
				xml.getTemplate().add(toadd);
				unitnamessofar.add(toadd.getName());
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
		for (String name : unitnamessofar)
		{
			if (unitnamessofar.indexOf(name) != unitnamessofar.lastIndexOf(name) || upgradenamessofar.contains(name))
			{
				return createExamplePlayer(r);
			}
		}
		for (String name : upgradenamessofar)
		{
			if (upgradenamessofar.indexOf(name) != upgradenamessofar.lastIndexOf(name) || unitnamessofar.contains(name))
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
}
