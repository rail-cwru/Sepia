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
package edu.cwru.sepia.environment.state.persistence;

import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.model.state.UpgradeTemplate;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUpgradeTemplate;

public class UpgradeTemplateAdapter {

	public static UpgradeTemplate fromXml(XmlUpgradeTemplate xml,int player) {
		UpgradeTemplate ut = new UpgradeTemplate(xml.getID());
		ut.setFoodCost(xml.getFoodCost());//if(obj.has("FoodCost"))
		ut.setGoldCost(xml.getGoldCost());//template.setGoldCost(obj.getInt("GoldCost"));
		ut.setName(xml.getName());//template.setName(obj.getString("Name"));

		ut.setTimeCost(xml.getTimeCost());//template.setTimeCost(obj.getInt("TimeCost"));
		ut.setWoodCost(xml.getWoodCost());//template.setWoodCost(obj.getInt("WoodCost"));
		ut.setPlayer(player);
		for (Integer i : xml.getUnitPrerequisite())//if(obj.has("BuildPrereq"))
			ut.addBuildPrerequisite(i);
		for (Integer i : xml.getUpgradePrerequisite())//if(obj.has("UpgradePrereq"))
			ut.addUpgradePrerequisite(i);
		
		for (Integer i : xml.getAffectedUnitTypes())//if(obj.has("Produces"))
			ut.addAffectedUnit(i);
		
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
		for (Integer i :ut.getBuildPrerequisites())
			xml.getUnitPrerequisite().add(i);
		for (Integer i:ut.getUpgradePrerequisites())
			xml.getUpgradePrerequisite().add(i);
		
		for (Integer affected :ut.getAffectedUnits())
			xml.getAffectedUnitTypes().add(affected);
		
		return xml;
	}
}
