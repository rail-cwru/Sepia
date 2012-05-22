package edu.cwru.SimpleRTS.environment.state.persistence;

import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public class UnitTemplateAdapter {

	public static UnitTemplate fromXml(XmlUnitTemplate xml,int player) {
		UnitTemplate ut = new UnitTemplate(xml.getID());
		ut.setArmor(xml.getArmor());//template.setArmor(obj.getInt("Armor"));
		ut.setBasicAttack(xml.getBaseAttack());//if(obj.has("BasicAttack"))
		ut.setBaseHealth(xml.getBaseHealth());//template.setBaseHealth(obj.getInt("HitPoints"));
		ut.setCharacter((char)xml.getCharacter());//template.setCharacter(obj.getString("Character").charAt(0));
		ut.setFoodCost(xml.getFoodCost());//if(obj.has("FoodCost"))
		ut.setFoodProvided(xml.getFoodProvided());//	if(obj.has("FoodGiven"))
		ut.setGoldCost(xml.getGoldCost());//template.setGoldCost(obj.getInt("GoldCost"));
		ut.setGoldGatherRate(xml.getGoldGatherRate());//if(obj.has("GoldPerTrip"))
		ut.setName(xml.getName());//template.setName(obj.getString("Name"));
		ut.setPiercingAttack(xml.getPiercingAttack());//if(obj.has("Piercing"))
		for (Integer i : xml.getProduces())//if(obj.has("Produces"))
			ut.addProductionItem(i);
		ut.setRange(xml.getRange());//if(obj.has("Range"))
		ut.setSightRange(xml.getSightRange());//template.setSightRange(obj.getInt("SightRange"));
		ut.setTimeCost(xml.getTimeCost());//template.setTimeCost(obj.getInt("TimeCost"));
		ut.setWoodCost(xml.getWoodCost());//template.setWoodCost(obj.getInt("WoodCost"));
		ut.setWoodGatherRate(xml.getWoodGatherRate());//if(obj.has("WoodPerTrip"))
		ut.setCanAcceptGold(xml.isCanAcceptGold());//if(obj.has("AcceptsGold"))
		ut.setCanAcceptWood(xml.isCanAcceptWood());//if(obj.has("AcceptsWood"))
		ut.setCanBuild(xml.isCanBuild());//if(obj.has("Builder"))
		ut.setCanGather(xml.isCanGather());//if(obj.has("Gatherer"))
		ut.setCanMove(xml.isCanMove());//if(obj.has("Mobile"))
		ut.setDurationAttack(xml.getDurationAttack());
		ut.setDurationDeposit(xml.getDurationDeposit());
		ut.setDurationMove(xml.getDurationMove());
		ut.setDurationGatherGold(xml.getDurationGatherGold());
		ut.setDurationGatherWood(xml.getDurationGatherWood());
		ut.setPlayer(player);
		for (Integer i : xml.getUnitPrerequisite())//if(obj.has("BuildPrereq"))
			ut.addBuildPrerequisite(i);
		for (Integer i : xml.getUpgradePrerequisite())//if(obj.has("UpgradePrereq"))
			ut.addUpgradePrerequisite(i);
		
		
		
		return ut;
	}
		
	
	public static XmlUnitTemplate toXml(UnitTemplate ut) {
		XmlUnitTemplate xml = new XmlUnitTemplate();
		xml.setFoodCost(ut.getFoodCost());
		xml.setID(ut.ID);
		xml.setName(ut.getName());
		xml.setTimeCost(ut.getTimeCost());
		xml.setWoodCost(ut.getWoodCost());
		xml.setGoldCost(ut.getGoldCost());
		
		for (Integer i : ut.getBuildPrerequisites())
			xml.getUnitPrerequisite().add(i);
		for (Integer i : ut.getUpgradePrerequisites())
			xml.getUpgradePrerequisite().add(i);
		
		xml.setArmor(ut.getArmor());
		xml.setBaseAttack(ut.getBasicAttack());
		xml.setBaseHealth(ut.getBaseHealth());
		xml.setCanAcceptGold(ut.canAcceptGold());
		xml.setCanAcceptWood(ut.canAcceptWood());
		xml.setCanBuild(ut.canBuild());
		xml.setCanGather(ut.canGather());
		xml.setCanMove(ut.canMove());
		xml.setCharacter((short)ut.getCharacter());
		xml.setFoodProvided(ut.getFoodProvided());
		xml.setGoldGatherRate(ut.getGatherRate(Type.GOLD_MINE));
		xml.setPiercingAttack(ut.getPiercingAttack());
		xml.setRange(ut.getRange());
		xml.setSightRange(ut.getSightRange());
		
		xml.setWoodGatherRate(ut.getGatherRate(Type.TREE));
		xml.setDurationAttack(ut.getDurationAttack());
		xml.setDurationDeposit(ut.getDurationDeposit());
		xml.setDurationMove(ut.getDurationMove());
		xml.setDurationGatherGold(ut.getDurationGatherGold());
		xml.setDurationGatherWood(ut.getDurationGatherWood());
		for (Integer i : ut.getProduces())
			xml.getProduces().add(i);
		
		return xml;
	}
}
