package edu.cwru.SimpleRTS.environment.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.Random;

import org.json.JSONException;
import org.junit.Test;

import edu.cwru.SimpleRTS.environment.PlayerState;
import edu.cwru.SimpleRTS.environment.state.persistence.PlayerAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public class PlayerAdapterTest {
	
	
	@Test
	public void textFromXml() throws FileNotFoundException, JSONException {
		XmlPlayer xml = AdapterTestUtil.createExamplePlayer(new Random(3));
		PlayerAdapter adapter = new PlayerAdapter();
		
		PlayerState player = adapter.fromXml(xml);	
		checkEquality(xml, player);
	}
	public void checkEquality(XmlPlayer xml, PlayerState player)
	{
		assertEquals("playernum did not match!", xml.getID(), player.playerNum);
		for(XmlResourceQuantity amount : xml.getResourceAmount())
		{
			assertEquals(amount.getType() + " amount did not match!", 
						 amount.getQuantity(), 
						 player.getCurrentResourceAmount(amount.getType()));
		}
		assertEquals("supply did not match!", xml.getSupply(), player.getCurrentSupply());
		assertEquals("supply cap did not match!", xml.getSupplyCap(), player.getCurrentSupplyCap());
		assertEquals("different number of upgrades",player.getUpgrades().size(),xml.getUpgrade().size());
		for(Integer i : xml.getUpgrade())
		{
			assertTrue("upgrade " + i + " from xml wasn't in player!", player.getUpgrades().contains(i));
		}
		for(Integer i : player.getUpgrades())
		{
			assertTrue("upgrade " + i + " in player wasn't in xml!", xml.getUpgrade().contains(i));
		}
		assertEquals("Number of units did not match",xml.getUnit().size(),player.getUnits().values().size());
		for (XmlUnit xmlunit : xml.getUnit())
		{
			Unit u = player.getUnit(xmlunit.getID());
			assertNotNull("Player did not contain unit with the right id",u);
			UnitAdapterTest.checkEquality(xmlunit, u);
		}
		for (XmlTemplate t : xml.getTemplate())
		{
			checkEquality(t,xml.getID(),player.getTemplate(t.getID()));
		}
	}
	public static void checkEquality(XmlTemplate xt, int playernum, Template t)
	{
		assertNotNull("xml one was null ",xt);
		assertNotNull("real one was null ",t);
		assertTrue("Weren't same type of template",xt instanceof XmlUnitTemplate && t instanceof UnitTemplate||xt instanceof XmlUpgradeTemplate && t instanceof UpgradeTemplate);
		assertEquals(t.ID, xt.getID());
		assertEquals(t.getFoodCost(),xt.getFoodCost());
		assertEquals(t.getGoldCost(),xt.getGoldCost());
		assertEquals(t.getName(),xt.getName());
		assertEquals(t.getPlayer(),playernum);
		assertEquals(t.getTimeCost(),xt.getTimeCost());
		assertEquals(t.getUnitPrerequisiteStrings().size(),xt.getUnitPrerequisite().size());
		for(String s : xt.getUnitPrerequisite())
		{
			assertTrue(t.getUnitPrerequisiteStrings().contains(s));
		}
		assertEquals(t.getUpgradePrerequisiteStrings().size(),xt.getUpgradePrerequisite().size());
		for(String s : xt.getUpgradePrerequisite())
		{
			assertTrue(t.getUpgradePrerequisiteStrings().contains(s));
		}
		assertEquals(t.getWoodCost(),xt.getWoodCost());
		if (t instanceof UpgradeTemplate)
		{
			UpgradeTemplate ut = (UpgradeTemplate)t;
			XmlUpgradeTemplate uxt = (XmlUpgradeTemplate)xt;
			assertEquals(ut.getAttackChange(),uxt.getAttackChange());
			assertEquals(ut.getDefenseChange(),uxt.getDefenseChange());
			
			assertEquals(ut.getAffectedUnits().size(),uxt.getAffectedUnitTypes().size());
			for(UnitTemplate affected : ut.getAffectedUnits())
			{
				assertTrue("xml:"+uxt.getAffectedUnitTypes()+" normal:"+ut.getAffectedUnits(),uxt.getAffectedUnitTypes().contains(affected.getName()));
			}
		}
		if (t instanceof UnitTemplate)
		{
			UnitTemplate ut = (UnitTemplate)t;
			XmlUnitTemplate uxt = (XmlUnitTemplate)xt;
			assertEquals(ut.canAcceptGold(),uxt.isCanAcceptGold());
			assertEquals(ut.canAcceptWood(),uxt.isCanAcceptWood());
			assertEquals(ut.canBuild(),uxt.isCanBuild());
			assertEquals(ut.canGather(),uxt.isCanGather());
			assertEquals(ut.canMove(),uxt.isCanMove());
			assertEquals(ut.getArmor(),uxt.getArmor());
			assertEquals(ut.getBaseHealth(),uxt.getBaseHealth());
			assertEquals(ut.getBasicAttack(),uxt.getBaseAttack());
			assertEquals(ut.getCharacter(),uxt.getCharacter());
			assertEquals(ut.getFoodProvided(),uxt.getFoodProvided());
			assertEquals(ut.getGatherRate(Type.GOLD_MINE),uxt.getGoldGatherRate());
			assertEquals(ut.getGatherRate(Type.TREE),uxt.getWoodGatherRate());
			assertEquals(ut.getPiercingAttack(),uxt.getPiercingAttack());
			assertEquals(ut.getDurationAttack(),uxt.getDurationAttack());
			assertEquals(ut.getDurationGatherGold(),uxt.getDurationGatherGold());
			assertEquals(ut.getDurationGatherWood(),uxt.getDurationGatherWood());
			assertEquals(ut.getDurationDeposit(),uxt.getDurationDeposit());
			assertEquals(ut.getDurationMove(),uxt.getDurationMove());
			assertEquals(ut.getProducesStrings().size(),uxt.getProduces().size());
			for(String s : uxt.getProduces())
			{
				assertTrue(ut.getProducesStrings().contains(s));
			}
			assertEquals(ut.getRange(),uxt.getRange());
			assertEquals(ut.getSightRange(),uxt.getSightRange());
		}
		
	}
}
