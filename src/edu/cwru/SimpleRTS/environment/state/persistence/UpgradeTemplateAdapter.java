package edu.cwru.SimpleRTS.environment.state.persistence;

import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public class UpgradeTemplateAdapter {

	public static UpgradeTemplate fromXml(XmlUpgradeTemplate xml,int player) {
		UpgradeTemplate ut = new UpgradeTemplate(xml.getID());
		ut.setFoodCost(xml.getFoodCost());//if(obj.has("FoodCost"))
		ut.setGoldCost(xml.getGoldCost());//template.setGoldCost(obj.getInt("GoldCost"));
		ut.setName(xml.getName());//template.setName(obj.getString("Name"));

		ut.setTimeCost(xml.getTimeCost());//template.setTimeCost(obj.getInt("TimeCost"));
		ut.setWoodCost(xml.getWoodCost());//template.setWoodCost(obj.getInt("WoodCost"));
		ut.setPlayer(player);
		for (String s : xml.getUnitPrerequisite())//if(obj.has("BuildPrereq"))
			ut.addBuildPrereqItem(s);
		for (String s : xml.getUpgradePrerequisite())//if(obj.has("UpgradePrereq"))
			ut.addUpgradePrereqItem(s);
		
		for (String s : xml.getAffectedUnitTypes())//if(obj.has("Produces"))
			ut.addAffectedUnit(s);
		
		ut.setPiercingAttackChange(xml.getPiercingAttackChange());
		ut.setBasicAttackChange(xml.getBasicAttackChange());
		ut.setArmorChange(xml.getArmorChange());
		ut.setHealthChange(xml.getHealthChange());
		ut.setRangeChange(xml.getRangeChange());
		ut.setSightRangeChange(xml.getSightRangeChange());
		return ut;
	}
		
	
	public static XmlUpgradeTemplate toXml(UpgradeTemplate ut) {
		XmlUpgradeTemplate xml = new XmlUpgradeTemplate();
		xml.setFoodCost(ut.getFoodCost());
		xml.setID(ut.ID);
		xml.setName(ut.getName());
		xml.setTimeCost(ut.getTimeCost());
		xml.setWoodCost(ut.getWoodCost());
		xml.setGoldCost(ut.getGoldCost());
		xml.setPiercingAttackChange(ut.getPiercingAttackChange());
		xml.setBasicAttackChange(ut.getBasicAttackChange());
		xml.setArmorChange(ut.getArmorChange());
		xml.setHealthChange(ut.getHealthChange());
		xml.setRangeChange(ut.getRangeChange());
		xml.setSightRangeChange(ut.getSightRangeChange());
		for (String s:ut.getUnitPrerequisiteStrings())
			xml.getUnitPrerequisite().add(s);
		for (String s:ut.getUpgradePrerequisiteStrings())
			xml.getUpgradePrerequisite().add(s);
		
		for (UnitTemplate affected :ut.getAffectedUnits())
			xml.getAffectedUnitTypes().add(affected.getName());
		
		return xml;
	}
}
